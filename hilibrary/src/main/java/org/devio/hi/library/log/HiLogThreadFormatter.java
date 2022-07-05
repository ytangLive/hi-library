package org.devio.hi.library.log;

public class HiLogThreadFormatter implements HiLogFormatter <Thread>{
 @Override
 public String format(Thread data) {
 return "Thread：" + data.getName();
 }
}
