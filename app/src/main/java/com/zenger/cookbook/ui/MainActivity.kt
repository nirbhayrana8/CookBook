package com.zenger.cookbook.ui

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.navigation.findNavController
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.zenger.cookbook.R
import com.zenger.cookbook.databinding.ActivityMainBinding
import com.zenger.cookbook.ui.fragments.DiscoverFragmentDirections
import com.zenger.cookbook.ui.fragments.RecipeFragmentDirections
import kotlinx.android.synthetic.main.toolbar_layout.*

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = DataBindingUtil.setContentView(this, R.layout.activity_main)

        val bottomNav = binding.bottomNav
        bottomNav.setOnNavigationItemSelectedListener(listener)

        val toolbar = toolBar
        setSupportActionBar(toolbar)
    }

    private val listener = BottomNavigationView.OnNavigationItemSelectedListener {

        when (it.itemId) {

            R.id.discover -> {
                if(findNavController(R.id.nav_host_fragment_container).currentDestination?.id != R.id.discoverFragment)
                    findNavController(R.id.nav_host_fragment_container)
                        .navigate(RecipeFragmentDirections.actionRecipeFragmentToDiscoverFragment())
            }

            R.id.saved -> {
                if (findNavController(R.id.nav_host_fragment_container).currentDestination?.id != R.id.recipeFragment)
                    findNavController(R.id.nav_host_fragment_container)
                        .navigate(DiscoverFragmentDirections.actionDiscoverFragmentToRecipeFragment())
            }

        }

        return@OnNavigationItemSelectedListener true
    }
}