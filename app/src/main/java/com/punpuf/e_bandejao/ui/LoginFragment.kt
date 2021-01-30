package com.punpuf.e_bandejao.ui

import android.graphics.Color
import androidx.core.content.res.ResourcesCompat
import com.google.android.material.transition.MaterialContainerTransform
import android.content.res.Configuration
import android.net.Uri
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.webkit.WebView
import androidx.fragment.app.viewModels
import androidx.lifecycle.observe
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.fragment.findNavController
import androidx.navigation.ui.NavigationUI
import androidx.webkit.WebSettingsCompat
import androidx.webkit.WebViewClientCompat
import androidx.webkit.WebViewFeature
import com.punpuf.e_bandejao.R
import com.punpuf.e_bandejao.model.LoginViewModel
import com.punpuf.e_bandejao.network.UspApi
import com.punpuf.e_bandejao.vo.LoginAccessTokenBundle
import com.punpuf.e_bandejao.vo.LoginRequestTokenBundle
import com.punpuf.e_bandejao.vo.Resource
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.android.synthetic.main.fragment_login.*
import kotlinx.android.synthetic.main.fragment_restaurant_list.*
import timber.log.Timber.d
import timber.log.Timber.e

@Suppress("COMPATIBILITY_WARNING")
@AndroidEntryPoint
class LoginFragment : Fragment() {

    private val model: LoginViewModel by viewModels()
    private var requestTokenBundle: LoginRequestTokenBundle? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        sharedElementEnterTransition = MaterialContainerTransform().apply {
            duration = resources.getInteger(android.R.integer.config_mediumAnimTime).toLong()
            drawingViewId = R.id.mainNavHostFragment
            scrimColor = Color.TRANSPARENT
            setAllContainerColors(ResourcesCompat.getColor(resources, R.color.colorPrimary, null))
        }

        return inflater.inflate(R.layout.fragment_login, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        NavigationUI.setupWithNavController(loginToolbar, NavHostFragment.findNavController(this))
        loginToolbar.title = ""
        findNavController().addOnDestinationChangedListener { _, _, _ ->
            restaurantListToolbar?.title = ""
        }

        // force dark mode inside webview, if device is in dark mode
        if (WebViewFeature.isFeatureSupported(WebViewFeature.FORCE_DARK)) {
            when (context?.resources?.configuration?.uiMode?.and(Configuration.UI_MODE_NIGHT_MASK)) {
                Configuration.UI_MODE_NIGHT_YES -> {
                    WebSettingsCompat.setForceDark(loginWebView.settings, WebSettingsCompat.FORCE_DARK_ON)
                }
                else -> {
                    WebSettingsCompat.setForceDark(loginWebView.settings, WebSettingsCompat.FORCE_DARK_OFF)
                }
            }
        }
        loginWebView.webViewClient = LoginWebViewClient()
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        model.requestTokenBundle.observe(viewLifecycleOwner) {
            when(it.status) {
                Resource.Status.SUCCESS -> {
                    d("requestTokenBundle -> SUCCESS")
                    requestTokenBundle = it.data
                    loginWebView.loadUrl(requestTokenBundle?.authEndpointUrl ?: "")
                    updateUiWithWebView()
                }
                Resource.Status.LOADING -> {
                    d("requestTokenBundle -> LOADING")
                    updateUiWithLoading(getString(R.string.login_state_fetching_auth_url))
                }
                Resource.Status.ERROR -> {
                    d("requestTokenBundle -> ERROR")
                    updateUiWithError(getString(R.string.login_state_waiting_request_token_retry))
                }
            }
        }

        model.accessTokenState.observe(viewLifecycleOwner) {
            when(it.status) {
                Resource.Status.SUCCESS -> {
                    d("accessTokenState -> SUCCESS")
                    findNavController().popBackStack()
                }
                Resource.Status.LOADING -> {
                    d("accessTokenState -> LOADING")
                    updateUiWithLoading(getString(R.string.login_state_fetching_access_token))
                }
                Resource.Status.ERROR -> {
                    d("accessTokenState -> ERROR")
                    updateUiWithError(getString(R.string.login_state_waiting_access_token_retry))
                }
            }
        }
    }

    private fun updateUiWithWebView() {
        loginWebView.visibility = View.VISIBLE
        loginProgressBar.visibility = View.GONE
        loginStateTv.visibility = View.GONE
    }

    private fun updateUiWithLoading(loadingMessage: String) {
        loginWebView.visibility = View.GONE
        loginProgressBar.visibility = View.VISIBLE
        loginStateTv.visibility = View.VISIBLE
        loginStateTv.text = loadingMessage
    }

    private fun updateUiWithError(errorMessage: String) {
        loginWebView.visibility = View.GONE
        loginProgressBar.visibility = View.GONE
        loginStateTv.visibility = View.VISIBLE
        loginStateTv.text = errorMessage
    }

    @Suppress("DEPRECATION")
    private inner class LoginWebViewClient : WebViewClientCompat() {

        override fun shouldOverrideUrlLoading(view: WebView?, url: String?): Boolean {
            val uri = Uri.parse(url)
            if (uri.toString().startsWith(UspApi.REDIRECT_URL_PREFIX)) {
                d("got the url back and it's: $url")
                val oauthVerifier = uri.getQueryParameter(UspApi.PARAMETER_OAUTH_VERIFIER)
                val oauthToken = uri.getQueryParameter(UspApi.PARAMETER_OAUTH_TOKEN)

                // should be the same, internal error, reload web view
                if (oauthToken != requestTokenBundle?.requestTokenToken) {
                    e("Web View's response request token != the internal one")
                    loginWebView.loadUrl(requestTokenBundle?.authEndpointUrl ?: "")
                    return true
                }

                val accessTokenBundle = LoginAccessTokenBundle(
                    requestTokenBundle?.requestTokenToken ?: "",
                    requestTokenBundle?.requestTokenSecret ?: "",
                    oauthVerifier ?: "",
                )

                d("shouldOverrideUrlLoading -> accessTokenBundle: $accessTokenBundle")

                model.setAccessTokenBundle(accessTokenBundle)
                return true
            }
            return super.shouldOverrideUrlLoading(view, url)
        }
    }

}