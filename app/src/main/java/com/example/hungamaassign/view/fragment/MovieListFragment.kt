package com.example.hungamaassign.view.fragment

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.hungamaassign.InternetCheck
import com.example.hungamaassign.R
import com.example.hungamaassign.model.Movie
import com.example.hungamaassign.view.adapter.MyMovieRecyclerViewAdapter
import com.example.hungamaassign.viewModel.MovieViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch

/**
 * A fragment representing a list of Items.
 */
class MovieListFragment : Fragment(), MyMovieRecyclerViewAdapter.ItemSelectedListener {


    private lateinit var viewModel: MovieViewModel
    val scope = CoroutineScope(Job() + Dispatchers.Main)
    private lateinit var adapter : MyMovieRecyclerViewAdapter
    private var movieList : List<Movie> = ArrayList<Movie>();

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }

    companion object {
        fun newInstance() = MovieListFragment()
    }
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_movie_list, container, false)
        val recyclerView = view.findViewById<RecyclerView>(R.id.list)
        adapter = MyMovieRecyclerViewAdapter( false, this)
        recyclerView.adapter = adapter
        recyclerView.layoutManager = LinearLayoutManager(context, LinearLayoutManager.VERTICAL ,false)

        return view
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        viewModel = ViewModelProvider(this).get(MovieViewModel::class.java)

        scope.launch {
//            Log.e("Result",  "*******");
            InternetCheck(object : InternetCheck.Consumer {
                override fun accept(internet: Boolean?) {
                    Log.d("test", "check")
                    if (internet!!)
                        viewModel.getList();

                }
            })
        }


        viewModel.allProgramsList.observe(this, Observer { list ->
            Log.e("Result","Size"+list.size)
            movieList = list;
            //set data
            adapter.setHomeMovie(list)

        })

    }

    override fun onItemSelected(item: String) {
        TODO("Not yet implemented")
    }
}