package com.example.smsreadingapp

import android.annotation.SuppressLint
import android.app.Service
import android.content.*
import android.provider.Telephony
import android.telephony.SmsManager
import android.util.Log
import android.widget.Toast
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import java.util.*

@Suppress("DEPRECATION")
class MyBroadcast : BroadcastReceiver() {

    private var mainArrayList: ArrayList<MyContact>? = null
    data class DuplicateContactList(val dubContact: String)
    private var duplicateArrayList: ArrayList<DuplicateContactList>? = null
    private lateinit var receivedContact: String
    private lateinit var receivedMessage: String
    private var prefCompanyName: String? = null
    private var prefCompanyAcc: String? = null

    @SuppressLint("UnsafeProtectedBroadcastReceiver")
    override fun onReceive(context: Context, intent: Intent) {

        val shareData: SharedPreferences =
            context.getSharedPreferences("Shared Data", Service.MODE_PRIVATE)
        val gson = Gson()
        val json: String? = shareData.getString("Array List", null)
        val type = object : TypeToken<ArrayList<MyContact>>() {}.type
        mainArrayList = gson.fromJson(json, type)
        prefCompanyName = shareData.getString("Company Name", null).toString()
        prefCompanyAcc = shareData.getString("Company Acc", null).toString()

        Log.i("MyLog", "Main Array: $mainArrayList\n$prefCompanyName\n$prefCompanyAcc")

        if (mainArrayList?.size == null || prefCompanyName == "" || prefCompanyAcc == "") {
            if (mainArrayList?.size == null){
                context.startActivity(Intent(context, AddContactActivity::class.java))
            }
            else{
                context.startActivity(Intent(context, AddProfileDetails::class.java))
            }
        } else {
            duplicateArrayList =
                mainArrayList?.map { DuplicateContactList(it.mContact) } as ArrayList<DuplicateContactList>?
            Log.i("dub list", "$duplicateArrayList")
        }

        for (mySms in Telephony.Sms.Intents.getMessagesFromIntent(intent)) {
            receivedContact = mySms.displayOriginatingAddress.toString()
            receivedMessage = mySms.displayMessageBody.toString()
            val contact2 = receivedContact.replace("+", "")
            Log.i("Received", contact2)

            if (duplicateArrayList?.isNotEmpty() == true) {

                // prefCompanyName & prefCompanyAcc coming from shared preferences
                val regex = prefCompanyName!!.toRegex()
                val regex1 = prefCompanyAcc!!.toRegex()
                if ((receivedMessage.contains(regex) || receivedMessage.contains(regex1)) && receivedContact.length > 2) {   //   && contactExists(context, receivedContact)
                    sendMessage(receivedMessage)
                    Toast.makeText(
                        context,
                        "Message received from $receivedContact",
                        Toast.LENGTH_LONG
                    ).show()
                    Toast.makeText(context, "Message Send", Toast.LENGTH_SHORT).show()
                } else {
                    Toast.makeText(
                        context,
                        "Message doesn't contain relevant information",
                        Toast.LENGTH_LONG
                    ).show()

                }
            }
        }
    }


    private fun sendMessage(msg: String) {
        val sms = SmsManager.getDefault()
        for (n in duplicateArrayList!!) {
            sms.sendTextMessage(
                n.dubContact,
                null,
                msg,
                null,
                null
            )
        }
        Log.i("Broadcast SMS send", "SMS send")
    }
}