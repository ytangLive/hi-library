package org.devio.hi.library.restful

import org.devio.hi.library.restful.annotation.*
import java.lang.reflect.Method
import java.lang.reflect.ParameterizedType
import java.lang.reflect.Type

class MethodParser(private val baseUrl: String, method: Method) {

    private var httpMethod: Int = 0
    private lateinit var domainUrl: String
    private lateinit var relativeUrl: String
    private var returnType: Type? = null
    private var headers: MutableMap<String, String> = mutableMapOf()
    private var parameters: MutableMap<String, String> = mutableMapOf()
    private var formPost: Boolean = false
    private var newRelativeUrl: String? = null
    private var cacheStrategy = CacheStrategy.NET_ONLY

    init {
        parseMethodAnnotations(method)

        parseMethodReturnType(method)

//parseMethodParameters(method, args)
    }

    fun newRequest(method: Method, args: Array<out Any>?): HiRequest {
        val arguments: Array<Any> = args as Array<Any>? ?: arrayOf()
        parseMethodParameters(method, arguments)

        val request = HiRequest()
        request.httpMethod = httpMethod
        request.domainUrl = domainUrl
        request.returnType = returnType
        request.relativeUrl = newRelativeUrl ?: relativeUrl
        request.parameters = parameters
        request.headers = headers
        request.httpMethod = httpMethod
        request.formPost = formPost
        request.cacheStrategy = cacheStrategy

        return request
    }

    private fun parseMethodAnnotations(method: Method) {
        val annotations = method.annotations
        for (annotation in annotations) {
            if (annotation is Get) {
                relativeUrl = annotation.value
                httpMethod = HiRequest.METHOD.GET
            } else if (annotation is Post) {
                relativeUrl = annotation.value
                formPost = annotation.formPost
                httpMethod = HiRequest.METHOD.POST
            } else if (annotation is Headers) {
                val headers = annotation.value
                for (value in headers) {
                    val colon = value.indexOf(":")
                    check(!(colon == 0 || colon == -1)) {
                        String.format(
                            "@headers value must be in the form [name:value] ,but found [%s]",
                            value
                        )
                    }
                    val keyValue = value.split(":")
                    this.headers[keyValue[0]] = keyValue[1]
                }
            } else if (annotation is BaseUrl) {
                domainUrl = annotation.value
            } else if (annotation is CacheStrategy) {
                cacheStrategy = annotation.value
            } else {
                throw IllegalStateException("cannot handle method annotation:" + annotation.javaClass.toString())
            }
        }

        require(httpMethod == HiRequest.METHOD.GET || httpMethod == HiRequest.METHOD.POST) {
            String.format("method %s must has one of GET,POST", method.name)
        }

        domainUrl = baseUrl

    }

    private fun parseMethodParameters(method: Method, args: Array<out Any>) {
        val parameterAnnotations = method.parameterAnnotations
        require(parameterAnnotations.size == args.size) {
            String.format(
                "arguments annotations count %s dont match expect count %s",
                parameterAnnotations.size, args.size
            )
        }

        for (index in args.indices) {
            val annotations = parameterAnnotations[index]
            require(annotations.size <= 1) { "filed can only has one annotation :index =$index" }

            val value = args[index]
            require(isPrimitive(value)) {
                "8 basic types are supported for now,index=$index"
            }

            val annotation = annotations[0]
            if (annotation is Filed) {
                val key = annotation.value
                parameters[key] = value.toString()
            } else if (annotation is Path) {
                val replaceName = annotation.value
                val replacement = value.toString()
                newRelativeUrl = relativeUrl.replace("{${replaceName}}", replacement)
            } else if (annotation is CacheStrategy) {
                cacheStrategy = value as Int
            } else {
                throw IllegalStateException("cannot handle parameter annotation :" + annotation.javaClass.toString())
            }
        }
    }

    private fun isPrimitive(value: Any): Boolean {

        if (value.javaClass == String::class.java) {
            return true
        }

        try {
            val field = value.javaClass.getField("TYPE")
            val clazz = field.get(null) as Class<*>
            if (clazz.isPrimitive) {
                return true
            }
        } catch (e: IllegalAccessException) {
            e.printStackTrace()
        } catch (e: NoSuchFieldException) {
            e.printStackTrace()
        }
        return false
    }

    private fun parseMethodReturnType(method: Method) {
        val returnType = method.returnType
        if (returnType != HiCall::class.java) {
            throw IllegalStateException(
                String.format(
                    "method %s must be type of HiCall.class", method.name
                )
            )
        }
        val genericReturnType = method.genericReturnType
        if (genericReturnType is ParameterizedType) {
            val actualTypeArguments = genericReturnType.actualTypeArguments
            require(actualTypeArguments.size == 1) {
                "method can only has one generic return type"
            }
            this.returnType = actualTypeArguments[0]
        } else {
            throw IllegalStateException(
                String.format(
                    "method %s must has one generic return type", method.name
                )
            )
        }
    }

    companion object {
        fun parse(baseUrl: String, method: Method): MethodParser {
            return MethodParser(baseUrl, method)
        }
    }
}