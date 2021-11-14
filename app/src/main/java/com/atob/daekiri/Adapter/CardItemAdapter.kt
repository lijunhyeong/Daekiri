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

class CardItemAdapter: ListAdapter<CardItem, CardItemAdapter.ViewHolder>(diffUtil){

    inner class ViewHolder(private val view: View): RecyclerView.ViewHolder(view){

        // 바인드 함수 --> 카드에 들어갈 값들을 바인드해줌
        fun bind(cardItem: CardItem){
            //view.findViewById<TextView>(R.id.nameTextView).text = cardItem.name
            //view.findViewById<ImageView>(R.id.cardProfileImage) = cardItem.profile

            view.findViewById<TextView>(R.id.cardAge).text = cardItem.age
            view.findViewById<TextView>(R.id.cardMajor).text = cardItem.major
            view.findViewById<TextView>(R.id.cardName).text = cardItem.name

            view.findViewById<TextView>(R.id.cardTeachItem1).text = cardItem.teachItem1
            view.findViewById<TextView>(R.id.cardTeachItem2).text = cardItem.teachItem2
            view.findViewById<TextView>(R.id.cardTeachItem3).text = cardItem.teachItem3

            view.findViewById<TextView>(R.id.cardLearnItem1).text = cardItem.learnItem1
            view.findViewById<TextView>(R.id.cardLearnItem2).text = cardItem.learnItem2
            view.findViewById<TextView>(R.id.cardLearnItem3).text = cardItem.learnItem3

            view.findViewById<TextView>(R.id.cardMateItem1).text = cardItem.mateItem1
            view.findViewById<TextView>(R.id.cardMateItem2).text = cardItem.mateItem2
            view.findViewById<TextView>(R.id.cardMateItem3).text = cardItem.mateItem3
        }
    }


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        return ViewHolder(inflater.inflate(R.layout.item_card, parent, false))
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