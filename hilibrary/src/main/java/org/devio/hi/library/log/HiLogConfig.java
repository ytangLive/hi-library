package org.devio.hi.library.log;

public abstract class HiLogConfig {

 static int MAX_LEN = 512;
 static HiLogThreadFormatter HI_LOG_THREAD_FORMATTER = new HiLogThreadFormatter();
 static HiLogStacktraceFormatter HI_LOG_STACK_TRACE_FORMATTER = new HiLogStacktraceFormatter();
 public JsonParser injectJsonParser(){
 return null;
 }

 public interface JsonParser {
 public String toJson(Object json);
 }

 public String getGlobaleTag(){
 return "HiLog";
 }
 public boolean enable(){
 return true;
 }
 public boolean includeThread() {
 return true;
 }
 public int stackTraceDept(){
 return 5;
 }

 public HiLogPrinter[] printers(){
 return null;
 }

}
