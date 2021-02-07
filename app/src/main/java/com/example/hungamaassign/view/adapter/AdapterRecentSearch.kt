package com.example.hungamaassign.view.adapter

import androidx.recyclerview.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import com.example.hungamaassign.R
import com.example.hungamaassign.model.RecentSearch


/**
 * TODO: Replace the implementation with code for your data type.
 */
class AdapterRecentSearch(
        private var values: List<RecentSearch>
) : RecyclerView.Adapter<AdapterRecentSearch.ViewHolder>() {


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
                .inflate(R.layout.list_recent_search, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = values[position]
        holder.tvTitle.text = item.name

    }


    internal fun setRecentList(recentList: List<RecentSearch>) {
        values = recentList
//        Log.e("Result", "AdapterRecent"+ recentList.size)
        notifyDataSetChanged()
    }


    override fun getItemCount(): Int = values.size

    inner class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val tvTitle: TextView = view.findViewById(R.id.tvtitle)
    }


}