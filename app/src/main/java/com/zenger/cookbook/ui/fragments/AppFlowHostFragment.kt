package com.zenger.cookbook.ui.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.addCallback
import androidx.coordinatorlayout.widget.CoordinatorLayout
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.navigation.NavController
import androidx.navigation.findNavController
import androidx.navigation.fragment.NavHostFragment
import com.zenger.cookbook.R
import com.zenger.cookbook.databinding.FragmentAppFlowHostBinding


class AppFlowHostFragment : Fragment() {
    private lateinit var navController: NavController

    private lateinit var binding: FragmentAppFlowHostBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        activity?.onBackPressedDispatcher?.addCallback {
            val navHostFragment = childFragmentManager
                    .findFragmentById(R.id.app_flow_nav_host_fragment) as NavHostFragment?
            navHostFragment?.navController?.navigateUp()
        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View {

        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_app_flow_host, container, false)
        bottomNavInit()

        return binding.root
    }

    private fun bottomNavInit() {
        binding.bottomNav.setOnNavigationItemSelectedListener { item ->
            navigate(item.itemId)
            true
        }

        binding.bottomNav.setOnNavigationItemReselectedListener {
            return@setOnNavigationItemReselectedListener
        }
    }

    private fun navigate(id: Int) {

        val host = view?.findViewById<CoordinatorLayout>(R.id.app_start_fragment)
        if (host != null) {
            navController = host.findNavController()
        }

        when (id) {

            R.id.discover -> navController.navigate(R.id.discoverFragment)

            R.id.saved -> navController.navigate(R.id.recipeFragment)
        }


    }


}
