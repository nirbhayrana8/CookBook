package com.zenger.cookbook.ui.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.addCallback
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.navigation.NavController
import androidx.navigation.fragment.NavHostFragment
import com.zenger.cookbook.R
import com.zenger.cookbook.databinding.FragmentAppFlowHostBinding


class AppFlowHostFragment : Fragment() {

    private val navHostFragment by lazy {
        childFragmentManager
                .findFragmentById(R.id.app_flow_nav_host_fragment) as NavHostFragment?
    }
    private val navController: NavController? by lazy { navHostFragment?.navController }

    private lateinit var binding: FragmentAppFlowHostBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        activity?.onBackPressedDispatcher?.addCallback {
            navController?.navigateUp()

            val backStackId = navController?.currentBackStackEntry?.destination?.id
            binding.bottomNav.apply {
                selectedItemId = if (backStackId == R.id.discoverFragment) R.id.discover else R.id.saved

            }
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

        navController?.run {
            when (id) {

                R.id.discover -> this.navigate(R.id.discoverFragment)

                R.id.saved -> this.navigate(R.id.recipeFragment)
            }
        }

    }


}
