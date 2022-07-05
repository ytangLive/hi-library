package org.devio.hi.library.log;

import androidx.annotation.NonNull;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.TimeZone;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.LinkedBlockingQueue;

public class HiFilePrinter implements HiLogPrinter {

    private static final ExecutorService EXECUTOR = Executors.newSingleThreadExecutor();
    private static HiFilePrinter instance;
    private final String logPath;
    private final long retentionTime;
    private final LogWriter writer;
    private final LogWorker worker;


    public HiFilePrinter(String logPath, long retentionTime) {
        this.logPath = logPath;
        this.retentionTime = retentionTime;
        this.worker = new LogWorker();
        this.writer = new LogWriter();
        cleanExpiredLog();
    }

    @Override
    public void print(@NonNull HiLogConfig config, @NonNull int level, @NonNull String tag, @NonNull String printStr) {
        long timeMillis = System.currentTimeMillis();
        if (!worker.isRunning()) {
            worker.start();
        }
        worker.put(new HiLogModel(timeMillis, level, tag, printStr));
    }

    public static HiFilePrinter getInstance(String logPath, long retentionTime) {
        if (instance == null) {
            instance = new HiFilePrinter(logPath, retentionTime);
        }
        return instance;
    }

    private void cleanExpiredLog() {
        if (retentionTime <= 0) {
            return;
        }
        File logDir = new File(logPath);
        File[] files = logDir.listFiles();
        if (files == null || files.length == 0) {
            return;
        }
        for (File file : files) {
            if (System.currentTimeMillis() - file.lastModified() < retentionTime) {
                file.delete();
            }
        }
    }

    private void doPrint(HiLogModel logModel) {
        String lastFileName = writer.getPreFileName();
        if (lastFileName == null) {
            lastFileName = genFileName();
            if (writer.isReady()) {
                writer.close();
            }
            if (!writer.ready(lastFileName)) {
                return;
            }
        }
        writer.append(logModel.flattenedLogs());
    }

    private String genFileName() {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd", Locale.CHINA);
        sdf.setTimeZone(TimeZone.getDefault());
        return sdf.format(new Date(System.currentTimeMillis()));
    }

    private class LogWorker implements Runnable {

        private BlockingQueue<HiLogModel> logs = new LinkedBlockingQueue<>();
        private volatile boolean running;

        public void put(HiLogModel logModel) {
            try {
                logs.put(logModel);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }

        public boolean isRunning() {
            synchronized (this) {
                return running;
            }
        }

        public void start() {
            synchronized (this) {
                EXECUTOR.execute(this);
                running = true;
            }
        }

        @Override
        public void run() {
            HiLogModel logModel;
            try {
                while (true) {
                    logModel = logs.take();
                    doPrint(logModel);
                }
            } catch (InterruptedException e) {
                e.printStackTrace();
                synchronized (this) {
                    running = false;
                }
            }
        }
    }

    private class LogWriter {

        private String preFileName;
        private File logFile;
        private BufferedWriter writer;

        public boolean isReady() {
            return writer != null;
        }

        String getPreFileName() {
            return preFileName;
        }

        public boolean ready(String fileName) {
            preFileName = fileName;
            logFile = new File(logPath, fileName);
            if (!logFile.exists()) {
                try {
                    File parent = logFile.getParentFile();
                    if (!parent.exists()) {
                        parent.mkdirs();
                    }
                    logFile.createNewFile();
                } catch (IOException e) {
                    e.printStackTrace();
                    preFileName = null;
                    logFile = null;
                    return false;
                }
            }

            try {
                writer = new BufferedWriter(new FileWriter(logFile, true));
            } catch (IOException e) {
                e.printStackTrace();
                preFileName = null;
                logFile = null;
                return false;
            }

            return true;
        }

        public void append(String flattenedLog) {
            try {
                writer.append(flattenedLog);
                writer.newLine();
                writer.flush();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        public boolean close() {
            try {
                writer.close();
            } catch (IOException e) {
                e.printStackTrace();
                return false;
            } finally {
                preFileName = null;
                logFile = null;
                writer = null;
            }
            return true;
        }
    }
}
