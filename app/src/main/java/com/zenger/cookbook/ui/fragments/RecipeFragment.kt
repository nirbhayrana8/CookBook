package com.zenger.cookbook.ui.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.navigation.navGraphViewModels
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.appbar.MaterialToolbar
import com.google.android.material.snackbar.Snackbar
import com.zenger.cookbook.R
import com.zenger.cookbook.adapters.RecyclerViewAdapter
import com.zenger.cookbook.databinding.FragmentRecipeBinding
import com.zenger.cookbook.viewmodels.MyRecipeViewModel
import com.zenger.cookbook.viewmodels.factories.MyRecipeViewModelFactory

class RecipeFragment : Fragment(), RecyclerViewAdapter.Interaction {

    private lateinit var binding: FragmentRecipeBinding
    private val adapter = RecyclerViewAdapter(this)
    private val factory by lazy { MyRecipeViewModelFactory(requireActivity().application) }
    private val viewModel: MyRecipeViewModel by navGraphViewModels(R.id.app_flow_nav) { factory }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View {

        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_recipe, container, false)

        binding.recyclerView.layoutManager = LinearLayoutManager(context, RecyclerView.VERTICAL, false)
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

        /*viewModel.recipes.observe(viewLifecycleOwner, {
            adapter.submitList(it)
        })*/
    }

    override fun onDestroyView() {
        viewModel.listState = binding.recyclerView.layoutManager?.onSaveInstanceState()
        super.onDestroyView()
    }

    override fun onItemSelected(position: Int) {
        val view = requireActivity().findViewById<View>(R.id.constraintLayout)
        val snackBar = Snackbar.make(view, "Clicked", Snackbar.LENGTH_LONG)
        snackBar.anchorView = view.findViewById(R.id.bottomNav)
        snackBar.show()
    }

}