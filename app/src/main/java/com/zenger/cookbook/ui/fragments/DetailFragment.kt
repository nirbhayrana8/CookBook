package com.zenger.cookbook.ui.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.zenger.cookbook.R
import com.zenger.cookbook.databinding.FragmentDetailBinding
import com.zenger.cookbook.viewmodels.DetailViewModel
import com.zenger.cookbook.viewmodels.DetailViewModelFactory

class DetailFragment : Fragment() {

    private lateinit var binding: FragmentDetailBinding
    private lateinit var viewModel: DetailViewModel

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {

        val viewModelFactory = DetailViewModelFactory(
            DetailFragmentArgs.fromBundle(requireArguments()).sourceUrl?:"")
        viewModel = ViewModelProvider(this, viewModelFactory).get(DetailViewModel::class.java)

        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_detail, container, false)
        binding.viewModel = viewModel

        return binding.root
    }

}