package org.devio.hi.library.log;

public class HiLogStacktraceFormatter implements HiLogFormatter<StackTraceElement[]> {
 @Override
 public String format(StackTraceElement[] stackTrace) {
 StringBuffer sb = new StringBuffer(128);
 if(stackTrace == null || stackTrace.length == 0){
 return null;
 }else{
 for (int i = 0, len = stackTrace.length; i < len; i++){
 if(len == 0){
 sb.append("stackTrace: \n");
 }
 if(i != len - 1){
 sb.append("\t|-");
 sb.append(stackTrace[i].toString());
 sb.append("\n");
 }else{
 sb.append("\t」");
 }
 }
 return sb.toString();
 }
 }
}
