package org.devio.hi.library.log;

import org.jetbrains.annotations.NotNull;

import java.util.Arrays;
import java.util.List;

public class HiLog {

    private static final String HI_LOG_PACKAGE;

    static {
        String className = HiLog.class.getName();
        HI_LOG_PACKAGE = className.substring(0, className.lastIndexOf("."));
    }

    public static void v(@NotNull Object... contents) {
        log(HiLogType.V, contents);
    }

    public static void vt(@NotNull String tag, @NotNull Object... contents) {
        log(HiLogType.V, tag, contents);
    }

    public static void d(@NotNull Object... contents) {
        log(HiLogType.D, contents);
    }

    public static void dt(@NotNull String tag, @NotNull Object... contents) {
        log(HiLogType.D, tag, contents);
    }

    public static void i(@NotNull Object... contents) {
        log(HiLogType.I, contents);
    }

    public static void it(@NotNull String tag, @NotNull Object... contents) {
        log(HiLogType.I, tag, contents);
    }

    public static void w(@NotNull Object... contents) {
        log(HiLogType.W, contents);
    }

    public static void wt(@NotNull String tag, @NotNull Object... contents) {
        log(HiLogType.W, tag, contents);
    }

    public static void e(@NotNull Object... contents) {
        log(HiLogType.E, contents);
    }

    public static void et(@NotNull String tag, @NotNull Object... contents) {
        log(HiLogType.E, tag, contents);
    }

    public static void a(@NotNull Object... contents) {
        log(HiLogType.A, contents);
    }

    public static void at(@NotNull String tag, @NotNull Object... contents) {
        log(HiLogType.A, tag, contents);
    }

    public static void log(@HiLogType.TYPE int type, Object... contents) {
        log(type, HiLogManager.getInstance().getConfig().getGlobaleTag(), contents);
    }

    public static void log(@HiLogType.TYPE int type, @NotNull String tag, Object... contents) {
        log(HiLogManager.getInstance().getConfig(), type, tag, contents);
    }

    public static void log(@NotNull HiLogConfig config, @HiLogType.TYPE int type, @NotNull String tag, Object... contents) {
        if (!config.enable()) {
            return;
        }
        StringBuffer sb = new StringBuffer();

        if (config.includeThread()) {
            String threadInfo = config.HI_LOG_THREAD_FORMATTER.format(Thread.currentThread());
            sb.append(threadInfo).append("\n");
        }
        if (config.stackTraceDept() > 0) {
            String traceInfo = config.HI_LOG_STACK_TRACE_FORMATTER.format(HiLogStackTraceUtil.getRealCropStackTrace(new Throwable().getStackTrace(), HI_LOG_PACKAGE, config.stackTraceDept()));
            sb.append(traceInfo).append("\n");
        }
        String body = parseBody(contents, config);
        sb.append(body);

        List<HiLogPrinter> printers = config.printers() != null ? Arrays.asList(config.printers()) : HiLogManager.getInstance().printers();
        if (printers == null) {
            return;
        }
        for (HiLogPrinter printer : printers) {
            printer.print(config, type, tag, sb.toString());
        }
    }

    private static String parseBody(@NotNull Object[] contents, @NotNull HiLogConfig config) {
        if (config.injectJsonParser() != null) {
            return config.injectJsonParser().toJson(contents);
        }
        StringBuffer sb = new StringBuffer();
        if (contents.length >= 0) {
            for (Object o : contents) {
                sb.append(o.toString()).append(";");
            }
            if (sb.length() > 0) {
                sb.deleteCharAt(sb.length() - 1);
            }
        }
        return sb.toString();
    }
}
