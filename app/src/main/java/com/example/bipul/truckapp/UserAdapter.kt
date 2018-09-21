package com.example.bipul.truckapp

import android.content.Context
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import com.example.bipul.truckapp.model.UserModel


class UserAdapter(val items : ArrayList<UserModel>, val context: Context) : RecyclerView.Adapter<ViewHolder>() {

    // Gets the number of animals in the list
    override fun getItemCount(): Int {
        return items.size
    }

    // Inflates the item views
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(LayoutInflater.from(context).inflate(R.layout.itme_transport, parent, false))
    }

    // Binds each animal in the ArrayList to a view
    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder?.userNameTxt?.text = items.get(position).userName
        holder?.userIdTxt?.text= items.get(position).userId
        holder?.userAdrTxt.text= items.get(position).userAdrs
    }
}

class ViewHolder (view: View) : RecyclerView.ViewHolder(view) {
    // Holds the TextView that will add each animal to
      val userNameTxt = view.findViewById<TextView>(R.id.userNameTxt)
      val userIdTxt = view.findViewById<TextView>(R.id.userIdTxt)
      val userAdrTxt = view.findViewById<TextView>(R.id.userAdrTxt)

}