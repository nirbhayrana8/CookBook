package com.zenger.cookbook.ui.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.StaggeredGridLayoutManager
import com.google.android.material.appbar.MaterialToolbar
import com.google.android.material.snackbar.Snackbar
import com.zenger.cookbook.R
import com.zenger.cookbook.adapters.RecyclerViewAdapter
import com.zenger.cookbook.databinding.FragmentDiscoverBinding
import com.zenger.cookbook.viewmodels.DiscoverViewModel
import com.zenger.cookbook.viewmodels.factories.DiscoverViewModelFactory


class DiscoverFragment : Fragment(), RecyclerViewAdapter.Interaction {

    private val factory by lazy { DiscoverViewModelFactory(requireActivity().application) }
    private val viewModel: DiscoverViewModel by viewModels { factory }
    private lateinit var binding: FragmentDiscoverBinding
    private val adapter = RecyclerViewAdapter(this)

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {

        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_discover, container, false)

        binding.recyclerView.layoutManager = StaggeredGridLayoutManager(2, 1)
        binding.recyclerView.adapter = adapter

        val layout = binding.toolBar
        val toolbar = layout.findViewById<MaterialToolbar>(R.id.toolBar)
        (requireActivity() as AppCompatActivity).setSupportActionBar(toolbar)

        return binding.root
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        if (viewModel.listState != null) {
            binding.recyclerView.layoutManager?.onRestoreInstanceState(viewModel.listState)
            viewModel.listState = null
        }

        viewModel.recipes.observe(viewLifecycleOwner, {
            adapter.submitList(it)
        })
    }

    override fun onDestroyView() {
        super.onDestroyView()
        viewModel.listState = binding.recyclerView.layoutManager?.onSaveInstanceState()

        (requireActivity() as AppCompatActivity).supportActionBar?.hide()
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