package org.devio.hi.library.restful

/**
 * callbak 回调
 */
interface HiCallBack<T> {
 fun onSuccess(response:HiResponse<T>)
 fun onFailed(throwable: Throwable)
}