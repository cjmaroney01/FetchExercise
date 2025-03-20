package com.colinmaroney.fetchexercise.ui

import android.graphics.Paint
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.colinmaroney.fetchexercise.R

class HiringDataAdapter(private val dataSet: Map<Int, String>):
    RecyclerView.Adapter<HiringDataAdapter.ViewHolder>() {

    class ViewHolder(view: View): RecyclerView.ViewHolder(view) {
        val namesText: TextView
        val listIdText: TextView
        val namesHeaderText: TextView
        val listIdHeaderText: TextView


        init {
            namesText = view.findViewById(R.id.names)
            listIdText = view.findViewById(R.id.list_id)
            namesHeaderText = view.findViewById(R.id.names_header)
            listIdHeaderText = view.findViewById(R.id.list_id_header)
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.hiring_item, parent, false)

        return ViewHolder(view)
    }

    override fun getItemCount(): Int {
        return dataSet.keys.size
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val sortedKeys = dataSet.keys.sorted()
        holder.listIdText.text = "${sortedKeys[position]}"
        holder.namesText.text = dataSet[sortedKeys[position]]
        holder.listIdHeaderText.paintFlags = holder.listIdHeaderText.paintFlags or Paint.UNDERLINE_TEXT_FLAG
        holder.namesHeaderText.paintFlags = holder.namesHeaderText.paintFlags or Paint.UNDERLINE_TEXT_FLAG
    }
}