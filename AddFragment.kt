package com.abdullahsajjad.i212477

import android.app.Activity
import android.app.AlertDialog
import android.content.ContentValues
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.Color
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
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.EditText
import android.widget.FrameLayout
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.Spinner
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import com.android.volley.Request
import com.android.volley.Response
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley
import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.database
import com.google.firebase.storage.FirebaseStorage
import org.json.JSONObject
import java.io.ByteArrayOutputStream

class AddFragment : Fragment() {

    var img: Bitmap?=null
    var imgURL: String?=null
    private var uploadedImage: ImageView? = null
    private lateinit var dialog: AlertDialog

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.activity_addmentor, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val URL="http://192.168.56.1/mentorme/addmentor.php";

        val columns = mapOf(
            "mentorID" to "INTEGER PRIMARY KEY AUTOINCREMENT",
            "name" to "TEXT",
            "description" to "TEXT",
            "status" to "TEXT",
            "profilepic" to "TEXT"
        )
        val dbHelper = MySqliteHelper(requireContext(), "mentors", columns)
        val db = dbHelper.writableDatabase

        try {
            db.execSQL(dbHelper.CREATE_TABLE)
        } catch (e: Exception) {
            // The table already exists. Handle the exception here.
            e.printStackTrace()
        }


        var back = view.findViewById<ImageView>(R.id.backtonavfromadd)
        val myButton: Button = view.findViewById(R.id.uploadbtn)
        val uploadPhoto: LinearLayout = view.findViewById(R.id.uploadmentorphoto)
        val uploadVideo: LinearLayout = view.findViewById(R.id.uploadmentorvid)
        val spinnerst = view.findViewById<Spinner>(R.id.spinnerstatus)
        val mentorName = view.findViewById<EditText>(R.id.mentorname)
        val mentordesc = view.findViewById<EditText>(R.id.mentordescription)
        uploadedImage = view.findViewById(R.id.uploadedmentorphoto)

        back.setOnClickListener{
            val context = view.context
            context.startActivity(
                Intent(context, navigation::class.java)
            )
        }

        var selectimage = registerForActivityResult(ActivityResultContracts.GetContent()) {
            img = MediaStore.Images.Media.getBitmap(requireContext().contentResolver, it)
            uploadedImage?.setImageBitmap(img)
        }

        uploadPhoto.setOnClickListener {
            selectimage.launch("image/*")
        }



        // Access the button by its ID


        val adapter: ArrayAdapter<CharSequence> = ArrayAdapter.createFromResource(
            view.context,
            R.array.status_array,
            android.R.layout.simple_spinner_item
        )

        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        spinnerst.adapter = adapter

        myButton.setBackgroundColor(Color.parseColor("#0D5995"))

        dialog = AlertDialog.Builder(requireContext())
            .setMessage("Uploading Picture for Profile...")
            .setCancelable(true)
            .create()

        myButton.setOnClickListener {





            var requestQueue = Volley.newRequestQueue(requireContext())
            var stringrequest = object : StringRequest(
                Request.Method.POST,
                URL,
                Response.Listener {
                    Toast.makeText(requireContext(), it.toString(), Toast.LENGTH_LONG).show()
                    Log.e("response", it.toString())
                    var res = JSONObject(it)
                    imgURL = "http://192.168.56.1/" + res.get("url")
                    Log.d("URL", "url: $imgURL")

                    val values = ContentValues().apply {
                        put("name", mentorName.text.toString())
                        put("description", mentordesc.text.toString())
                        put("status", spinnerst.selectedItem.toString())
                        put("profilepic", imgURL)
                    }

                    val id = dbHelper.insert(values)
                    Log.d("API Response", "id returned from sqlite insertion: $id")

                    val rows = dbHelper.read(null,null,"name", "description","status","profilepic")
                    for (row in rows) {
                        Log.d("API Response", "Row: $row")
                        // Do something with the data...
                    }


                },
                Response.ErrorListener {
                    Toast.makeText(requireContext(), it.toString(), Toast.LENGTH_LONG).show()
                    Log.e("error", it.toString())
                }
            ) {
                override fun getParams(): MutableMap<String, String>? {
                    var params = HashMap<String, String>()
                    params["u"] = "http://192.168.56.1"
                    params["name"] = mentorName.text.toString()
                    params["description"] = mentordesc.text.toString()
                    params["status"] = spinnerst.selectedItem.toString()
                    params["image"] = bitmapToBase64(img!!)
                    return params
                }
            }
            requestQueue.add(stringrequest)

            //change fragment to homefragment
            val fragment = HomeFragment()

            var b = Bundle()
            b.putString("id", arguments?.getString("id"))
            b.putString("userName", arguments?.getString("userName"))
            fragment.arguments = b

            val fragmentManager = requireActivity().supportFragmentManager
            val fragmentTransaction = fragmentManager.beginTransaction()
            fragmentTransaction.replace(R.id.addmentorlayout, fragment).commit()

        }

        uploadVideo.setOnClickListener {
            startActivity(
                Intent(requireContext(), TakeVideo::class.java)
            );
        }
    }

    fun bitmapToBase64(dp:Bitmap):String{
        var stream= ByteArrayOutputStream()
        dp.compress(Bitmap.CompressFormat.JPEG,100,stream)
        stream.toByteArray()
        return Base64.encodeToString(stream.toByteArray(), Base64.DEFAULT).toString()

    }
}
