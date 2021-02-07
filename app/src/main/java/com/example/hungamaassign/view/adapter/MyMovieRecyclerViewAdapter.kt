package com.example.hungamaassign.view.adapter

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.os.AsyncTask
import android.os.Bundle
import android.util.Log
import androidx.recyclerview.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.navigation.findNavController
import com.example.hungamaassign.InternetCheck
import com.example.hungamaassign.R
import com.example.hungamaassign.model.Movie
import com.example.hungamaassign.viewModel.MovieViewModel
import kotlinx.coroutines.*
import java.io.IOException
import java.io.InputStream
import java.net.HttpURLConnection
import java.net.URL

/**
 * TODO: Replace the implementation with code for your data type.
 */
class MyMovieRecyclerViewAdapter(
     private var isSearch : Boolean, val itemListener: ItemSelectedListener
) : RecyclerView.Adapter<MyMovieRecyclerViewAdapter.ViewHolder>() {
    private var values = emptyList<Movie>()

    companion object {
        var itemListener: ItemSelectedListener? = null
    }
    private var homeMovieList = emptyList<Movie>()
    private val imgUrl : String  = "https://www.themoviedb.org/t/p/w440_and_h660_face"
    private lateinit var viewModel: MovieViewModel

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.movie_list, parent, false)

        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = values[position]
        holder.tvTitle.text = item.title
        holder.tvSecond.text = item.release_date
        holder.tvThird.text = item.overview
        var url:String = imgUrl + item.poster_path;

        holder.layout.setOnClickListener{
            if(isSearch) {
                //custom set interface and title and implement in SearchFragment
                itemListener?.onItemSelected(item.title)
            }

            var bundle : Bundle = Bundle();
            bundle.putInt("id", item.id)
            bundle.putString("title", item.title)
            bundle.putString("overview", item.overview)
            bundle.putString("release_date", item.release_date)
            bundle.putString("language", item.original_language)
            it.findNavController().navigate(R.id.detailsFragment, bundle)

        }
        //check internet is available or not
        InternetCheck(object : InternetCheck.Consumer {
            override fun accept(internet: Boolean?) {
                Log.d("test", "asdasdas")
                if (internet!!)
                    DownloadImageFromInternet(holder.image).execute(url);

            }
        })
//        getBitmapFromURL(url, holder.image)
    }

    private fun getBitmapFromURL(uri: String?, imageview: ImageView) {
        CoroutineScope(Job() + Dispatchers.IO).launch {
            try {
                val url = URL(uri)
                val bitMap = BitmapFactory.decodeStream(url.openConnection().getInputStream())
//                imageview = Bitmap.createScaledBitmap(bitMap, 100, 100, true)
                imageview.setImageBitmap(bitMap);

            } catch (e: IOException) {
                // Log exception
            }
        }
    }

    private inner class DownloadImageFromInternet(var imageView: ImageView) : AsyncTask<String, Void, Bitmap?>() {

        override fun doInBackground(vararg urls: String): Bitmap? {
            val imageURL = urls[0]
            var image: Bitmap? = null
            try {

                val inputStream: InputStream
                val conn: HttpURLConnection = URL(imageURL).openConnection() as HttpURLConnection
                conn.connect()

                // receive response as inputStream
                inputStream = conn.inputStream
                if(inputStream != null)
                    image = BitmapFactory.decodeStream(inputStream)
            }
            catch (e: Exception) {
                Log.e("Error Message", e.message.toString())
                e.printStackTrace()
                image = null;
            }
            return image
        }
        override fun onPostExecute(result: Bitmap?) {
            if(result == null){
                imageView.setImageResource(R.drawable.ic_default_img)
            }else
            imageView.setImageBitmap(result)
        }
    }


    internal fun setHomeMovie(values: List<Movie>) {
        this.values = values
        Log.e("Result", "Adapter"+ values.size)
        notifyDataSetChanged()
    }


    override fun getItemCount(): Int = values.size

    inner class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val layout : ConstraintLayout = view.findViewById(R.id.consMain);
        val tvTitle: TextView = view.findViewById(R.id.tvTitle)
        val tvSecond: TextView = view.findViewById(R.id.tvSecondText)
        val tvThird: TextView = view.findViewById(R.id.tvThreeText)
        val image: ImageView = view.findViewById(R.id.img)

    }

    interface ItemSelectedListener {
        fun onItemSelected(item:String)
    }
}