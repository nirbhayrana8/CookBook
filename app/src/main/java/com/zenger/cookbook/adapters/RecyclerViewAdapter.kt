package com.zenger.cookbook.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.annotation.NonNull
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions
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
                return oldItem == newItem
            }

        }
    }


    override fun onCreateViewHolder(@NonNull parent: ViewGroup, viewType: Int): DiscoverViewHolder {

        binding = DataBindingUtil.inflate(LayoutInflater.from(parent.context), R.layout.list_layout, parent,
                false)

        return DiscoverViewHolder(binding, interaction)
    }

    override fun onBindViewHolder(@NonNull holder: DiscoverViewHolder, position: Int) {

        val currentItem = getItem(position)

        holder.textView.text = currentItem.title

        Glide
                .with(binding.root)
                .load(currentItem.imageUrl)
                .centerCrop()
                .transition(DrawableTransitionOptions.withCrossFade())
                .error(R.drawable.ic_baseline_error_24)
                .into(holder.imageView)
    }


    inner class DiscoverViewHolder(@NonNull itemView: ListLayoutBinding,
                                   private val interaction: Interaction?) : RecyclerView.ViewHolder(itemView.root), View.OnClickListener {

        var textView: TextView
        var imageView: ImageView

        init {
            itemView.root.setOnClickListener(this)

            textView = binding.textView
            imageView = binding.imageView
        }


        override fun onClick(v: View) {
            interaction?.onItemSelected(bindingAdapterPosition)
        }

    }

    interface Interaction {
        fun onItemSelected(position: Int)
    }
}