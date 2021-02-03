package com.zenger.cookbook.ui.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.navigation.navGraphViewModels
import com.zenger.cookbook.R
import com.zenger.cookbook.adapters.LoadStateAdapter
import com.zenger.cookbook.adapters.SavedRecipesAdapter
import com.zenger.cookbook.api.models.Recipe
import com.zenger.cookbook.databinding.FragmentRecipeBinding
import com.zenger.cookbook.room.tables.SavedRecipeTable
import com.zenger.cookbook.viewmodels.MyRecipeViewModel
import com.zenger.cookbook.viewmodels.factories.MyRecipeViewModelFactory
import kotlinx.coroutines.launch

class RecipeFragment : Fragment(R.layout.fragment_recipe), SavedRecipesAdapter.OnItemClickListener {

    private val factory by lazy { MyRecipeViewModelFactory(requireActivity().application) }
    private val viewModel: MyRecipeViewModel by navGraphViewModels(R.id.app_flow_nav) { factory }
    private lateinit var binding: FragmentRecipeBinding

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_recipe, container, false)

        val adapter = SavedRecipesAdapter(this)

        binding.apply {
            recyclerView.setHasFixedSize(true)
            recyclerView.adapter = adapter.withLoadStateHeaderAndFooter(
                    header = LoadStateAdapter { adapter.retry() },
                    footer = LoadStateAdapter { adapter.retry() }
            )
        }

        lifecycleScope.launch {
            viewModel.savedRecipes.observe(viewLifecycleOwner) {
                adapter.submitData(viewLifecycleOwner.lifecycle, it)
            }
        }

        return binding.root
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

    override fun onItemClick(recipe: SavedRecipeTable) {
        val item = Recipe(id = recipe.itemId, title = recipe.title, imageUrl = recipe.imageUrl)
        val action = RecipeFragmentDirections.actionRecipeFragmentToDetailFragment(item)

        findNavController().navigate(action)
    }
}