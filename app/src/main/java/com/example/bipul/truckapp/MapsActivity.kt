package com.example.bipul.truckapp

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
import android.graphics.Color
import android.os.AsyncTask
import android.util.Log
import com.example.bipul.truckapp.model.GoogleMapDTO
import com.google.android.gms.maps.model.PolylineOptions
import com.google.gson.Gson
import com.squareup.okhttp.OkHttpClient
import com.squareup.okhttp.Request
import java.io.BufferedReader
import java.io.InputStream
import java.io.InputStreamReader
import java.net.HttpURLConnection
import java.net.URL


class MapsActivity : AppCompatActivity(), OnMapReadyCallback {

    private lateinit var mMap: GoogleMap

var type = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_maps)
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.

        val mapFragment = supportFragmentManager
                .findFragmentById(R.id.map) as SupportMapFragment
        mapFragment.getMapAsync(this)

        cmpltLay.setOnClickListener(View.OnClickListener { Toast.makeText(this, "We are working on it" as String, Toast.LENGTH_LONG).show() })
        cnclLay.setOnClickListener(View.OnClickListener { Toast.makeText(this, "We are working on it" as String, Toast.LENGTH_LONG).show() })
        emgncyLay.setOnClickListener(View.OnClickListener { Toast.makeText(this, "We are working on it" as String, Toast.LENGTH_LONG).show() })

        val editor = getSharedPreferences("truck", Context.MODE_PRIVATE)
        type = editor.getString("type","type")

        if (!type.equals(type,ignoreCase = true))
        {
            linearLay.visibility = View.GONE
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
    override fun onMapReady(googleMap: GoogleMap) {
        mMap = googleMap

        // Add a marker in Sydney and move the camera
        val sydney = LatLng(-34.0, 151.0)
        mMap.addMarker(MarkerOptions().position(sydney).title("Marker in Sydney"))
        mMap.moveCamera(CameraUpdateFactory.newLatLng(sydney))
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

    /** A method to download json data from url */

    @SuppressLint("LongLogTag")
   fun downloadUrl( strUrl:String?):String{
        var data:String? = null;
       var iStream : InputStream? = null;
        var urlConnection: HttpURLConnection? = null;
        try{
            var url: URL =  URL(strUrl);

            // Creating an http connection to communicate with url
            urlConnection =  url.openConnection() as HttpURLConnection;

            // Connecting to url
            urlConnection.connect();

            // Reading data from url
            iStream = urlConnection.getInputStream();

            var br : BufferedReader =  BufferedReader(InputStreamReader(iStream));

            var sb :StringBuffer  = StringBuffer();

            var line = "";
           /* while( ( line = br.readLine())  != null){
                sb.append(line);
            }*/

            do {
                line = br.readLine()

                if (line == null)

                    break

                else
                    sb.append(line)
                //  println(line)

            }
            // while (true)
            while (line == null)

            data = sb.toString();

            br.close();

        }catch(e: Exception ){
            Log.d("Exception while downloading url", e.toString());
        }finally{
            iStream?.close();
            urlConnection?.disconnect();
        }
        return data as String;


    }

   /*inner class DownloadTask : AsyncTask<String, Void, String>() {

        internal var pdLoading2: ProgressDialog? = null
        override fun onPreExecute() {
            super.onPreExecute()
            *//* pdLoading2? = ProgressDialog(this@MapsActivity)
             pdLoading2?.setMessage("\tVerifying...")
             pdLoading2?.setCancelable(false)
             pdLoading2?.show()*//*
        }

        // Downloading data in non-ui thread
        override fun doInBackground(vararg url: String): String {

            // For storing data from web service
            var data = ""

            try {
                // Fetching the data from web service
                data = downloadUrl(url[0])
            } catch (e: Exception) {
                Log.d("Background Task", e.toString())
            }

            return data
        }

        // Executes in UI thread, after the execution of
        // doInBackground()
        override fun onPostExecute(result: String) {
            super.onPostExecute(result)

            val parserTask = ParserTask()

            // Invokes the thread for parsing the JSON data
            parserTask.execute(result)
          //  pdLoading2?.dismiss()
        }


    }*/

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

    public fun decodePolyline(encoded: String): List<LatLng> {

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
