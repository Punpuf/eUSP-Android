package com.punpuf.e_bandejao.ui

import android.graphics.Color
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.res.ResourcesCompat
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.fragment.findNavController
import androidx.navigation.ui.NavigationUI
import androidx.preference.PreferenceFragmentCompat
import com.google.android.material.transition.MaterialContainerTransform
import com.punpuf.e_bandejao.R
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.android.synthetic.main.fragment_settings.*

@AndroidEntryPoint
class SettingsFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        sharedElementEnterTransition = MaterialContainerTransform().apply {
            duration = 2000 //resources.getInteger(android.R.integer.config_mediumAnimTime).toLong()
            drawingViewId = R.id.mainNavHostFragment
            scrimColor = Color.TRANSPARENT
            setAllContainerColors(ResourcesCompat.getColor(resources, R.color.colorPrimary, null))
        }

        return inflater.inflate(R.layout.fragment_settings, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        NavigationUI.setupWithNavController(settingsToolbar, NavHostFragment.findNavController(this))
        settingsToolbar.title = ""
        findNavController().addOnDestinationChangedListener { _, _, _ ->
            settingsToolbar?.title = ""
        }
    }
}
