package com.abdullahsajjad.i212477

import android.content.Context
import android.content.Intent
import android.net.ConnectivityManager
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.cardview.widget.CardView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.android.volley.Request
import com.android.volley.Response
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.storage.FirebaseStorage
import com.squareup.picasso.Picasso
import org.json.JSONArray
import org.json.JSONException
import org.json.JSONObject
import java.util.ArrayList

class HomeFragment : Fragment() {

    private var url: String? = null
    private var userId: String? = null
    private var userName: String? = null

    private lateinit var mentorsArrayList: ArrayList<HomeMentorsClass>
    private lateinit var mentorsAdaptor: MentorsListAdaptor
    private lateinit var topmentorRecyclerView: RecyclerView
    private lateinit var educationmentorRecyclerView: RecyclerView
    private lateinit var recentmentorsRecyclerView: RecyclerView

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.activity_homepage, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        url = "http://192.168.56.1/mentorme/allmentors.php";
        userId = arguments?.getString("id")
        userName = arguments?.getString("userName")



        val userNameTextView = view.findViewById<TextView>(R.id.homedisplayName)

        userNameTextView.text = userName


        val layoutManager = LinearLayoutManager(context, RecyclerView.HORIZONTAL, false)
        val layoutManager2 = LinearLayoutManager(context, RecyclerView.HORIZONTAL, false)
        val layoutManager3 = LinearLayoutManager(context, RecyclerView.HORIZONTAL, false)
        topmentorRecyclerView = view.findViewById(R.id.topmentorsrecyclerView)
        topmentorRecyclerView.layoutManager = layoutManager
        topmentorRecyclerView.setHasFixedSize(true)

        educationmentorRecyclerView = view.findViewById(R.id.educationalmentorsrecyclerView)
        educationmentorRecyclerView.layoutManager = layoutManager2
        educationmentorRecyclerView.setHasFixedSize(true)

        recentmentorsRecyclerView = view.findViewById(R.id.recentmentorsrecyclerView)
        recentmentorsRecyclerView.layoutManager = layoutManager3
        recentmentorsRecyclerView.setHasFixedSize(true)

        dataInitialize()

        var checknotif = view.findViewById<CardView>(R.id.checknotifications)
        checknotif.setOnClickListener{
            val context = view.context
            context.startActivity(
                Intent(context, Notifications::class.java)
            )
        }

    }

    private fun dataInitialize() {

        val columns = mapOf(
            "mentorID" to "INTEGER PRIMARY KEY AUTOINCREMENT",
            "name" to "TEXT",
            "description" to "TEXT",
            "status" to "TEXT",
            "profilepic" to "TEXT"
        )
        val dbHelper = MySqliteHelper(requireContext(), "mentors", columns)
        val db = dbHelper.writableDatabase

        mentorsArrayList = ArrayList<HomeMentorsClass>()

        Log.d("API", "About to send request to API URL: $url")

        if (isInternetAvailable(requireContext())) {
            // Fetch data from PHP server
            val jsonObjectRequest = JsonObjectRequest(Request.Method.GET, url, null,
                { response ->
                    try {
                        val success = response.getInt("success")
                        if (success == 1) {
                            val contactsArray = response.getJSONArray("mentors")
                            for (i in 0 until contactsArray.length()) {
                                val contact = contactsArray.getJSONObject(i)
                                val mentorId = contact.getString("mentorID")
                                val name = contact.getString("name")
                                val description = contact.getString("description")
                                val status = contact.getString("status")
                                val profilepic = contact.getString("profilepic")

                                val mentor = HomeMentorsClass(mentorId,name, "Full Stack Developer", status, "heart", "1200 $")
                                Log.d("API", "Mentor: $mentor")
                                mentorsArrayList.add(mentor)

                                mentorsAdaptor = MentorsListAdaptor(mentorsArrayList, userId!!,userName!!,this@HomeFragment)
                                topmentorRecyclerView.adapter = mentorsAdaptor
                                educationmentorRecyclerView.adapter = mentorsAdaptor
                                recentmentorsRecyclerView.adapter = mentorsAdaptor
                                mentorsAdaptor.notifyDataSetChanged()

                            }
                        } else {
                            Log.e("FetchData", "API call was not successful")
                        }
                    } catch (e: JSONException) {
                        e.printStackTrace()
                    }
                },
                { error ->
                    Log.e("FetchData", "Error occurred: ${error.message}")
                })

            Volley.newRequestQueue(requireContext()).add(jsonObjectRequest)
        } else {
            // Fetch data from SQLite database
            val rows = dbHelper.read(null, null, "mentorID", "name", "description", "status", "profilepic")
            for (row in rows) {
                val id = row["mentorID"]
                val name = row["name"]
                val description = row["description"]
                val status = row["status"]
                val profilepic = row["profilepic"]

                Log.d("API Response", "Name SQLite: $name")
                Log.d("API Response", "ID SQLite: $id")
                Log.d("API Response", "Description SQLite: $description")
                Log.d("API Response", "Status SQLite: $status")
                Log.d("API Response", "ProfilePic SQLite: $profilepic")

                val mentor = HomeMentorsClass(id.toString(), name.toString(), description.toString(), status.toString(), "heart", "1200 $")

                mentorsArrayList.add(mentor)

                mentorsAdaptor = MentorsListAdaptor(mentorsArrayList, userId!!,userName!!,this@HomeFragment)
                topmentorRecyclerView.adapter = mentorsAdaptor
                educationmentorRecyclerView.adapter = mentorsAdaptor
                recentmentorsRecyclerView.adapter = mentorsAdaptor
                mentorsAdaptor.notifyDataSetChanged()

                // Do something with the data...
            }
        }




    }

    fun isInternetAvailable(context: Context): Boolean {
        val connectivityManager = context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        val networkInfo = connectivityManager.activeNetworkInfo
        return networkInfo != null && networkInfo.isConnected
    }
}
