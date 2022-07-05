package org.devio.hi.library.log;

import java.text.SimpleDateFormat;
import java.util.Locale;

public class HiLogModel {

 private SimpleDateFormat dateFormat = new SimpleDateFormat("yy-MM-dd HH:mm:ss", Locale.CHINA);
 public long timeMillis;
 public int level;
 public String tag;
 public String log;

 public HiLogModel(long timeMillis, int level, String tag, String log) {
 this.timeMillis = timeMillis;
 this.level = level;
 this.tag = tag;
 this.log = log;
 }

 public String flattenedLogs() {
 return getFlattened() + "\n" + log;
 }

 public String getFlattened(){
 return format(timeMillis) + "|" + this.level + "|" + this.tag + "|:";
 }

 public String format(long timeMillis){
 return dateFormat.format(timeMillis);
 }
}
