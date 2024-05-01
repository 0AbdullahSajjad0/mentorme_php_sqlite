package com.abdullahsajjad.i212477

import android.content.Intent
import android.graphics.Color
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
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
import org.json.JSONException
import org.json.JSONObject

class login_screen : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login_screen)

        val url="http://192.168.56.1/mentorme/signin.php";

        val columns = mapOf(
            "UserID" to "INTEGER PRIMARY KEY AUTOINCREMENT",
            "name" to "TEXT",
            "email" to "TEXT",
            "password" to "TEXT",
            "country" to "TEXT",
            "city" to "TEXT",
            "phone" to "TEXT",
            "profilepic" to "TEXT",
            "profilecover" to "TEXT",
            "otp" to "INTEGER"
        )
        val dbHelper = MySqliteHelper(this, "users", columns)

        val email = findViewById<EditText>(R.id.logemail)
        val pass = findViewById<EditText>(R.id.logpass)
        var btn = findViewById<TextView>(R.id.signup)
        var lgnbtn = findViewById<TextView>(R.id.lgnbtn)
        var forgotbtn = findViewById<TextView>(R.id.forgotpasslink)

        lgnbtn.setBackgroundColor(Color.WHITE)

        lgnbtn.setOnClickListener() {
            val Uemail = email.text.toString()
            val Upass = pass.text.toString()

            if(Uemail.isNullOrEmpty()){
                email.error = "Please Enter Email"
            }
            else if(Upass.isNullOrEmpty()){
                pass.error = "Please Enter Password"
            }
            else {

                val rows = dbHelper.read("email = ? AND password = ?", arrayOf(Uemail, Upass), "UserID", "name")
                for (row in rows) {
                    val id = row["UserID"]
                    val name = row["name"]

                    Log.d("API Response", "Name SQLite: $name")
                    Log.d("API Response", "ID SQLite: $id")

                    val intent = Intent(this@login_screen, navigation::class.java)
                    intent.putExtra("name", name)
                    intent.putExtra("id", id)
                    startActivity(intent)

                    // Do something with the data...
                }


                val stringRequest = object : StringRequest(
                    Method.POST, url,
                    Response.Listener { response ->
                        // Handle successful response
                        if (response != null) {
                            Log.d("API Response", response)
                            try {
                                Log.d("API Response", "Response: $response")
                                val jsonResponse = JSONObject(response)
                                Log.d("API Response", "JSON Response: $jsonResponse")
                                val name = jsonResponse.getString("name") // replace "name" with the actual key in your JSON response
                                val id = jsonResponse.getString("UserID")
                                // Start new activity here
                                Log.d("API Response", "Name: $name")
                                Log.d("API Response", "ID: $id")

                                val intent = Intent(this@login_screen, navigation::class.java)
                                intent.putExtra("name", name)
                                intent.putExtra("id", id)
                                startActivity(intent)
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
                        params["email"] = Uemail
                        params["password"] = Upass
                        return params
                    }
                }
                // Add the request to the RequestQueue
                Volley.newRequestQueue(this).add(stringRequest)
            }
        }

        btn.setOnClickListener{
            startActivity(
                Intent(this,
                    registeration_screen::class.java)
            );
        }

        forgotbtn.setOnClickListener{
            startActivity(
                Intent(this,
                    forgotpassword::class.java)
            );
        }

    }
}