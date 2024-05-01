package com.abdullahsajjad.i212477

import android.content.ContentValues
import android.content.Intent
import android.graphics.Color
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Spinner
import android.widget.ArrayAdapter
import android.widget.AdapterView
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import com.android.volley.Request
import com.android.volley.Response
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley
import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseAuthUserCollisionException
import com.google.firebase.auth.PhoneAuthCredential
import com.google.firebase.auth.PhoneAuthOptions
import com.google.firebase.auth.PhoneAuthProvider
import com.google.firebase.database.database
import org.json.JSONException
import org.json.JSONObject
import java.util.concurrent.TimeUnit

class registeration_screen : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_registeration_screen)

        val url="http://192.168.56.1/mentorme/signup.php";
        val signinurl="http://192.168.56.1/mentorme/signin.php";

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



        val name = findViewById<EditText>(R.id.fullname)
        val email = findViewById<EditText>(R.id.regemail)
        val pass = findViewById<EditText>(R.id.pass)
        val num = findViewById<EditText>(R.id.phoneNum)
        val spinnerct = findViewById<Spinner>(R.id.spinnercity)
        val spinnercnt = findViewById<Spinner>(R.id.spinnercountry)

        val cities = arrayOf(R.array.city_array)
        val countries = arrayOf(R.array.country_array)

        val adapter: ArrayAdapter<CharSequence> = ArrayAdapter.createFromResource(
            this,
            R.array.city_array,
            android.R.layout.simple_spinner_item
        )

        val adapter2: ArrayAdapter<CharSequence> = ArrayAdapter.createFromResource(
            this,
            R.array.country_array,
            android.R.layout.simple_spinner_item
        )

        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        adapter2.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        spinnerct.adapter = adapter
        spinnercnt.adapter = adapter2

        val registerbtn = findViewById<Button>(R.id.signupbtn)
        val lgnlink = findViewById<TextView>(R.id.loginlink)

        registerbtn.setBackgroundColor(Color.WHITE)

        registerbtn.setOnClickListener() {
            val Uname = name.text.toString()
            val Uemail = email.text.toString()
            val Unum = num.text.toString()
            val selectedcity = spinnerct.selectedItem.toString()
            val selectedcountry = spinnercnt.selectedItem.toString()
            val Upass = pass.text.toString()

            if(Uname.isNullOrEmpty()){
                name.error = "Please Enter Name"
            }
            else if(Uemail.isNullOrEmpty()){
                email.error = "Please Enter Email"
            }
            else if(Unum.isNullOrEmpty()){
                num.error = "Please Enter Phone Number"
            }
            else if(selectedcity.isNullOrEmpty() or (selectedcity == "Select City")){
                Toast.makeText(this@registeration_screen, "Please Select a City", Toast.LENGTH_SHORT).show()
            }
            else if(selectedcountry.isNullOrEmpty() or (selectedcountry == "Select Country")){
                Toast.makeText(this@registeration_screen, "Please Select a Country", Toast.LENGTH_SHORT).show()
            }
            else if(pass.text.isNullOrEmpty()){
                pass.error = "Please Enter Password"
            }
            else {
                val stringRequest = object : StringRequest(
                    Request.Method.POST, url,
                    Response.Listener { response ->
                        // Handle successful response
                        val stringRequest = object : StringRequest(
                            Method.POST, signinurl,
                            Response.Listener { response ->

                                val values = ContentValues().apply {
                                    put("name", Uname)
                                    put("email", Uemail)
                                    put("password", Upass)
                                    put("country", selectedcountry)
                                    put("city", selectedcity)
                                    put("phone", Unum)
                                    put("profilepic", "default")
                                    put("profilecover", "default")
                                    put("otp", 123456)
                                }
                                val id = dbHelper.insert(values)
                                Log.d("API Response", "id returned from sqlite insertion: $id")

                                val rows = dbHelper.read(null,null,"name", "email","password","country","city","phone","profilepic","profilecover","otp")
                                for (row in rows) {
                                    Log.d("API Response", "Row: $row")
                                    // Do something with the data...
                                }

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

                                        val intent = Intent(this, verfiyphone::class.java)
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

                        Log.d("API Response", response)
                    },
                    Response.ErrorListener { error ->
                        // Handle error
                        Log.e("API Error", "Error occurred: ${error.message}")
                    }) {
                    override fun getParams(): MutableMap<String, String> {
                        val params = HashMap<String, String>()
                        params["name"] = Uname
                        params["email"] = Uemail
                        params["password"] = pass.text.toString()
                        params["country"] = selectedcountry
                        params["city"] = selectedcity
                        params["phone"] = Unum
                        params["profilepic"] = "default"
                        return params
                    }
                }
                // Add the request to the RequestQueue
                Volley.newRequestQueue(this).add(stringRequest)





                var intent = Intent(this, verfiyphone::class.java)
                intent.putExtra("name", Uname)
                intent.putExtra("email", Uemail)
                startActivity(intent)
            }
        }
        lgnlink.setOnClickListener{
            startActivity(
                Intent(this,
                    login_screen::class.java)
            );
        }


    }
}