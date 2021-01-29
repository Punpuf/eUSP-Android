package com.punpuf.e_bandejao.ui

import android.graphics.Color
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.res.ResourcesCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.fragment.findNavController
import androidx.navigation.ui.NavigationUI
import com.google.android.material.transition.MaterialContainerTransform
import com.punpuf.e_bandejao.R
import com.punpuf.e_bandejao.model.SettingsViewModel
import com.punpuf.e_bandejao.util.Utils
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.android.synthetic.main.fragment_settings.*
import timber.log.Timber.d

@AndroidEntryPoint
class SettingsFragment : Fragment() {

    private val model: SettingsViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        sharedElementEnterTransition = MaterialContainerTransform().apply {
            duration = resources.getInteger(android.R.integer.config_shortAnimTime).toLong()
            drawingViewId = R.id.mainNavHostFragment
            scrimColor = Color.TRANSPARENT
            setAllContainerColors(ResourcesCompat.getColor(resources, R.color.colorPrimary, null))
        }

        return inflater.inflate(R.layout.fragment_settings, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        NavigationUI.setupWithNavController(settingsToolbar, NavHostFragment.findNavController(this))
        settingsToolbar?.title = ""
        findNavController().addOnDestinationChangedListener { _, _, _ ->
            settingsToolbar?.title = ""
        }

        settingsAccountLogoutBtn.setOnClickListener {
            model.logoutUser()
        }
    }

    override fun onResume() {
        super.onResume()
        model.userInfo.observe(viewLifecycleOwner) {
            when {
                // user not logged in
                it == null || it.numberUSP.isBlank() -> {
                    Utils.makeViewsGone(settingsAccountLogoutBtn,)
                    settingsAccountDescriptionTv.text = getString(R.string.settings_account_not_logged_in)
                }
                // user logged in
                else -> {
                    Utils.makeViewsVisible(settingsAccountLogoutBtn,)
                    settingsAccountDescriptionTv.text = getString(R.string.settings_account_description, it.name, it.numberUSP)
                }
            }
        }
    }

}
