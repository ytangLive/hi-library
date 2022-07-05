package org.devio.hi.library.restful

import org.devio.hi.library.cache.HiStorage
import org.devio.hi.library.executor.HiExecutor
import org.devio.hi.library.log.HiLog
import org.devio.hi.library.restful.annotation.CacheStrategy
import org.devio.hi.library.util.MainHandler

class Scheduler(
    private val factory: HiCall.Factory,
    private val interceptors: MutableList<HiInterceptor>
) {

    fun newCall(request: HiRequest): HiCall<*> {
        val call = factory.newCall(request)
        return ProxyCall(call, request)
    }

    internal inner class ProxyCall<T>(
        var delegate: HiCall<T>,
        var request: HiRequest
    ) : HiCall<T> {
        override fun execute(): HiResponse<T> {

            dispatchInterceptor(request, null)

            val response = delegate.execute()

            dispatchInterceptor(request, response)

            return response
        }

        override fun enqueue(callBack: HiCallBack<T>) {

            dispatchInterceptor(request, null)

            if (request.cacheStrategy == CacheStrategy.CACHE_FIRST) {
                HiExecutor.execute(runnable = Runnable {
                    val cacheResponse = readCache<T>()
                    if (cacheResponse.data != null) {
                        MainHandler.sendAtFrontOfQueue(Runnable {
                            callBack.onSuccess(cacheResponse)
                        })
                    }
                })

                HiLog.d("enqueue ,cache : " + request.cacheStrategyKey)
            }
            delegate.enqueue(object : HiCallBack<T> {
                override fun onSuccess(response: HiResponse<T>) {
                    dispatchInterceptor(request, response)

                    saveCacheIfNeed(response)
                    callBack.onSuccess(response)
                }

                override fun onFailed(throwable: Throwable) {
                    callBack.onFailed(throwable)
                }
            })
        }

        private fun <T> readCache(): HiResponse<T> {
            val cacheKey = request.getCacheKey()
            val cacheData = HiStorage.getCache<T>(cacheKey)
            val response = HiResponse<T>()
            response.code = HiResponse.CACHE_SUCCESS
            response.data = cacheData
            response.msg = "缓存获取成功"
            return response
        }

        private fun <T> saveCacheIfNeed(response: HiResponse<T>) {
            if ((request.cacheStrategy == CacheStrategy.CACHE_FIRST
                        || request.cacheStrategy == CacheStrategy.NET_CACHE)
                && response.data != null
            ) {
                HiExecutor.execute(runnable = Runnable {
                    HiStorage.saveCache(request.getCacheKey(), response.data)
                })
            }
        }

        private fun dispatchInterceptor(request: HiRequest, response: HiResponse<T>?) {
            if (interceptors.size <= 0)
                return
            InterceptorChain(request, response).dispatch()
        }

        internal inner class InterceptorChain(
            private val request: HiRequest,
            private val response: HiResponse<T>?

        ) : HiInterceptor.Chain {

            var callIndex: Int = 0

            override val isRequestPeriod: Boolean
                get() = response == null

            override fun request(): HiRequest {
                return request
            }

            override fun response(): HiResponse<*>? {
                return response
            }

            fun dispatch() {
                val interceptor = interceptors[callIndex]
                val flag = interceptor.interceptor(this)
                callIndex++
                if (callIndex < interceptors.size && !flag) {
                    dispatch()
                }
            }
        }
    }
}