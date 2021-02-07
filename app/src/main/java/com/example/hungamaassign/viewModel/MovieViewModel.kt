package com.example.hungamaassign.viewModel

import android.app.Application
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.example.hungamaassign.AppDatabase
import com.example.hungamaassign.model.Movie
import com.example.hungamaassign.model.RecentSearch
import com.example.hungamaassign.repository.HomeMovieRepository
import com.example.hungamaassign.repository.Result
import kotlinx.coroutines.launch
import org.json.JSONArray
import org.json.JSONObject


class MovieViewModel(application: Application) : AndroidViewModel(application) {

    private var repository: HomeMovieRepository
    var allProgramsList: LiveData<List<Movie>>
    var allSearchList: LiveData<List<RecentSearch>>
//    var searchProgramsList: LiveData<List<Movie>>
    private var mAppDatabase: AppDatabase? = null

    init{
        this.mAppDatabase = AppDatabase.getDatabase(application.getApplicationContext());

        val homeMovieDao = AppDatabase.getDatabase(application).homeMovieDao()

        repository = HomeMovieRepository(homeMovieDao)
        allProgramsList = repository.allPrograms
        allSearchList = homeMovieDao.allRecentSearch;
    }

    fun searchQuery(query: String?): LiveData<List<Movie>> {
        var data : LiveData<List<Movie>>  = query?.let { mAppDatabase?.homeMovieDao()?.searchForProgram(
            it
        ) }!!

        return  data;
    }


    fun setRecentList(searchString:String){
        viewModelScope.launch {

            var recentSearch : RecentSearch = RecentSearch(0,searchString)

            repository.insertRecent(recentSearch)


        }
    }

    /*fun getRecentListCount() : Int{
        return  repository.countSearch
    }*/

    suspend  fun deleteRecentSearch(){

        repository.deleteRecentSearch()
    }

    fun getList() {
        // Create a new coroutine to move the execution off the UI thread

        viewModelScope.launch {
            // Make the network call and suspend execution until it finishes
            val result = repository.makeHttpRequest()
            var movieListTemp : MutableList<Movie> = emptyList<Movie>().toMutableList();
            // Display result of the network request to the user
            when (result) {
                is Result.Success<String> -> {
                    // Happy path
                    val json: JSONObject = JSONObject(result.data.toString())
                    val jsonArray: JSONArray = json.getJSONArray("results")
                    for (i in 0 until jsonArray.length()) {
                        val item = jsonArray.getJSONObject(i)

                        val movie: Movie = Movie(
                            item.getInt("id"),
                            item.getString("title"),
                            item.getString(
                                "overview"
                            ),
                            item.getString("poster_path"),
                            item.getString("release_date"),
                            item.getBoolean("video"),
                            item.getDouble(
                                "popularity"
                            ),
                            item.getInt("vote_average"),
                            item.getInt("vote_count"),item.getString("original_language")
                        )

                        movieListTemp.add(movie)
                        // repository.insert(movie);
                    }

                    repository.insertAll(movieListTemp)
                }
                else -> Log.e("Result::", "Error: T" + result.toString())
// Show error in UI
            }
        }

    }

//    suspend fun getResults() = Dispatchers.Default {
//        val result: String
//                = URL("https://api.themoviedb.org/3/movie/now_playing?api_key=cbb24520cd983647bdb9d26c626a2e07").readText()
//        // make network call
//        return@Default result
//    }

}
