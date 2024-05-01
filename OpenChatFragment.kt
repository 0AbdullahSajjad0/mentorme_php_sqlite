package com.abdullahsajjad.i212477

import android.app.AlertDialog
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.hardware.Camera
import android.media.MediaPlayer
import android.media.MediaRecorder
import android.os.Bundle
import android.os.Handler
import android.provider.MediaStore
import android.util.Base64
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.android.volley.Request
import com.android.volley.Response
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.google.firebase.storage.FirebaseStorage
import com.squareup.picasso.Picasso
import org.json.JSONException
import org.json.JSONObject
import java.io.ByteArrayOutputStream
import java.io.File
import java.io.FileInputStream
import java.text.SimpleDateFormat
import java.util.ArrayList
import java.util.Date

class OpenChatFragment : Fragment(), DisplayMessagesAdaptor.OnItemClickListener {

    private lateinit var auth: FirebaseAuth
    private lateinit var database: FirebaseDatabase
    private lateinit var storage: FirebaseStorage
    private lateinit var messagesNodeRef: DatabaseReference
    private lateinit var dialog: AlertDialog

    private lateinit var messagesadaptor: DisplayMessagesAdaptor
    private lateinit var messagesrecyclerView: RecyclerView
    private lateinit var messagesArrayList: ArrayList<Message>

    private var addmsgurl: String? = null
    private var chaturl: String? = null
    private var imageurl: String? = null
    private var chatId: String? = null
    private var userId: String? = null
    private var userTalkingto: String? = null
    private var img: Bitmap? = null
    private lateinit var audioFile: File

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.activity_open_chat, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)


        addmsgurl = "http://192.168.56.1/mentorme/addmessage.php";
        chaturl = "http://192.168.56.1/mentorme/getmessages.php";
        imageurl = "http://192.168.56.1/mentorme/addimagemessage.php";

        chatId = arguments?.getString("chatId")
        userId = arguments?.getString("senderId")
        userTalkingto = arguments?.getString("recieverId")
        val userName = arguments?.getString("userName")
        var userTalkingtoName = arguments?.getString("recieverName")


        dialog = AlertDialog.Builder(view.context)
            .setMessage("Sending Picture...")
            .setCancelable(false)
            .create()

        database = FirebaseDatabase.getInstance()
        storage = FirebaseStorage.getInstance()
        auth = FirebaseAuth.getInstance()




        Log.d("OpenChatFragment", "I am talking to: $userTalkingto  $userTalkingtoName")

        val uploadPhoto: ImageView = view.findViewById(R.id.opencamera)
        val sendImg: ImageView = view.findViewById(R.id.opengallery)
        val usemic: ImageView = view.findViewById(R.id.usemic)
        val startAudioCall: ImageView = view.findViewById(R.id.callperson)
        val startVideoCall: ImageView = view.findViewById(R.id.videocallperson)
        val backButton: ImageView = view.findViewById(R.id.backtoChatsFromChat)
        val sendButton: ImageView = view.findViewById(R.id.sendmessage)
        val chatName: TextView = view.findViewById(R.id.chatpersonname)
        val messageWritten = view.findViewById<TextView>(R.id.writtenmessage)
        chatName.text = userTalkingtoName

        dataInitialize()

        val layoutManager2 = LinearLayoutManager(context)
        messagesrecyclerView = view.findViewById(R.id.completeChatRecycler)
        messagesrecyclerView.layoutManager = layoutManager2
        messagesrecyclerView.setHasFixedSize(true)

        backButton.setOnClickListener {
            val transaction = requireActivity().supportFragmentManager.beginTransaction()
            val fragment = ChatsFragment().apply {
                arguments = Bundle().apply {
                    putString("id", userId)
                    putString("userName", userName)
                }
            }
            transaction.replace(R.id.navfragment, fragment)
            transaction.addToBackStack(null)
            transaction.commit()
        }

        var selectimage = registerForActivityResult(ActivityResultContracts.GetContent())
        { it ->
            img = MediaStore.Images.Media.getBitmap(requireContext().contentResolver, it)

            Handler().postDelayed({
                var requestQueue = Volley.newRequestQueue(requireContext())

                var stringrequest = object : StringRequest(
                    Request.Method.POST,
                    imageurl,
                    Response.Listener {
                        Toast.makeText(requireContext(), it.toString(), Toast.LENGTH_LONG).show()
                        Log.e("response", it.toString())
                        var res = JSONObject(it)
                        var url = "http://192.168.56.1/" + res.get("url")


                        val message = Message("", url, "" , userId!!, getCurrentTime())
                        messagesArrayList.add(message)
                        messagesadaptor = DisplayMessagesAdaptor(requireContext(),messagesArrayList, this, userId!!, chatId!!)
                        messagesrecyclerView.adapter = messagesadaptor
                        messagesrecyclerView.scrollToPosition(messagesArrayList.size - 1)
                        messagesadaptor.notifyDataSetChanged()
                        messagesrecyclerView.scrollToPosition(messagesArrayList.size - 1)


                    },
                    Response.ErrorListener {
                        Toast.makeText(requireContext(), it.toString(), Toast.LENGTH_LONG).show()
                        Log.e("error", it.toString())

                    }
                ) {
                    override fun getParams(): MutableMap<String, String> {
                        var params = HashMap<String, String>()

                        params["u"] = "http://192.168.56.1"
                        params["senderID"] = userId.toString()
                        params["recieverID"] = userTalkingto.toString()
                        params["messageText"] = "NULL"
                        params["messageImage"] = bitmapToBase64(img!!)
                        params["messageFile"] = "NULL"
                        params["time"] = getCurrentTime()
                        params["chatID"] = chatId.toString()
                        return params
                    }

                }
                requestQueue.add(stringrequest)
            }, 2000)


        }

        sendImg.setOnClickListener {
            selectimage.launch("image/*")
        }

        sendButton.setOnClickListener {
            /*val message = Message(messageWritten.text.toString(), "" ,auth.currentUser!!.uid, getCurrentTime())
            val messageId = messagesNodeRef.push().key!!
            messagesNodeRef.child(messageId).setValue(message)
                .addOnCompleteListener {
                    Log.d("OpenChatFragment", "Message sent")
                    messagesArrayList.add(message)
                    messagesadaptor = DisplayMessagesAdaptor(requireContext(),messagesArrayList, this, chatId)
                    messagesrecyclerView.adapter = messagesadaptor
                    messagesrecyclerView.scrollToPosition(messagesArrayList.size - 1)
                    messagesadaptor.notifyDataSetChanged()
                    messagesrecyclerView.scrollToPosition(messagesArrayList.size - 1)
                    messageWritten.text = ""
                }*/

            val stringRequest = object : StringRequest(
                Request.Method.POST, addmsgurl,
                Response.Listener { response ->
                    // Handle successful response

                    val message = Message(messageWritten.text.toString(), "", "" , userId!!, getCurrentTime())

                    Log.d("OpenChatFragment", "Message sent")
                    messagesArrayList.add(message)
                    messagesadaptor = DisplayMessagesAdaptor(requireContext(),messagesArrayList, this, userId!!, chatId!!)
                    messagesrecyclerView.adapter = messagesadaptor
                    messagesrecyclerView.scrollToPosition(messagesArrayList.size - 1)
                    messagesadaptor.notifyDataSetChanged()
                    messagesrecyclerView.scrollToPosition(messagesArrayList.size - 1)
                    messageWritten.text = ""

                    Log.d("API Response", response)
                },
                Response.ErrorListener { error ->
                    // Handle error
                    Log.e("API Error", "Error occurred: ${error.message}")
                }) {
                override fun getParams(): MutableMap<String, String> {
                    val params = HashMap<String, String>()
                    params["senderID"] = userId.toString()
                    params["recieverID"] = userTalkingto.toString()
                    params["messageText"] = messageWritten.text.toString()
                    params["messageImage"] = "NULL"
                    params["messageFile"] = "NULL"
                    params["time"] = getCurrentTime()
                    params["chatID"] = chatId.toString()
                    return params
                }
            }
            // Add the request to the RequestQueue
            Volley.newRequestQueue(requireContext()).add(stringRequest)



        }

        val audioRecorder = MediaRecorder().apply {
            setAudioSource(MediaRecorder.AudioSource.MIC)
            setOutputFormat(MediaRecorder.OutputFormat.THREE_GPP)
            setAudioEncoder(MediaRecorder.AudioEncoder.AMR_NB)
        }

        var isRecording = false
        audioFile = File.createTempFile("audio", null, requireContext().cacheDir)

        usemic.setOnTouchListener { _, event ->
            when (event.action) {
                MotionEvent.ACTION_DOWN -> {
                    // Start recording
                    audioRecorder.setOutputFile(audioFile.absolutePath)
                    audioRecorder.prepare()
                    audioRecorder.start()
                    isRecording = true
                }
                MotionEvent.ACTION_UP -> {
                    // Stop recording
                    if (isRecording) {
                        audioRecorder.stop()
                        audioRecorder.reset()
                        isRecording = false
                    }

                    // Convert the audio file to a Base64 string
                    val audioContent = audioFile.readBytes()
                    val audioBase64 = Base64.encodeToString(audioContent, Base64.DEFAULT)

                    // Create a new Message object with the audioBase64 string
                    val message = Message("", "", audioBase64, userId!!, getCurrentTime())

                    // Add the new message to the messagesArrayList
                    messagesArrayList.add(message)

                    // Notify the adapter that the data set has changed
                    messagesadaptor.notifyDataSetChanged()

                    // Scroll to the position of the new message
                    messagesrecyclerView.scrollToPosition(messagesArrayList.size - 1)

                    val mediaPlayer = MediaPlayer().apply {
                        setOnCompletionListener {
                            // Delete the temporary audio file after the MediaPlayer has finished using it
                            release()

                            audioFile.delete()
                        }
                    }

                    // Ensure the file exists and is readable
                    if (audioFile.exists() && audioFile.canRead()) {
                        val fis = FileInputStream(audioFile)
                        mediaPlayer.setDataSource(fis.fd)
                        fis.close()

                        // Prepare and start the MediaPlayer
                        mediaPlayer.prepare()
                        mediaPlayer.start()
                    } else {
                        // Handle error when file does not exist or is not readable
                        Log.e("AudioPlay", "File does not exist or is not readable")
                    }

                }
            }
            true
        }




        uploadPhoto.setOnClickListener {
            startActivity(
                Intent(requireContext(), TakePicture::class.java)
            );
        }

        startAudioCall.setOnClickListener {
            startActivity(
                Intent(requireContext(), CallPerson::class.java)
            );
        }

        startVideoCall.setOnClickListener {
            /*startActivity(
                Intent(requireContext(), VideoCallPerson::class.java)
            );*/
        }

    }

    fun bitmapToBase64(dp: Bitmap): String {
        var stream = ByteArrayOutputStream()
        dp.compress(Bitmap.CompressFormat.JPEG, 100, stream)
        stream.toByteArray()
        return Base64.encodeToString(stream.toByteArray(), Base64.DEFAULT).toString()

    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        /*if(requestCode == 0 && resultCode == -1 && data != null) {

            val currentUserId = auth.currentUser!!.uid.toString()
            val userTalkingTo = userTalkingto // Assuming User A's ID

            // Sort user IDs alphabetically to ensure consistency
            val sortedUserIds = listOf(currentUserId, userTalkingTo).sorted()

            // Form the chat ID by concatenating sorted user IDs
            val chatId = "${sortedUserIds[0]}_${sortedUserIds[1]}"


            dialog.show()
            val uri = data.data
            val messageId = messagesNodeRef.push().key!!
            val ref = storage.reference.child("UserChats/MessageImages/${messageId}")
            ref.putFile(uri!!).addOnSuccessListener {
                dialog.dismiss()
                val message = Message("", messageId ,auth.currentUser!!.uid, getCurrentTime())
                messagesNodeRef.child(messageId).setValue(message)
                    .addOnCompleteListener {
                        Log.d("OpenChatFragment", "Message sent")
                        messagesArrayList.add(message)
                        messagesadaptor = DisplayMessagesAdaptor(requireContext(),messagesArrayList, this, chatId)
                        messagesrecyclerView.adapter = messagesadaptor
                        messagesrecyclerView.scrollToPosition(messagesArrayList.size - 1)
                        messagesadaptor.notifyDataSetChanged()
                        messagesrecyclerView.scrollToPosition(messagesArrayList.size - 1)
                    }
            }
        }*/
    }

    private fun dataInitialize(){
        messagesArrayList = arrayListOf<Message>()

        val stringRequest = object : StringRequest(
            Method.POST, chaturl,
            Response.Listener<String> { response ->
                try {
                    val jsonResponse = JSONObject(response)
                    val success = jsonResponse.getInt("success")
                    if (success == 1) {
                        val bookingsArray = jsonResponse.getJSONArray("messages")
                        for (i in 0 until bookingsArray.length()) {
                            val messages = bookingsArray.getJSONObject(i)
                            val messageID = messages.getString("messageID")
                            val senderID = messages.getString("senderID")
                            val recieverID = messages.getString("recieverID")
                            val messageText = messages.getString("messageText")
                            val messageImage = messages.getString("messageImage")
                            val messageFile = messages.getString("messageFile")
                            val time = messages.getString("time")

                            Log.d("OpenChatFragment", "Message: $messages")
                            Log.d("OpenChatFragment", "Message Image: $messageImage")

                            if(messageImage!="NULL"){
                                val message = Message("", messageImage, "" , senderID, time)
                                messagesArrayList.add(message)
                            }
                            else if(messageFile!="NULL"){

                            }
                            else{
                                Log.d("OpenChatFragment", "Message Text: $messageText")
                                val message = Message(messageText, "", "" , senderID, time)
                                messagesArrayList.add(message)
                            }

                            Log.d("OpenChatFragment", "Message sent")
                            messagesadaptor = DisplayMessagesAdaptor(requireContext(),messagesArrayList, this, userId!!, chatId!!)
                            messagesrecyclerView.adapter = messagesadaptor
                            messagesrecyclerView.scrollToPosition(messagesArrayList.size - 1)
                            messagesadaptor.notifyDataSetChanged()
                            messagesrecyclerView.scrollToPosition(messagesArrayList.size - 1)

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
                params["chatId"] = chatId.toString()
                return params
            }
        }

        Volley.newRequestQueue(requireContext()).add(stringRequest)

    }

    fun getCameraInstance(): Camera? {

        var check = checkCameraHardware(requireContext())

        if (check) {
            return try {
                Camera.open() // attempt to get a Camera instance
            } catch (e: Exception) {
                // Camera is not available (in use or does not exist)
                null // returns null if camera is unavailable
            }
        }
        return null
    }

    private fun checkCameraHardware(context: Context): Boolean {
        if (context.packageManager.hasSystemFeature(PackageManager.FEATURE_CAMERA_ANY)) {
            // this device has a camera
            return true
        } else {
            // no camera on this device
            return false
        }
    }

    fun getCurrentTime(): String {
        val currentTime = System.currentTimeMillis()
        val dateFormat = SimpleDateFormat("HH:mm")
        val timeString = dateFormat.format(Date(currentTime))
        return timeString
    }

    override fun onItemClick(messageContent: String) {
        Log.d("Adapter", "Hello C")
        // Enable editing on click
        //holder.message.inputType = InputType.TYPE_CLASS_TEXT
    }

}
