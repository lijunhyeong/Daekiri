package com.atob.daekiri.Adapter

import android.content.Context
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.TextView
import com.atob.daekiri.R

class LearnAdapter(var context: Context, var arrayList: ArrayList<String>): BaseAdapter(){


    override fun getItem(position: Int): Any {
        return arrayList.get(position)
    }

    override fun getItemId(position: Int): Long {
        return position.toLong()
    }

    override fun getCount(): Int {
        return arrayList.size
    }

    override fun getView(position: Int, convertView: View?, parent: ViewGroup?): View {

        var view:View = View.inflate(context, R.layout.item_learn, null)

        var titles: TextView = view.findViewById(R.id.learnTitle)

        var listItem: String = arrayList.get(position)

        titles.text = listItem

        return view
    }

}