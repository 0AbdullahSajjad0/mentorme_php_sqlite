package com.abdullahsajjad.i212477

import android.content.Intent
import android.graphics.Color
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.core.content.ContextCompat
import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseAuthUserCollisionException
import com.google.firebase.auth.PhoneAuthProvider
import com.google.firebase.database.database

class verfiyphone : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_verfiyphone)

        var url="http://127.0.0.1/mentorme/signup.php";

        val vbtn = findViewById<Button>(R.id.verifybtn)
        val backtoreg = findViewById<ImageView>(R.id.backtoregisterbtn)
        val obtn = findViewById<Button>(R.id.oneButton)
        val twobtn = findViewById<Button>(R.id.twoButton)
        val thrbtn = findViewById<Button>(R.id.threeButton)
        val fourbtn = findViewById<Button>(R.id.fourButton)
        val fivebtn = findViewById<Button>(R.id.fiveButton)
        val sixbtn = findViewById<Button>(R.id.sixButton)
        val sevbtn = findViewById<Button>(R.id.sevenButton)
        val ebtn = findViewById<Button>(R.id.eightButton)
        val nbtn = findViewById<Button>(R.id.nineButton)
        val zbtn = findViewById<Button>(R.id.zeroButton)
        val firstinput = findViewById<TextView>(R.id.inputA)
        val secondinput = findViewById<TextView>(R.id.inputB)
        val thirdinput = findViewById<TextView>(R.id.inputC)
        val fourthinput = findViewById<TextView>(R.id.inputD)
        val fifthinput = findViewById<TextView>(R.id.inputE)
        val sixthinput = findViewById<TextView>(R.id.inputF)
        val backspace = findViewById<ImageView>(R.id.backspace)

        val name = intent.getStringExtra("name")
        val id = intent.getStringExtra("id")
        var otpField = ""

        fun updateTextViews() {
            for ((index, char) in otpField.withIndex()) {
                when (index) {
                    0 -> firstinput.text = char.toString()
                    1 -> secondinput.text = char.toString()
                    2 -> thirdinput.text = char.toString()
                    3 -> fourthinput.text = char.toString()
                    4 -> fifthinput.text = char.toString()
                    5 -> sixthinput.text = char.toString()
                    // You can add more conditions as needed
                }
            }
        }

        obtn.setOnClickListener {
            if(otpField.length < 6)
            {
                otpField += '1'
                updateTextViews()
            }
        }
        twobtn.setOnClickListener {
            if(otpField.length < 6)
            {
                otpField += '2'
                updateTextViews()
            }
        }
        thrbtn.setOnClickListener {
            if(otpField.length < 6)
            {
                otpField += '3'
                updateTextViews()
            }
        }
        fourbtn.setOnClickListener {
            if(otpField.length < 6)
            {
                otpField += '4'
                updateTextViews()
            }
        }
        fivebtn.setOnClickListener {
            if(otpField.length < 6)
            {
                otpField += '5'
                updateTextViews()
            }
        }
        sixbtn.setOnClickListener {
            if(otpField.length < 6)
            {
                otpField += '6'
                updateTextViews()
            }
        }
        sevbtn.setOnClickListener {
            if(otpField.length < 6)
            {
                otpField += '7'
                updateTextViews()
            }
        }
        ebtn.setOnClickListener {
            if(otpField.length < 6)
            {
                otpField += '8'
                updateTextViews()
            }
        }
        nbtn.setOnClickListener {
            if(otpField.length < 6)
            {
                otpField += '9'
                updateTextViews()
            }
        }
        zbtn.setOnClickListener {
            if(otpField.length < 6)
            {
                otpField += '0'
                updateTextViews()
            }
        }
        backspace.setOnClickListener {
            if (otpField.isNotEmpty()) {
                otpField = otpField.substring(0, otpField.length - 1)
                if(otpField.isEmpty())
                    firstinput.text = ""
                if(otpField.length == 1)
                    secondinput.text = ""
                if(otpField.length == 2)
                    thirdinput.text = ""
                if(otpField.length == 3)
                    fourthinput.text = ""
                if(otpField.length == 4)
                    fifthinput.text = ""
                if(otpField.length == 5)
                    sixthinput.text = ""
                updateTextViews()
            }
        }

        backtoreg.setOnClickListener{
            startActivity(
                Intent(this,
                    registeration_screen::class.java)
            );
        }

        vbtn.setOnClickListener{
            if(otpField == "123456")
            {
                var intent = Intent(this, navigation::class.java)
                intent.putExtra("name", name)
                intent.putExtra("id", id)
                startActivity(intent)
            }
        }

        vbtn.setBackgroundColor(Color.WHITE)
        obtn.setBackgroundColor(Color.WHITE)
        twobtn.setBackgroundColor(Color.WHITE)
        thrbtn.setBackgroundColor(Color.WHITE)
        fourbtn.setBackgroundColor(Color.WHITE)
        fivebtn.setBackgroundColor(Color.WHITE)
        sixbtn.setBackgroundColor(Color.WHITE)
        sevbtn.setBackgroundColor(Color.WHITE)
        ebtn.setBackgroundColor(Color.WHITE)
        nbtn.setBackgroundColor(Color.WHITE)
        zbtn.setBackgroundColor(Color.WHITE)

    }

}