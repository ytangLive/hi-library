package org.devio.hi.library.restful

interface HiInterceptor {

 fun interceptor(chain : Chain) : Boolean

 interface Chain {

 val isRequestPeriod:Boolean get() = false

 fun request() : HiRequest

 /**
  * 这个response对象 在网络发起之前 ，是为空的
  */
 fun response() : HiResponse<*>?

 }
}