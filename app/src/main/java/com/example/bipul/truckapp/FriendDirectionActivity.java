package com.example.bipul.truckapp;


import android.Manifest;
import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.location.Location;
import android.location.LocationManager;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.UiSettings;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.PolylineOptions;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.myapp.bipul.traceyou.Helper.Help;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

//import android.location.LocationListener;

public class FriendDirectionActivity extends AppCompatActivity implements OnMapReadyCallback,
        GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener,
        LocationListener {



    protected LocationManager locationManager;

    protected Context context;

    public double currentLat;
    public double currentLong;
    private GoogleMap googleMap;
    ArrayList<LatLng> markerPoints = new ArrayList<LatLng>();
    MarkerOptions options1,options;

    private GoogleApiClient client;
    private LocationRequest locationRequest;
    private Location lastLocation;
    private Marker currentLocationMarker;

    private Button sViewBtn,nViewBtn;

    CountDownTimer timer;

    String frinedNumber ="";
    String frinedNanme ="";
    String lat="0";
    Intent globIntent;
    LocationBroadCast receiver;

    DatabaseReference databaseReference;

/*  private static final String LOG_TAG = "ExampleApp";
    private static final String PLACES_API_BASE = "https://maps.googleapis.com/maps/api/place";
    private static final String TYPE_AUTOCOMPLETE = "/autocomplete";
    private static final String OUT_JSON = "/json";
*//*


    String direction[] = new String[2];
    String userId;
SharedPreferences pref;
String fName="";
String fNumber="";
String countryCode="";

String globLat="",globLng="",lastTime="";

Geo geo;
Help help;


ProgressDialog pdLoading;

    boolean networkStatus;
    //------------ make your specific key ------------

private static final String API_KEY = "AIzaSyDrlkIJJl7VUmpkgPcNWT46ORfOekTzDB8";
  //  private static final String API_KEY = "AIzaSyB5lm0Tjli-omMyu4oWPz29xglCGhYa5gg";


    private static final String ACTION_IN_LOC = "com.example.mobot.ecoline";

    public FriendDirectionActivity(){}

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_friend_direction);

       */
/* Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
*//*

        final IntentFilter filter = new IntentFilter();
        filter.addAction(ACTION_IN_LOC);
        this.receiver = new LocationBroadCast();
        this.registerReceiver(this.receiver, filter);

        pref =getSharedPreferences("traceYou", Context.MODE_PRIVATE);
        fName = pref.getString("fname","");
        fNumber = pref.getString("fnumber","");
        countryCode = pref.getString("countryCode","");

        help = new Help(FriendDirectionActivity.this);

        globIntent = getIntent();
        frinedNumber = globIntent.getStringExtra("fnumber");
        frinedNanme = globIntent.getStringExtra("fname");

        nViewBtn = (Button)findViewById(R.id.nViewBtn);
        sViewBtn = (Button)findViewById(R.id.sViewBtn);

     //   startAdThread();

      */
/*  adView = (AdView)findViewById(R.id.adView);
       // MobileAds.initialize(this, "ca-app-pub-3940256099942544/6300978111");
        MobileAds.initialize(this, "ca-app-pub-1806296421186622~2732604891");
        AdRequest adRequest = new AdRequest.Builder().build();
        adView.loadAd(adRequest);*/



        sViewBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                googleMap.setMapType(GoogleMap.MAP_TYPE_HYBRID);
            }
        });

        nViewBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                googleMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);
               // onMapReady();
            }
        });

        options = new MarkerOptions();
        options1 = new MarkerOptions();

        geo = new Geo();

        pdLoading = new ProgressDialog(FriendDirectionActivity.this);
        pdLoading.setMessage("\tSearching your friend location...");
        pdLoading.setCancelable(false);
        pdLoading.show();

        timer= new CountDownTimer(20000, 1000) {

            public void onTick(long millisUntilFinished) {
                    Log.d("countDown",".."+(millisUntilFinished/1000));
                if(Double.parseDouble(lat)>0)
                {
                    pdLoading.dismiss();
                    timer.cancel();
                }

                if(millisUntilFinished/1000 == 8)
                {
                    getLatLng(fNumber);
                }
            }


            public void onFinish() {
                Log.d("countDown","done!");
                pdLoading.dismiss();
                if(Double.parseDouble(lat)>0)
                {

                }
                else
                    {
                        pdLoading = new ProgressDialog(FriendDirectionActivity.this);
                        pdLoading.setMessage("\tSearching your friend location...");
                        pdLoading.setCancelable(false);
                        pdLoading.show();
                      //  getLatLng(fNumber);
                        blockedCheck(fNumber);
                    }
            }

        }.start();

        MapFragment mapFragment = ((MapFragment) getFragmentManager().findFragmentById(R.id.mapFragment));
        mapFragment.getMapAsync(this);

        locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission
                (this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }

     //   locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, (LocationListener) FriendDirectionActivity.this);

    }

    @Override
    public void onConnected(@Nullable Bundle bundle) {

        locationRequest =LocationRequest.create();
        locationRequest.setInterval(1000);
        locationRequest.setFastestInterval(1000);
        locationRequest.setPriority(LocationRequest.PRIORITY_BALANCED_POWER_ACCURACY);

        if(ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) ==(PackageManager.PERMISSION_GRANTED)) {

            LocationServices.FusedLocationApi.requestLocationUpdates(client, locationRequest, this);

        }


    }

    @Override
    public void onConnectionSuspended(int i) {

    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

    }

@Override
    public void onMapReady(GoogleMap map) {

    Log.d("mapRedyy","in on Map ready");
    googleMap=map;


    UiSettings uiSettings = googleMap.getUiSettings();
    googleMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);
    googleMap.setMyLocationEnabled(true);
    googleMap.setTrafficEnabled(false);
    googleMap.setIndoorEnabled(false);
    googleMap.setBuildingsEnabled(true);
    uiSettings.setRotateGesturesEnabled(true);
    // googleMap.getUiSettings().setZoomControlsEnabled(true);
    // googleMap.moveCamera(CameraUpdateFactory.newLatLng(SomePos));
    googleMap.moveCamera(CameraUpdateFactory.newCameraPosition(new CameraPosition.Builder()
            .target(googleMap.getCameraPosition().target)
            .zoom(17)
            .bearing(40)
            .tilt(45)
            .build()));
    if(ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED)
    {

        buildGoogleApiClient();
        googleMap.setMyLocationEnabled(true);
    }
    LatLng lng = new LatLng(currentLat,currentLong);
    googleMap.animateCamera(CameraUpdateFactory.newLatLng(lng));


    }

    protected  synchronized void buildGoogleApiClient()
    {
        client = new GoogleApiClient.Builder(this)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(LocationServices.API)
                .build();
        client.connect();
    }


    @Override
    public void onLocationChanged(Location location) {


      //  LOCATION_BURN_BY = new LatLng(22.572646, -88.363895);
        Log.d("changedLoc", "startLocationUpdatesAfterResume called");

        lastLocation = location;

        if(currentLocationMarker !=null)
        {
            currentLocationMarker.remove();
        }

        Log.d("mapRedyy","in on LocationChange");
        Log.d("mapRedyy","in on Map ready  "+location.getLongitude()+"  "+location.getLatitude());
        LatLng latLng = new LatLng(location.getLatitude(),location.getLongitude());
        currentLat = location.getLatitude();
        currentLong=location.getLongitude();
        MarkerOptions markerOptions = new MarkerOptions();
        markerOptions.position(latLng);
        markerOptions.title("my position");
        markerOptions.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_RED));


        currentLocationMarker = googleMap.addMarker(markerOptions);
        googleMap.moveCamera(CameraUpdateFactory.newLatLng(latLng));
        googleMap.animateCamera(CameraUpdateFactory.zoomBy(10));

        if(client !=null)
        {
            LocationServices.FusedLocationApi.removeLocationUpdates(client,this);
        }
    }

    public  void showFrdLoc(LatLng latLng, String number)
    {
String address="";
        if( help.getMrkAdrs(latLng) == null)
        {
            address = number;
        }
        else
            {
                address=  help.getMrkAdrs(latLng);
            }
        MarkerOptions markerOptions = new MarkerOptions();
        markerOptions.position(latLng);
        markerOptions.title("("+fName+") "+address);
        markerOptions.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_BLUE));

        googleMap.addMarker(markerOptions);
        googleMap.moveCamera(CameraUpdateFactory.newLatLng(latLng));
        googleMap.animateCamera(CameraUpdateFactory.zoomTo(12));

if(fNumber.contains(countryCode)) {
    //  getDirectionsUrl(new LatLng(currentLat,currentLong),latLng);
    String url = getDirectionsUrl(new LatLng(currentLat,currentLong),latLng);

    DownloadTask downloadTask = new DownloadTask();

    // Start downloading json data from Google Directions API
    downloadTask.execute(url);
}


    }


private void dialog(String message, final String number)
{

    if(message.equalsIgnoreCase("error"))
    {
        final AlertDialog.Builder builder;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            builder = new AlertDialog.Builder(FriendDirectionActivity.this, android.R.style.Theme_Material_Dialog_Alert);
        } else {
            builder = new AlertDialog.Builder(FriendDirectionActivity.this);
        }
        builder.setTitle("Error")
                .setMessage("Please try after some time Your friend may blocked you ")
                .setPositiveButton("ok", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {

                                finish();
                            }
                        }

                )



                .setIcon(android.R.drawable.ic_dialog_alert)
                .show();

    }
    else if(message.equalsIgnoreCase("lastupdate"))
    {
        pdLoading.dismiss();
        final AlertDialog.Builder builder;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            builder = new AlertDialog.Builder(FriendDirectionActivity.this, android.R.style.Theme_Material_Dialog_Alert);
        } else {
            builder = new AlertDialog.Builder(FriendDirectionActivity.this);
        }
        builder.setTitle("Last Location Update")
                .setMessage("Your friend may turned off location service. do you want to see his/her last location update at\n\n "+lastTime+"  ")
                .setPositiveButton("ok", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {

                                Toast.makeText(FriendDirectionActivity.this, "Please Wait...", Toast.LENGTH_LONG).show();
/*  databaseReference = FirebaseDatabase.getInstance().getReference(number);
                                databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
                                    @Override
                                    public void onDataChange(DataSnapshot dataSnapshot) {
                                        String lat = dataSnapshot.child("lat").getValue().toString();
                                        String lng = dataSnapshot.child("lng").getValue().toString();

                                        showFrdLoc(new LatLng(Double.parseDouble(lat), Double.parseDouble(lng)), number); //Calling method to show last location update


                                    }

                                    @Override
                                    public void onCancelled(DatabaseError databaseError) {

                                    }
                                });*//*


                                showFrdLoc(new LatLng(Double.parseDouble(globLat), Double.parseDouble(globLng)), number); //Calling method to show last location update
                            }

                        }

                )
                .setNegativeButton("no", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        finish();
                    }
                })


                .setIcon(android.R.drawable.ic_dialog_alert)
                .show();


      */
/*  Toast.makeText(FriendDirectionActivity.this, "Please Wait...", Toast.LENGTH_LONG).show();
        databaseReference = FirebaseDatabase.getInstance().getReference(number);
        databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                String lat = dataSnapshot.child("lat").getValue().toString();
                String lng = dataSnapshot.child("lng").getValue().toString();

                showFrdLoc(new LatLng(Double.parseDouble(lat), Double.parseDouble(lng)), number); //Calling method to show last location update


            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });*/

    }
    pdLoading.dismiss();
}


void getLatLng(final String number) //getLatlng And Time Stemp
{
    try {
        databaseReference = FirebaseDatabase.getInstance().getReference(number);
        databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                globLat = dataSnapshot.child("lat").getValue().toString();
                globLng = dataSnapshot.child("lng").getValue().toString();
                lastTime=dataSnapshot.child("time").getValue().toString();

                //   showFrdLoc(new LatLng(Double.parseDouble(lat), Double.parseDouble(lng)), number); //Calling method to show last location update
               // pdLoading.dismiss();
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                pdLoading.dismiss();
            }
        });
    }
    finally {
      //  pdLoading.dismiss();
    }

}




boolean blockedCheck(String number)  // Blocing check
{
    pref =getSharedPreferences("traceYou", Context.MODE_PRIVATE);
    String myNumber = pref.getString("number","");
    number = number.trim();
    if(!number.contains("+"))
    {
        number="+"+number;
    }
    final String finalNum=number;
    databaseReference = FirebaseDatabase.getInstance().getReference(finalNum).child("friends").child(myNumber);
    databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
        @Override
        public void onDataChange(DataSnapshot dataSnapshot) {
            String block ="no";
            String friend = "yes";
            try {

                try {
                    if(dataSnapshot.child("name").getValue().toString()==null)
                    {
                        //friend= "no";
                        friend = "yes";
                        //   dialog("error",finalNum);
                    }
                    else
                    {
                        friend= "yes";
                    }
                }
                catch (Exception ex)
                {
                   // friend= "no";
                    friend = "yes";
                    // dialog("error",finalNum);
                }

                block = dataSnapshot.child("block").getValue().toString();
                Log.d("blockFrd","... "+block);

            }
            catch (Exception ex)
            {

            }

            if(block.equalsIgnoreCase("no") & friend.equalsIgnoreCase("yes"))
            {
                dialog("lastupdate",finalNum);
            }
            else
                {
                    dialog("error",finalNum);
                }

        }

        @Override
        public void onCancelled(DatabaseError databaseError) {

        }
    });
    return  true;

}


    public class LocationBroadCast extends BroadcastReceiver
    {
        Bundle bundle;
        String state;

        public LocationBroadCast() {}
        @Override
        public void onReceive(Context context, Intent intent) {

            if(intent.getAction().equals(ACTION_IN_LOC))
            {
                lat =intent.getStringExtra("lat");
                String lng =intent.getStringExtra("lng");
                String number =intent.getStringExtra("number");
                String tag =intent.getStringExtra("tag");

                // if(number.equalsIgnoreCase(frinedNanme) & Double.parseDouble(lat)>0)
                if(Double.parseDouble(lat)>0)
                {
                    timer.cancel();
                    pdLoading.dismiss();
                    showFrdLoc(new LatLng(Double.parseDouble(lat), Double.parseDouble(lng)),number); // calling method for showing frnd location

                    // dialog("lastupdate",number);
                    //  blockedCheck(fNumber);
                }

                else if(tag.equalsIgnoreCase("error"))
                {
                    dialog("error",fNumber);
                }

                else
                    // dialog("lastupdate",number);
                    blockedCheck(fNumber);

            }
        }
    }

    @Override
    public void onBackPressed() {
        finish();
    }


    //_________________________________________________                 map direction starts

    private String getDirectionsUrl(LatLng origin, LatLng dest)
        {

        // Origin of route
        String str_origin = "origin="+origin.latitude+","+origin.longitude;

        // Destination of route
        String str_dest = "destination="+dest.latitude+","+dest.longitude;


        // Sensor enabled
        String sensor = "sensor=false";

        // Building the parameters to the web service
        String parameters = str_origin+"&"+str_dest+"&"+sensor;

        // Output format
        String output = "json";

        // Building the url to the web service
        String url = "https://maps.googleapis.com/maps/api/directions/"+output+"?"+parameters;


        return url;
    }

/** A method to download json data from url */

    @SuppressLint("LongLogTag")
    private String downloadUrl(String strUrl) throws IOException {
        String data = "";
        InputStream iStream = null;
        HttpURLConnection urlConnection = null;
        try{
            URL url = new URL(strUrl);

            // Creating an http connection to communicate with url
            urlConnection = (HttpURLConnection) url.openConnection();

            // Connecting to url
            urlConnection.connect();

            // Reading data from url
            iStream = urlConnection.getInputStream();

            BufferedReader br = new BufferedReader(new InputStreamReader(iStream));

            StringBuffer sb  = new StringBuffer();

            String line = "";
            while( ( line = br.readLine())  != null){
                sb.append(line);
            }

            data = sb.toString();

            br.close();

        }catch(Exception e){
            Log.d("Exception while downloading url", e.toString());
        }finally{
            iStream.close();
            urlConnection.disconnect();
        }
        return data;
    }


    // Fetches data from url passed
    private class DownloadTask extends AsyncTask<String, Void, String> {
        ProgressDialog pdLoading2 = new ProgressDialog(FriendDirectionActivity.this);
        protected void onPreExecute() {
            super.onPreExecute();
            pdLoading2.setMessage("\tFetching data...");
            pdLoading2.setCancelable(false);
            pdLoading2.show();
        }
        // Downloading data in non-ui thread
        @Override
        protected String doInBackground(String... url) {

            // For storing data from web service
            String data = "";

            try{
                // Fetching the data from web service
                data = downloadUrl(url[0]);
            }catch(Exception e){
                Log.d("Background Task",e.toString());
            }
            return data;
        }

        // Executes in UI thread, after the execution of
        // doInBackground()
        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);

            ParserTask parserTask = new ParserTask();

            // Invokes the thread for parsing the JSON data
            parserTask.execute(result);
            pdLoading2.dismiss();
        }
    }

/** A class to parse the Google Places in JSON format */

    private class ParserTask extends AsyncTask<String, Integer, List<List<HashMap<String,String>>> > {


        // Parsing the data in non-ui thread
        @Override
        protected List<List<HashMap<String, String>>> doInBackground(String... jsonData) {

            JSONObject jObject;
            List<List<HashMap<String, String>>> routes = null;

            try{
                jObject = new JSONObject(jsonData[0]);
                DirectionsJSONParser parser = new DirectionsJSONParser();

                // Starts parsing data
                routes = parser.parse(jObject);
            }catch(Exception e){
                e.printStackTrace();
            }
            return routes;
        }

        // Executes in UI thread, after the parsing process
        @Override
        protected void onPostExecute(List<List<HashMap<String, String>>> result) {
            ArrayList<LatLng> points = null;
            PolylineOptions lineOptions = null;
            MarkerOptions markerOptions = new MarkerOptions();

            // Traversing through all the routes
            for(int i=0;i<result.size();i++){
                points = new ArrayList<LatLng>();
                lineOptions = new PolylineOptions();

                // Fetching i-th route
                List<HashMap<String, String>> path = result.get(i);

                // Fetching all the points in i-th route
                for(int j=0;j<path.size();j++){
                    HashMap<String,String> point = path.get(j);

                    double lat = Double.parseDouble(point.get("lat"));
                    double lng = Double.parseDouble(point.get("lng"));
                    LatLng position = new LatLng(lat, lng);

                    points.add(position);
                }

                // Adding all the points in the route to LineOptions
                lineOptions.addAll(points);
                lineOptions.width(10);
                lineOptions.color(Color.RED);

            }

            // Drawing polyline in the Google Map for the i-th route
try {
    googleMap.addPolyline(lineOptions);
}
catch (Exception ex)
{
    ex.printStackTrace();
}



        }
    }

    public class DirectionsJSONParser {

/** Receives a JSONObject and returns a list of lists containing latitude and longitude *//*

        public List<List<HashMap<String,String>>> parse(JSONObject jObject){

            List<List<HashMap<String, String>>> routes = new ArrayList<List<HashMap<String,String>>>() ;
            JSONArray jRoutes = null;
            JSONArray jLegs = null;
            JSONArray jSteps = null;

            try {

                jRoutes = jObject.getJSONArray("routes");

                */
/** Traversing all routes *//*

                for(int i=0;i<jRoutes.length();i++){
                    jLegs = ( (JSONObject)jRoutes.get(i)).getJSONArray("legs");
                    List path = new ArrayList<HashMap<String, String>>();

                    */
/** Traversing all legs *//*

                    for(int j=0;j<jLegs.length();j++){
                        jSteps = ( (JSONObject)jLegs.get(j)).getJSONArray("steps");

                        */
/** Traversing all steps *//*

                        for(int k=0;k<jSteps.length();k++){
                            String polyline = "";
                            polyline = (String)((JSONObject)((JSONObject)jSteps.get(k)).get("polyline")).get("points");
                            List<LatLng> list = decodePoly(polyline);

                            */
/** Traversing all points *//*

                            for(int l=0;l<list.size();l++){
                                HashMap<String, String> hm = new HashMap<String, String>();
                                hm.put("lat", Double.toString(((LatLng)list.get(l)).latitude) );
                                hm.put("lng", Double.toString(((LatLng)list.get(l)).longitude) );
                                path.add(hm);
                            }
                        }
                        routes.add(path);
                    }
                }

            } catch (JSONException e) {
                e.printStackTrace();
            }catch (Exception e){
            }


            return routes;
        }


        */
/**
         * Method to decode polyline points
         * Courtesy : http://jeffreysambells.com/2010/05/27/decoding-polylines-from-google-maps-direction-api-with-java
         * *//*

        private List<LatLng> decodePoly(String encoded) {

            List<LatLng> poly = new ArrayList<LatLng>();
            int index = 0, len = encoded.length();
            int lat = 0, lng = 0;

            while (index < len) {
                int b, shift = 0, result = 0;
                do {
                    b = encoded.charAt(index++) - 63;
                    result |= (b & 0x1f) << shift;
                    shift += 5;
                } while (b >= 0x20);
                int dlat = ((result & 1) != 0 ? ~(result >> 1) : (result >> 1));
                lat += dlat;

                shift = 0;
                result = 0;
                do {
                    b = encoded.charAt(index++) - 63;
                    result |= (b & 0x1f) << shift;
                    shift += 5;
                } while (b >= 0x20);
                int dlng = ((result & 1) != 0 ? ~(result >> 1) : (result >> 1));
                lng += dlng;

                LatLng p = new LatLng((((double) lat / 1E5)),
                        (((double) lng / 1E5)));
                poly.add(p);
            }

            return poly;
        }
    }


   */
/* void startAdThread() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                //  MobileAds.initialize(MainActivity.this, "ca-app-pub-3940256099942544/6300978111");
                MobileAds.initialize(FriendDirectionActivity.this, "ca-app-pub-1806296421186622~2732604891");
                AdRequest adRequest = new AdRequest.Builder().build();
                adView.loadAd(adRequest);
            }
        });

    }*//*


}
*/
