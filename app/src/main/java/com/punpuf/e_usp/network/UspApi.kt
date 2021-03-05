package com.punpuf.e_usp.network

import com.github.scribejava.core.builder.api.DefaultApi10a

class UspApi : DefaultApi10a() {

    companion object {
        const val API_KEY = "cetilq"
        const val API_SECRET = "pOQYX8kg5hTxQiGjSHBcYwcfSgtUmWapVkPm1TCR"
        const val AUTHENTICATE_URL = "https://uspdigital.usp.br/wsusuario/oauth/usuariousp"
        const val REDIRECT_URL_PREFIX = "http://localhost"

        const val PARAMETER_OAUTH_VERIFIER = "oauth_verifier"
        const val PARAMETER_OAUTH_TOKEN = "oauth_token"

        private const val AUTHORIZE_BASE_URL = "https://uspdigital.usp.br/wsusuario/oauth/authorize"
        private const val REQUEST_TOKEN_RESOURCE = "https://uspdigital.usp.br/wsusuario/oauth/request_token"
        private const val ACCESS_TOKEN_RESOURCE = "https://uspdigital.usp.br/wsusuario/oauth/access_token"
    }

    override fun getRequestTokenEndpoint(): String {
        return REQUEST_TOKEN_RESOURCE
    }

    override fun getAccessTokenEndpoint(): String {
        return ACCESS_TOKEN_RESOURCE
    }

    override fun getAuthorizationBaseUrl(): String {
        return AUTHORIZE_BASE_URL
    }
















    /*override fun getAuthorizationUrl(token: Token): String {
        val arrobject = arrayOf<Any>(token.getToken())
        return String.format(
            AUTHORIZE_URL,
            *arrobject
        )
    }*/

    /*class Authenticate : SSL() {
        override fun getAuthorizationUrl(token: Token): String {
            val arrobject = arrayOf<Any>(token.getToken())
            return String.format(
                AUTHENTICATE_URL,
                *arrobject
            )
        }
    }*/

    /*class Authorize : SSL()
    class SSL : USPApi() {
        override fun getAccessTokenEndpoint(): String {
            return "https://uspdigital.usp.br/wsusuario/oauth/access_token"
        }

        override fun getRequestTokenEndpoint(): String {
            return "https://uspdigital.usp.br/wsusuario/oauth/request_token"
        }
    }*/

}