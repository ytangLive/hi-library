package org.devio.hi.library.restful

import android.os.Build
import android.text.TextUtils
import androidx.annotation.IntDef
import androidx.annotation.RequiresApi
import org.devio.hi.library.restful.annotation.CacheStrategy
import java.lang.StringBuilder
import java.lang.reflect.Type
import java.net.URLEncoder

open class HiRequest {

    @METHOD
    var httpMethod: Int = 0
    var domainUrl: String? = null
    var relativeUrl: String? = null
    var returnType: Type? = null
    var headers: MutableMap<String, String>? = null
    var parameters: MutableMap<String, String>? = null
    var formPost: Boolean = false
    var cacheStrategy = CacheStrategy.NET_ONLY
    var cacheStrategyKey: String = ""

    @IntDef(value = [METHOD.GET, METHOD.POST])
    annotation class METHOD {
        companion object {
            const val GET = 0
            const val POST = 1
        }
    }

    /**
     * //scheme-host-port:443
    //https://api.devio.org/v1/ ---relativeUrl: user/login===>https://api.devio.org/v1/user/login

    //可能存在别的域名的场景
    //https://api.devio.org/v2/


    //https://api.devio.org/v1/ ---relativeUrl: /v2/user/login===>https://api.devio.org/v2/user/login
     */
    fun endPointUrl(): String {
        if (relativeUrl == null) {
            throw IllegalStateException("relative url must bot be null ")
        }
        if (!relativeUrl!!.startsWith("/")) {
            return domainUrl + relativeUrl
        }

        val indexOf = domainUrl!!.indexOf("/")
        return domainUrl!!.substring(0, indexOf) + relativeUrl
    }

    fun addHeader(key: String, value: String) {
        if (headers == null) {
            headers = mutableMapOf()
        }
        headers!![key] = value
    }

    fun getCacheKey(): String {
        if (!TextUtils.isEmpty(cacheStrategyKey)) {
            return cacheStrategyKey
        }
        val build = StringBuilder()
        val endUrl = endPointUrl()
        build.append(endUrl)

        if (endUrl.indexOf("?") > 0 || endUrl.indexOf("&") > 0) {
            build.append("&")
        } else {
            build.append("?")
        }

        if (parameters != null) {
            for ((key, value) in parameters!!) {
                val encodeValue = URLEncoder.encode(value, "UTF-8")
                build.append("$key=$encodeValue&")
            }
            build.deleteCharAt(build.length - 1)

            cacheStrategyKey = build.toString()
        } else {
            cacheStrategyKey = endUrl
        }

        return cacheStrategyKey
    }

}