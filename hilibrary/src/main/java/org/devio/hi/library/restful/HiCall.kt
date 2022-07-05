package org.devio.hi.library.restful

interface HiCall<T> {

 fun execute() : HiResponse<T>
 fun enqueue(callBack : HiCallBack<T>)

 interface Factory{
 fun newCall(request : HiRequest) : HiCall<*>
 }
}