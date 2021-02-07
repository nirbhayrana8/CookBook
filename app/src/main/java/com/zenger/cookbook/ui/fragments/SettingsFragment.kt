package com.zenger.cookbook.ui.fragments

import android.os.Bundle
import androidx.preference.PreferenceFragmentCompat
import com.zenger.cookbook.R

class SettingsFragment : PreferenceFragmentCompat() {

    override fun onCreatePreferences(savedInstanceState: Bundle?, rootKey: String?) {
        setPreferencesFromResource(R.xml.root_preferences, rootKey)
    }
}