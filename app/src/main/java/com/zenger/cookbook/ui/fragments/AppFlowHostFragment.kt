package com.zenger.cookbook.ui.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.navigation.NavController
import androidx.navigation.Navigation
import com.zenger.cookbook.R
import com.zenger.cookbook.databinding.FragmentAppFlowHostBinding


class AppFlowHostFragment : Fragment() {
    private lateinit var navController: NavController
    private lateinit var binding: FragmentAppFlowHostBinding

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {

        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_app_flow_host, container, false)
        bottomNavInit()

        return binding.root
    }

    private fun bottomNavInit() {
        binding.bottomNav.setOnNavigationItemSelectedListener { item ->
            when (item.itemId) {
                R.id.discover -> navController.navigate(RecipeFragmentDirections.actionRecipeFragmentToDiscoverFragment())

                R.id.saved -> navController.navigate(DiscoverFragmentDirections.actionDiscoverFragmentToRecipeFragment())
            }
            true
        }

        binding.bottomNav.setOnNavigationItemReselectedListener {
            return@setOnNavigationItemReselectedListener
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val container = view.findViewById<View>(R.id.app_flow_nav_host_fragment)
        navController = Navigation.findNavController(container)
    }

}
