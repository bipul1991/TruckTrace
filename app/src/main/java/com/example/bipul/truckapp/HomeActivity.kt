package com.example.bipul.truckapp

import android.app.ProgressDialog
import android.content.Context
import android.content.Intent
import android.location.Location
import android.location.LocationListener
import android.net.Uri
import android.os.AsyncTask
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.support.v7.app.AlertDialog
import android.util.Log
import android.view.View
import android.widget.EditText
import android.widget.Toast
import com.example.bipul.truckapp.R.id.actvTnspLay
import com.example.bipul.truckapp.R.id.crtTsptLay
import com.google.android.gms.common.ConnectionResult
import com.google.android.gms.common.api.GoogleApiClient
import kotlinx.android.synthetic.main.activity_home.*
import org.json.JSONException
import org.json.JSONObject
import java.io.*
import java.net.HttpURLConnection
import java.net.MalformedURLException
import java.net.URL

class HomeActivity : AppCompatActivity(), GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener, LocationListener {
    override fun onConnected(p0: Bundle?) {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun onConnectionSuspended(p0: Int) {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun onConnectionFailed(p0: ConnectionResult) {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun onLocationChanged(p0: Location?) {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun onStatusChanged(p0: String?, p1: Int, p2: Bundle?) {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun onProviderEnabled(p0: String?) {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun onProviderDisabled(p0: String?) {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    var username : String?=null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_home)
        crtTsptLay.setOnClickListener(View.OnClickListener { showNewNameDialog(); })

        actvTnspLay.setOnClickListener(View.OnClickListener {  val intent = Intent(this, MapsActivity::class.java)
            startActivity(intent); })

        val editor = getSharedPreferences("truck", Context.MODE_PRIVATE)
        username = editor.getString("username", "username")

    }


    fun showNewNameDialog() {
        val dialogBuilder = AlertDialog.Builder(this)
        val inflater = this.layoutInflater
        val dialogView = inflater.inflate(R.layout.dialog_create_transport, null)
        dialogBuilder.setView(dialogView)

        val editText = dialogView.findViewById<View>(R.id.userIdEdt) as EditText
        val editText = dialogView.findViewById<View>(R.id.userIdEdt) as EditText


        dialogBuilder.show()
    }

    inner class CreateTransport : AsyncTask<String, String, String>() {


        internal var pdLoading: ProgressDialog? = null
        override fun onPreExecute() {
            super.onPreExecute()

            //            pdLoading = new ProgressDialog(getActivity());
            //            pdLoading.setMessage("\tVerifying...");
            //            pdLoading.setCancelable(false);
            //            pdLoading.show();

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
               // user_id, transporter_id, current_lat, current_long, current_address, transport_desc, created_by

                        .appendQueryParameter("user_id", username)
                        .appendQueryParameter("transporter_id", "123")
                        .appendQueryParameter("current_lat", "843")
                        .appendQueryParameter("current_long", "gsdfgs54")
                        .appendQueryParameter("current_address", "barackpur")
                        .appendQueryParameter("transport_desc", "nbfdh")
                        .appendQueryParameter("created_by", "hjhds")


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

                    Toast.makeText(this@HomeActivity, msg, Toast.LENGTH_LONG).show()

                    val editor = getSharedPreferences("truck", Context.MODE_PRIVATE).edit()

                 /*   editor.putString("username", username)

                    editor.putString("device_id", device_id)*/
                    editor.commit()

                    val intent = Intent(this@HomeActivity, HomeActivity::class.java)
                    startActivity(intent)
                    finish()

                } else {

                    Toast.makeText(this@HomeActivity, msg, Toast.LENGTH_LONG).show()

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
    
    
}
