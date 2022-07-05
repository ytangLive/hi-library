package org.devio.hi.library.log;

import org.jetbrains.annotations.NotNull;

public interface HiLogPrinter {
 void print(@NotNull HiLogConfig config, @NotNull @HiLogType.TYPE int level, @NotNull String tag, @NotNull String printStr);
}
