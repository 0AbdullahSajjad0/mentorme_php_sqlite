package com.abdullahsajjad.i212477

import android.content.Intent
import android.graphics.BitmapFactory
import android.graphics.Color
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import androidx.cardview.widget.CardView
import com.android.volley.Request
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

class reviewpage : AppCompatActivity() {

    private var mentorId: String? = null
    private var userId: String? = null
    private var findmentorURL: String? = null
    private lateinit var DisplayName: TextView
    private lateinit var mentorImage: ImageView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_reviewpage)

        mentorId = intent.getStringExtra("MentorID")
        findmentorURL = "http://192.168.56.1/mentorme/findmentor.php"
        var url = "http://192.168.56.1/mentorme/addreview.php";

        userId = intent.getStringExtra("UserID");

        DisplayName = findViewById<TextView>(R.id.name)
        mentorImage = findViewById<ImageView>(R.id.profilepic)

        userDataInitialise()
        var feedback = findViewById<TextView>(R.id.feedback)

        var OneStarRating = findViewById<ImageView>(R.id.onestarrating)
        var TwoStarRating = findViewById<ImageView>(R.id.twostarrating)
        var ThreeStarRating = findViewById<ImageView>(R.id.threestarrating)
        var FourStarRating = findViewById<ImageView>(R.id.fourstarrating)
        var FiveStarRating = findViewById<ImageView>(R.id.fivestarrating)
        var rating : Int = 0
        var backtodesc = findViewById<ImageView>(R.id.backtodescription)
        var feedbackbtn = findViewById<Button>(R.id.submitfeedback)

        feedbackbtn.setBackgroundColor(Color.parseColor("#0D5995"))

        OneStarRating.setOnClickListener{
            if(rating > 1)
            {
                TwoStarRating.setImageResource(R.drawable.baseline_star_outline_24)
                ThreeStarRating.setImageResource(R.drawable.baseline_star_outline_24)
                FourStarRating.setImageResource(R.drawable.baseline_star_outline_24)
                FiveStarRating.setImageResource(R.drawable.baseline_star_outline_24)
            }
            OneStarRating.setImageResource(R.drawable.baseline_star_rate_24)
            rating = 1
        }
        TwoStarRating.setOnClickListener{
            if(rating > 2)
            {
                ThreeStarRating.setImageResource(R.drawable.baseline_star_outline_24)
                FourStarRating.setImageResource(R.drawable.baseline_star_outline_24)
                FiveStarRating.setImageResource(R.drawable.baseline_star_outline_24)
            }
            OneStarRating.setImageResource(R.drawable.baseline_star_rate_24)
            TwoStarRating.setImageResource(R.drawable.baseline_star_rate_24)
            rating = 2
        }
        ThreeStarRating.setOnClickListener{
            if(rating > 3)
            {
                FourStarRating.setImageResource(R.drawable.baseline_star_outline_24)
                FiveStarRating.setImageResource(R.drawable.baseline_star_outline_24)
            }
            OneStarRating.setImageResource(R.drawable.baseline_star_rate_24)
            TwoStarRating.setImageResource(R.drawable.baseline_star_rate_24)
            ThreeStarRating.setImageResource(R.drawable.baseline_star_rate_24)
            rating = 3
        }
        FourStarRating.setOnClickListener{
            if(rating > 3)
            {
                FiveStarRating.setImageResource(R.drawable.baseline_star_outline_24)
            }
            OneStarRating.setImageResource(R.drawable.baseline_star_rate_24)
            TwoStarRating.setImageResource(R.drawable.baseline_star_rate_24)
            ThreeStarRating.setImageResource(R.drawable.baseline_star_rate_24)
            FourStarRating.setImageResource(R.drawable.baseline_star_rate_24)
            rating = 4
        }
        FiveStarRating.setOnClickListener{
            OneStarRating.setImageResource(R.drawable.baseline_star_rate_24)
            TwoStarRating.setImageResource(R.drawable.baseline_star_rate_24)
            ThreeStarRating.setImageResource(R.drawable.baseline_star_rate_24)
            FourStarRating.setImageResource(R.drawable.baseline_star_rate_24)
            FiveStarRating.setImageResource(R.drawable.baseline_star_rate_24)
            rating = 5
        }

        backtodesc.setOnClickListener{
            finish()
        }

        feedbackbtn.setOnClickListener{

            val stringRequest = object : StringRequest(
                Request.Method.POST, url,
                Response.Listener { response ->
                    // Handle successful response
                    Log.d("API Response", response)
                },
                Response.ErrorListener { error ->
                    // Handle error
                    Log.e("API Error", "Error occurred: ${error.message}")
                }) {
                override fun getParams(): MutableMap<String, String> {
                    val params = HashMap<String, String>()
                    params["mentorID"] = mentorId.toString()
                    params["mentorname"] = DisplayName.text.toString()
                    params["userID"] = userId.toString()
                    params["feedback"] = feedback.text.toString()
                    params["rating"] = rating.toString()
                    return params
                }
            }
            // Add the request to the RequestQueue
            Volley.newRequestQueue(this).add(stringRequest)
            finish()
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