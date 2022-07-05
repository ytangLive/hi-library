package org.devio.hi.library.log;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class HiLogManager {
 public HiLogConfig config;
 public static HiLogManager instance;
 private List<HiLogPrinter> printers = new ArrayList<HiLogPrinter>();

 private HiLogManager(@NotNull HiLogConfig config, HiLogPrinter[] printers){
 this.config = config;
 this.printers.addAll(Arrays.asList(printers));
 }
 public static HiLogManager getInstance(){
 return instance;
 }
 public static void init(@NotNull HiLogConfig config, HiLogPrinter... printers){
 instance = new HiLogManager(config, printers);
 }
 public HiLogConfig getConfig() {
 return config;
 }
 public List<HiLogPrinter> printers(){
 return this.printers;
 }

 public void addPrinter(HiLogPrinter printer){
 if(printer != null) {
 this.printers.add(printer);
 }
 }

 public void delPrinter(HiLogPrinter printer){
 if(printer != null){
 this.printers.remove(printer);
 }
 }
}
