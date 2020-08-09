package com.zenger.cookbook.adapters

import androidx.recyclerview.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.annotation.NonNull
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import com.bumptech.glide.Glide
import com.zenger.cookbook.R
import com.zenger.cookbook.databinding.ListLayoutBinding
import com.zenger.cookbook.room.tables.RecipeTable

class RecyclerViewAdapter(private val interaction: Interaction? = null) :
    ListAdapter<RecipeTable, RecyclerViewAdapter.DiscoverViewHolder>(DIFF_CALLBACK) {

    lateinit var binding: ListLayoutBinding

    companion object {

        val DIFF_CALLBACK = object : DiffUtil.ItemCallback<RecipeTable>() {

            override fun areItemsTheSame(oldItem: RecipeTable, newItem: RecipeTable): Boolean {
                return oldItem.id == newItem.id
            }

            override fun areContentsTheSame(oldItem: RecipeTable, newItem: RecipeTable): Boolean {
                return oldItem.title == newItem.title && oldItem.imageUrl == newItem.imageUrl
            }

        }
    }


    override fun onCreateViewHolder(@NonNull parent: ViewGroup, viewType: Int): DiscoverViewHolder {

        binding = DataBindingUtil.inflate(LayoutInflater.from(parent.context), R.layout.list_layout,  parent,
            false)

        return DiscoverViewHolder(binding , interaction)
    }

    override fun onBindViewHolder(@NonNull holder: DiscoverViewHolder, position: Int) {

        val currentItem = getItem(position)
        holder.textView.text = currentItem.title

        Glide
            .with(binding.root)
            .load(currentItem.imageUrl)
            .centerCrop()
            .placeholder(R.drawable.ic_launcher_foreground)
            .into(holder.imageView)
    }


    inner class DiscoverViewHolder (@NonNull itemView: ListLayoutBinding,
        private val interaction: Interaction?) : RecyclerView.ViewHolder(itemView.root), View.OnClickListener {

        var textView: TextView
        var imageView: ImageView

        init {
            itemView.root.setOnClickListener(this)

            textView = binding.textView
            imageView = binding.imageView
        }


        override fun onClick(v: View) {
            interaction?.onItemSelected(adapterPosition)
        }
    }

    interface Interaction {
        fun onItemSelected(position: Int)
    }
}