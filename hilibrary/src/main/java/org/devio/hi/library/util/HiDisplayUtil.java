package org.devio.hi.library.util;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Point;
import android.util.TypedValue;
import android.view.Display;
import android.view.WindowManager;

import androidx.annotation.NonNull;

public class HiDisplayUtil {
 public static int dp2px(float dp, Resources resources){
 return (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dp, resources.getDisplayMetrics());
 }

 public static int dp2px(float dp){
 Resources resources = AppGlobals.INSTANCE.getApplication().getResources();
 return (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dp, resources.getDisplayMetrics());
 }

 public static int sp2px(float sp){
 Resources resources = AppGlobals.INSTANCE.getApplication().getResources();
 return (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_SP, sp, resources.getDisplayMetrics());
 }

 public static int getDisplayWidthInPx(@NonNull Context context) {
 WindowManager wm = (WindowManager)context.getSystemService(context.WINDOW_SERVICE);
 if(wm !=null){
 Display display = wm.getDefaultDisplay();
 Point size = new Point();
 display.getSize(size);
 return size.x;
 }
 return 0;
 }

 public static int getDisplayHeightInPx(@NonNull Context context) {
 WindowManager wm = (WindowManager)context.getSystemService(context.WINDOW_SERVICE);
 if(wm !=null){
 Display display = wm.getDefaultDisplay();
 Point size = new Point();
 display.getSize(size);
 return size.y;
 }
 return 0;
 }
}
