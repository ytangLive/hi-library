package org.devio.hi.library.executor

import android.os.Handler
import android.os.Looper
import android.os.Message
import android.util.Log
import androidx.annotation.IntRange
import java.util.concurrent.*
import java.util.concurrent.atomic.AtomicLong
import java.util.concurrent.locks.Condition
import java.util.concurrent.locks.ReentrantLock

object HiExecutor {
    private val TAG: String = "HiExecutor"
    private var lock: ReentrantLock = ReentrantLock()
    private var condition: Condition = lock.newCondition()
    private var executor: ThreadPoolExecutor

    @Volatile
    private var isPaused = false
    private var mainHandler: Handler

    init {
        val cpuNum = Runtime.getRuntime().availableProcessors()
        val coreThreadSize = cpuNum + 1
        val maximumPoolSize = cpuNum * 2 + 1
        val keepAliveTime = 30L
        val blockQueue: PriorityBlockingQueue<out Runnable> = PriorityBlockingQueue()

        mainHandler = object : Handler(Looper.getMainLooper()) {
            override fun dispatchMessage(msg: Message) {
                super.dispatchMessage(msg)
                try {
                    lock.lock()
                    condition.signalAll()
                } finally {
                    lock.unlock()
                }
            }
        }

        var seq = AtomicLong()
        val threadFactory = ThreadFactory {
            val thread = Thread(it)
            thread.name = "hi-executor-" + seq.getAndIncrement()
            return@ThreadFactory thread
        }

        executor = object : ThreadPoolExecutor(
            coreThreadSize,
            maximumPoolSize,
            keepAliveTime,
            TimeUnit.SECONDS,
            blockQueue as BlockingQueue<Runnable>,
            threadFactory
        ) {

            override fun beforeExecute(t: Thread?, r: Runnable?) {
                super.beforeExecute(t, r)
                try {
                    lock.lock()
                    if (isPaused) {
                        condition.await()
                    }
                } finally {
                    lock.unlock()
                }
            }

            override fun afterExecute(r: Runnable?, t: Throwable?) {
                super.afterExecute(r, t)
                Log.e(TAG, "已执行完的任务的优先级是：" + (r as PriorityRunnable).priority)
            }
        }
    }

    @JvmOverloads
    fun execute(@IntRange(from = 0, to = 10) priority: Int = 0, runnable: Runnable) {
        executor.execute(PriorityRunnable(priority, runnable))
    }

    @JvmOverloads
    fun execute(@IntRange(from = 0, to = 10) priority: Int = 0, runnable: Callable<*>) {
        executor.execute(PriorityRunnable(priority, runnable))
    }

    @Synchronized
    fun pause() {
        isPaused = true
    }

    fun resume() {
        isPaused = false
        try {
            lock.lock()
            condition.signalAll()
        } finally {
            lock.unlock()
        }
    }

    abstract class Callable<T> : Runnable {

        override fun run() {
            mainHandler.post { onPrepare() }

            try {
                lock.lock()
                condition.await()
            } finally {
                lock.unlock()
            }

            val t = onBackground()

            mainHandler.post { onCompleted(t) }
        }

        open fun onPrepare() {
            Log.e(TAG, "转菊花")
        }

        abstract fun onBackground(): T

        abstract fun onCompleted(t: T)

    }

    class PriorityRunnable(val priority: Int, private val runnable: Runnable) : Runnable,
        Comparable<PriorityRunnable> {
        override fun compareTo(other: PriorityRunnable): Int {
            return if (this.priority < other.priority) -1 else if (this.priority > other.priority) 1 else 0
        }

        override fun run() {
            runnable.run()
        }

    }
}