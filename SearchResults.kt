package com.abdullahsajjad.i212477

import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.FrameLayout
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.SearchView
import android.widget.Spinner
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.android.volley.Response
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.google.firebase.storage.FirebaseStorage
import org.json.JSONException
import org.json.JSONObject
import java.util.ArrayList

private lateinit var adaptor: MyAdaptor2
private lateinit var recyclerView: RecyclerView
private lateinit var newsArrayList: ArrayList<HomeMentorsClass>

private lateinit var auth: FirebaseAuth
private lateinit var database: FirebaseDatabase
private lateinit var storage: FirebaseStorage

class SearchResults : Fragment() {

    private var url: String? = null
    private var findmentorURL: String? = null
    private var query: String? = null
    private var userId: String? = null
    private var userName: String? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.activity_search_results, container, false)
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        url = "http://192.168.56.1/mentorme/searchmentors.php";
        findmentorURL = "http://192.168.56.1/mentorme/findmentor.php"
        query = arguments?.getString("query")
        userId = arguments?.getString("id")
        userName = arguments?.getString("userName")

        Log.d("SearchResults", "Query: $query")

        val backtohome = view.findViewById<ImageView>(R.id.backtohomeFromResults)
        database = FirebaseDatabase.getInstance()
        auth = FirebaseAuth.getInstance()

        val filterspinner = view.findViewById<Spinner>(R.id.filterid)
        val adapter: ArrayAdapter<CharSequence> = ArrayAdapter.createFromResource(
            view.context,
            R.array.options_array,
            android.R.layout.simple_spinner_item
        )

        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        filterspinner.adapter = adapter

        backtohome.setOnClickListener {

            val searchFragment = SearchFragment()
            val bundle = Bundle()
            bundle.putString("id", userId)
            bundle.putString("userName", userName)
            searchFragment.arguments = bundle

            val transaction = requireActivity().supportFragmentManager.beginTransaction()
            transaction.replace(R.id.navfragment, searchFragment)
            transaction.addToBackStack(null)
            transaction.commit()
        }

        //Populating the recycler list
        dataInitialize(query!!)
        val layoutManager = LinearLayoutManager(context)
        recyclerView = view.findViewById(R.id.resultlistview)
        recyclerView.layoutManager = layoutManager
        recyclerView.setHasFixedSize(true)

        //


    }


    private fun dataInitialize(input: String){
        newsArrayList = ArrayList<HomeMentorsClass>()

        /*val mentor = HomeMentorsClass(mentorId,mentorName!!, "Full Stack Developer", mentorAvailability!!, "default", mentorPrice)

        newsArrayList.add(mentor)
        //val mentorTitle = userSnapshot.child("mentorTitle").getValue(String::class.java)

        }

        adaptor = MyAdaptor2(newsArrayList, this@SearchResults)
        recyclerView.adapter = adaptor
        adaptor.notifyDataSetChanged()*/

        val stringRequest = object : StringRequest(
            Method.POST, url,
            Response.Listener<String> { response ->
                try {
                    val jsonResponse = JSONObject(response)
                    val success = jsonResponse.getInt("success")
                    if (success == 1) {
                        val contactsArray = jsonResponse.getJSONArray("mentors")
                        for (i in 0 until contactsArray.length()) {
                            val contact = contactsArray.getJSONObject(i)
                            val mentorId = contact.getString("mentorID")
                            val name = contact.getString("name")
                            val description = contact.getString("description")
                            val status = contact.getString("status")
                            val profilepic = contact.getString("profilepic")

                            val mentor = HomeMentorsClass(mentorId,name, "Full Stack Developer", status, "heart", "1200 $")

                            newsArrayList.add(mentor)

                            adaptor = MyAdaptor2(newsArrayList, userId!!,userName!!,this@SearchResults)
                            recyclerView.adapter = adaptor
                            adaptor.notifyDataSetChanged()

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
                params["input"] = query.toString()
                return params
            }
        }

        Volley.newRequestQueue(requireContext()).add(stringRequest)

    }
}
