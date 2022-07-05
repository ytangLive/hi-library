package org.devio.hi.library.cache

import java.io.ByteArrayInputStream
import java.io.ByteArrayOutputStream
import java.io.ObjectInputStream
import java.io.ObjectOutputStream

object HiStorage {
 fun<T> saveCache(key: String, body: T){
 val cache = Cache()
 cache.key = key
 cache.data = toByteArray(body)
 CacheDatabase.get().cacheDao.saveCache(cache)
 }

 fun<T> getCache(key: String): T?{
 val cache = CacheDatabase.get().cacheDao.getCache(key)
 return (if(cache?.data != null)
 toObject(cache.data!!)
 else null) as? T
 }

 fun deleteCache(key: String){
 val cache = Cache()
 cache.key = key
 CacheDatabase.get().cacheDao.deleteCache(cache)
 }

 private fun <T> toByteArray(body: T): ByteArray? {
 var bos:ByteArrayOutputStream? = null
 var oos:ObjectOutputStream? = null
 try {
 bos = ByteArrayOutputStream()
 oos = ObjectOutputStream(bos)
 oos.writeObject(body)
 oos.flush()
 return bos.toByteArray()
 }catch (e: Exception) {
 e.printStackTrace()
 }finally {
 bos?.close()
 oos?.close()
 }

 return ByteArray(0)
 }

 private fun toObject(data: ByteArray): Any? {
 var bos:ByteArrayInputStream? = null
 var oos:ObjectInputStream? = null
 try {
 bos = ByteArrayInputStream(data)
 oos = ObjectInputStream(bos)
 return oos.readObject()
 }catch (e: Exception) {
 e.printStackTrace()
 }finally {
 bos?.close()
 oos?.close()
 }
 return null
 }

}