package com.atob.daekiri.Adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.atob.daekiri.DataClass.CardItem
import com.atob.daekiri.R

class MatchedUserAdapter : ListAdapter<CardItem, MatchedUserAdapter.ViewHolder>(diffUtil){

    inner class ViewHolder(private val view: View): RecyclerView.ViewHolder(view){

        // 바인드 함수 --> 카드에 들어갈 값들을 바인드해줌
        fun bind(cardItem: CardItem){
            view.findViewById<TextView>(R.id.userNameTextView).text = cardItem.name
        }
    }


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        return ViewHolder(inflater.inflate(R.layout.item_matched_user, parent, false))
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(currentList[position])
    }

    companion object{
        val diffUtil = object: DiffUtil.ItemCallback<CardItem>(){
            override fun areItemsTheSame(oldItem: CardItem, newItem: CardItem): Boolean {
                return oldItem.userId == newItem.userId
            }

            override fun areContentsTheSame(oldItem: CardItem, newItem: CardItem): Boolean {
                return oldItem == newItem
            }

        }
    }


}