package com.example.bipul.truckapp

import android.content.Intent
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.support.v7.app.AlertDialog
import android.view.View
import android.widget.EditText
import kotlinx.android.synthetic.main.activity_home.*

class HomeActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_home)
        crtTsptLay.setOnClickListener(View.OnClickListener { showNewNameDialog(); })

        actvTnspLay.setOnClickListener(View.OnClickListener {  val intent = Intent(this, MapsActivity::class.java)
            startActivity(intent); })
    }


    fun showNewNameDialog() {
        val dialogBuilder = AlertDialog.Builder(this)
        val inflater = this.layoutInflater
        val dialogView = inflater.inflate(R.layout.dialog_create_transport, null)
        dialogBuilder.setView(dialogView)

        val editText = dialogView.findViewById<View>(R.id.userIdEdt) as EditText

      /*  dialogBuilder.setTitle("Custom dialog")
        dialogBuilder.setMessage("Enter Name Below")
        dialogBuilder.setPositiveButton("Save", { dialog, whichButton ->
            //do something with edt.getText().toString();

            // Add Name in list
       //     nameList.add(editText.text.toString())
            // Handler code here.
            val intent = Intent(this, NewKitListActivity::class.java)
            startActivity(intent);

        })
        dialogBuilder.setNegativeButton("Cancel", { dialog, whichButton ->
            //pass
        })*/

      /*  val b = dialogBuilder.create()
        b.show()*/
        dialogBuilder.show()
    }
}
