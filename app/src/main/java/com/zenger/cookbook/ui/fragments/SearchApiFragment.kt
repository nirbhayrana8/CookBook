package com.zenger.cookbook.ui.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.google.android.material.snackbar.Snackbar
import com.zenger.cookbook.R
import com.zenger.cookbook.adapters.LoadStateAdapter
import com.zenger.cookbook.adapters.SearchApiAdapter
import com.zenger.cookbook.databinding.FragmentApiSearchBinding
import com.zenger.cookbook.room.tables.SearchResultsTable
import com.zenger.cookbook.viewmodels.SearchApiViewModel
import com.zenger.cookbook.viewmodels.factories.SearchApiViewModelFactory

class SearchApiFragment : Fragment(R.layout.fragment_api_search), SearchApiAdapter.OnItemClickListener {

    private val factory by lazy { SearchApiViewModelFactory(requireActivity().application) }
    private val viewModel by viewModels<SearchApiViewModel> { factory }

    private lateinit var binding: FragmentApiSearchBinding

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_api_search, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val adapter = SearchApiAdapter(this)

        binding.apply {
            recyclerView.setHasFixedSize(true)
            recyclerView.adapter = adapter.withLoadStateHeaderAndFooter(
                    header = LoadStateAdapter { adapter.retry() },
                    footer = LoadStateAdapter { adapter.retry() }
            )
        }

        try {
            val query = SearchApiFragmentArgs.fromBundle(requireArguments()).query
            viewModel.searchRecipeByName(query).observe(viewLifecycleOwner) {
                adapter.submitData(viewLifecycleOwner.lifecycle, it)
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    override fun onItemClick(searchResults: SearchResultsTable) {
        val view = requireActivity().findViewById<View>(R.id.constraintLayout)
        val snackBar = Snackbar.make(view, "Clicked", Snackbar.LENGTH_LONG)
        snackBar.anchorView = view.findViewById(R.id.bottomNav)
        snackBar.show()
    }

}