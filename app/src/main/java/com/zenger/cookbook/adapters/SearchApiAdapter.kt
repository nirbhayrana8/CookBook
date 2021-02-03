package com.zenger.cookbook.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.paging.PagingDataAdapter
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions
import com.zenger.cookbook.R
import com.zenger.cookbook.databinding.ListLayoutBinding
import com.zenger.cookbook.room.tables.SearchResultsTable

class SearchApiAdapter(private val listener: OnItemClickListener) :
        PagingDataAdapter<SearchResultsTable, SearchApiAdapter.ViewHolder>(DIFF_CALLBACK) {

    companion object {

        val DIFF_CALLBACK = object : DiffUtil.ItemCallback<SearchResultsTable>() {

            override fun areItemsTheSame(oldItem: SearchResultsTable, newItem: SearchResultsTable) = oldItem.itemId == newItem.itemId

            override fun areContentsTheSame(oldItem: SearchResultsTable, newItem: SearchResultsTable) = oldItem == newItem

        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = ListLayoutBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val currentItem = getItem(position)

        currentItem?.let { holder.bind(it) }
    }

    inner class ViewHolder(private val binding: ListLayoutBinding) : RecyclerView.ViewHolder(binding.root) {

        init {
            binding.root.setOnClickListener {
                val position = bindingAdapterPosition

                if (position != RecyclerView.NO_POSITION) {
                    val item = getItem(position)
                    item?.let { listener.onItemClick(it) }
                }
            }
        }

        fun bind(recipe: SearchResultsTable) {

            binding.apply {
                Glide
                        .with(itemView)
                        .load(recipe.imageUrl)
                        .centerCrop()
                        .transition(DrawableTransitionOptions.withCrossFade())
                        .error(R.drawable.ic_baseline_error_24)
                        .into(imageView)

                textView.text = recipe.title
            }
        }


    }

    interface OnItemClickListener {
        fun onItemClick(searchResults: SearchResultsTable)
    }
}