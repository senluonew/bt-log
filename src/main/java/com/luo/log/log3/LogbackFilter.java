package com.luo.log.log3;

import ch.qos.logback.classic.pattern.ThrowableProxyConverter;
import ch.qos.logback.classic.spi.ILoggingEvent;
import ch.qos.logback.core.filter.Filter;
import ch.qos.logback.core.spi.FilterReply;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.logging.logback.ExtendedWhitespaceThrowableProxyConverter;

import java.io.IOException;
import java.io.Writer;
import java.util.Date;

/**
 * @author pudding
 * @version 0.1.0
 * @design
 * @date 2018/12/12.20:40
 * @see
 */
public class LogbackFilter extends Filter<ILoggingEvent> implements LogFilter {

    private static final Logger log = LoggerFactory.getLogger(LogbackFilter.class);

    private static final LogFilters logFilters = LogFilters.INSTANCE;
    private static final ThrowableProxyConverter converter = new ExtendedWhitespaceThrowableProxyConverter();
    private LogQueueList<LogMsg> logQueueList;

    public LogbackFilter() {
        this.logQueueList = new LogQueueList<>();
        logFilters.setLogFilter(this);
    }

    @Override
    public FilterReply decide(ILoggingEvent event) {
        addToQueue(new LogbackMsg(event));
        return FilterReply.NEUTRAL; // 给下一个过滤器
    }

    @Override
    public LogQueueList getLogQueueList() {
        return logQueueList;
    }

    @Override
    public void setLogQueueList(LogQueueList logQueueList) {
        this.logQueueList = logQueueList;
    }

    private void addToQueue(LogMsg logMsg) {
        try {
            logQueueList.forEach(logQueue -> {
                logQueue.add(logMsg);
            });
        } catch (Exception e) {
            e.printStackTrace();
            log.warn("LogMsg {} add to LogQueue failed for {}, {}", logMsg, e, e.getMessage());
        }
    }

    private class LogbackMsg extends LogMsg {

        private ILoggingEvent event;

        private LogbackMsg(ILoggingEvent event) {
            this.event = event;
        }

        @Override
        public long getTimeStamp() {
            return event.getTimeStamp();
        }

        @Override
        public String getTimeStr() {
            return dateFormat.format(new Date(event.getTimeStamp()));
        }

        @Override
        public String getLevelStr() {
            return event.getLevel().levelStr;
        }

        @Override
        public String getThreadName() {
            return event.getThreadName();
        }

        @Override
        public String getClassName() {
            return event.getLoggerName();
        }

        @Override
        public String getLine() {
            return event.getMessage();
        }

        @Override
        public String getFormattedLine() {
            return event.getFormattedMessage();
        }

        @Override
        public StackTraceElement[] getCallerData() {
            return event.getCallerData();
        }

        @Override
        public ThrowableProxy getThrowable() {
            if (event.getThrowableProxy() != null) return new LogbackThrowableProxy(event);
            return null;
        }
    }

    private class LogbackThrowableProxy extends LogMsg.ThrowableProxy {

        private ILoggingEvent iLoggingEvent;

        public LogbackThrowableProxy(ILoggingEvent iLoggingEvent) {
            this.iLoggingEvent = iLoggingEvent;
            this.builderMode = STRING_BUILDER;
        }

        @Override
        public void printStackTrace(final Writer builder) {
            if (iLoggingEvent.getThrowableProxy() != null) {
                try {
                    converter.start();
                    builder.append(converter.convert(iLoggingEvent));
                    converter.stop();
                } catch (IOException e) {
                    throw new RuntimeException(e.getMessage());
                }
            }
        }

        @Override
        public void printStackTrace(StringBuilder builder) {
            if (iLoggingEvent.getThrowableProxy() != null) {
                converter.start();
                builder.append(converter.convert(iLoggingEvent));
                converter.stop();
            }
        }
    }
}
