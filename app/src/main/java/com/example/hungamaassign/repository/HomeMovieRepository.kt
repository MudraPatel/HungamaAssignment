package com.example.hungamaassign.repository

import android.util.Log
import android.widget.SearchView
import androidx.lifecycle.LiveData
import com.example.hungamaassign.Constant
import com.example.hungamaassign.dao.HomeMovieDao
import com.example.hungamaassign.model.Movie
import com.example.hungamaassign.model.RecentSearch
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.BufferedInputStream
import java.io.BufferedReader
import java.io.InputStream
import java.io.InputStreamReader
import java.net.HttpURLConnection
import java.net.URL
import kotlin.coroutines.coroutineContext


class HomeMovieRepository(private val homeProgramDao: HomeMovieDao){

    val allPrograms: LiveData<List<Movie>> = homeProgramDao.getAllHomePrograms()
    private val loginUrl = "https://api.themoviedb.org/3/movie/now_playing?api_key="+Constant.token


    suspend fun getSearchProgram(search:String) {
        homeProgramDao.searchForProgram(search)
    }

    suspend fun insert(homeMovie: Movie) {
        homeProgramDao.insert(homeMovie)
    }

    suspend fun insertAll(homeMovie: List<Movie>) {
        homeProgramDao.insertAll(homeMovie)
    }

    suspend fun deleteAllHomePrograms(){
        homeProgramDao.deleteAllPrograms()
    }
    suspend fun deleteSearch(search: RecentSearch){
        homeProgramDao.deleteSearch()
    }

    //delete recent search in database - to maintain the MAX recent list size limit = 5
     suspend fun deleteRecentSearch(){
         CoroutineScope(coroutineContext).launch {
             var count :Int = homeProgramDao.recentListCount()
             Log.e("Result", "Count:**deleteRecentSearch::"+homeProgramDao.recentListCount())

             if(count >= 6)
                 homeProgramDao.deleteSearch()

         }
    }

    //add recent search in database
    suspend fun insertRecent(search: RecentSearch) {
        homeProgramDao.insertRecent(search)
    }

    // Function that makes the network request, blocking the current thread
    fun makeLoginRequest1(
        jsonBody: String
    ): Result<String> {
        val url = URL(loginUrl)
        (url.openConnection() as? HttpURLConnection)?.run {
            requestMethod = "GET"
            setRequestProperty("Content-Type", "application/json; utf-8")
            setRequestProperty("Accept", "application/json")
            doOutput = true
            outputStream.write(jsonBody.toByteArray())
            return Result.Success(homeProgramDao.toString())
        }
        return Result.Error(Exception("Cannot open HttpURLConnection"))
    }

    suspend fun makeHttpRequest(
    ): Result<String> {

        // Move the execution of the coroutine to the I/O dispatcher
        return  withContext(Dispatchers.IO) {
            // Blocking network request code

            val inputStream:InputStream
            val result:String

            // create URL
            val url:URL = URL(loginUrl)

            // create HttpURLConnection
            val conn:HttpURLConnection = url.openConnection() as HttpURLConnection

            // make GET request to the given URL
            conn.connect()

            // receive response as inputStream
            inputStream = conn.inputStream

            // convert inputstream to string
            if(inputStream != null) {
                result = convertInputStreamToString(inputStream)
                Log.e("Result", result + ":::*****");
                Result.Success(result)
            }
            else {
                result = "Did not work!"
                Result.Error(Exception("Cannot open HttpURLConnection"))
            }
        }
    }

    private fun convertInputStreamToString(inputStream: InputStream): String {
        val bufferedReader:BufferedReader? = BufferedReader(InputStreamReader(inputStream))

        var line:String? = bufferedReader?.readLine()
        var result:String = ""

        while (line != null) {
            result += line
            line = bufferedReader?.readLine()
        }

        inputStream.close()
        return result
    }



//    suspend fun getResults() = Dispatchers.Default {
//        val result: String
//                = URL("https://api.themoviedb.org/3/movie/now_playing?api_key=cbb24520cd983647bdb9d26c626a2e07").readText()
//        // make network call
//        return@Default result
//    }
//
//    suspend fun makeNetworkCall(){
//        // the getResults() function should be a suspend function too, more on that later
//        val result = getResults()
//    }
}