package com.punpuf.e_bandejao.ui

import android.os.Bundle
import androidx.preference.PreferenceFragmentCompat
import com.punpuf.e_bandejao.R

class PrefsFragment : PreferenceFragmentCompat() {
    override fun onCreatePreferences(savedInstanceState: Bundle?, rootKey: String?) {
        setPreferencesFromResource(R.xml.settings_prefs, rootKey)
    }
}