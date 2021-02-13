package com.zenger.cookbook.ui.fragments

import android.app.AlertDialog
import android.content.DialogInterface
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.preference.Preference
import androidx.preference.PreferenceFragmentCompat
import com.google.firebase.auth.FirebaseAuth
import com.zenger.cookbook.R

class SettingsFragment : PreferenceFragmentCompat() {

    override fun onCreatePreferences(savedInstanceState: Bundle?, rootKey: String?) {
        setPreferencesFromResource(R.xml.root_preferences, rootKey)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {

        val signOutPreference: Preference? = findPreference("sign_out")
        signOutPreference?.onPreferenceClickListener = Preference.OnPreferenceClickListener {
            AlertDialog.Builder(context)
                    .setTitle(R.string.sign_out)
                    .setMessage(R.string.sign_out_message)
                    .setPositiveButton(R.string.sign_out) { _: DialogInterface, _: Int ->
                        FirebaseAuth.getInstance().signOut()
                    }
                    .setNegativeButton(R.string.com_facebook_loginview_cancel_action) { dialog: DialogInterface, _: Int ->
                        dialog.dismiss()
                    }
                    .create()
                    .show()
            true
        }

        val deleteAccountPreference: Preference? = findPreference("delete_account")
        deleteAccountPreference?.onPreferenceClickListener = Preference.OnPreferenceClickListener {
            val dialog = DeleteAccountDialog()
            dialog.show(parentFragmentManager, "delete_dialog")
            true
        }

        return super.onCreateView(inflater, container, savedInstanceState)
    }
}