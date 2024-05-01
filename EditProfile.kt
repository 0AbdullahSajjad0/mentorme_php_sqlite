package com.abdullahsajjad.i212477

import android.annotation.SuppressLint
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.Color
import android.graphics.drawable.BitmapDrawable
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.provider.MediaStore
import android.util.Base64
import android.util.Log
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.Spinner
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import com.android.volley.Request
import com.android.volley.Response
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley
import com.google.firebase.database.FirebaseDatabase
import com.squareup.picasso.Picasso
import org.json.JSONException
import org.json.JSONObject
import java.io.ByteArrayOutputStream


class EditProfile : AppCompatActivity() {
    @SuppressLint("MissingInflatedId")
    var finduserURL: String? = null
    var userId: String? = null
    var profpic: ImageView? = null

    var img: Bitmap?=null
    var imgURL: String?=null
    var pName: String?=null
    var pEmail: String?=null
    var pCountry: String?=null
    var pCity: String?=null
    var pPhone: String?=null
    var pProfilePic: String?=null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_edit_profile)

        finduserURL="http://192.168.56.1/mentorme/finduser.php"
        val dpURL="http://192.168.56.1/mentorme/uploaddp.php";
        val updateURL="http://192.168.56.1/mentorme/updateprofile.php";


        val userName = intent.getStringExtra("name")
        userId = intent.getStringExtra("id")
        profpic = findViewById(R.id.profpic)

        var name = findViewById<EditText>(R.id.updateName)
        var email = findViewById<EditText>(R.id.updateEmail)
        var pNum = findViewById<EditText>(R.id.updateNumber)
        val update = findViewById<Button>(R.id.updateprofbtn)
        val spinnerct = findViewById<Spinner>(R.id.EPspinnercity)
        val spinnercnt = findViewById<Spinner>(R.id.EPspinnercountry)


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

        update.setBackgroundColor(Color.parseColor("#0D5995"))
        userDataInitialise()

        var selectimage = registerForActivityResult(ActivityResultContracts.GetContent()) {
            img = MediaStore.Images.Media.getBitmap(contentResolver, it)
            profpic?.setImageBitmap(img)
        }

        profpic?.setOnClickListener {
            selectimage.launch("image/*")
        }

        update.setOnClickListener{

            if(name.text.toString().isNotEmpty()){
                pName = name.text.toString()
            }
            if(email.text.toString().isNotEmpty()){
                pEmail = email.text.toString()
                Log.d("API Response", "After checking if Email: $pEmail")
            }
            if(pNum.text.toString().isNotEmpty()){
                pPhone = pNum.text.toString()
                Log.d("API Response", "After checking if Phone: $pPhone")
            }
            if(spinnercnt.selectedItem.toString() != "Select Country"){
                pCountry = spinnercnt.selectedItem.toString()
            }
            if(spinnerct.selectedItem.toString() != "Select City"){
                pCity = spinnerct.selectedItem.toString()
            }

            var requestQueue = Volley.newRequestQueue(this)
            var stringrequest = object : StringRequest(
                Request.Method.POST,
                dpURL,
                Response.Listener {
                    Toast.makeText(this, it.toString(), Toast.LENGTH_LONG).show()
                    Log.e("response", it.toString())
                    var res = JSONObject(it)
                    imgURL = "http://192.168.56.1/" + res.get("url")
                    Log.d("URL", "url: $imgURL")
                    val stringRequest = object : StringRequest(
                        Request.Method.POST, updateURL,
                        Response.Listener { response ->
                            // Handle successful response
                            Log.d("API Response", response)
                        },
                        Response.ErrorListener { error ->
                            // Handle error
                            Log.e("API Error", "Error occurred: ${error.message}")
                        }) {
                        override fun getParams(): MutableMap<String, String> {
                            Log.d("API Response", "Name: $pName")
                            Log.d("API Response", "Email: $pEmail")
                            Log.d("API Response", "Country: $pCountry")
                            Log.d("API Response", "City: $pCity")
                            Log.d("API Response", "Phone: $pPhone")
                            Log.d("API Response", "Profile Pic In Updating: $imgURL")
                            val params = HashMap<String, String>()
                            params["id"] = userId.toString()
                            params["name"] = pName.toString()
                            params["email"] = pEmail.toString()
                            params["country"] = pCountry.toString()
                            params["city"] = pCity.toString()
                            params["phone"] = pPhone.toString()
                            params["profilepic"] = imgURL.toString()
                            return params
                        }
                    }
                    // Add the request to the RequestQueue
                    Volley.newRequestQueue(this).add(stringRequest)
                },
                Response.ErrorListener {
                    Toast.makeText(this, it.toString(), Toast.LENGTH_LONG).show()
                    Log.e("error", it.toString())
                }
            ) {
                override fun getParams(): MutableMap<String, String>? {
                    var params = HashMap<String, String>()
                    params["name"] = userName.toString()
                    params["image"] = bitmapToBase64(img!!)
                    return params
                }
            }
            requestQueue.add(stringrequest)

            Handler().postDelayed({
                finish()
            }, 2000)

        }

        val btn = findViewById<ImageView>(R.id.gobacktoproffromedit)
        btn.setOnClickListener{
            finish()
        }

    }



    fun bitmapToBase64(dp:Bitmap):String{
        var stream= ByteArrayOutputStream()
        dp.compress(Bitmap.CompressFormat.JPEG,100,stream)
        stream.toByteArray()
        return Base64.encodeToString(stream.toByteArray(), Base64.DEFAULT).toString()

    }

    private fun userDataInitialise(){
        Log.d("API Response", "About to find user in DB")
        val stringRequest = object : StringRequest(
            Method.POST, finduserURL,
            Response.Listener { response ->
                // Handle successful response
                if (response != null) {
                    Log.d("API Response", response)
                    try {
                        Log.d("API Response", "In edit prof Response: $response")
                        val jsonResponse = JSONObject(response)
                        Log.d("API Response", "In edit prof JSON Response: $jsonResponse")
                        pName = jsonResponse.getString("name")
                        pEmail = jsonResponse.getString("email")
                        pCountry = jsonResponse.getString("country")
                        pCity = jsonResponse.getString("city")
                        pPhone = jsonResponse.getString("phone")
                        pProfilePic = jsonResponse.getString("profilepic")

                        if(pProfilePic != "default"){
                            Picasso.get().load(pProfilePic).into(profpic)
                        }

                        // Start new activity here
                        Log.d("API Response", "Name: $pName")
                        Log.d("API Response", "Email: $pEmail")
                        Log.d("API Response", "Country: $pCountry")
                        Log.d("API Response", "City: $pCity")
                        Log.d("API Response", "Phone: $pPhone")
                        Log.d("API Response", "Profile Pic: $pProfilePic")

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
                params["id"] = userId.toString()
                return params
            }
        }
        // Add the request to the RequestQueue
        Volley.newRequestQueue(this).add(stringRequest)
    }
}