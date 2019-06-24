package com.luo.log.log3;

import java.io.Writer;
import java.text.SimpleDateFormat;

/**
 * <P>
 *     日志消息的包装类，消除日志实现的差异
 * </P>
 * @author pudding
 * @version 0.1.0
 * @design 外观模式
 * @date 2018/12/14.12:41
 * @see
 */
public abstract class LogMsg {

    protected static String DEFAULT_DATE_FORMAT_STR = "yyyy-MM-dd HH:mm:ss.SSS";
    protected static SimpleDateFormat DEFAULT_DATE_FORMAT = new SimpleDateFormat(DEFAULT_DATE_FORMAT_STR);

    protected String dateFormatStr;
    protected SimpleDateFormat dateFormat;

    public LogMsg() {
        this.dateFormatStr = DEFAULT_DATE_FORMAT_STR;
        this.dateFormat = DEFAULT_DATE_FORMAT;
    }

    public static String getDefaultDateFormatStr() {
        return DEFAULT_DATE_FORMAT_STR;
    }

    public static SimpleDateFormat getDefaultDateFormat() {
        return DEFAULT_DATE_FORMAT;
    }

    public static void setDefaultDateFormat(SimpleDateFormat dateFormat) {
        DEFAULT_DATE_FORMAT = dateFormat;
        DEFAULT_DATE_FORMAT_STR = dateFormat.toString();
    }

    public static void setDefaultDateFormat(String dateFormat) {
        DEFAULT_DATE_FORMAT = new SimpleDateFormat(dateFormat);
        DEFAULT_DATE_FORMAT_STR = dateFormat;
    }

    public String getDateFormatStr() {
        return dateFormatStr;
    }

    public SimpleDateFormat getDateFormat() {
        return dateFormat;
    }

    public void setDateFormat(SimpleDateFormat dateFormat) {
        this.dateFormat = dateFormat;
        this.dateFormatStr = dateFormat.toString();
    }

    public void setDateFormat(String dateFormat) {
        this.dateFormat = new SimpleDateFormat(dateFormat);
        this.dateFormatStr = dateFormat;
    }

    public abstract long getTimeStamp();

    public abstract String getTimeStr();

    public abstract String getLevelStr();

    public abstract String getThreadName();

    public abstract String getClassName();

    public abstract String getLine();

    public abstract String getFormattedLine();

    public abstract StackTraceElement[] getCallerData();

    public abstract ThrowableProxy getThrowable();

    /**
     * 异常信息处理
     */
    public static abstract class ThrowableProxy {

        public static final int WRITER = 0, STRING_BUILDER = 1;

        protected int builderMode;

        public int getBuilderMode() {
            return builderMode;
        }

        protected void setBuilderMode(int builderMode) {
            this.builderMode = builderMode;
        }

        public void printStackTrace(final Writer builder) {

        }

        public void printStackTrace(final StringBuilder builder) {

        }
    }
}
