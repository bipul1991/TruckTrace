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
import android.support.v4.app.ActivityCompat
import android.support.v4.content.ContextCompat
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
    var fcm_id = "hjhkjgjf"
    var type = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_log_in)

        btn_login.setOnClickListener{
            attempLogin()
        }

     //   type="trucker"

        val editor = getSharedPreferences("truck", Context.MODE_PRIVATE)
      //  username = editor.getString("username", "username")
        if(!editor.getString("username", "username").equals("username"))
        {
            val intent = Intent(this@LogInActivity, HomeActivity::class.java)
            startActivity(intent)
            finish()
        }
    }

    private fun attempLogin()
    {
     /*  val intent = Intent(this, HomeActivity::class.java)
        startActivity(intent);*/

        username=login_user_edtxt.text.toString()
        password=login_pass_edtxt.text.toString()

      //  requsetPerms();

        checkPermission()


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
/*

                     pdLoading = ProgressDialog(this@LogInActivity);
                       pdLoading?.setMessage("\tVerifying...");
                     pdLoading?.setCancelable(false);
                    pdLoading?.show();
*/

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
                       // .appendQueryParameter("device_id", device_id)
                        .appendQueryParameter("fcm_token", fcm_id)


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

                Log.d("jsonLogIn2",""+parentobjt)
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
                Log.d("jsonLogIn",""+parentobjt)

                val status = parentobjt.getString("type")
                // String result = parentobjt.getString("result");



                if (status.equals("success")) {


                    val editor = getSharedPreferences("truck", Context.MODE_PRIVATE).edit()

                    editor.putString("username", parentobjt.getString("username"))
                    editor.putString("userId", parentobjt.getString("id"))

                    editor.putString("device_id", device_id)
                    editor.putString("type", parentobjt.getString("usertype"))
                    editor.commit()

                    val intent = Intent(this@LogInActivity, HomeActivity::class.java)
                    startActivity(intent)
                    finish()

                } else {

                    Toast.makeText(this@LogInActivity, "error", Toast.LENGTH_LONG).show()

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

        val permissions = arrayOf( Manifest.permission.RECEIVE_SMS, Manifest.permission.READ_PHONE_STATE,
                Manifest.permission.ACCESS_NETWORK_STATE,Manifest.permission.ACCESS_FINE_LOCATION,
                Manifest.permission.ACCESS_COARSE_LOCATION)


        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {

            requestPermissions(permissions, PERMS_CODE)

        }

    }


    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<String>, grantResults: IntArray) {

        var allowed = true


                for (res in grantResults) {

                   // allowed = allowed && res == PackageManager.PERMISSION_GRANTED

                    if(res == PackageManager.PERMISSION_GRANTED)

                        allowed=true;

                    else
                        allowed=false
                    break
                }




        if (allowed) {


            // new LoginApiCall().execute("http://schoolradius.net/sms_app/api/login");

            if (NetworkUtlities.isConnected(applicationContext)) {

     //           LoginApiCall().execute(BaseUrl.baseUrl + "login")
                LoginApiCall().execute("http://triptoe.pearnode.com/api_mobile/api/validate")

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

    fun checkPermission()
    {
        if (ContextCompat.checkSelfPermission(this,
                Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {
            // Should we show an explanation?
            if (ActivityCompat.shouldShowRequestPermissionRationale(this,
                    Manifest.permission.ACCESS_FINE_LOCATION)) {
                // Show an explanation to the user *asynchronously* -- don't block
                // this thread waiting for the user's response! After the user
                // sees the explanation, try again to request the permission.
            }

            else {
                // No explanation needed, we can request the permission.
                ActivityCompat.requestPermissions(this,
                        arrayOf(Manifest.permission.ACCESS_FINE_LOCATION),
                        123)
                // MY_PERMISSIONS_REQUEST_READ_CONTACTS is an
                // app-defined int constant. The callback method gets the
                // result of the request.

            }
        } else {
            // Permission has already been granted
            LoginApiCall().execute("http://triptoe.pearnode.com/api_mobile/api/validate")
        }
    }

}
