package com.example.hungamaassign.view.adapter

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.os.AsyncTask
import android.util.Log
import androidx.recyclerview.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.constraintlayout.widget.ConstraintLayout
import com.example.hungamaassign.InternetCheck
import com.example.hungamaassign.R
import com.example.hungamaassign.model.SimilarvideoModel
import kotlinx.coroutines.*
import java.io.IOException
import java.net.URL

/**
 * [RecyclerView.Adapter] that can display a [DummyItem].
 * TODO: Replace the implementation with code for your data type.
 */
class SimilarRecyclerViewAdapter(
) : RecyclerView.Adapter<SimilarRecyclerViewAdapter.ViewHolder>() {
    private var values = emptyList<SimilarvideoModel>()

    private val imgUrl : String  = "https://www.themoviedb.org/t/p/w440_and_h660_face"

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.cast_list, parent, false)

        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = values[position]
        holder.tvTitle.text = item.title
        var url:String = imgUrl + item.posterPath;
        holder.tvTitle.visibility = View.GONE

        InternetCheck(object : InternetCheck.Consumer {
            override fun accept(internet: Boolean?) {
                Log.d("test", "internet")
                if (internet!!) {
                    //call async task to load bitmap image
                    DownloadImageFromInternet(holder.image).execute(url);
                }

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
                val `in` = java.net.URL(imageURL).openStream()
                image = BitmapFactory.decodeStream(`in`)
            }
            catch (e: Exception) {
                Log.e("Error Message", e.message.toString())
                e.printStackTrace()
                image = null
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


    internal fun setSimilarList(values: List<SimilarvideoModel>) {
        this.values = values
//        Log.e("Result", "Adapter"+ values.size)
        notifyDataSetChanged()
    }


    override fun getItemCount(): Int = values.size

    inner class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val layout : ConstraintLayout = view.findViewById(R.id.consMain);
        val tvTitle: TextView = view.findViewById(R.id.tvTitle)
        val image: ImageView = view.findViewById(R.id.img)

    }

}