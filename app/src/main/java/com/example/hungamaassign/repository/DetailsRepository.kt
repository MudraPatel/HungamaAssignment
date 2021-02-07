package com.example.hungamaassign.repository

import android.util.Log
import android.widget.SearchView
import androidx.lifecycle.LiveData
import com.example.hungamaassign.Constant
import com.example.hungamaassign.dao.DetailsDao
import com.example.hungamaassign.dao.HomeMovieDao
import com.example.hungamaassign.model.CastModel
import com.example.hungamaassign.model.Movie
import com.example.hungamaassign.model.RecentSearch
import com.example.hungamaassign.model.SimilarvideoModel
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


class DetailsRepository(private val detailsDao: DetailsDao){

    private val baseUrl = "https://api.themoviedb.org/3/movie/"

    private val endUrl = "?api_key="+Constant.token

    val allCastList: LiveData<List<CastModel>> = detailsDao.getAllCast()
    val allCrewList: LiveData<List<CastModel>> = detailsDao.getAllCrew()
    val allSimilarList: LiveData<List<SimilarvideoModel>> = detailsDao.getAllSimilar()

    suspend fun insertAll(castList: List<CastModel>) {
        detailsDao.insertAll(castList)
    }

    suspend fun insertAllSimilar(similarList: List<SimilarvideoModel>) {
        detailsDao.insertAllSimilarList(similarList)
    }

    suspend fun makeCastRequest(id:Int, requestCalls:String
    ): Result<String> {

        // Move the execution of the coroutine to the I/O dispatcher
        return  withContext(Dispatchers.IO) {
            // Blocking network request code

            val inputStream:InputStream
            val result:String

            //prepare the url
            var urlPrepared :String = baseUrl + id+ requestCalls +endUrl
            // create URL
            val url:URL = URL(urlPrepared)

            // create HttpURLConnection
            val conn:HttpURLConnection = url.openConnection() as HttpURLConnection

            // make GET request to the given URL
            conn.connect()

            // receive response as inputStream
            inputStream = conn.inputStream

            // convert inputstream to string
            if(inputStream != null) {
                result = convertInputStreamToString(inputStream)
//                Log.e("Result", result + ":::*****");
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

}