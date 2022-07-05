package org.devio.hi.library.log;

public class HiLogStackTraceUtil {

    public static StackTraceElement[] getRealCropStackTrace(StackTraceElement[] cropStack, String ignorPackage, int maxDepth) {
        StackTraceElement[] realStack = getRealStackTrace(cropStack, ignorPackage);
        return cropStackTrace(realStack, maxDepth);
    }

    private static StackTraceElement[] getRealStackTrace(StackTraceElement[] cropStack, String ignorPackage) {
        int ignorDepth = 0;
        int srcDepth = cropStack.length;
        for (int i = srcDepth - 1; i >= 0; i--) {
            String className = cropStack[i].getClassName();
            if (ignorPackage != null && className.startsWith(ignorPackage)) {
                ignorDepth = i + 1;
                break;
            }
        }

        int realDepth = srcDepth - ignorDepth;
        StackTraceElement[] realStack = new StackTraceElement[realDepth];
        System.arraycopy(cropStack, ignorDepth, realStack, 0, realDepth);
        return realStack;
    }

    private static StackTraceElement[] cropStackTrace(StackTraceElement[] cropStack, int maxDepth) {
        int realDepth = cropStack.length;
        if (maxDepth > 0) {
            realDepth = Math.min(maxDepth, realDepth);
        }
        StackTraceElement[] realStack = new StackTraceElement[realDepth];
        System.arraycopy(cropStack, 0, realStack, 0, realDepth);
        return realStack;

    }
}
