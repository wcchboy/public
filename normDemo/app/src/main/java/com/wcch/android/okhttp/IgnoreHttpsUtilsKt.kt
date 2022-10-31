package com.wcch.android.okhttp

import android.annotation.SuppressLint
import java.security.KeyManagementException
import java.security.NoSuchAlgorithmException
import java.security.SecureRandom
import java.security.cert.CertificateException
import java.security.cert.X509Certificate
import javax.net.ssl.*

class IgnoreHttpsUtilsKt private constructor(){

    var isDebug = false
    val TLS = "TLS"
    val SSL = "SSL"
    val SSLV2 = "SSLv2"

    companion object {
        fun getInstance() = IgnoreHttpsUtils.instance
    }

    private object IgnoreHttpsUtils {
        val instance = IgnoreHttpsUtilsKt()
    }

    val hostnameVerifier = HostnameVerifier { _, _ -> true }
    var sslContext : SSLContext? = null
    var sslSocketFactory : SSLSocketFactory? = null
    var trustAllCerts : Array<X509TrustManager>? = null

    init {
        trustAllCerts = arrayOf(object : X509TrustManager {
            @SuppressLint("TrustAllX509TrustManager")
            @Throws(CertificateException::class)
            override fun checkClientTrusted(chain: Array<out X509Certificate>?, authType: String?) {
            }

            @SuppressLint("TrustAllX509TrustManager")
            @Throws(CertificateException::class)
            override fun checkServerTrusted(chain: Array<out X509Certificate>?, authType: String?) {
            }

            @Throws(CertificateException::class)
            override fun getAcceptedIssuers(): Array<X509Certificate?> {
                return arrayOfNulls(0)
            }

        })
        try {
            sslContext = SSLContext.getInstance(TLS)
            sslContext!!.init(null, trustAllCerts, SecureRandom())
            sslSocketFactory = sslContext!!.socketFactory
        } catch (e: NoSuchAlgorithmException) {

        } catch (e: KeyManagementException) {

        }
    }
}