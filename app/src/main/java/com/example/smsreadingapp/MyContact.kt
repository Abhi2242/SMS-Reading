package com.example.smsreadingapp

data class MyContact(
    val mName: String,
    val mContact: String
)
{
    fun getName(): String {
        return mName
    }

    fun getContact(): String{
        return mContact
    }
}
