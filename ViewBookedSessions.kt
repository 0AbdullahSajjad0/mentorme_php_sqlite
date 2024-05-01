package com.abdullahsajjad.i212477

import android.content.Intent
import android.graphics.BitmapFactory
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.ImageView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.android.volley.Request
import com.android.volley.Response
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.google.firebase.storage.FirebaseStorage
import com.squareup.picasso.Picasso
import org.json.JSONException
import org.json.JSONObject
import java.util.ArrayList

class ViewBookedSessions : AppCompatActivity() {

    private lateinit var adaptor: BookingsListAdaptor
    private lateinit var recyclerView: RecyclerView
    private lateinit var newsArrayList: ArrayList<Bookings>

    private var url: String? = null
    private var userId: String? = null
    private var findmentorURL: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_view_booked_sessions)

        url = "http://192.168.56.1/mentorme/allbookings.php";
        findmentorURL = "http://192.168.56.1/mentorme/findmentor.php"
        userId = intent.getStringExtra("id")

        dataInitialize()
        val layoutManager = LinearLayoutManager(this)
        recyclerView = findViewById(R.id.bookingsRecycler)
        recyclerView.layoutManager = layoutManager
        recyclerView.setHasFixedSize(true)

        var btn = findViewById<ImageView>(R.id.backtoprof)

        btn.setOnClickListener{
            finish()
        }

    }

    private fun dataInitialize(){
        newsArrayList = ArrayList<Bookings>()

        val stringRequest = object : StringRequest(
            Method.POST, url,
            Response.Listener<String> { response ->
                try {
                    val jsonResponse = JSONObject(response)
                    val success = jsonResponse.getInt("success")
                    if (success == 1) {
                        val bookingsArray = jsonResponse.getJSONArray("bookings")
                        for (i in 0 until bookingsArray.length()) {
                            val booking = bookingsArray.getJSONObject(i)
                            val bookingID = booking.getString("bookingID")
                            val mentorId = booking.getString("mentorID")
                            val userID = booking.getString("userID")
                            val date = booking.getString("date")
                            val time = booking.getString("time")

                            var profpic : String
                            var mentorname : String
                            val stringRequest = object : StringRequest(
                                Method.POST, findmentorURL,
                                Response.Listener { response ->
                                    // Handle successful response
                                    if (response != null) {
                                        Log.d("API Response", response)
                                        try {
                                            Log.d("API Response", "Response: $response")
                                            val jsonResponse = JSONObject(response)
                                            Log.d("API Response", "JSON Response: $jsonResponse")
                                            mentorname = jsonResponse.getString("name")
                                            profpic = jsonResponse.getString("profilepic")
                                            // Start new activity here
                                            Log.d("API Response", "Name: $mentorname")
                                            Log.d("API Response", "Profile Pic: $profpic")

                                            val bookingObj = Bookings(bookingID, userID, mentorId, mentorname, date, time, profpic)
                                            newsArrayList.add(bookingObj)

                                            adaptor = BookingsListAdaptor(newsArrayList, this@ViewBookedSessions)
                                            recyclerView.adapter = adaptor
                                            adaptor.notifyDataSetChanged()

                                        } catch (e: JSONException) {
                                            e.printStackTrace()
                                        }
                                    } else {
                                        Log.e("API Error", "No response from the server")
                                    }
                                },
                                Response.ErrorListener { error ->
                                    // Handle error
                                    Log.e("API Error", "Error occurred: ${error.message}")
                                }) {
                                override fun getParams(): MutableMap<String, String> {
                                    val params = HashMap<String, String>()
                                    params["id"] = mentorId.toString()
                                    return params
                                }
                            }
                            // Add the request to the RequestQueue
                            Volley.newRequestQueue(this).add(stringRequest)



                        }
                    } else {
                        Log.e("FetchData", "API call was not successful")
                    }
                } catch (e: JSONException) {
                    e.printStackTrace()
                }
            },
            Response.ErrorListener { error ->
                Log.e("FetchData", "Error occurred: ${error.message}")
            }) {
            override fun getParams(): Map<String, String> {
                val params = HashMap<String, String>()
                params["UserId"] = userId.toString()
                return params
            }
        }

        Volley.newRequestQueue(this).add(stringRequest)

    }
}