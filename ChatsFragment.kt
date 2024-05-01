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
import android.widget.ArrayAdapter
import android.widget.FrameLayout
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.Spinner
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
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
import com.google.firebase.storage.FirebaseStorage
import org.json.JSONException
import org.json.JSONObject
import java.util.ArrayList

class ChatsFragment : Fragment(), ChatsDisplayAdaptor.OnItemClickListener, CommunityDisplayAdaptor.OnItemClickListener {

    private lateinit var communityadaptor: CommunityDisplayAdaptor
    private lateinit var communityrecyclerView: RecyclerView
    private lateinit var comunityArrayList: ArrayList<CommunityDisplayClass>

    private lateinit var chatsadaptor: ChatsDisplayAdaptor
    private lateinit var chatsrecyclerView: RecyclerView
    private lateinit var chatsArrayList: ArrayList<ChatsDisplayClass>

    lateinit var communityImages: Array<Int>

    private var url: String? = null
    private var chaturl: String? = null
    private var userId: String? = null
    private var userName: String? = null
    private var recieverId: String? = null
    private var recieverName: String? = null


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.activity_chatspage, container, false)
    }

    override fun onCommunityClick(position: Int) {
        val transaction = requireActivity().supportFragmentManager.beginTransaction()
        transaction.replace(R.id.navfragment, OpenCommunityFragment())
        transaction.addToBackStack(null)
        transaction.commit()
    }

    override fun onItemClick(position: Int, recieverId: String, recieverName: String) {


        val stringRequest = object : StringRequest(
            Method.POST, chaturl,
            Response.Listener { response ->
                // Handle successful response
                if (response != null) {
                    Log.d("API Response", response)
                    try {
                        Log.d("API Response", "Response: $response")
                        val jsonResponse = JSONObject(response)
                        Log.d("API Response", "JSON Response: $jsonResponse")
                        val id = jsonResponse.getString("chatID")
                        // Start new activity here
                        Log.d("API Response", "ChatID: $id")

                        val transaction = requireActivity().supportFragmentManager.beginTransaction()
                        val fragment = OpenChatFragment().apply {
                            arguments = Bundle().apply {
                                putString("recieverId", recieverId)
                                putString("recieverName", recieverName)
                                putString("senderId", userId)
                                putString("userName", userName)
                                putString("chatId", id)
                            }
                        }
                        transaction.replace(R.id.navfragment, fragment)
                        transaction.addToBackStack(null)
                        transaction.commit()


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
                params["userID1"] = userId!!
                params["userID2"] = recieverId
                return params
            }
        }
        // Add the request to the RequestQueue
        Volley.newRequestQueue(requireContext()).add(stringRequest)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        url = "http://192.168.56.1/mentorme/allusers.php";
        chaturl = "http://192.168.56.1/mentorme/makechat.php";
        userId = arguments?.getString("id")
        userName = arguments?.getString("userName")

        var back = view.findViewById<ImageView>(R.id.backtonavfromchats)
        back.setOnClickListener{
            val context = view.context
            var intent = Intent(context, navigation::class.java)
            intent.putExtra("name", userName)
            intent.putExtra("id", userId)
            context.startActivity(intent)
        }

        //Populating the recycler list
        dataInitialize()

        val layoutManager = LinearLayoutManager(context, RecyclerView.HORIZONTAL, false)
        communityrecyclerView = view.findViewById(R.id.communityrecyclerView)
        communityrecyclerView.layoutManager = layoutManager
        communityrecyclerView.setHasFixedSize(true)
        communityadaptor = CommunityDisplayAdaptor(comunityArrayList, this)
        communityrecyclerView.adapter = communityadaptor

        val layoutManager2 = LinearLayoutManager(context)
        chatsrecyclerView = view.findViewById(R.id.messagesrecyclerView)
        chatsrecyclerView.layoutManager = layoutManager2
        chatsrecyclerView.setHasFixedSize(true)

    }

    private fun dataInitialize(){
        comunityArrayList = arrayListOf<CommunityDisplayClass>()
        chatsArrayList = arrayListOf<ChatsDisplayClass>()

        val jsonObjectRequest = JsonObjectRequest(
            Request.Method.GET, url, null,
            { response ->
                try {
                    val success = response.getInt("success")
                    if (success == 1) {
                        val contactsArray = response.getJSONArray("users")
                        for (i in 0 until contactsArray.length()) {
                            val contact = contactsArray.getJSONObject(i)
                            val id = contact.getString("UserID")
                            val name = contact.getString("name")
                            val email = contact.getString("email")
                            val profilepic = contact.getString("profilepic")

                            val messages = "No New Messages"
                            val color = R.color.grey
                            if(id!=userId){
                                val chat = ChatsDisplayClass(id,profilepic, name, messages, color)
                                Log.d("API", "Chat: $chat")
                                chatsArrayList.add(chat)

                                chatsadaptor = ChatsDisplayAdaptor(chatsArrayList, userId!!,this@ChatsFragment)
                                chatsrecyclerView.adapter = chatsadaptor
                                chatsadaptor.notifyDataSetChanged()
                            }

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

        communityImages = arrayOf(
            R.drawable.baseline_person_24,
            R.drawable.baseline_person_24,
            R.drawable.baseline_person_24,
            R.drawable.baseline_person_24,
            R.drawable.baseline_person_24
        )

        for (i in communityImages.indices){
            val n = CommunityDisplayClass(communityImages[i])
            comunityArrayList.add(n)
        }

    }

    fun isInternetAvailable(context: Context): Boolean {
        val connectivityManager = context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        val networkInfo = connectivityManager.activeNetworkInfo
        return networkInfo != null && networkInfo.isConnected
    }

}