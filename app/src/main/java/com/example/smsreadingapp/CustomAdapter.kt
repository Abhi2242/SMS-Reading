package com.example.smsreadingapp

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class CustomAdapter(contactArrayList: ArrayList<MyContact>, context: Context) :
    RecyclerView.Adapter<CustomAdapter.ViewHolder>() {

    // creating a variable for array list and context.
    private val selectedContactArrayList: ArrayList<MyContact>
    private val context: Context
    private lateinit var rvListener: OnItemClickListener

    interface OnItemClickListener{
        fun onItemLongClick(position: Int)
    }

    fun setOnItemClickListener(listener: OnItemClickListener){
        rvListener = listener
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        // below line is to inflate our layout.
        val view: View =
            LayoutInflater.from(parent.context).inflate(R.layout.contact_rv_item, parent, false)
        return ViewHolder(view, rvListener)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        // setting data to our views of recycler view.
        val len: MyContact = selectedContactArrayList[position]
        holder.name.text =len.getName()
        holder.contact.text = len.getContact()
    }

    override fun getItemCount(): Int {
        // returning the size of array list.
        return selectedContactArrayList.size
    }

    inner class ViewHolder(itemView: View, listener: OnItemClickListener) : RecyclerView.ViewHolder(itemView) {
        // creating variables for our views.
        val name: TextView
        val contact: TextView

        init {
            // initializing our views with their ids.
            name = itemView.findViewById(R.id.tv_contact_name)
            contact = itemView.findViewById(R.id.tv_contact_number)

            itemView.setOnLongClickListener {
                listener.onItemLongClick(adapterPosition)
                return@setOnLongClickListener true
            }
        }
    }

    // creating a constructor for our variables.
    init {
        this.selectedContactArrayList = contactArrayList
        this.context = context
    }
}