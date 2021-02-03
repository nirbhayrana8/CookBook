package com.zenger.cookbook.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.paging.LoadState
import androidx.paging.LoadStateAdapter
import androidx.recyclerview.widget.RecyclerView
import com.zenger.cookbook.databinding.LoadStateLayoutBinding

class LoadStateAdapter(private val retry: () -> Unit) :
        LoadStateAdapter<com.zenger.cookbook.adapters.LoadStateAdapter.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, loadState: LoadState): ViewHolder {
        val binding = LoadStateLayoutBinding.inflate(LayoutInflater.from(parent.context), parent, false)

        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, loadState: LoadState) {
        holder.bind(loadState)
    }

    inner class ViewHolder(private val binding: LoadStateLayoutBinding) : RecyclerView.ViewHolder(binding.root) {

        init {
            binding.button.setOnClickListener { retry.invoke() }
        }

        fun bind(loadState: LoadState) {
            binding.apply {
                progressBar.isVisible = loadState is LoadState.Loading
                button.isVisible = loadState !is LoadState.Loading
                textView.isVisible = loadState !is LoadState.Loading
            }
        }
    }
}