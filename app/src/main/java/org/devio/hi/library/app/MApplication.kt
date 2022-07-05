package org.devio.hi.library.app

import android.app.Application
import com.google.gson.Gson
import org.devio.hi.library.log.HiFilePrinter
import org.devio.hi.library.log.HiLogConfig
import org.devio.hi.library.log.HiLogConsolePrinter
import org.devio.hi.library.log.HiLogManager

/* 继承 使用":" 表明继承自哪个类, 这里Animal要写() 的原因是父类没有显式的构造函数, 所以系统加了一个默认的空参的构造函数
*  https://blog.csdn.net/c1392851600/article/details/80990570
*/

class MApplication : Application() {
    override fun onCreate() {
        super.onCreate()
        HiLogManager.init(object : HiLogConfig() {
            override fun injectJsonParser(): JsonParser {
                return JsonParser { src -> Gson().toJson(src) }
            }
            override fun getGlobaleTag(): String {
                return "MApplication"
            }
            override fun enable(): Boolean {
                return true
            }

            override fun stackTraceDept(): Int {
                return 5;
            }
        }, HiLogConsolePrinter(), HiFilePrinter(applicationContext.cacheDir.absolutePath, 0))
    }


}