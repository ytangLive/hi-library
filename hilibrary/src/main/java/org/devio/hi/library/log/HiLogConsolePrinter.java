package org.devio.hi.library.log;

import androidx.annotation.NonNull;
import static org.devio.hi.library.log.HiLogConfig.MAX_LEN;

import android.util.Log;

public class HiLogConsolePrinter implements HiLogPrinter{
 @Override
 public void print(@NonNull HiLogConfig config, @NonNull @HiLogType.TYPE int level, @NonNull String tag, @NonNull String printStr) {
 int len = printStr.length();
 int countOfSub = len/MAX_LEN;
 if(countOfSub == 0){
 Log.println(level, tag, printStr);
 }else if(countOfSub > 0){
 int index = 0;
 for (int i = 0; i < countOfSub; i++){
 Log.println(level, tag, printStr.substring(index, index + MAX_LEN));
 index += MAX_LEN;
 }
 if(index != len){
 Log.println(level, tag, printStr.substring(index, len));
 }
 }
 }
}
