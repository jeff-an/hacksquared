package com.austinabell8.canihackit.adapters

import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import com.austinabell8.canihackit.R

class TagRecyclerAdapter(tags: List<String>) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    private val mTags: List<String> = tags


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {

        val mView = LayoutInflater.from(parent.context)
                .inflate(R.layout.item_tag, parent, false)
        return TagViewHolder(mView)
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val r = mTags[position]
        val rHolder = holder as TagViewHolder

        //Update data in TagViewHolder
        rHolder.name.text = r
    }

    override fun getItemCount(): Int {
        return mTags.size
    }

    private inner class TagViewHolder internal constructor(view: View) : RecyclerView.ViewHolder(view) {
        internal var name: TextView = view.findViewById(R.id.tv_tag)
    }

}