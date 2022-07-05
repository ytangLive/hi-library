package org.devio.hi.library.util

import android.os.Handler
import android.os.Looper
import android.os.Message

object MainHandler {

 private val handler = Handler(Looper.getMainLooper())

 fun post(runnable: Runnable){
 handler.post(runnable)
 }

 fun postDelay(runnable: Runnable, delayMills: Long) {
 handler.postDelayed(runnable, delayMills)
 }

 fun sendAtFrontOfQueue(runnable: Runnable){
 val message = Message.obtain(handler, runnable)
 handler.sendMessageAtFrontOfQueue(message)
 }

 fun remove(runnable: Runnable){
 handler.removeCallbacks(runnable)
 }
}