package com.example.hungamaassign.viewModel

import android.app.Application
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.viewModelScope
import com.example.hungamaassign.AppDatabase
import com.example.hungamaassign.model.CastModel
import com.example.hungamaassign.model.Movie
import com.example.hungamaassign.model.SimilarvideoModel
import com.example.hungamaassign.repository.DetailsRepository
import com.example.hungamaassign.repository.HomeMovieRepository
import com.example.hungamaassign.repository.Result
import kotlinx.coroutines.launch
import org.json.JSONArray
import org.json.JSONObject

class DetailsViewModel(application: Application) : AndroidViewModel(application) {
    private var repository: DetailsRepository

    var allCastList: LiveData<List<CastModel>>
    var allCrewList: LiveData<List<CastModel>>
    var allSimilarList: LiveData<List<SimilarvideoModel>>

    init {
        val detailsDao = AppDatabase.getDatabase(application).detailsDao()
        repository = DetailsRepository(detailsDao)
        allCastList = repository.allCastList
        allCrewList = repository.allCrewList
        allSimilarList = repository.allSimilarList

    }

    fun getCastList( id:Int, type: String) {
        viewModelScope.launch {
            val result = repository.makeCastRequest(id, "/credits")
            var listCastTemp : MutableList<CastModel> = emptyList<CastModel>().toMutableList();
            when(result){
                is Result.Success<String> -> {
                    // Happy path
                    val json: JSONObject = JSONObject(result.data.toString())
                    val jsonArray: JSONArray = json.getJSONArray(type)
                    for (i in 0 until jsonArray.length()) {
                        val item = jsonArray.getJSONObject(i)

                        val typeId : String
                        //check if type is cast or crew
                        if(type.equals("cast")){
                            typeId = item.getString("cast_id")
                        }else{
                            typeId = item.getString("credit_id")
                        }
                        val cast: CastModel = CastModel(
                            item.getInt("id"), typeId,
                            item.getString("name"),
                            item.getString(
                                "profile_path"
                            ),
                            item.getString("known_for_department"),
                            item.getInt("gender"),
                            item.getDouble(
                                "popularity"
                            ),
                            type
                        )

                        listCastTemp.add(cast)
                        // repository.insert(movie);
                    }

                    repository.insertAll(listCastTemp)
                }
                else -> Log.e("Result::", "Error: T" + result.toString())
// Show error in UI
            }
        }
    }


    fun getSimilarList( id:Int) {
        viewModelScope.launch {
            val result = repository.makeCastRequest(id, "/similar")
            var listSimilarTemp : MutableList<SimilarvideoModel> = emptyList<SimilarvideoModel>().toMutableList();
            when(result){
                is Result.Success<String> -> {
                    // Happy path
                    val json: JSONObject = JSONObject(result.data.toString())
                    val jsonArray: JSONArray = json.getJSONArray("results")
                    for (i in 0 until jsonArray.length()) {
                        val item = jsonArray.getJSONObject(i)

                        val similar: SimilarvideoModel = SimilarvideoModel(
                            item.getInt("id"),item.getString("title"),
                            item.getString(
                                "poster_path"
                            ),item.getString("overview"),
                            item.getString("release_date"),
                            item.getBoolean("video"),
                            item.getDouble(
                                "popularity"
                            ),
                        )

                        listSimilarTemp.add(similar)
                        // repository.insert(movie);
                    }

                    repository.insertAllSimilar(listSimilarTemp)
                }
                else -> Log.e("Result::", "Error: T" + result.toString())
// Show error in UI
            }
        }
    }
}