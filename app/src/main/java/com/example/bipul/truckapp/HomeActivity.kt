package com.example.bipul.truckapp

import android.Manifest
import android.app.ProgressDialog
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.location.Location
import android.location.LocationListener
import android.location.LocationManager
import android.net.Uri
import android.os.AsyncTask
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.support.multidex.MultiDex
import android.support.v4.app.ActivityCompat
import android.support.v4.content.ContextCompat
import android.support.v7.app.AlertDialog
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import com.example.bipul.truckapp.R.id.actvTnspLay
import com.example.bipul.truckapp.R.id.crtTsptLay
import com.google.android.gms.common.ConnectionResult
import com.google.android.gms.common.api.GoogleApiClient
import com.google.android.gms.location.FusedLocationProviderClient
import kotlinx.android.synthetic.main.activity_home.*
import org.json.JSONException
import org.json.JSONObject
import java.io.*
import java.net.HttpURLConnection
import java.net.MalformedURLException
import java.net.URL
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices
import com.google.android.gms.tasks.OnSuccessListener


class HomeActivity : AppCompatActivity(),  GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener, com.google.android.gms.location.LocationListener {


    var client: GoogleApiClient?=null
    var locationRequest: LocationRequest?=null

    var latitude: Double?=12.45
    var longitude: Double?=34.2

    val TAG = "MainActivity"
    private lateinit var mGoogleApiClient: GoogleApiClient
    private var mLocationManager: LocationManager? = null
    lateinit var mLocation: Location
    private var mLocationRequest: LocationRequest? = null
    private val listener: com.google.android.gms.location.LocationListener? = null
    private val UPDATE_INTERVAL = (2 * 1000).toLong()  /* 10 secs */
    private val FASTEST_INTERVAL: Long = 2000 /* 2 sec */

    lateinit var locationManager: LocationManager


    override fun onStart() {
        super.onStart();
        if (mGoogleApiClient != null) {
            mGoogleApiClient.connect();
        }
    }

    override fun onStop() {
        super.onStop();
        if (mGoogleApiClient.isConnected()) {
            mGoogleApiClient.disconnect();
        }
    }

    override fun onConnected(p0: Bundle?) {
       //To change body of created functions use File | Settings | File Templates.

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {

            return;
        }


        startLocationUpdates();

        var fusedLocationProviderClient :
                FusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this);
        fusedLocationProviderClient .getLastLocation()
                .addOnSuccessListener(this, OnSuccessListener<Location> { location ->
                    // Got last known location. In some rare situations this can be null.
                    if (location != null) {
                        // Logic to handle location object
                        mLocation = location;
                        /*txt_latitude.setText("" + mLocation.latitude)
                        txt_longitude.setText("" + mLocation.longitude)*/
                    }
                })
    }

    override fun onConnectionSuspended(p0: Int) {
        //To change body of created functions use File | Settings | File Templates.

        Log.i(TAG, "Connection Suspended");
        mGoogleApiClient.connect();
    }

    override fun onConnectionFailed(connectionResult: ConnectionResult) {
    //To change body of created functions use File | Settings | File Templates.
        Log.i(TAG, "Connection failed. Error: " + connectionResult.getErrorCode());
        Log.i(TAG, "Connection failed. Error2: " + connectionResult.errorMessage);
    }

    override fun onLocationChanged(location: Location?) {
       //To change body of created functions use File | Settings | File Templates.

        var msg = "Updated Location: Latitude " + location?.longitude.toString() + location?.longitude;
       // txt_latitude.setText(""+location.latitude);
     //   txt_longitude.setText(""+location.longitude);

        latitude = location?.latitude
        longitude=location?.longitude
        Toast.makeText(this, msg, Toast.LENGTH_SHORT).show();
    }


   /* fun  buildGoogleApiClient()
    {
        client = GoogleApiClient.Builder(this)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(LocationServices.API)
                .build();
        client.connect();
    }*/

    var username : String?=null
    var userId : String?=null
    var userType : String?=null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_home)

        MultiDex.install(this)

        mGoogleApiClient = GoogleApiClient.Builder(this)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(LocationServices.API)
                .build()

        mLocationManager = this.getSystemService(Context.LOCATION_SERVICE) as LocationManager


        crtTsptLay.setOnClickListener(View.OnClickListener { if(userType.equals("3",true)) showNewNameDialog();
        else intent = Intent(this, Pending::class.java)
            startActivity(intent);  })

        canceledLay.setOnClickListener(View.OnClickListener {  val intent = Intent(this, Cancelde::class.java)
            startActivity(intent); })

        completeLay.setOnClickListener(View.OnClickListener {  val intent = Intent(this, CompletedActivity::class.java)
            startActivity(intent); })


        actvTnspLay.setOnClickListener(View.OnClickListener {  val intent = Intent(this, Active::class.java)
            startActivity(intent); })

        pendingTrnsLay.setOnClickListener(View.OnClickListener {  val intent = Intent(this, Pending::class.java)
            startActivity(intent); })

        val editor = getSharedPreferences("truck", Context.MODE_PRIVATE)
        username = editor.getString("username", "username")
        userType = editor.getString("type", "username")
        userId = editor.getString("userId", "username")

        if(!userType.equals("3",true))
        {
            // This is transporter
            pendingTrnsLay.visibility=View.GONE
            crtTxt.text="Pending Transport"
        }

    }



    private fun isLocationEnabled(): Boolean {
        locationManager = getSystemService(Context.LOCATION_SERVICE) as LocationManager
        return locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER) || locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER)
    }

    protected fun startLocationUpdates() {

        // Create the location request
        mLocationRequest = LocationRequest.create()
                .setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY)
                .setInterval(UPDATE_INTERVAL)
                .setFastestInterval(FASTEST_INTERVAL);
        // Request location updates
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return;
        }
        LocationServices.FusedLocationApi.requestLocationUpdates(mGoogleApiClient,
                mLocationRequest, this);
    }


    fun showNewNameDialog() {
        val dialogBuilder = AlertDialog.Builder(this)
        val inflater = this.layoutInflater
        val dialogView = inflater.inflate(R.layout.dialog_create_transport, null)
        dialogBuilder.setView(dialogView)

        val userIdEdt = dialogView.findViewById<View>(R.id.userIdEdt) as EditText
        val descEdt = dialogView.findViewById<View>(R.id.descEdt) as EditText
        val submitBtn = dialogView.findViewById<View>(R.id.submitBtn) as Button

        submitBtn.setOnClickListener(View.OnClickListener {

            var IdSend:String?=userIdEdt.text.toString();
            var desc:String?=userIdEdt.text.toString();
            CreateTransport().execute("http://triptoe.pearnode.com/api_mobile/api/create_new_transport",
                    IdSend,desc)


            })


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

                        .appendQueryParameter("user_id", urls[1] )
                        .appendQueryParameter("transporter_id", userId)
                        .appendQueryParameter("current_lat",""+ latitude)
                        .appendQueryParameter("current_long",""+ longitude)
                        .appendQueryParameter("current_address", "barackpur")
                        .appendQueryParameter("transport_desc", urls[2])
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
            catch (ex: Exception)
            {
                ex.printStackTrace()
                Log.d("exExp",ex.message);
            }


            return null
        }

        override fun onPostExecute(response: String) {
            super.onPostExecute(response)

            try {

                val parentobjt = JSONObject(response)

            Log.d("jsonStatus",""+parentobjt)
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

