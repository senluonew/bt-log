package com.luo.log.log3;

/**
 * <P>
 *     添加日志的过滤器插件，避免从日志文件读取日志，减少IO
 * </P>
 * @author pudding
 * @version 0.1.0
 * @design
 * @date 2018/12/12.15:29
 * @see
 */
public interface LogFilter {

    String SPACE = " ";
    String NAME_LEFT = " --- [";
    String NAME_RIGHT = "] ";
    String COLON = ":";

    LogQueueList getLogQueueList();

    void setLogQueueList(LogQueueList logQueueList);
}
