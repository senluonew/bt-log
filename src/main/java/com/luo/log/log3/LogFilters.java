package com.luo.log.log3;

/**
 * @author pudding
 * @version 0.1.0
 * @design 适配器模式，获取适配的日志实现；单例模式
 * @date 2018/12/12.20:36
 * @see
 */
public class LogFilters implements LogFilter {

    private static final String LOG_IMPL_FILTER = "log.reader.log.impl.filter";

    private LogFilter logFilter;

    public static final LogFilters INSTANCE = new LogFilters();

    private LogFilters() {
        //this.logFilter = getLogFilter();
    }

    private LogFilter getLogFilter0() {
        try {
            String logFilterName = System.getProperty(LOG_IMPL_FILTER); // 天大地大，系统最大
            if (logFilterName != null) {
                try {
                    Class clazz = Class.forName(logFilterName);
                    return (LogFilter) clazz.newInstance();
                } catch (Exception e) {
                }
            }
            /*logFilterName = logImplFilter;  // spring老二
            if (! logFilterName.trim().equals("")) {
                try {
                    Class clazz = Class.forName(logFilterName);
                    return (LogFilter) clazz.newInstance();
                } catch (Exception e) {
                }
            }*/
            // 默认
            return new LogbackFilter();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public void setLogFilter(LogFilter logFilter) {
        this.logFilter = logFilter;
    }

    @Override
    public LogQueueList getLogQueueList() {
        return logFilter.getLogQueueList();
    }

    @Override
    public void setLogQueueList(LogQueueList logQueueList) {
        logFilter.setLogQueueList(logQueueList);
    }
}
