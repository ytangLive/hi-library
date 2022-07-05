package org.devio.hi.library.util

import androidx.lifecycle.*
import java.util.concurrent.ConcurrentHashMap

object HiDataBus {

    private val eventMap = ConcurrentHashMap<String, StickyLiveData<*>>()
    fun <T> with(eventName: String): StickyLiveData<T> {
        var liveData = eventMap[eventName]
        if (liveData == null) {
            liveData = StickyLiveData<T>(eventName)
            eventMap[eventName] = liveData
        }
        return liveData as StickyLiveData<T>
    }

    class StickyLiveData<T>(private val eventName: String) : LiveData<T>() {

        internal var stickyData: T? = null
        internal var mVersion = 0

        fun setStickyData(value: T) {
            mVersion++
            stickyData = value
            setValue(value)
        }

        fun postStickyData(value: T) {
            mVersion++
            stickyData = value
            postValue(value)
        }

        override fun observe(owner: LifecycleOwner, observer: Observer<in T>) {
            observerSticky(owner, false, observer)
        }

        fun observerSticky(owner: LifecycleOwner, sticky: Boolean, observer: Observer<in T>) {
            owner.lifecycle.addObserver(LifecycleEventObserver { _, event ->
                if (event == Lifecycle.Event.ON_DESTROY) {
                    eventMap.remove(eventName)
                }
            })

            super.observe(owner, StickyObserver(this, sticky, observer))
        }
    }


    class StickyObserver<T>(
        private val stickyLiveData: HiDataBus.StickyLiveData<T>,
        val sticky: Boolean,
        private val observer: Observer<in T>
    ) : Observer<T> {

        private var lastVersion = stickyLiveData.mVersion
        override fun onChanged(t: T) {
            if (lastVersion >= stickyLiveData.mVersion) {
                //就说明stickyLiveData.没有更新的数据需要发送。
                if (sticky && stickyLiveData.stickyData != null) {
                    observer.onChanged(stickyLiveData.stickyData)
                }
                return
            }

            lastVersion = stickyLiveData.mVersion
            observer.onChanged(t)
        }
    }
}
