package com.abdullahsajjad.i212477

import android.app.AlertDialog
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Color
import android.graphics.drawable.BitmapDrawable
import android.graphics.drawable.Drawable
import android.net.Uri
import android.os.Bundle
import android.os.Handler
import android.provider.MediaStore
import android.util.Base64
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.FrameLayout
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.cardview.widget.CardView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.abdullahsajjad.i212477.databinding.ActivityProfileBinding
import com.android.volley.Request
import com.android.volley.Response
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.getValue
import com.google.firebase.storage.FirebaseStorage
import com.squareup.picasso.Picasso
import org.json.JSONException
import org.json.JSONObject
import java.io.ByteArrayOutputStream
import java.util.ArrayList


class ProfileFragment : Fragment() {

    private lateinit var auth: FirebaseAuth
    private lateinit var database: FirebaseDatabase
    private lateinit var storage: FirebaseStorage

    private lateinit var reviewsArrayList: ArrayList<Reviews>
    private lateinit var reviewsAdaptor: ReviewsListAdaptor
    private lateinit var reviewsRecyclerView: RecyclerView
    private lateinit var profilename: TextView
    private lateinit var dp: ImageView
    private lateinit var profileCoverImage: ImageView
    private lateinit var profilelocation: TextView
    private lateinit var selectedImage: Uri
    private var img: Bitmap? = null
    private var name: String? = null
    private var userId: String? = null
    private var profpic: String? = null
    private var finduserURL: String? = null
    private var reviewsURL: String? = null

    private lateinit var dialog: AlertDialog
    private lateinit var dialog2: AlertDialog

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.activity_profile, container, false)


    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val coverURL = "http://192.168.56.1/mentorme/uploadcover.php";
        val url = "http://192.168.56.1/mentorme/uploaddp.php";
        finduserURL = "http://192.168.56.1/mentorme/finduser.php"
        reviewsURL = "http://192.168.56.1/mentorme/allreviews.php"

        reviewsArrayList = ArrayList<Reviews>()

        userId = arguments?.getString("id")
        userDataInitialise()

        profilename = view.findViewById(R.id.profilename)
        profilelocation = view.findViewById(R.id.profilelocation)
        profileCoverImage = view.findViewById(R.id.backgroundimageofprofile)
        dp = view.findViewById(R.id.dppicture)

        var back = view.findViewById<ImageView>(R.id.backtonavfromprof)
        val editcover = view.findViewById<CardView>(R.id.editCover)

        dialog = AlertDialog.Builder(view.context)
            .setMessage("Updating Profile Picture...")
            .setCancelable(false)
            .create()

        dialog2 = AlertDialog.Builder(view.context)
            .setMessage("Updating Cover Picture...")
            .setCancelable(false)
            .create()


        var selectimage = registerForActivityResult(ActivityResultContracts.GetContent())
        { it ->
            img = MediaStore.Images.Media.getBitmap(requireContext().contentResolver, it)

            Handler().postDelayed({
                var requestQueue = Volley.newRequestQueue(requireContext())

                var stringrequest = object : StringRequest(
                    Request.Method.POST,
                    coverURL,
                    Response.Listener {
                        Toast.makeText(requireContext(), it.toString(), Toast.LENGTH_LONG).show()
                        Log.e("response", it.toString())
                        var res = JSONObject(it)
                        var url = "http://192.168.56.1/" + res.get("url")
                        Picasso.get().load(url).into(profileCoverImage)
                    },
                    Response.ErrorListener {
                        Toast.makeText(requireContext(), it.toString(), Toast.LENGTH_LONG).show()
                        Log.e("error", it.toString())

                    }
                ) {
                    override fun getParams(): MutableMap<String, String>? {
                        var params = HashMap<String, String>()
//                    params.put("name",name.text.toString())
//                    params.put("phone",phone.text.toString())
//                    params.put("email",email.text.toString())
                        params["u"] = "http://192.168.56.1"
                        params["id"] = userId.toString()
                        params["name"] = name.toString()
                        params["image"] = bitmapToBase64(img!!)
                        return params
                    }

                }
                requestQueue.add(stringrequest)
            }, 2000)


        }

        editcover.setOnClickListener {
            selectimage.launch("image/*")
        }

        reviewsInitialize()
        val layoutManager = LinearLayoutManager(context, RecyclerView.HORIZONTAL, false)
        reviewsRecyclerView = view.findViewById(R.id.reviewsRecycle)
        reviewsRecyclerView.layoutManager = layoutManager
        reviewsRecyclerView.setHasFixedSize(true)

        back.setOnClickListener {
            val context = view.context
            var intent = Intent(context, navigation::class.java)
            intent.putExtra("id", userId)
            intent.putExtra("name", name)
            context.startActivity(intent)
        }

        var edit = view.findViewById<CardView>(R.id.editProfile)
        edit.setOnClickListener {
            val context = view.context
            val intent = Intent(context, EditProfile::class.java)
            intent.putExtra("userName", name)
            intent.putExtra("id", userId)
            context.startActivity(intent)
        }

        var btn = view.findViewById<Button>(R.id.viewBookedSessions)

        btn.setBackgroundColor(Color.parseColor("#FFC107"))

        btn.setOnClickListener {
            val context = view.context
            var intent = Intent(context, ViewBookedSessions::class.java)
            intent.putExtra("id", userId)
            context.startActivity(intent)
        }

    }

    override fun onResume() {
        super.onResume()
        // Call your method to update data here
        Log.d("Debug", "On Resume called")
        userDataInitialise()
    }

    fun bitmapToBase64(dp: Bitmap): String {
        var stream = ByteArrayOutputStream()
        dp.compress(Bitmap.CompressFormat.JPEG, 100, stream)
        stream.toByteArray()
        return Base64.encodeToString(stream.toByteArray(), Base64.DEFAULT).toString()

    }

    private fun userDataInitialise() {
        val stringRequest = object : StringRequest(
            Method.POST, finduserURL,
            Response.Listener { response ->
                // Handle successful response
                if (response != null) {
                    Log.d("API Response", response)
                    try {
                        Log.d("API Response", "Response: $response")
                        val jsonResponse = JSONObject(response)
                        Log.d("API Response", "JSON Response: $jsonResponse")
                        name = jsonResponse.getString("name")
                        profpic = jsonResponse.getString("profilepic")
                        var profcover = jsonResponse.getString("profilecover")
                        var profloc = jsonResponse.getString("city")
                        // Start new activity here
                        Log.d("API Response", "Name: $name")
                        Log.d("API Response", "Profile Pic: $profpic")
                        Log.d("API Response", "Cover Pic: $profcover")
                        Log.d("API Response", "Location: $profloc")

                        profilename.text = name
                        profilelocation.text = profloc
                        if (profpic != "default") {
                            Picasso.get().load(profpic).into(dp)
                        }
                        if (profcover != "default") {
                            Picasso.get().load(profcover).into(profileCoverImage)
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
                params["id"] = userId.toString()
                return params
            }
        }
        // Add the request to the RequestQueue
        Volley.newRequestQueue(requireContext()).add(stringRequest)
    }

    private fun reviewsInitialize() {
        reviewsArrayList.clear()

        val stringRequest = object : StringRequest(
            Method.POST, reviewsURL,
            Response.Listener<String> { response ->
                try {
                    val jsonResponse = JSONObject(response)
                    val success = jsonResponse.getInt("success")
                    if (success == 1) {
                        val reviewsArray = jsonResponse.getJSONArray("reviews")
                        for (i in 0 until reviewsArray.length()) {
                            val review = reviewsArray.getJSONObject(i)
                            val reviewId = review.getString("reviewID")
                            val mentorId = review.getString("mentorID")
                            val mentorName = review.getString("mentorName")
                            val userID = review.getString("userID")
                            val feedback = review.getString("feedback")
                            val rating = review.getString("rating")

                            Log.d("FetchData", "Found Review ID: $reviewId")
                            Log.d("FetchData", "Mentor ID: $mentorId")

                            val reviewElement = Reviews(
                                reviewId,
                                mentorId,
                                mentorName,
                                userID,
                                feedback,
                                rating.toInt()
                            )

                            reviewsArrayList.add(reviewElement)

                            reviewsAdaptor =
                                ReviewsListAdaptor(reviewsArrayList, this@ProfileFragment)
                            reviewsRecyclerView.adapter = reviewsAdaptor
                            reviewsAdaptor.notifyDataSetChanged()

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

        Volley.newRequestQueue(requireContext()).add(stringRequest)


    }

}