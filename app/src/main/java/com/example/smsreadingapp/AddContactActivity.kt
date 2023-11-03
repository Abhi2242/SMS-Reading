package com.example.smsreadingapp

import android.Manifest
import android.annotation.SuppressLint
import android.app.AlertDialog
import android.content.ActivityNotFoundException
import android.content.Intent
import android.content.SharedPreferences
import android.database.Cursor
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.ContactsContract
import android.provider.Settings
import android.util.Log
import android.widget.AdapterView
import android.widget.ListView
import android.widget.Toast
import androidx.appcompat.widget.SearchView
import androidx.cursoradapter.widget.SimpleCursorAdapter
import com.google.android.material.floatingactionbutton.ExtendedFloatingActionButton
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.karumi.dexter.Dexter
import com.karumi.dexter.MultiplePermissionsReport
import com.karumi.dexter.PermissionToken
import com.karumi.dexter.listener.PermissionRequest
import com.karumi.dexter.listener.multi.MultiplePermissionsListener

class AddContactActivity : AppCompatActivity() {
    private lateinit var addContact: ExtendedFloatingActionButton
    private lateinit var cursor: Cursor
    private lateinit var listView: ListView
    private lateinit var search: SearchView
    //    private lateinit var profileImage: ImageView
    private var mArrayList: ArrayList<MyContact>? = null
//    private lateinit var etSearch: SearchView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_contact)

        /* Here I'm loading previous values from ArrayList 'mArrayList' so that it should not
        assume array is empty and start new ArrayList */
        loadData()

        listView = findViewById(R.id.lv_contact)
        addContact = findViewById(R.id.add_contact)
        search = findViewById(R.id.search_bar)

        addContact.setOnClickListener {
            if (mArrayList?.isEmpty() == true){
                Toast.makeText(this, "Select at least one contact", Toast.LENGTH_SHORT).show()
            }else{
                Toast.makeText(this, "Contact Saved", Toast.LENGTH_SHORT).show()
                val intent = Intent(this, MainActivity::class.java)
                startActivity(intent)
            }
        }
        checkForPermissions()
    }


    private fun loadData() {
        val shareData: SharedPreferences = getSharedPreferences("Shared Data", MODE_PRIVATE)
        val gson = Gson()
        val json: String? = shareData.getString("Array List", null)
        val type = object : TypeToken<ArrayList<MyContact>>() {}.type
        mArrayList = gson.fromJson(json, type)
        Log.i("load contact", "$mArrayList")

        if (mArrayList?.size == null) {
            mArrayList = ArrayList()
        }
    }


    @SuppressLint("CommitPrefEdits")
    private fun saveData() {
        val shareData: SharedPreferences = getSharedPreferences("Shared Data", MODE_PRIVATE)
        val editor: SharedPreferences.Editor = shareData.edit()
        val gson = Gson()
        val json: String = gson.toJson(mArrayList?.distinct())
        editor.putString("Array List", json)
        editor.apply()
    }

    private fun checkForPermissions() {
        Dexter.withActivity(this).withPermissions(
            Manifest.permission.READ_CONTACTS,
            Manifest.permission.READ_SMS,
            Manifest.permission.SEND_SMS
        ).withListener(object : MultiplePermissionsListener {
            override fun onPermissionsChecked(report: MultiplePermissionsReport?) {
                if (report!!.areAllPermissionsGranted()) {
                    getContacts()
                }
            }

            override fun onPermissionRationaleShouldBeShown(
                permissions: MutableList<PermissionRequest>,
                token: PermissionToken
            ) {
                showRationalDialogForPermission()
            }
        }).onSameThread().check()
    }

    private fun showRationalDialogForPermission() {
        AlertDialog.Builder(this)
            .setMessage("You have turned off permissions required for this feature.")
            .setPositiveButton("GO TO SETTINGS") { _, _ ->
                try {
                    val intent = Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS)
                    val uri = Uri.fromParts("package", packageName, null)
                    intent.data = uri
                    startActivity(intent)
                } catch (e: ActivityNotFoundException) {
                    e.printStackTrace()
                }
            }
            .setNegativeButton("Cancel") { dialog, _ ->
                dialog.dismiss()
            }.show()
    }


    // Theis code show all contact
    @SuppressLint("Range")
    private fun getContacts() {
        cursor = contentResolver.query(
            ContactsContract.CommonDataKinds.Phone.CONTENT_URI,
            null,
            null,
            null,
            ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME
        )!!
        startManagingCursor(cursor)

        // data is a array of String type which is used to store Number ,Names and id.
        val data = arrayOf(
            ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME,
            ContactsContract.CommonDataKinds.Phone.NUMBER
        )

        val displayIn = intArrayOf(R.id.text1, R.id.text2)

        // creation of adapter using SimpleCursorAdapter class
//        val adapter = SimpleCursorAdapter(this, R.layout.list_item, cursor, data, displayIn)
        val adapter = SimpleCursorAdapter(this, R.layout.list_item, cursor, data, displayIn)

        // Method to set created adapter
        listView.adapter = adapter
        listView.choiceMode = ListView.CHOICE_MODE_MULTIPLE

//        search.isActivated = true
//        search.queryHint = "Type your keyword here"
//        search.onActionViewExpanded()
//        search.isIconified = false
//        search.clearFocus()
//
//        search.setOnSuggestionListener(object: SearchView.OnSuggestionListener {
//            override fun onSuggestionSelect(position: Int): Boolean {
//                return false
//            }
//
//            override fun onSuggestionClick(position: Int): Boolean {
//                cursor = search.suggestionsAdapter.getItem(position) as Cursor
//                val selection = cursor.getString(cursor.getColumnIndex(SearchManager.SUGGEST_COLUMN_TEXT_1))
//                search.setQuery(selection, false)
//
//                // Do something with selection
//                return true
//            }
//        })

//        etSearch = findViewById(R.id.et_search)
//
//        etSearch.setOnQueryTextListener(object : OnQueryTextListener{
//            override fun onQueryTextSubmit(query: String?): Boolean {
//                Log.i("Query", "$query")
//                adapter.filter.filter(query)
//                return false
//            }
//            override fun onQueryTextChange(newText: String?): Boolean {
//                Log.i("Query1", "$newText")
//                adapter.filter.filter(newText)
//                return false
//            }
//        })

        // Here it will save clicked item in mArrayList
        listView.onItemClickListener = AdapterView.OnItemClickListener { _, _, _, _ ->
//            val position: Int = cursor.position
//            profileImage = findViewById(R.id.iv_profile)
            val name: String =
                cursor.getString(cursor.getColumnIndexOrThrow(ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME))
            val contact: String =
                cursor.getString(cursor.getColumnIndexOrThrow(ContactsContract.CommonDataKinds.Phone.NUMBER))
            val contact1 = contact.replace("\\s".toRegex(), "")
            val contact2 = contact1.replace("-", "")
            Log.i("mLog", "Value is: {$name, $contact2},")

            if (mArrayList?.contains(MyContact(name, contact2)) == true) {
                Toast.makeText(this@AddContactActivity, "$name Unselected", Toast.LENGTH_SHORT)
                    .show()
//                profileImage.setImageResource(R.drawable.profile_image)
//                listView[position].setBackgroundColor(getColor(R.color.unselectedContact)) // Check if this line is giving errors
                mArrayList?.size?.let { mArrayList?.remove(MyContact(name, contact2)) }
                saveData()
            } else {
                Toast.makeText(this@AddContactActivity, "$name selected", Toast.LENGTH_SHORT).show()
//                profileImage.setImageResource(R.drawable.ic_baseline_person_24)
//                listView[position].setBackgroundColor(getColor(R.color.selectedContact)) // Check if this line is giving errors
                mArrayList?.size?.let { mArrayList?.add(it, MyContact(name, contact2)) }
                saveData()
            }
        }
    }
}