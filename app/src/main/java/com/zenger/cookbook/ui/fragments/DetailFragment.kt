package com.zenger.cookbook.ui.fragments

import android.graphics.Color
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.navArgs
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions
import com.google.android.material.snackbar.Snackbar
import com.zenger.cookbook.R
import com.zenger.cookbook.api.state.Result
import com.zenger.cookbook.databinding.FragmentDetailBinding
import com.zenger.cookbook.viewmodels.DetailViewModel
import com.zenger.cookbook.viewmodels.factories.DetailViewModelFactory

class DetailFragment : Fragment(R.layout.fragment_detail) {

    private val factory by lazy { DetailViewModelFactory(requireActivity().application) }
    private val viewModel by viewModels<DetailViewModel> { factory }

    private val args by navArgs<DetailFragmentArgs>()
    private lateinit var binding: FragmentDetailBinding

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_detail, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val recipe = args.recipeObject

        viewModel.getRecipeInstructions(recipe.id)

        binding.apply {
            Glide.with(this@DetailFragment)
                    .load(recipe.imageUrl)
                    .centerCrop()
                    .transition(DrawableTransitionOptions.withCrossFade())
                    .error(R.drawable.ic_baseline_error_24)
                    .into(imageView)

            toolBar.title = recipe.title
        }

        binding.fab.setOnClickListener {
            if (binding.fab.isChecked)
                generateSnackBar(getString(R.string.recipe_saved))
            else
                generateSnackBar(getString(R.string.recipe_deleted))
        }

        viewModel.outcome.observe(viewLifecycleOwner) {

            when (it) {
                is Result.Progress -> {
                    binding.apply {
                        progressBar.isVisible = it.loading
                        container.isVisible = !(it.loading)
                    }
                }

                is Result.Success -> {
                    binding.apply {
                        progressBar.isVisible = false
                        container.isVisible = true
                    }
                    viewModel.apply {
                        steps.observe(viewLifecycleOwner) { step ->
                            binding.stepsTextView.text = step
                        }
                        ingredients.observe(viewLifecycleOwner) { ingredient ->
                            binding.ingredientsTextView.text = ingredient
                        }
                    }
                    viewModel.parseInstructions(it.data)
                }

                is Result.Failure -> {
                    binding.apply {
                        textViewError.isVisible = true
                        progressBar.isVisible = false
                    }
                }
            }
        }
    }

    private fun generateSnackBar(message: String) {
        Snackbar.make(binding.root, message, Snackbar.LENGTH_LONG)
                .setAction(getString(R.string.undo)) {

                }.setActionTextColor(Color.RED)
                .show()
    }

}