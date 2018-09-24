package com.example.bipul.truckapp

import android.Manifest
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Toast

import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import kotlinx.android.synthetic.main.activity_maps.*
import android.R.attr.data
import android.annotation.SuppressLint
import android.app.ProgressDialog
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Color
import android.location.Location
import android.location.LocationManager
import android.net.Uri
import android.os.AsyncTask
import android.support.multidex.MultiDex
import android.support.v4.app.ActivityCompat
import android.support.v7.app.AlertDialog
import android.util.Log
import android.view.LayoutInflater
import com.example.bipul.truckapp.model.GoogleMapDTO
import com.google.android.gms.common.ConnectionResult
import com.google.android.gms.common.api.GoogleApiClient
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationServices
import com.google.android.gms.maps.model.PolylineOptions
import com.google.android.gms.tasks.OnSuccessListener
import com.google.gson.Gson
import com.squareup.okhttp.OkHttpClient
import com.squareup.okhttp.Request
import org.json.JSONException
import org.json.JSONObject
import java.io.*
import java.net.HttpURLConnection
import java.net.MalformedURLException
import java.net.URL
import kotlinx.android.synthetic.main.dialog_submit_transport.view.*


class MapsActivity : AppCompatActivity(), OnMapReadyCallback,GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener, com.google.android.gms.location.LocationListener {

    var type = ""
    var tranId=""
    var intentType=""




    private lateinit var mMap: GoogleMap

    var latitude: Double?=12.45
    var longitude: Double?=34.2

    var userLatLng : LatLng?=null
    var trackerLatLng: LatLng?=null

    val TAG = "tagLog"
    private lateinit var mGoogleApiClient: GoogleApiClient
    private var mLocationManager: LocationManager? = null
    lateinit var mLocation: Location
    private var mLocationRequest: LocationRequest? = null
    private val listener: com.google.android.gms.location.LocationListener? = null
    private val UPDATE_INTERVAL = (2 * 1000).toLong()  /* 10 secs */
    private val FASTEST_INTERVAL: Long = 2000 /* 2 sec */

    lateinit var locationManager: LocationManager




    override fun onConnected(p0: Bundle?) {

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
        longitude = location?.longitude
        Toast.makeText(this, msg, Toast.LENGTH_SHORT).show();

        if (type.equals("3", true))
        {
                trackerLatLng = LatLng(latitude!!, longitude!!)
        }

        else
        {
          //  userLatLng = LatLng(latitude!!, longitude!!)
        }
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
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_maps)
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.

        val mapFragment = supportFragmentManager
                .findFragmentById(R.id.map) as SupportMapFragment
        mapFragment.getMapAsync( this@MapsActivity)

     //   MultiDex.install(this)

        mGoogleApiClient = GoogleApiClient.Builder( this@MapsActivity)
                .addConnectionCallbacks( this@MapsActivity)
                .addOnConnectionFailedListener( this@MapsActivity)
                .addApi(LocationServices.API)
                .build()

         mLocationManager = this@MapsActivity.getSystemService(Context.LOCATION_SERVICE) as LocationManager



        cmpltLay.setOnClickListener(View.OnClickListener
        { /*UserConfirmedAsync().
                execute("http://triptoe.pearnode.com/api_mobile/api/submitStatus","id, user_feedback")*/
            createDialog()})


        emgncyLay.setOnClickListener(View.OnClickListener { Toast.makeText(this, "We are working on it" as String, Toast.LENGTH_LONG).show() })

        val editor = getSharedPreferences("truck", Context.MODE_PRIVATE)
        type = editor.getString("type","type")

        val intentGet = getIntent();
        tranId=intentGet.getStringExtra("trsnId")
      //  intentType=intentGet.getStringExtra("type")



        if(type.equals("3",true))
        {
            // Trucker
            cmpltLay.visibility=View.GONE

        }
        else
        {
            emgncyLay.visibility=View.GONE
        }

    }

    fun createDialog()
    {
        val mDialogView = LayoutInflater.from(this).inflate(R.layout.dialog_submit_transport, null)
        //AlertDialogBuilder
        val mBuilder = AlertDialog.Builder(this)
                .setView(mDialogView)
                .setTitle("Submit Form")
        //show dialog
        val  mAlertDialog = mBuilder.show()
        //login button click of custom layout
        mDialogView.submitBtn.setOnClickListener {
            //dismiss dialog
            mAlertDialog.dismiss()
            //get text from EditTexts of custom layout
            val msg = mDialogView.descEdt.text.toString()

            //set the input text in TextView
         //   mainInfoTv.setText("Name:"+ name +"\nEmail: "+ email +"\nPassword: "+ password)
        }
        //cancel button click of custom layout
        mDialogView.canCeltBtn.setOnClickListener {
            //dismiss dialog
            mAlertDialog.dismiss()
        }
    }

    inner class TransportDetailAsync : AsyncTask<String, String, String>() {


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

                        .appendQueryParameter("id", urls[1])

                // .appendQueryParameter("device_id", device_id)


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
                Log.d("jsonDetails",""+parentobjt)


                //Toast.makeText(Login.this,status+result+msg,Toast.LENGTH_LONG).show();


                // Trucker
                var innerObj2  =parentobjt.getString("end_location")   //User
                var jObjUser = JSONObject(innerObj2)

                userLatLng = LatLng(jObjUser.getString("location_lat")
                        as Double,jObjUser.getString("location_long") as Double)


                if (!type.equals("3", true))
                {
                    var innerObj  =parentobjt.getString("start_location")
                    var jObjTrucker = JSONObject(innerObj)
                    trackerLatLng = LatLng(jObjTrucker.getString("location_lat")      //Trucker
                            as Double,jObjTrucker.getString("location_long") as Double)
                }

                Log.d("latLngChk",""+trackerLatLng+"  "+userLatLng)

                GetDirection( getDirectionsUrl(userLatLng,trackerLatLng)).execute()



            }
            catch (e: JSONException) {
                e.printStackTrace()
            } catch (ex: Exception) {
                ex.printStackTrace()
                Log.d("expst", " Nullpointer exception")
            }

        }
    }

    inner class UserConfirmedAsync : AsyncTask<String, String, String>() {


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

                        .appendQueryParameter("id", urls[1])

                // .appendQueryParameter("device_id", device_id)


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
                Log.d("jsonDetails",""+parentobjt)


                //Toast.makeText(Login.this,status+result+msg,Toast.LENGTH_LONG).show();


                // Trucker
                var innerObj2  =parentobjt.getString("end_location")   //User
                var jObjUser = JSONObject(innerObj2)

                userLatLng = LatLng(jObjUser.getString("location_lat")
                        as Double,jObjUser.getString("location_long") as Double)


                if (!type.equals("3", true))
                {
                    var innerObj  =parentobjt.getString("start_location")
                    var jObjTrucker = JSONObject(innerObj)
                    trackerLatLng = LatLng(jObjTrucker.getString("location_lat")      //Trucker
                            as Double,jObjTrucker.getString("location_long") as Double)
                }

                GetDirection( getDirectionsUrl(userLatLng,trackerLatLng)).execute()



            }
            catch (e: JSONException) {
                e.printStackTrace()
            } catch (ex: Exception) {
                ex.printStackTrace()
                Log.d("expst", " Nullpointer exception")
            }

        }
    }



    /**
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     * This is where we can add markers or lines, add listeners or move the camera. In this case,
     * we just add a marker near Sydney, Australia.
     * If Google Play services is not installed on the device, the user will be prompted to install
     * it inside the SupportMapFragment. This method will only be triggered once the user has
     * installed Google Play services and returned to the app.
     */
    @SuppressLint("MissingPermission")
    override fun onMapReady(googleMap: GoogleMap) {
        mMap = googleMap

        mMap.setMyLocationEnabled(true);
        // Add a marker in Sydney and move the camera
        val sydney = LatLng(22.779200, 88.367870)
        val sydney2 = LatLng(22.564056, 88.353853)
        mMap.addMarker(MarkerOptions().position(sydney).title("Marker in Sydney"))
        mMap.moveCamera(CameraUpdateFactory.newLatLng(sydney))

     //   GetDirection( getDirectionsUrl(sydney,sydney2)).execute()
        TransportDetailAsync().execute("http://triptoe.pearnode.com/api_mobile/api/transport_details",tranId)

    }


   fun getDirectionsUrl(origin:LatLng?, dest:LatLng?):String
    {

        // Origin of route
        var str_origin = "origin="+origin?.latitude+","+origin?.longitude;

        // Destination of route
        var str_dest = "destination="+dest?.latitude+","+dest?.longitude;


        // Sensor enabled
        var sensor = "sensor=false";

        // Building the parameters to the web service
        var parameters = str_origin+"&"+str_dest+"&"+sensor;

        // Output format
        var output = "json";

        // Building the url to the web service
        var url = "https://maps.googleapis.com/maps/api/directions/"+output+"?"+parameters;

        return url;
    }



    private inner class GetDirection(val url : String) : AsyncTask<Void,Void,List<List<LatLng>>>(){
        override fun doInBackground(vararg params: Void?): List<List<LatLng>> {
            val client = OkHttpClient()
            val request = Request.Builder().url(url).build()
            val response = client.newCall(request).execute()
            val data = response.body()!!.string()
            Log.d("GoogleMap" , " data : $data")
            val result =  ArrayList<List<LatLng>>()
            try{
                val respObj = Gson().fromJson(data, GoogleMapDTO::class.java)

                val path =  ArrayList<LatLng>()

                for (i in 0..(respObj.routes[0].legs[0].steps.size-1)){
//                    val startLatLng = LatLng(respObj.routes[0].legs[0].steps[i].start_location.lat.toDouble()
//                            ,respObj.routes[0].legs[0].steps[i].start_location.lng.toDouble())
//                    path.add(startLatLng)
//                    val endLatLng = LatLng(respObj.routes[0].legs[0].steps[i].end_location.lat.toDouble()
//                            ,respObj.routes[0].legs[0].steps[i].end_location.lng.toDouble())
                    path.addAll(decodePolyline(respObj.routes[0].legs[0].steps[i].polyline.points))
                }
                result.add(path)
            }catch (e:Exception){
                e.printStackTrace()
            }
            return result
        }

        override fun onPostExecute(result: List<List<LatLng>>) {
            val lineoption = PolylineOptions()
            for (i in result.indices){
                lineoption.addAll(result[i])
                lineoption.width(10f)
                lineoption.color(Color.BLUE)
                lineoption.geodesic(true)
            }
            mMap.addPolyline(lineoption)
        }
    }

   fun decodePolyline(encoded: String): List<LatLng> {

        val poly = ArrayList<LatLng>()
        var index = 0
        val len = encoded.length
        var lat = 0
        var lng = 0

        while (index < len) {
            var b: Int
            var shift = 0
            var result = 0
            do {
                b = encoded[index++].toInt() - 63
                result = result or (b and 0x1f shl shift)
                shift += 5
            } while (b >= 0x20)
            val dlat = if (result and 1 != 0) (result shr 1).inv() else result shr 1
            lat += dlat

            shift = 0
            result = 0
            do {
                b = encoded[index++].toInt() - 63
                result = result or (b and 0x1f shl shift)
                shift += 5
            } while (b >= 0x20)
            val dlng = if (result and 1 != 0) (result shr 1).inv() else result shr 1
            lng += dlng

            val latLng = LatLng((lat.toDouble() / 1E5),(lng.toDouble() / 1E5))
            poly.add(latLng)
        }

        return poly
    }



}
