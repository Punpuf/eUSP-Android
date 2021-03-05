package com.punpuf.e_usp.ui

import android.content.res.Configuration
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.updatePadding
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.setupWithNavController
import com.google.android.material.bottomnavigation.LabelVisibilityMode.LABEL_VISIBILITY_UNLABELED
import com.punpuf.e_usp.Const
import com.punpuf.e_usp.R
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.android.synthetic.main.layout_main.*

@AndroidEntryPoint
class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.layout_main)

        window.decorView.apply {
            systemUiVisibility =
                this.systemUiVisibility or
                View.SYSTEM_UI_FLAG_LAYOUT_STABLE or
                View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
        }
        window.setBackgroundDrawableResource(R.color.colorPrimary)

        val navHostFrag = supportFragmentManager.findFragmentById(R.id.mainNavHostFragment) as NavHostFragment
        mainBottomNav.setupWithNavController(navHostFrag.navController)

        if (resources.configuration.orientation == Configuration.ORIENTATION_LANDSCAPE) {
            mainBottomNav.labelVisibilityMode = LABEL_VISIBILITY_UNLABELED
        }

        ViewCompat.setOnApplyWindowInsetsListener(mainLinearLayout) { _, insets ->
            mainBottomNav.updatePadding(bottom = insets.systemWindowInsets.bottom)
            insets
        }

        if (intent?.data.toString() in listOf(Const.SHORTCUT_QRCODE_INTENT_DATA, Const.SHORTCUT_BARCODE_INTENT_DATA)) {
            val controller = (supportFragmentManager.findFragmentById(R.id.mainNavHostFragment) as NavHostFragment).navController
            val graph = controller.navInflater.inflate(R.navigation.nav_graph)
            graph.startDestination = R.id.cardFragment
            controller.graph = graph
        }
    }
    
}