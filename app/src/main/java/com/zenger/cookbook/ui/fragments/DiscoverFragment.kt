package com.zenger.cookbook.ui.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.StaggeredGridLayoutManager
import com.google.android.material.appbar.MaterialToolbar
import com.google.android.material.snackbar.Snackbar
import com.zenger.cookbook.R
import com.zenger.cookbook.adapters.RecyclerAdapter
import com.zenger.cookbook.databinding.FragmentDiscoverBinding
import com.zenger.cookbook.room.tables.RecipeTable
import com.zenger.cookbook.viewmodels.DiscoverViewModel
import com.zenger.cookbook.viewmodels.factories.DiscoverViewModelFactory
import timber.log.Timber


class DiscoverFragment : Fragment(), RecyclerAdapter.OnItemClickListener {

    private val factory by lazy { DiscoverViewModelFactory(requireActivity().application) }
    private val viewModel: DiscoverViewModel by viewModels { factory }
    private lateinit var binding: FragmentDiscoverBinding

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {

        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_discover, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val adapter = RecyclerAdapter(this)

        binding.apply {
            recyclerView.layoutManager = StaggeredGridLayoutManager(2, RecyclerView.VERTICAL)
            recyclerView.setHasFixedSize(true)
            recyclerView.adapter = adapter
        }

        Timber.d("Lookie hereeeeee mofo")

        viewModel.randomRecipes.observe(viewLifecycleOwner) {
            adapter.submitData(viewLifecycleOwner.lifecycle, it)
        }

        Timber.d("Lookie hereeeeee!!!")
        val layout = binding.toolBar
        val toolbar = layout.findViewById<MaterialToolbar>(R.id.toolBar)
        (requireActivity() as AppCompatActivity).setSupportActionBar(toolbar)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        if (viewModel.listState != null) {
            binding.recyclerView.layoutManager?.onRestoreInstanceState(viewModel.listState)
            viewModel.listState = null
        }

    }

    override fun onDestroyView() {
        viewModel.listState = binding.recyclerView.layoutManager?.onSaveInstanceState()
        super.onDestroyView()

    }

    override fun onItemClick(recipe: RecipeTable) {
        val view = requireActivity().findViewById<View>(R.id.constraintLayout)
        val snackBar = Snackbar.make(view, "Clicked", Snackbar.LENGTH_LONG)
        snackBar.anchorView = view.findViewById(R.id.bottomNav)
        snackBar.show()

    }
}