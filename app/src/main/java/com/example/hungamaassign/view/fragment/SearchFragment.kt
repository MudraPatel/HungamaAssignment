package com.example.hungamaassign.view.fragment

import android.annotation.SuppressLint
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.Menu
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.widget.SearchView
import androidx.constraintlayout.widget.Group
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.hungamaassign.R
import com.example.hungamaassign.model.Movie
import com.example.hungamaassign.model.RecentSearch
import com.example.hungamaassign.view.adapter.AdapterRecentSearch
import com.example.hungamaassign.view.adapter.MyMovieRecyclerViewAdapter
import com.example.hungamaassign.viewModel.MovieViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch


/**
 * A simple [Fragment] subclass.
 * Use the [SearchFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class SearchFragment : Fragment(), MyMovieRecyclerViewAdapter.ItemSelectedListener {
    private lateinit var viewModel: MovieViewModel
    val scope = CoroutineScope(Job() + Dispatchers.Main)
    private lateinit var adapter : MyMovieRecyclerViewAdapter
    private var movieList : List<Movie> = ArrayList<Movie>();
    private lateinit var constraintGroup :Group;

    private lateinit var adapterRecent : AdapterRecentSearch
    private var recentList : List<RecentSearch> = ArrayList<RecentSearch>();
    lateinit var recyclerView : RecyclerView;
    lateinit var recyclerViewRecent : RecyclerView;
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }

    //hide search icon in toolbar
    override fun onPrepareOptionsMenu(menu: Menu) {
        menu.findItem(R.id.searchFragment).setVisible(false)
        super.onPrepareOptionsMenu(menu)
    }

    override fun onCreateView(
            inflater: LayoutInflater, container: ViewGroup?,
            savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_search, container, false)
        val searchView = view.findViewById<SearchView>(R.id.searchView)
        constraintGroup = view.findViewById(R.id.group);
        setHasOptionsMenu(true)

         recyclerView = view.findViewById<RecyclerView>(R.id.list)
        adapter = MyMovieRecyclerViewAdapter( true, this)
        recyclerView.adapter = adapter
        recyclerView.layoutManager = LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false)

        recyclerView.visibility = View.GONE

         recyclerViewRecent = view.findViewById<RecyclerView>(R.id.recent_list)
        adapterRecent = AdapterRecentSearch(recentList);
        recyclerViewRecent.adapter = adapterRecent
        recyclerViewRecent.layoutManager = LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false)


        searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {
                return false
            }

            override fun onQueryTextChange(newText: String?): Boolean {
              //  Log.e("Result", "IF" + newText + "::")
                //check if text is empty or not
                // if empty - then display recent search else list
                if (newText != null && newText.isNotEmpty()) {
                    getResults(newText)
                }else{
                    constraintGroup.visibility = View.VISIBLE
                    recyclerView.visibility = View.GONE
//                    viewModel.allProgramsList.observe(this@SearchFragment, {
//                        list ->
//                        Log.e("Result","Size"+list.size)
//                        adapter.setHomeMovie(list)
//
//                    })
                }
                return true
            }

            @SuppressLint("FragmentLiveDataObserve")
            private fun getResults(queryText: String) {
//                val queryText = newText //"%$newText%"
                //get search list using like operate in query
                viewModel.searchQuery(queryText).observe(this@SearchFragment, Observer { listSearch ->

                    // search - split the title and check if the word startwith searchString or not
                    movieList = binarySearch(listSearch, queryText);

                    if(movieList.size>0){
                        constraintGroup.visibility = View.GONE
                        recyclerView.visibility = View.VISIBLE
                    }else{
                        constraintGroup.visibility = View.GONE
                    }

                    //set data
                    adapter.setHomeMovie(movieList)

                })
            }
        })
        return view
    }

    companion object {
        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @param param1 Parameter 1.
         * @param param2 Parameter 2.
         * @return A new instance of fragment SearchFragment.
         */
        // TODO: Rename and change types and number of parameters
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            SearchFragment()

    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        viewModel = ViewModelProvider(this).get(MovieViewModel::class.java)

        viewModel.allSearchList.observe(this, {
            recentSearchList ->
//            Log.e("Result", "recentSearchList:Size:"+ recentSearchList.size)
            adapterRecent.setRecentList(recentSearchList)
        })

    }

    //search function
    fun binarySearch(movieData: List<Movie>, x: String): List<Movie> {
        //empty list
        var listData : MutableList<Movie> = emptyList<Movie>().toMutableList();
//        Log.e("Result", "BinarySearch"+movieData.size +"::"+listData.size);
        var l = 0
        var r = movieData.size - 1
        while (l <= r) {
            //split the title into array
            val resArr:List<String> = movieData[l].title.split(" ") as List<String>
            val key = x.contains(" ")
            Log.e("Result", "key :" + key)

            // iterate it splitArray
            for (i in resArr.indices) {
                // check if split word is startwith same word or not using startwith function
                //if yes then add in list and break the loop
                //check search string contains space or not
                if(key) {
                        listData.add(movieData[l])
                        break
                    //}
                }else {
                    if (resArr[i].startsWith(x, true)) {
                        Log.d("Result", "BinarySearch3: " + movieData.size + "::" + listData.size);
                        println(movieData[l].title + "YEs" + i + resArr[i])
                        listData.add(movieData[l])
                        break
                    }
                }

            }
            l++
        }
        Log.e("Result", "Search4: "+movieData.size + "::"+listData.size);

        return listData;
    }

    //implement the interface
    override fun onItemSelected(item: String) {
        scope.launch {
                scope.launch {
                    //delete recent search data depend on conditon which was mentioned in HomeMovieRepository file
                    viewModel.deleteRecentSearch()
                }
            //set recent list data in recyclerview
            viewModel.setRecentList(item)
        }
    }
}