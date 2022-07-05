package org.devio.hi.library.util

import android.app.Application
import java.lang.Exception

object AppGlobals {

    private var application: Application? = null

    fun getApplication(): Application? {
        if (application == null) {
            try {
                application =
                    Class.forName("android.app.ActivityThread").getMethod("currentApplication")
                        .invoke(null) as Application
            } catch (ex: Exception) {
                ex.printStackTrace()
            }
        }
        return application
    }
}