package com.zenger.cookbook.ui.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.zenger.cookbook.R
import com.zenger.cookbook.viewmodels.SplashViewModel

class SplashFragment : Fragment() {

    private val viewModel: SplashViewModel by viewModels()

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {

        return inflater.inflate(R.layout.fragment_splash, container, false)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        viewModel.checkIfUserIsAuthenticated()
        viewModel.isUserAuthenticatedLiveData.observe(viewLifecycleOwner, { user ->

            if (!user.isAuthenticated) {
                findNavController().navigate(R.id.loginHostFragment)
            } else {
                viewModel.getUserFromDatabase(user.uid)
                viewModel.userLiveData.observe(viewLifecycleOwner, {
                    findNavController().navigate(R.id.appFlowHostFragment)
                })
            }
        })
    }


}