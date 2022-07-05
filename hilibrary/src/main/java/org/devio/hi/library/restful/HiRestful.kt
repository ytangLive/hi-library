package org.devio.hi.library.restful

import java.lang.reflect.Method
import java.lang.reflect.Proxy
import java.util.concurrent.ConcurrentHashMap

class HiRestful constructor(private val baseUrl: String, private val factory: HiCall.Factory) {

 private val methodService:ConcurrentHashMap<Method, MethodParser> = ConcurrentHashMap()

 private val interceptors:MutableList<HiInterceptor> = mutableListOf()

 private var scheduler:Scheduler = Scheduler(factory, interceptors)

 fun addInterceptor(interceptor: HiInterceptor){
 interceptors.add(interceptor)
 }

 fun <T> create(service: Class<T>): T {
 return Proxy.newProxyInstance(service.classLoader, arrayOf<Class<*>>(service)
 ) { proxy, method:Method, args ->

 var methodParser = methodService[method]
 if(methodParser == null){
 methodParser = MethodParser.parse(baseUrl, method)
 methodService[method] = methodParser
 }

 val request = methodParser.newRequest(method, args)
 scheduler.newCall(request)

 } as T


 }

}