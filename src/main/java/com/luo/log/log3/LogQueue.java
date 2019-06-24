package com.luo.log.log3;

import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.TimeUnit;

/**
 * <P>
 *     日志消息队列，协调消费者和生产者之间的速度差
 * </P>
 * @author pudding
 * @version 0.1.0
 * @design
 * @date 2018/12/12.15:22
 * @see
 */
public class LogQueue<E> {

    private LogSender logSender;

    LogQueue<E> prev;
    LogQueue<E> next;

    private BlockingQueue<E> logQueue = new ArrayBlockingQueue<>(1000);  // 并发，速度

    public LogQueue() {
    }

    public LogQueue(LogSender logSender) {
        this.logSender = logSender;
    }

    public void add(E e) {
        if (logSender != null) logSender.reuse();   //  先处理，再唤醒
        logQueue.add(e);
    }

    public E poll(long timeout, TimeUnit unit) throws InterruptedException {
        return logQueue.poll(timeout, unit);
    }
}
