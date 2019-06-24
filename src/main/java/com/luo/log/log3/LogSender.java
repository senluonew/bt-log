package com.luo.log.log3;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.StringWriter;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * <P>
 *     使用点对点通信机制，每个连接占用一个{@link LogSender}。未了保证不浪费资源，默认时间关闭连接
 * </P>
 * @author pudding
 * @version 0.1.0
 * @design
 * @date 2018/12/12.20:46
 * @see
 */
public class LogSender {

    private static final Logger log = LoggerFactory.getLogger(LogSender.class);

    private final LogFilter logFilter = LogFilters.INSTANCE;
    private static final LogEncoder logEncoder = LogEncoder.getDefaultEncoder();
    private static final int nThread = 5;
    private static final ExecutorService service = Executors.newFixedThreadPool(nThread, new ThreadFactory() {    // 最多5人同时查看，通过释放线程占用，可以做到一定的扩容
        private int num;
        @Override
        public Thread newThread(Runnable r) {
            Thread thread = new Thread(r, "LogSenderTask-" + num++);
            thread.setPriority(3);
            thread.setDaemon(true);
            return thread;
        }
    });
    private static AtomicInteger num = new AtomicInteger(0); // 同时连接的数量
    private static byte DEFAULT_USE_NUM = 10;   // 默认使用次数，默认10次，用于切换
    private WebsocketConfig websocketConfig;
    private WebsocketServer websocketServer;

    private LogQueue<LogMsg> logQueue;
    private StringBuilder builder;
    private long lastUseTime;
    private boolean isClosed = false;
    private LogSenderTask logSenderTask;    // 日志任务，反复使用

    LogSender(WebsocketServer websocketServer) {
        this.websocketServer = websocketServer;
        this.lastUseTime = websocketServer.openTime;
    }

    public static byte getDefaultUseNum() {
        return DEFAULT_USE_NUM;
    }

    public static void setDefaultUseNum(byte defaultUseNum) {
        DEFAULT_USE_NUM = defaultUseNum;
    }

    public WebsocketConfig getWebsocketConfig() {
        return websocketConfig;
    }

    public void setWebsocketConfig(WebsocketConfig websocketConfig) {
        this.websocketConfig = websocketConfig;
    }

    void init() throws IOException {
        this.logQueue = new LogQueue<>(this);
        this.logFilter.getLogQueueList().add(this.logQueue);
        this.builder = new StringBuilder();
        this.num.getAndIncrement();
        this.logSenderTask = new LogSenderTask();
        this.service.submit(this.logSenderTask);
    }

    private boolean checkLastUseTime(long logTime) throws IOException {
        long activeTimeout = logTime - lastUseTime;
        long connectionTimeout = logTime - websocketServer.openTime;
        if (activeTimeout > websocketConfig.getActiveTime() || connectionTimeout > websocketConfig.getConnectionTime()) {
            log.info("Log reader {} timeout out with activeTimeout-{}(activeTime-{}) / connectionTimeout-{}(connectionTime-{}) and well be closed !",
                    websocketServer.sid, activeTimeout, websocketConfig.getActiveTime(),
                    connectionTimeout, websocketConfig.getConnectionTime());
            websocketServer.close();
            return false;
        }
        return true;
    }

    private void parseThrowable(LogMsg logMsg) throws IOException {
        LogMsg.ThrowableProxy throwableProxy = logMsg.getThrowable();
        if (throwableProxy != null) {
            String line;
            if (throwableProxy.getBuilderMode() == LogMsg.ThrowableProxy.STRING_BUILDER) {
                throwableProxy.printStackTrace(builder);
                //lines = logEncoder.encodeThrowable(builder);
                line = builder.toString();
            } else {
                StringWriter sw = new StringWriter();
                throwableProxy.printStackTrace(sw);
                //lines = logEncoder.encodeThrowable(sw);
                line = sw.toString();
            }
            websocketServer.sendText(line);
        }
    }

    void reuse() {
        if (! logSenderTask.isUsing) {
            logSenderTask.isUsing = true;
            service.submit(logSenderTask);   // 重新加入
        }
    }

    public void recycle() {
        logFilter.getLogQueueList().remove(this.logQueue);
        isClosed = true;
        if (logSenderTask.isUsing) logSenderTask.currentThread.interrupt();
        else log.info("Log reader {} is closed and not work in LogSenderTask !", websocketServer.sid);
        num.getAndDecrement();
    }

    /**
     * 日志的异步发送任务，必须异步，通信线程不能被占用
     */
    private class LogSenderTask implements Callable {

        private byte useNum;    // 使用次数
        private boolean isUsing = true; // 是否处于循环中
        private Thread currentThread;

        @Override
        public Object call() throws Exception {
            currentThread = Thread.currentThread();
            for (;;) {
                try {
                    // 校验开启时间
                    if (isClosed || checkUseNum()) break;   // 校验使用数量上限
                    LogMsg logMsg = logQueue.poll(num.get() > nThread ? 1000 : 180000, TimeUnit.MILLISECONDS); // 理论上1秒切换速度
                    if (logMsg != null) {
                        long logTime = logMsg.getTimeStamp();
                        if (! checkLastUseTime(logTime)) break;
                        // 序列化
                        logEncoder.encodeLine(logMsg, builder);
                        websocketServer.sendText(builder.toString());
                        builder.delete(0, builder.length());
                        // 异常解析
                        parseThrowable(logMsg);
                        lastUseTime = logTime;
                    } else {
                        if (num.get() > nThread) break; // 连接数量超过线程数量，适当切换
                        if (! checkLastUseTime(System.currentTimeMillis())) break;
                        //if (websocketServer.closeCheck()) break;  // 频率太高，不做
                    }
                } catch (InterruptedException e) {
                    if (isClosed) { // 退出循环，释放线程
                        log.info("Log reader {} is closed and exit from LogSenderTask !", websocketServer.sid);
                        break;
                    }
                }
            }
            isUsing = false;
            currentThread = null;
            return null;
        }

        /**
         * 校验使用次数，超过返回{@code true}
         * @return
         */
        private boolean checkUseNum() {
            if (num.get() > nThread) {
                useNum ++;
                if (useNum > DEFAULT_USE_NUM) {
                    useNum = 0;
                    return true;
                }
            }
            return false;
        }
    }
}
