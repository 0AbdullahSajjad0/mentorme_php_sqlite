package com.abdullahsajjad.i212477

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import com.google.android.material.bottomnavigation.BottomNavigationView

class navigation : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_navigation)

        // Create a fragment instance and set the arguments
        val homeFragment = HomeFragment()
        val searchFragment = SearchFragment()
        val addFragment = AddFragment()
        val chatsFragment = ChatsFragment()
        val profFragment = ProfileFragment()

        val name = intent.getStringExtra("name")
        Log.d("Debug", "User Name Received in navigation is: $name")
        val id = intent.getStringExtra("id")
        Log.d("Debug", "User ID Received in navigation is: $id")

        if (name != null) {
            Log.d("Debug", "User Name Received in navigation is: $name")

            val bundle = Bundle()
            bundle.putString("userName", name)
            bundle.putString("id", id)

            val searchbundle = Bundle()
            searchbundle.putString("userName", name)
            searchbundle.putString("id", id)

            val addbundle = Bundle()
            addbundle.putString("userName", name)
            addbundle.putString("id", id)

            val chatsbundle = Bundle()
            chatsbundle.putString("userName", name)
            chatsbundle.putString("id", id)

            val profbundle = Bundle()
            profbundle.putString("userName", name)
            profbundle.putString("id", id)

            homeFragment.arguments = bundle
            searchFragment.arguments = searchbundle
            addFragment.arguments = addbundle
            chatsFragment.arguments = chatsbundle
            profFragment.arguments = profbundle
        }
        else {
            Log.e("Debug", "User object is null")
        }

        supportFragmentManager.beginTransaction()
            .replace(R.id.navfragment, homeFragment)
            .commit()

        val bottomNavigationView = findViewById<BottomNavigationView>(R.id.navigationbar)

        bottomNavigationView.setOnNavigationItemSelectedListener { item ->
            when (item.itemId) {
                R.id.home -> {
                    supportFragmentManager.beginTransaction()
                        .replace(R.id.navfragment, homeFragment)
                        .commit()
                    return@setOnNavigationItemSelectedListener true
                }
                R.id.search -> {
                    supportFragmentManager.beginTransaction()
                        .replace(R.id.navfragment, searchFragment)
                        .commit()
                    return@setOnNavigationItemSelectedListener true
                }
                R.id.addmentor -> {
                    supportFragmentManager.beginTransaction()
                        .replace(R.id.navfragment, addFragment)
                        .commit()
                    return@setOnNavigationItemSelectedListener true
                }
                R.id.chats -> {
                    supportFragmentManager.beginTransaction()
                        .replace(R.id.navfragment, chatsFragment)
                        .commit()
                    return@setOnNavigationItemSelectedListener true
                }
                R.id.profile -> {
                    supportFragmentManager.beginTransaction()
                        .replace(R.id.navfragment, profFragment)
                        .commit()
                    return@setOnNavigationItemSelectedListener true
                }
                else -> false
            }
        }

    }
}