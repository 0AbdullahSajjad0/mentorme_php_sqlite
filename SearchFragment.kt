package com.abdullahsajjad.i212477

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.content.Context
import android.graphics.Color
import android.util.Log
import android.widget.Button
import android.widget.FrameLayout
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.SearchView
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.FragmentTransaction
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import java.util.ArrayList




class SearchFragment : Fragment() {

    private var userId: String? = null
    private var userName: String? = null

    private lateinit var adaptor: MyAdaptor
    private lateinit var recyclerView: RecyclerView
    private lateinit var newsArrayList: ArrayList<news>

    private lateinit var imageId: Array<Int>
    private lateinit var heading: Array<String>
    private lateinit var News: Array<String>

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.activity_searchpage, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        userId = arguments?.getString("id")
        userName = arguments?.getString("userName")

        val backtohome = view.findViewById<ImageView>(R.id.backtohomeFromSearch)

        backtohome.setOnClickListener {
            val transaction = requireActivity().supportFragmentManager.beginTransaction()

            var homeFragment = HomeFragment()
            val bundle = Bundle()
            bundle.putString("id", userId)
            bundle.putString("userName", userName)
            homeFragment.arguments = bundle

            transaction.replace(R.id.navfragment, homeFragment)
            transaction.addToBackStack(null)
            transaction.commit()
        }

        //Populating the recycler list
        dataInitialize()
        val layoutManager = LinearLayoutManager(context)
        recyclerView = view.findViewById(R.id.recentsearchlist)
        recyclerView.layoutManager = layoutManager
        recyclerView.setHasFixedSize(true)
        adaptor = MyAdaptor(newsArrayList)
        recyclerView.adapter = adaptor


        //Search to go to search results
        val searchView = view.findViewById<SearchView>(R.id.searchbar)
        searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {
                if (query!=null) {
                    performSearch(query)
                    return true
                }
                    return false
            }

            override fun onQueryTextChange(newText: String?): Boolean {
                return true
            }

        })


    }

    private fun performSearch(query: String) {
        //Log.d("Debug", "Search query is: $query")

        var searchResults = SearchResults()
        val args = Bundle()
        args.putString("query", query)
        args.putString("id", userId)
        args.putString("userName", userName)
        searchResults.arguments = args

        val transaction = requireActivity().supportFragmentManager.beginTransaction()
        transaction.replace(R.id.navfragment, searchResults)
        transaction.addToBackStack(null)
        transaction.commit()
    }



    private fun dataInitialize(){
        newsArrayList = arrayListOf<news>()

        imageId = arrayOf(
            R.drawable.close_line_icon,
            R.drawable.close_line_icon,
            R.drawable.close_line_icon
        )

        heading = arrayOf(
            getString(R.string.mentorone),
            getString(R.string.mentortwo),
            getString(R.string.mentorthree)
        )

        News = arrayOf(
            getString(R.string.mentorone),
            getString(R.string.mentortwo),
            getString(R.string.mentorthree)
        )

        for (i in imageId.indices){

            val n = news(imageId[i], heading[i])
            newsArrayList.add(n)
        }

    }
}

