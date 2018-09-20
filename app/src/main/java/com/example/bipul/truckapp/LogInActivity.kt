package com.example.bipul.truckapp

import android.content.Intent
import android.os.AsyncTask
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import kotlinx.android.synthetic.main.activity_log_in.*

class LogInActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_log_in)

        btn_login.setOnClickListener{
            attempLogin()
        }
    }

    private fun attempLogin()
    {
       val intent = Intent(this, HomeActivity::class.java)
        startActivity(intent);
    }

    private  class UserLoginAsync : AsyncTask<String, String, String>()
    {
        override fun onPreExecute() {
            super.onPreExecute()
        }
        override fun doInBackground(vararg p0: String?): String {
            TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
        }

        override fun onPostExecute(result: String?) {
            super.onPostExecute(result)
        }

    }
}
