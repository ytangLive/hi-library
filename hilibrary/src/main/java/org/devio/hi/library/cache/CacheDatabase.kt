package org.devio.hi.library.cache

import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import org.devio.hi.library.util.AppGlobals

@Database(entities = [Cache::class], version = 1)
abstract class CacheDatabase: RoomDatabase() {

 companion object{
 private var dataBase:CacheDatabase
 fun get(): CacheDatabase{
 return dataBase
 }
 init {
 val context = AppGlobals.getApplication()!!.applicationContext
 dataBase = Room.databaseBuilder(context, CacheDatabase::class.java, "howow_cache").build()
 }
 }

 abstract val cacheDao:CacheDao
}