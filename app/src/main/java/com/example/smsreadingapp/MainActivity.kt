package com.example.smsreadingapp

import android.Manifest
import android.annotation.SuppressLint
import android.content.Intent
import android.content.SharedPreferences
import android.content.pm.PackageManager
import android.graphics.Color
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.floatingactionbutton.ExtendedFloatingActionButton
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken

class MainActivity : AppCompatActivity() {
    private lateinit var addContact: ExtendedFloatingActionButton
    private lateinit var textMessage1: TextView
    private lateinit var textMessage: TextView
    private lateinit var contactRV: RecyclerView
    private lateinit var appInfo: ImageView
    private lateinit var changeProfile: ImageView
    private lateinit var adapter: CustomAdapter
    private var mainArrayList: ArrayList<MyContact>? = null
    data class DuplicateContactList(val dubContact: String)
    private var duplicateArrayList: ArrayList<DuplicateContactList>? = null
    private lateinit var prefCompanyName: String
    private lateinit var prefCompanyAcc: String
    private lateinit var displayCompName: TextView
    private lateinit var displayCompAcc: TextView


    @SuppressLint("SetTextI18n", "ResourceAsColor")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        addContact = findViewById(R.id.add_new_contact)
        contactRV = findViewById(R.id.rv_my_contact_view)
        appInfo = findViewById(R.id.iv_setting)
        changeProfile = findViewById(R.id.iv_profile_edit)
        textMessage = findViewById(R.id.text_message)
        textMessage1 = findViewById(R.id.text_message1)
        displayCompName = findViewById(R.id.tv_cn1)
        displayCompAcc = findViewById(R.id.tv_cAcc1)

        if (ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.RECEIVE_SMS
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            ActivityCompat.requestPermissions(
                this,
                arrayOf(
                    Manifest.permission.RECEIVE_SMS,
                    Manifest.permission.SEND_SMS,
                    Manifest.permission.READ_SMS,
                    Manifest.permission.READ_CONTACTS
                ),
                111
            )
        } else {
            // loadData() should be placed on top of all other function so that we can retrieve data sand store it in mainArrayList to use later
            loadData()
            if (prefCompanyName.isNotEmpty()){ //  || prefCompanyAcc != ""  There is a problem here check for pref value

                Toast.makeText(this@MainActivity, "Company Name: $prefCompanyName, Company A/C No.: $prefCompanyAcc", Toast.LENGTH_LONG).show()
                displayCompName.text = prefCompanyName
                displayCompAcc.text = prefCompanyAcc
            }
            else{
                Toast.makeText(this@MainActivity, "Add Company details", Toast.LENGTH_LONG).show()
//                startActivity(Intent(this, AddProfileDetails::class.java))
            }

            if (mainArrayList?.isNotEmpty() == true) {
                textMessage.text = "SMS service is activated"
                textMessage1.text = ""
                textMessage.textSize = 25F
                textMessage.setTextColor(Color.parseColor("#37EA37"))
                startService(Intent(this, MyService::class.java))
            }
            else{
                textMessage1.text = "Add Contact to start SMS service"
            }

            buildRecyclerView()
        }

        addContact.setOnClickListener {
            startActivity(Intent(this, AddContactActivity::class.java))
        }

        appInfo.setOnClickListener {
            startActivity(Intent(this, DisplayAppInfo::class.java))
        }

        changeProfile.setOnClickListener {
            startActivity(Intent(this, AddProfileDetails::class.java))
        }

    }


    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == 111 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            if (mainArrayList?.isEmpty() == true) {
                startActivity(Intent(this, AddContactActivity::class.java))
            }
            loadData()
            buildRecyclerView()
        }
    }


    private fun buildRecyclerView() {
        adapter = CustomAdapter(mainArrayList!!, this)
        val manager = LinearLayoutManager(this)

        contactRV.setHasFixedSize(true)
        contactRV.layoutManager = manager
        contactRV.adapter = adapter

        adapter.setOnItemClickListener(object : CustomAdapter.OnItemClickListener {
            override fun onItemLongClick(position: Int) {
                val data = mainArrayList?.get(position)
                Toast.makeText(this@MainActivity, "Contact ${data?.mName} was removed", Toast.LENGTH_SHORT)
                    .show()
                mainArrayList?.removeAt(position)
                duplicateArrayList?.removeAt(position)
                adapter.notifyItemRemoved(position)
                if (mainArrayList?.isNotEmpty() == true) {
                    saveData()
                } else {
                    saveData()
                    startActivity(Intent(this@MainActivity, AddContactActivity::class.java))
                }
            }
        })
    }

    private fun loadData() {
        val shareData: SharedPreferences = getSharedPreferences("Shared Data", MODE_PRIVATE)
        val gson = Gson()
        val json: String? = shareData.getString("Array List", null)
        val type = object : TypeToken<ArrayList<MyContact>>() {}.type
        val check: ArrayList<MyContact>? = gson.fromJson(json, type)
        check?.distinct()
        mainArrayList = check
        Log.i("MyLog", "Main Array: $mainArrayList")
        if (mainArrayList?.size == null) {
            mainArrayList = ArrayList()
        }
        val prefCompanyName1: String = shareData.getString("Company Name", "My Test").toString()
        prefCompanyName = prefCompanyName1
        val prefCompanyAcc1: String = shareData.getString("Company Acc", null).toString()
        prefCompanyAcc = prefCompanyAcc1
    }

    private fun saveData() {
        Log.i("Save", "Data")
        val shareData: SharedPreferences = getSharedPreferences("Shared Data", MODE_PRIVATE)
        val editor: SharedPreferences.Editor = shareData.edit()
        val gson = Gson()
        val json: String? = gson.toJson(mainArrayList)
        editor.putString("Array List", json)
        val prefCompanyName1: String? = gson.toJson(prefCompanyName)
        val prefCompanyAcc1: String? = gson.toJson(prefCompanyAcc)
        editor.putString("Company Name", prefCompanyName1)
        editor.putString("Company Acc", prefCompanyAcc1)
        editor.apply()
    }
}