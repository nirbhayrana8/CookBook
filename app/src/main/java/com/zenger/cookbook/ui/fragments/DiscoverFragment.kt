package com.zenger.cookbook.ui.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import androidx.navigation.navGraphViewModels
import androidx.recyclerview.widget.StaggeredGridLayoutManager
import com.google.android.material.snackbar.Snackbar
import com.zenger.cookbook.R
import com.zenger.cookbook.adapters.RecyclerViewAdapter
import com.zenger.cookbook.databinding.FragmentDiscoverBinding
import com.zenger.cookbook.viewmodels.DiscoverViewModel


class DiscoverFragment : Fragment(), RecyclerViewAdapter.Interaction {

    private val viewModel: DiscoverViewModel by navGraphViewModels(R.id.navigation)
    private lateinit var binding: FragmentDiscoverBinding
    private val adapter = RecyclerViewAdapter(this)

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {

        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_discover, container, false)

        binding.recyclerView.layoutManager = StaggeredGridLayoutManager(2, 1)
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

        findNavController().navigate(DiscoverFragmentDirections
            .actionDiscoverFragmentToDetailFragment(viewModel.recipes.value!![position].sourceUrl))
    }
}