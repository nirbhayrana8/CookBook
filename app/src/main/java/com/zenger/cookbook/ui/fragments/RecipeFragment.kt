package com.zenger.cookbook.ui.fragments

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.Observer
import androidx.navigation.navGraphViewModels
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.material.snackbar.Snackbar
import com.zenger.cookbook.R
import com.zenger.cookbook.adapters.RecyclerViewAdapter
import com.zenger.cookbook.databinding.FragmentRecipeBinding
import com.zenger.cookbook.viewmodels.MyRecipeViewModel

class RecipeFragment : Fragment(), RecyclerViewAdapter.Interaction {

    private lateinit var binding: FragmentRecipeBinding
    private val adapter = RecyclerViewAdapter(this)
    private val viewModel:MyRecipeViewModel by navGraphViewModels(R.id.navigation)

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {

        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_recipe, container, false)

        binding.recyclerView.layoutManager = LinearLayoutManager(context)
        binding.recyclerView.adapter = adapter

        return binding.root
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        if (viewModel.listState != null) {
            binding.recyclerView.layoutManager?.onRestoreInstanceState(viewModel.listState)
            viewModel.listState = null
        }

        viewModel.recipes.observe(viewLifecycleOwner, Observer {
            adapter.submitList(it)
        })
    }

    override fun onDestroyView() {
        super.onDestroyView()
        viewModel.listState = binding.recyclerView.layoutManager?.onSaveInstanceState()
    }

    override fun onItemSelected(position: Int) {
        val view = requireActivity().findViewById<View>(R.id.constraintLayout)
        val snackBar = Snackbar.make(view, "Clicked", Snackbar.LENGTH_LONG)
        snackBar.anchorView = view.findViewById(R.id.bottomNav)
        snackBar.show()
    }

}