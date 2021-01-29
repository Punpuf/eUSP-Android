package com.punpuf.e_bandejao.ui

import android.content.Intent
import android.content.SharedPreferences
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.preference.Preference
import androidx.preference.PreferenceFragmentCompat
import androidx.preference.PreferenceManager
import com.punpuf.e_bandejao.R
import com.punpuf.e_bandejao.util.Utils
import timber.log.Timber.d

class PrefsFragment : PreferenceFragmentCompat(), SharedPreferences.OnSharedPreferenceChangeListener {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        PreferenceManager.getDefaultSharedPreferences(context).registerOnSharedPreferenceChangeListener(this)
    }

    override fun onDestroy() {
        super.onDestroy()
        PreferenceManager.getDefaultSharedPreferences(context).unregisterOnSharedPreferenceChangeListener(this)
    }

    override fun onCreatePreferences(savedInstanceState: Bundle?, rootKey: String?) {
        setPreferencesFromResource(R.xml.settings_prefs, rootKey)
    }

    override fun onPreferenceTreeClick(preference: Preference?): Boolean {
        if(preference?.key != null) {
            when (preference.key) {
                getString(R.string.prefs_privacy_key) -> {
                    val intent = Intent(Intent.ACTION_VIEW).apply {
                        data = Uri.parse(getString(R.string.prefs_privacy_url))
                    }
                    startActivity(intent)
                }

                getString(R.string.prefs_libraries_key) -> {
                    val intent = Intent(Intent.ACTION_VIEW).apply {
                        data = Uri.parse(getString(R.string.prefs_libraries_url))
                    }
                    startActivity(intent)
                }

                getString(R.string.prefs_feedback_key) -> {
                    val intent = Intent(Intent.ACTION_VIEW).apply {
                        data = Uri.parse(getString(R.string.prefs_feedback_url))
                    }
                    startActivity(intent)
                }

                getString(R.string.prefs_about_key) -> {
                    val intent = Intent(Intent.ACTION_VIEW).apply {
                        data = Uri.parse(getString(R.string.prefs_about_url))
                    }
                    startActivity(intent)
                }
            }
        }
        return super.onPreferenceTreeClick(preference)
    }

    override fun onSharedPreferenceChanged(sharedPreferences: SharedPreferences?, key: String?) {
        when (key) {
            getString(R.string.prefs_qr_code_update_key) -> { Utils.updateTokenUpdater(sharedPreferences, requireContext()) }
            getString(R.string.prefs_ui_mode_key) -> { Utils.updateUiMode(sharedPreferences, context) }
        }
    }
}