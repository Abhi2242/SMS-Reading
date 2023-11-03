package com.example.smsreadingapp

import android.annotation.SuppressLint
import android.content.Intent
import android.content.SharedPreferences
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.EditText
import android.widget.Toast

class AddProfileDetails : AppCompatActivity() {
    private lateinit var btnSave: Button
    private lateinit var etCompanyName: EditText
    private lateinit var etCompanyAcc: EditText
    private lateinit var companyName: String
    private lateinit var companyAcc: String

    @SuppressLint("MissingInflatedId")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_profile_details)

        btnSave = findViewById(R.id.btn_save_profile)
        etCompanyName = findViewById(R.id.et_cn)
        etCompanyAcc = findViewById(R.id.et_cAcc)
        companyName = etCompanyName.text.toString()
        companyAcc = etCompanyAcc.text.toString()

        btnSave.setOnClickListener {
//            companyName = etCompanyName.text.toString()
//            companyAcc = etCompanyAcc.text.toString()
            if (etCompanyName.text.isNotEmpty() && etCompanyAcc.text.isNotEmpty()){
                saveData()
                startActivity(Intent(this,MainActivity::class.java))
            }
            else{
                saveData()
                Toast.makeText(this, "Else ${companyName}\n${companyAcc}", Toast.LENGTH_LONG).show()
            }
        }
    }

    private fun saveData() {
        Log.i("Save Company", "Data: ${etCompanyName.text}\n${etCompanyAcc.text}")
        val shareData: SharedPreferences = getSharedPreferences("Shared Data", MODE_PRIVATE)
        val editor = shareData.edit()
        editor.putString("Company Name", "${etCompanyName.text}")
        editor.putString("Company Acc", "${etCompanyAcc.text}")
        editor.apply()
    }
}