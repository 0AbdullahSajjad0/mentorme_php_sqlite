package com.abdullahsajjad.i212477

import android.content.Intent
import android.graphics.BitmapFactory
import android.graphics.Color
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.CalendarView
import android.widget.ImageView
import android.widget.TextView
import androidx.annotation.RequiresApi
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
import java.time.LocalDate
import java.time.Month
import java.time.format.TextStyle
import java.util.Locale

class booksessionpage : AppCompatActivity() {

    private lateinit var auth: FirebaseAuth
    private lateinit var database: FirebaseDatabase
    private lateinit var storage: FirebaseStorage

    private lateinit var MentorName: TextView
    private lateinit var mentorImage: ImageView
    private var mentorId: String? = null
    private var findmentorURL: String? = null

    fun getDayWithSuffix(day: Int): String {
        return when {
            day in 11..13 -> "${day}th"
            day % 10 == 1 -> "${day}st"
            day % 10 == 2 -> "${day}nd"
            day % 10 == 3 -> "${day}rd"
            else -> "${day}th"
        }
    }

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_booksessionpage)

        findmentorURL = "http://192.168.56.1/mentorme/findmentor.php"
        var bookingURL = "http://192.168.56.1/mentorme/makebooking.php"
        mentorId = intent.getStringExtra("MentorID")
        var userId = intent.getStringExtra("UserID")
        var userName = intent.getStringExtra("UserName")

        database = FirebaseDatabase.getInstance()
        storage = FirebaseStorage.getInstance()
        auth = FirebaseAuth.getInstance()

        val myRef = database.getReference("Bookings")

        var backtodesc = findViewById<ImageView>(R.id.backtodescription)
        var slot1btn = findViewById<Button>(R.id.timeslot1)
        var slot2btn = findViewById<Button>(R.id.timeslot2)
        var slot3btn = findViewById<Button>(R.id.timeslot3)
        var calender = findViewById<CalendarView>(R.id.date)
        var bookbtn = findViewById<Button>(R.id.submitbooking)
        MentorName = findViewById(R.id.name)
        mentorImage = findViewById(R.id.mentorimage)

        val currentDate = LocalDate.now()
        var date_day = currentDate.dayOfMonth
        var date_month = currentDate.monthValue
        var date_year = currentDate.year
        var date: String
        var time = "11:00 AM"

        bookbtn.setBackgroundColor(Color.parseColor("#0D5995"))
        slot1btn.setBackgroundColor(Color.parseColor("#DDDEDF"))
        slot2btn.setBackgroundColor(Color.parseColor("#0D5995"))
        slot3btn.setBackgroundColor(Color.parseColor("#DDDEDF"))

        slot1btn.setOnClickListener{
            slot1btn.setBackgroundColor(Color.parseColor("#0D5995"))
            slot2btn.setBackgroundColor(Color.parseColor("#DDDEDF"))
            slot3btn.setBackgroundColor(Color.parseColor("#DDDEDF"))
            slot1btn.setTextColor(Color.WHITE)
            slot2btn.setTextColor(Color.BLACK)
            slot3btn.setTextColor(Color.BLACK)
            time = "10:00 AM"
        }
        slot2btn.setOnClickListener{
            slot1btn.setBackgroundColor(Color.parseColor("#DDDEDF"))
            slot2btn.setBackgroundColor(Color.parseColor("#0D5995"))
            slot3btn.setBackgroundColor(Color.parseColor("#DDDEDF"))
            slot1btn.setTextColor(Color.BLACK)
            slot2btn.setTextColor(Color.WHITE)
            slot3btn.setTextColor(Color.BLACK)
            time = "11:00 AM"
        }
        slot3btn.setOnClickListener{
            slot1btn.setBackgroundColor(Color.parseColor("#DDDEDF"))
            slot2btn.setBackgroundColor(Color.parseColor("#DDDEDF"))
            slot3btn.setBackgroundColor(Color.parseColor("#0D5995"))
            slot1btn.setTextColor(Color.BLACK)
            slot2btn.setTextColor(Color.BLACK)
            slot3btn.setTextColor(Color.WHITE)
            time = "12:00 PM"
        }

        userDataInitialise()

        calender.setOnDateChangeListener { view, year, month, dayOfMonth ->
            date_year = year
            date_month = month + 1
            date_day = dayOfMonth
            date = "$dayOfMonth/$month/$year"
            Log.d("Debug", "Date Selected: $date")
        }

        backtodesc.setOnClickListener{
            finish()
        }

        bookbtn.setOnClickListener{
            /*startActivity(
                Intent(this,
                    navigation::class.java)
            );*/
            var formattedDay = getDayWithSuffix(date_day)
            var nameMonth = Month.of(date_month).getDisplayName(TextStyle.SHORT, Locale.getDefault())
            var formattedDate = "$formattedDay $nameMonth $date_year"

            /*var bookingId = myRef.push().key!!
            var booking = Bookings(bookingId, auth.currentUser?.uid, mentorID, MentorName.text.toString(), formattedDate, time, R.drawable.baseline_person_24)
            myRef.child(auth.currentUser!!.uid).child(booking.bookingId!!).setValue(booking)
                .addOnCompleteListener {
                    val intent = Intent(this@booksessionpage, navigation::class.java)
                    startActivity(intent)
                    finish()
                }*/

            val stringRequest = object : StringRequest(
                Request.Method.POST, bookingURL,
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
                    params["userID"] = userId.toString()
                    params["date"] = formattedDate
                    params["time"] = time
                    return params
                }
            }
            // Add the request to the RequestQueue
            Volley.newRequestQueue(this).add(stringRequest)
            var intent = Intent(this, navigation::class.java)

            Log.d("MentorsListAdaptor", "User Name Being Sent to nav from booking: $userName")
            intent.putExtra("id", userId)
            intent.putExtra("name", userName)
            startActivity(intent)

            Log.d("Debug", "Date Selected: $formattedDate")
        }

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

                        MentorName.text = name
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