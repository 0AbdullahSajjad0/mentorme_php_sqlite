package com.abdullahsajjad.i212477

import android.app.AlertDialog
import android.content.Context
import android.content.Intent
import android.media.MediaPlayer
import android.os.ParcelFileDescriptor
import android.text.InputType
import android.util.Base64
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.EditorInfo
import android.view.inputmethod.InputMethodManager
import android.widget.EditText
import android.widget.ImageView
import android.widget.RelativeLayout
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
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
import java.io.ByteArrayInputStream
import java.io.File
import java.io.FileInputStream
import java.io.FileOutputStream
import java.io.IOException
import java.text.SimpleDateFormat
import java.util.Date
import java.util.concurrent.TimeUnit

class MyAdaptor(private val newsList: ArrayList<news>) : RecyclerView.Adapter<MyAdaptor.MyViewHolder>() {


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val itemView = LayoutInflater.from(parent.context).inflate(R.layout.recentmentorssearch,
        parent,false)
        return MyViewHolder(itemView)
    }

    override fun getItemCount(): Int {
        return newsList.size
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        val currentItem = newsList[position]
        holder.titleImage.setImageResource(currentItem.titleImage)
        holder.tvHeading.text = currentItem.heading
    }


    class MyViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView)
    {
        val titleImage : ImageView = itemView.findViewById(R.id.title_image)
        val tvHeading : TextView = itemView.findViewById(R.id.tvHeading)
    }

}


class MyAdaptor2(private val resultsList: ArrayList<HomeMentorsClass>, private val userId: String ,private val userName: String ,private val itemClickListener: SearchResults) : RecyclerView.Adapter<MyAdaptor2.MyViewHolder>() {


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val itemView = LayoutInflater.from(parent.context).inflate(R.layout.searchresultslist,
            parent,false)
        return MyViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        val currentItem = resultsList[position]
        holder.name.text = currentItem.name
        holder.description.text = currentItem.profession
        holder.status.text = currentItem.status
        holder.cost.text = currentItem.cost
        Log.d("MyAdaptor2", "Current Item: $currentItem")
        if(currentItem.image == "heart") {
            Log.d("MyAdaptor2", "Heart image found")
            holder.image.setImageResource(R.drawable.greyheart)
        }
        else if(currentItem.image != "default") {
            Log.d("MyAdaptor2", "Heart image not found")
            Picasso.get().load(currentItem.image).into(holder.image)
        }

        holder.itemView.setOnClickListener {
            val MentorID = currentItem.MentorId
            Log.d("MentorsListAdaptor", "Mentor ID clicked: $MentorID")
            val context = holder.itemView.context
            val intent = Intent(context, mentordescription::class.java)
            intent.putExtra("MentorID", MentorID) // Add MentorID as an extra to the intent
            intent.putExtra("UserID", userId)
            Log.d("MentorsListAdaptor", "User Name Being Send to mentor Description: $userName")
            intent.putExtra("UserName", userName)
            context.startActivity(intent)
        }
    }

    override fun getItemCount(): Int {
        return resultsList.size
    }

    class MyViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView)
    {
        val image : ImageView = itemView.findViewById(R.id.fav_emoji)
        val name : TextView = itemView.findViewById(R.id.resultname)
        val description : TextView = itemView.findViewById(R.id.prof)
        val status : TextView = itemView.findViewById(R.id.resultstatus)
        val cost : TextView = itemView.findViewById(R.id.resultprice)
    }

}


class CommunityDisplayAdaptor(private val communityList: ArrayList<CommunityDisplayClass>, private val itemClickListener: OnItemClickListener) : RecyclerView.Adapter<CommunityDisplayAdaptor.MyViewHolder>() {


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val itemView = LayoutInflater.from(parent.context).inflate(R.layout.communitydisplay,
            parent,false)
        return MyViewHolder(itemView)
    }

    interface OnItemClickListener {
        fun onCommunityClick(position: Int)
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        val currentItem = communityList[position]
        holder.image.setImageResource(currentItem.titleImage)

        holder.itemView.setOnClickListener {
            itemClickListener.onCommunityClick(position)
        }
    }

    override fun getItemCount(): Int {
        return communityList.size
    }

    class MyViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView)
    {
        val image : ImageView = itemView.findViewById(R.id.communitypic)
    }

}




class MentorsListAdaptor(private val mentorsList: ArrayList<HomeMentorsClass>, private val userId: String ,private val userName: String ,private val itemClickListener: HomeFragment) : RecyclerView.Adapter<MentorsListAdaptor.MyViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val itemView = LayoutInflater.from(parent.context).inflate(R.layout.homepagementors,
            parent,false)
        return MyViewHolder(itemView)
    }

    interface OnItemClickListener {
        fun onItemClick(position: Int)
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        val currentItem = mentorsList[position]
        if(currentItem.image == "heart") {
            holder.favorited.setImageResource(R.drawable.greyheart)
        }
        else if(currentItem.image != "default") {
            Picasso.get().load(currentItem.image).into(holder.favorited)
        }
        holder.mentorName.text = currentItem.name
        holder.mentorDescription.text = currentItem.profession
        holder.mentorStatus.text = currentItem.status
        holder.mentorCost.text = currentItem.cost


        holder.itemView.setOnClickListener {
            val MentorID = currentItem.MentorId
            Log.d("MentorsListAdaptor", "Mentor ID clicked: $MentorID")
            val context = holder.itemView.context
            val intent = Intent(context, mentordescription::class.java)
            intent.putExtra("MentorID", MentorID) // Add MentorID as an extra to the intent
            intent.putExtra("UserID", userId)
            Log.d("MentorsListAdaptor", "User Name Being Send to mentor Description: $userName")
            intent.putExtra("UserName", userName)
            context.startActivity(intent)
        }
    }

    override fun getItemCount(): Int {
        return mentorsList.size
    }

    class MyViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView)
    {
        val mentorName : TextView = itemView.findViewById(R.id.mentorname)
        val mentorDescription : TextView = itemView.findViewById(R.id.mentorProfession)
        val mentorStatus : TextView = itemView.findViewById(R.id.mentorStatus)
        val favorited : ImageView = itemView.findViewById(R.id.favouriteIcon)
        val mentorCost : TextView = itemView.findViewById(R.id.mentorPrice)
    }

}

class BookingsListAdaptor(private val bookingsList: ArrayList<Bookings>, private val itemClickListener: ViewBookedSessions) : RecyclerView.Adapter<BookingsListAdaptor.MyViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val itemView = LayoutInflater.from(parent.context).inflate(R.layout.bookings,
            parent,false)
        return MyViewHolder(itemView)
    }

    interface OnItemClickListener {
        fun onItemClick(position: Int)
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        val currentItem = bookingsList[position]

        if(currentItem.profilePic == "default") {
            holder.dp.setImageResource(R.drawable.picturedummy4)
        }
        else {
            Picasso.get().load(currentItem.profilePic).into(holder.dp)
        }


        holder.mentorName.text = currentItem.name
        holder.bookingDate.text = currentItem.date
        holder.time.text = currentItem.time

        holder.itemView.setOnClickListener {
            // Handle item click event
        }
    }

    override fun getItemCount(): Int {
        return bookingsList.size
    }

    class MyViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView)
    {
        val mentorName : TextView = itemView.findViewById(R.id.name)
        val bookingDate : TextView = itemView.findViewById(R.id.date)
        val time : TextView = itemView.findViewById(R.id.time)
        val dp : ImageView = itemView.findViewById(R.id.dppicture)
    }

}



class ReviewsListAdaptor(private val reviewsList: ArrayList<Reviews>, private val itemClickListener: ProfileFragment) : RecyclerView.Adapter<ReviewsListAdaptor.MyViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val itemView = LayoutInflater.from(parent.context).inflate(R.layout.myreviews,
            parent,false)
        return MyViewHolder(itemView)
    }

    interface OnItemClickListener {
        fun onItemClick(position: Int)
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        val currentItem = reviewsList[position]
        holder.mentorName.text = currentItem.mentorname
        holder.reviewFeedback.text = currentItem.feedback

        when (currentItem.rating) {
            1 -> {
                holder.oneStar.setImageResource(R.drawable.baseline_star_rate_24)
                holder.twoStar.setImageResource(R.drawable.star_outline_yellow)
                holder.threeStar.setImageResource(R.drawable.star_outline_yellow)
                holder.fourStar.setImageResource(R.drawable.star_outline_yellow)
                holder.fiveStar.setImageResource(R.drawable.star_outline_yellow)
            }
            2 -> {
                holder.oneStar.setImageResource(R.drawable.baseline_star_rate_24)
                holder.twoStar.setImageResource(R.drawable.baseline_star_rate_24)
                holder.threeStar.setImageResource(R.drawable.star_outline_yellow)
                holder.fourStar.setImageResource(R.drawable.star_outline_yellow)
                holder.fiveStar.setImageResource(R.drawable.star_outline_yellow)
            }
            3 -> {
                holder.oneStar.setImageResource(R.drawable.baseline_star_rate_24)
                holder.twoStar.setImageResource(R.drawable.baseline_star_rate_24)
                holder.threeStar.setImageResource(R.drawable.baseline_star_rate_24)
                holder.fourStar.setImageResource(R.drawable.star_outline_yellow)
                holder.fiveStar.setImageResource(R.drawable.star_outline_yellow)
            }
            4 -> {
                holder.oneStar.setImageResource(R.drawable.baseline_star_rate_24)
                holder.twoStar.setImageResource(R.drawable.baseline_star_rate_24)
                holder.threeStar.setImageResource(R.drawable.baseline_star_rate_24)
                holder.fourStar.setImageResource(R.drawable.baseline_star_rate_24)
                holder.fiveStar.setImageResource(R.drawable.star_outline_yellow)
            }
            5 -> {
                holder.oneStar.setImageResource(R.drawable.baseline_star_rate_24)
                holder.twoStar.setImageResource(R.drawable.baseline_star_rate_24)
                holder.threeStar.setImageResource(R.drawable.baseline_star_rate_24)
                holder.fourStar.setImageResource(R.drawable.baseline_star_rate_24)
                holder.fiveStar.setImageResource(R.drawable.baseline_star_rate_24)
            }
        }

        holder.itemView.setOnClickListener {
            // Handle item click event
        }
    }

    override fun getItemCount(): Int {
        return reviewsList.size
    }

    class MyViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView)
    {
        val mentorName : TextView = itemView.findViewById(R.id.reviewName)
        val reviewFeedback : TextView = itemView.findViewById(R.id.reviewFeedback)
        val oneStar : ImageView = itemView.findViewById(R.id.onestarrating)
        val twoStar : ImageView = itemView.findViewById(R.id.twostarrating)
        val threeStar : ImageView = itemView.findViewById(R.id.threestarrating)
        val fourStar : ImageView = itemView.findViewById(R.id.fourstarrating)
        val fiveStar : ImageView = itemView.findViewById(R.id.fivestarrating)

    }

}

fun getCurrentTime(): String {
    val currentTime = System.currentTimeMillis()
    val dateFormat = SimpleDateFormat("HH:mm")
    return dateFormat.format(Date(currentTime))
}

class ChatsDisplayAdaptor(private val chatsList: ArrayList<ChatsDisplayClass>, private val senderID: String ,private val itemClickListener: OnItemClickListener) : RecyclerView.Adapter<ChatsDisplayAdaptor.MyViewHolder>() {


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val itemView = LayoutInflater.from(parent.context).inflate(R.layout.chatsdisplay,
            parent,false)
        return MyViewHolder(itemView)
    }

    interface OnItemClickListener {
        fun onItemClick(position: Int, recieverId: String, recieverName: String)
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        val currentItem = chatsList[position]

        holder.yourName.text = currentItem.name
        holder.yourMessages.text = currentItem.messages
        holder.yourMessages.setTextColor(ContextCompat.getColor(holder.itemView.context, currentItem.textColor))
        if(currentItem.titleImage == "default") {
            holder.image.setImageResource(R.drawable.picturedummy4)
        }
        else {
            Picasso.get().load(currentItem.titleImage).into(holder.image)
        }

        val recieverId = currentItem.userId

        holder.itemView.setOnClickListener {
            itemClickListener.onItemClick(position, currentItem.userId, currentItem.name)
        }
    }

    override fun getItemCount(): Int {
        return chatsList.size
    }

    class MyViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView)
    {
        val image : ImageView = itemView.findViewById(R.id.yourpic)
        val yourName : TextView = itemView.findViewById(R.id.personname)
        val yourMessages : TextView = itemView.findViewById(R.id.ifmessage)
    }

}


class DisplayMessagesAdaptor(private val context: Context, private val chatsList: ArrayList<Message>,
                             private val itemClickListener: OpenChatFragment, private val senderID: String,
                             private val chatID: String) : RecyclerView.Adapter<DisplayMessagesAdaptor.MyViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val itemView = LayoutInflater.from(parent.context).inflate(
            if (viewType == MESSAGE_TYPE_SENT) R.layout.sendmessage else R.layout.recievemessage,
            parent,false)
        return MyViewHolder(itemView)
    }

    override fun getItemViewType(position: Int): Int {
        val message = chatsList[position]
        return if (message.writtenBy == senderID) {
            MESSAGE_TYPE_SENT
        } else {
            MESSAGE_TYPE_RECEIVED
        }
    }

    interface OnItemClickListener {

        fun onItemClick(messageContent: String)
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        val message = chatsList[position]
        Log.d("Adapter", "Message: $message")
        holder.message.text = message.message
        var oldmessageContent = holder.message.text.toString()
        var messageContent = message.message

        holder.message.setOnClickListener(View.OnClickListener {
            // Perform your actions here when the EditText is clicked
            Log.d("Adapter", "Hello A")
            Log.d("Adapter", "Before editing: $messageContent")
            Log.d("Adapter", "SenderID: ${message.writtenBy}")

            if(message.writtenBy == senderID){

                val messageTime = SimpleDateFormat("HH:mm").parse(message.timestamp)

                // Get the current time and parse it to a Date object
                val currentTime = SimpleDateFormat("HH:mm").parse(getCurrentTime())

                // Calculate the difference in minutes
                val diff = currentTime.time - messageTime.time
                val diffMinutes = TimeUnit.MILLISECONDS.toMinutes(diff)

                Log.d("Adapter", "Time difference: $diffMinutes")

                if (diffMinutes <= 5 && diffMinutes >= -5) {
                    Log.d("Adapter", "SenderID: $senderID")
                    holder.message.setTextIsSelectable(true)
                    // Enable editing on click
                    holder.message.editableText
                    holder.message.inputType = InputType.TYPE_CLASS_TEXT
                    val inputMethodManager = context.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
                    inputMethodManager.showSoftInput(holder.message, InputMethodManager.SHOW_IMPLICIT)
                }
                else
                {
                    holder.message.setTextIsSelectable(false)
                }
            }
            else
            {
                holder.message.setTextIsSelectable(false)
            }

        })

        // Set an OnEditorActionListener to handle input completion (e.g., pressing Enter)
        holder.message.setOnEditorActionListener { _, actionId, _ ->
            holder.message.setTextIsSelectable(false)
            messageContent = holder.message.text.toString()

            // Perform your actions here when the Enter key is pressed
            var url = "http://192.168.56.1/mentorme/editmessage.php";

            val stringRequest = object : StringRequest(
                Request.Method.POST, url,
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
                    params["senderID"] = senderID
                    params["messageText"] = message.message
                    params["time"] = message.timestamp
                    params["chatID"] = chatID
                    params["newmessage"] = messageContent
                    return params
                }
            }
            // Add the request to the RequestQueue
            Volley.newRequestQueue(context).add(stringRequest)

            if (actionId == EditorInfo.IME_ACTION_DONE) {
                val inputMethodManager = context.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
                inputMethodManager.hideSoftInputFromWindow(holder.message.windowToken, 0)
                return@setOnEditorActionListener true
            }
            false
        }

        if(message.imageUrl == "" && message.file == "") {
            Log.d("Adapter", "There was no Image in this message")
            Log.d("Adapter", "And There was no file in this message")
            Log.d("Adapter", "Message: ${message.message}")
            holder.time.text = message.timestamp
            holder.image.visibility = View.GONE
        }
        else if(message.message == "" && message.imageUrl == "") {
            Log.d("Adapter", "There was Audio in this message")
            holder.time.text = message.timestamp
            holder.audioPlayButton.visibility = View.VISIBLE

        }
        else if(message.message == "" && message.file == ""){
            Log.d("Adapter", "There was Image in this message")
            holder.messageLayout.visibility = View.GONE
            holder.time.text = message.timestamp
            Picasso.get().load(message.imageUrl).into(holder.image)
        }

        holder.message.setOnLongClickListener {
            var url = "http://192.168.56.1/mentorme/deletemessage.php";
            Log.d("Adapter", "Long click event detected")
            // Show a confirmation dialog if necessary
            AlertDialog.Builder(context)
                .setTitle("Delete Item")
                .setMessage("Are you sure you want to delete this item?")
                .setPositiveButton("Yes") { _, _ ->
                    // Delete the item from the dataset
                    chatsList.removeAt(position)
                    // Notify adapter about item removal
                    val request: StringRequest = object : StringRequest(
                        Request.Method.POST,
                        url,
                        Response.Listener<String> { response ->
                            Log.d("API Response", response)
                        },
                        Response.ErrorListener { error ->
                            Log.e("API Error", "Error occurred: ${error.message}")
                        }
                    ) {
                        override fun getParams(): Map<String, String>? {
                            val data: MutableMap<String, String> = HashMap()

                            data["time"] = message.timestamp
                            data["messageText"] = holder.message.text.toString()

                            return data
                        }

                    }

                    notifyItemRemoved(position)
                }
                .setNegativeButton("No", null)
                .show()

            true // indicate that the long click event is consumed
        }

        holder.image.setOnLongClickListener {
            var url = "http://192.168.56.1/mentorme/deletemessage.php";
            Log.d("Adapter", "Long click event detected")
            Log.d("Adapter", "Time of message: ${message.timestamp}")
            Log.d("Adapter", "Image URL: ${message.imageUrl}")
            // Show a confirmation dialog if necessary
            AlertDialog.Builder(context)
                .setTitle("Delete Item")
                .setMessage("Are you sure you want to delete this item?")
                .setPositiveButton("Yes") { _, _ ->
                    // Delete the item from the dataset
                    chatsList.removeAt(position)
                    // Notify adapter about item removal
                    val request: StringRequest = object : StringRequest(
                        Request.Method.POST,
                        url,
                        Response.Listener<String> { response ->
                            Log.d("API Response", response)
                        },
                        Response.ErrorListener { error ->
                            Log.e("API Error", "Error occurred: ${error.message}")
                        }
                    ) {
                        override fun getParams(): Map<String, String>? {
                            val data: MutableMap<String, String> = HashMap()

                            data["time"] = message.timestamp
                            data["messageImage"] = message.imageUrl

                            return data
                        }

                    }

                    Volley.newRequestQueue(context).add(request)

                    notifyItemRemoved(position)
                }
                .setNegativeButton("No", null)
                .show()

            true // indicate that the long click event is consumed
        }

        holder.audioPlayButton.setOnClickListener {
            Log.d("Adapter", "Audio play button clicked")
            // Decode the Base64 string to a byte array
            val audioBytes = Base64.decode(message.file, Base64.DEFAULT)

            try {
                val tempFile = File.createTempFile("temp", null, context.cacheDir)
                tempFile.deleteOnExit()
                val fileOutputStream = FileOutputStream(tempFile)
                fileOutputStream.write(audioBytes)
                fileOutputStream.close()

                val parcelFileDescriptor = ParcelFileDescriptor.open(tempFile, ParcelFileDescriptor.MODE_READ_ONLY)
                val fileDescriptor = parcelFileDescriptor.fileDescriptor

                // Initialize the MediaPlayer and set the data source to the FileDescriptor
                val mediaPlayer = MediaPlayer().apply {
                    setDataSource(fileDescriptor)
                    prepare()
                    start()
                }
            } catch (e: IOException) {
                // Handle exception
                Log.e("AudioPlay", "Error playing audio", e)
            }
        }

        /*holder.itemView.setOnClickListener {
            itemClickListener.onItemClick(message.message)
        }*/

    }

    override fun getItemCount(): Int {
        return chatsList.size
    }

    class MyViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val message: TextView = itemView.findViewById(R.id.message)
        val time: TextView = itemView.findViewById(R.id.time)
        val image: ImageView = itemView.findViewById(R.id.image)
        val audioPlayButton: ImageView = itemView.findViewById(R.id.playvm)
        val messageLayout: RelativeLayout = itemView.findViewById(R.id.messageLayout)

    }

    companion object {
        const val MESSAGE_TYPE_SENT = 1
        const val MESSAGE_TYPE_RECEIVED = 2
    }

}
