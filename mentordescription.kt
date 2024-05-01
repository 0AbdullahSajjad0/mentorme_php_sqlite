package com.abdullahsajjad.i212477

import android.content.Intent
import android.graphics.BitmapFactory
import android.graphics.Color
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.CalendarView
import android.widget.ImageView
import android.widget.TextView
import androidx.cardview.widget.CardView
import com.android.volley.Response
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

class mentordescription : AppCompatActivity() {

    private lateinit var auth: FirebaseAuth
    private lateinit var database: FirebaseDatabase
    private lateinit var storage: FirebaseStorage

    private var mentorId: String? = null
    private var findmentorURL: String? = null
    private lateinit var DisplayName: TextView
    private lateinit var DisplayDescription: TextView
    private lateinit var mentorImage: ImageView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_mentordescription)

        var userid = intent.getStringExtra("UserID")
        var userName = intent.getStringExtra("UserName")
        Log.d("MentorsListAdaptor", "User Name Being Recieved at mentor Description: $userName")
        mentorId = intent.getStringExtra("MentorID")
        findmentorURL = "http://192.168.56.1/mentorme/findmentor.php"
        DisplayName = findViewById(R.id.mentorname)
        DisplayDescription = findViewById(R.id.mentoraboutme)
        mentorImage = findViewById(R.id.mentorimage)

        userDataInitialise()

        var gobackbtn = findViewById<ImageView>(R.id.backtohome)
        var reviewbtn = findViewById<CardView>(R.id.review)
        var btn = findViewById<Button>(R.id.booksession)

        btn.setBackgroundColor(Color.parseColor("#0D5995"))

        gobackbtn.setOnClickListener{
            finish()
        }

        reviewbtn.setOnClickListener{
            val intent = Intent(this, reviewpage::class.java)
            intent.putExtra("MentorID", mentorId)
            intent.putExtra("UserID", userid)
            startActivity(intent)
        }

        btn.setOnClickListener{
            val intent = Intent(this, booksessionpage::class.java)
            intent.putExtra("MentorID", mentorId)
            intent.putExtra("UserID", userid)
            Log.d("MentorsListAdaptor", "User Name Being Sent to book session: $userName")
            intent.putExtra("UserName", userName)
            startActivity(intent)
        }
    }

    override fun onResume() {
        super.onResume()
        userDataInitialise()
    }

    private fun userDataInitialise(){
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
                        var name = jsonResponse.getString("name")
                        var description = jsonResponse.getString("description")
                        var profpic = jsonResponse.getString("profilepic")
                        // Start new activity here
                        Log.d("API Response", "Name: $name")
                        Log.d("API Response", "Description: $description")
                        Log.d("API Response", "Profile Pic: $profpic")

                        DisplayName.text = name
                        DisplayDescription.text = description
                        if(profpic != "default"){
                            Picasso.get().load(profpic).into(mentorImage)
                        }

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
}