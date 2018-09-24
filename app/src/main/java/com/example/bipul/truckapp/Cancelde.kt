package com.example.bipul.truckapp

import android.app.ProgressDialog
import android.content.Context
import android.net.Uri
import android.os.AsyncTask
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.TextView
import com.example.bipul.truckapp.model.UserModel
import kotlinx.android.synthetic.main.activity_completed.*
import org.json.JSONArray
import org.json.JSONException
import org.json.JSONObject
import java.io.*
import java.net.HttpURLConnection
import java.net.MalformedURLException
import java.net.URL

class Cancelde : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_completed)

        recyclerView.layoutManager = LinearLayoutManager(this)

        val editor = getSharedPreferences("truck", Context.MODE_PRIVATE)

        var  userType = editor.getString("type", "username")
        var  userId = editor.getString("userId", "username")

        CanceledAsync().execute("http://triptoe.pearnode.com/api_mobile/api/transport_cancled",userId,userType)
    }

    inner class CanceledAsync : AsyncTask<String, String, String>() {


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
                        .appendQueryParameter("user_id", urls[1])
                        .appendQueryParameter("usertype", urls[2] )


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
                reader = BufferedReader(InputStreamReader(stream) as Reader?)

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

                //  val parentobjt = JSONObject(finaljson)

                //               ShippingAddressModel shippingAddressModel=new ShippingAddressModel();
                //
                //               shippingAddressModel.setMsg(parentobjt.getString("msg"));
                //               shippingAddressModel.setStatus(parentobjt.getBoolean("status"));

                Log.d("jsonAct",""+finaljson)

                return finaljson

            } catch (e: MalformedURLException) {
                e.printStackTrace()
            } catch (e: IOException) {
                e.printStackTrace()
            } catch (e: JSONException) {
                e.printStackTrace()
            }
            catch (e:Exception)
            {
                e.printStackTrace()
            }


            return null
        }

        override fun onPostExecute(response: String) {
            super.onPostExecute(response)

            try {

                val parentArray = JSONArray(response)

                // var arraylist: ArrayList<UserModel>?=null
                val modelList = ArrayList<UserModel>()

                for(x in 0..parentArray.length()-1)
                {
                    var jsonObj : JSONObject = parentArray.getJSONObject(x);
                    var userName=jsonObj.getString("user_name")
                    var userId=jsonObj.getString("user_id")
                    var tnsPtName=jsonObj.getString("transporter_name")
                    var transporter_id=jsonObj.getString("transporter_id")

                    var innerObj  =jsonObj.getString("start_location")
                    var jObj = JSONObject(innerObj)
                    var address=jObj.getString("address")

                    modelList.add(UserModel(userName,userId,tnsPtName,address,"123lat","lung34",transporter_id))
                }
                var adapter = UserAdapter(modelList,this@Cancelde)
                recyclerView.adapter = adapter

                //  recyclerView.adapter = UserAdapter(modelList!!, this@Active)


                //Toast.makeText(Login.this,status+result+msg,Toast.LENGTH_LONG).show();

            } catch (e: JSONException) {
                e.printStackTrace()
            } catch (ex: Exception) {
                ex.printStackTrace()
                Log.d("expst", " Nullpointer exception")
            }

        }
    }

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

            /*    holder.itmLay.setOnClickListener(View.OnClickListener {
                    val intent = Intent(context, MapsActivity::class.java)
                    intent.putExtra("trsnId",items.get(position).transPortId)
                    context.startActivity(intent)
                })*/
        }
    }

    class ViewHolder (view: View) : RecyclerView.ViewHolder(view) {
        // Holds the TextView that will add each animal to
        val userNameTxt = view.findViewById<TextView>(R.id.userNameTxt)
        val userIdTxt = view.findViewById<TextView>(R.id.userIdTxt)
        val userAdrTxt = view.findViewById<TextView>(R.id.userAdrTxt)
        val itmLay = view.findViewById<LinearLayout>(R.id.itmLay)

    }
}
