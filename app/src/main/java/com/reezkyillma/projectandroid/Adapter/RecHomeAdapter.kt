package com.reezkyillma.projectandroid.Adapter

import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.bumptech.glide.Glide
import com.example.hp.weathermodule.Model.ModelMakanan
import com.reezkyillma.projectandroid.R
import kotlinx.android.synthetic.main.rec_home_item.view.*

class RecHomeAdapter(private val listItem: List<ModelMakanan>
) : RecyclerView.Adapter<FoodHolder>() {
    override fun onCreateViewHolder(viewGroup: ViewGroup, p1: Int): FoodHolder {
        return FoodHolder(LayoutInflater.from(viewGroup.context).inflate(R.layout.rec_home_item, viewGroup, false))
    }

    override fun getItemCount(): Int = listItem.size

    override fun onBindViewHolder(holder: FoodHolder, position: Int) {
        holder.bindFoodView(listItem[position])
    }
}

public interface RecHomeListener{
    fun onItemClick(item: ModelMakanan)
}

class FoodHolder(view: View) : RecyclerView.ViewHolder(view){
    private val foodImage = view.item_image
    private val title = view.item_title
    private val subtitle = view.item_subtitle

    fun bindFoodView(dataMakanan: ModelMakanan){
        Glide.with(itemView)
            .load(dataMakanan.URLImage)
            .placeholder(R.drawable.ic_launcher_background)
            .into(foodImage)
        title.text = dataMakanan.name
        subtitle.text = dataMakanan.desc

//        itemView.setOnClickListener { listener.onItemClick(dataMakanan)}
    }
}