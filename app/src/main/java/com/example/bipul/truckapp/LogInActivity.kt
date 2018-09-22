package com.example.bipul.truckapp

import android.Manifest
import android.app.PendingIntent.getActivity
import android.app.ProgressDialog
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.AsyncTask
import android.os.Build
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import com.example.bipul.truckapp.Utility.NetworkUtlities
import kotlinx.android.synthetic.main.activity_log_in.*
import org.json.JSONException
import org.json.JSONObject
import java.io.*
import java.net.HttpURLConnection
import java.net.MalformedURLException
import java.net.URL

class LogInActivity : AppCompatActivity() {

    val PERMS_CODE = 123

    var username=""
    var device_id = ""
    var password = ""
    var fcm_id = ""
    var type = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_log_in)

        btn_login.setOnClickListener{
            attempLogin()
        }

        type="trucker"
    }

    private fun attempLogin()
    {
     /*  val intent = Intent(this, HomeActivity::class.java)
        startActivity(intent);*/

        username=login_user_edtxt.text.toString()
        password=login_pass_edtxt.text.toString()

        LoginApiCall().execute("http://triptoe.pearnode.com/api_mobile/api/validate")
    }

    /*private  class UserLoginAsync : AsyncTask<String, String, String>()
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
*/
    inner class LoginApiCall : AsyncTask<String, String, String>() {


        internal var pdLoading: ProgressDialog? = null
        override fun onPreExecute() {
            super.onPreExecute()

                     pdLoading = ProgressDialog(this@LogInActivity);
                       pdLoading?.setMessage("\tVerifying...");
                     pdLoading?.setCancelable(false);
                    pdLoading?.show();

        }

        override fun doInBackground(vararg urls: String): String? {

            var connection: HttpURLConnection? = null
            var reader: BufferedReader? = null

            try {
                val url = URL(urls[0])
                connection = url.openConnection() as HttpURLConnection

                connection.readTimeout = 10000
                connection.connectTimeout = 15000
                connection.requestMethod = "POST"
                connection.doInput = true
                connection.doOutput = true

                val builder = Uri.Builder()

                        .appendQueryParameter("username", username)
                        .appendQueryParameter("password", password)
                        .appendQueryParameter("device_id", device_id)
                        .appendQueryParameter("fcm_id", fcm_id)


                val query = builder.build().query
                val os = connection.outputStream
                val writer = BufferedWriter(
                        OutputStreamWriter(os))
                writer.write(query)
                writer.flush()
                writer.close()
                os.close()
                connection.connect()

                val stream = connection.inputStream
                reader = BufferedReader(InputStreamReader(stream))

                var line : String? = ""
                val buffer = StringBuffer()

                /* while ((line = reader.readLine()) != null) {
                     buffer.append(line)
                 }*/

                do {
                    line = reader.readLine()

                    if (line == null)

                        break

                    else
                        buffer.append(line)
                    //  println(line)

                }
                // while (true)
                while (line == null)

                val finaljson = buffer.toString()

                val parentobjt = JSONObject(finaljson)

                //               ShippingAddressModel shippingAddressModel=new ShippingAddressModel();
                //
                //               shippingAddressModel.setMsg(parentobjt.getString("msg"));
                //               shippingAddressModel.setStatus(parentobjt.getBoolean("status"));

                return finaljson

            } catch (e: MalformedURLException) {
                e.printStackTrace()
            } catch (e: IOException) {
                e.printStackTrace()
            } catch (e: JSONException) {
                e.printStackTrace()
            }


            return null
        }

        override fun onPostExecute(response: String) {
            super.onPostExecute(response)

            try {

                val parentobjt = JSONObject(response)

                val status = parentobjt.getString("status")
                // String result = parentobjt.getString("result");
                val msg = parentobjt.getString("msg")

                if (status == "true") {

                    Toast.makeText(this@LogInActivity, msg, Toast.LENGTH_LONG).show()

                    val editor = getSharedPreferences("truck", Context.MODE_PRIVATE).edit()

                    editor.putString("username", username)

                    editor.putString("device_id", device_id)
                    editor.putString("type", type)
                    editor.commit()

                    val intent = Intent(this@LogInActivity, LogInActivity::class.java)
                    startActivity(intent)
                    finish()

                } else {

                    Toast.makeText(this@LogInActivity, msg, Toast.LENGTH_LONG).show()

                }

                //Toast.makeText(Login.this,status+result+msg,Toast.LENGTH_LONG).show();

            } catch (e: JSONException) {
                e.printStackTrace()
            } catch (ex: Exception) {
                ex.printStackTrace()
                Log.d("expst", " Nullpointer exception")
            }

        }
    }

    fun requsetPerms() {

        val permissions = arrayOf(Manifest.permission.READ_SMS, Manifest.permission.RECEIVE_SMS, Manifest.permission.READ_PHONE_STATE, Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.ACCESS_NETWORK_STATE)

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {

            requestPermissions(permissions, PERMS_CODE)

        }

    }


    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<String>, grantResults: IntArray) {

        var allowed = true

        when (requestCode) {

            PERMS_CODE ->

                for (res in grantResults) {

                    allowed = allowed && res == PackageManager.PERMISSION_GRANTED
                }

            else ->

                allowed = false
        }


        if (allowed) {


            // new LoginApiCall().execute("http://schoolradius.net/sms_app/api/login");

            if (NetworkUtlities.isConnected(applicationContext)) {

     //           LoginApiCall().execute(BaseUrl.baseUrl + "login")

            } else {
                Toast.makeText(applicationContext, "Please Check Your Internet Connection.", Toast.LENGTH_LONG).show()

            }


        } else {

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {

                if ( shouldShowRequestPermissionRationale(Manifest.permission.ACCESS_FINE_LOCATION)
                        || shouldShowRequestPermissionRationale(Manifest.permission.ACCESS_COARSE_LOCATION)
                        || shouldShowRequestPermissionRationale(Manifest.permission.ACCESS_NETWORK_STATE)) {

                    Toast.makeText(this, "Please Give Access", Toast.LENGTH_SHORT).show()

                    requsetPerms()

                }
            }

        }
    }

}
