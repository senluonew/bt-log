package com.luo.log.log3;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.socket.server.standard.ServerEndpointExporter;

/**
 * @author pudding
 * @version 0.1.0
 * @design
 * @date 2018/12/13.19:26
 * @see
 */
@Configuration
public class WebsocketConfig {

    @Value("${log.reader.websocket.connection.time:1800000}")
    private long connectionTime;    // 连接时间，超过该时间需要重新连接，默认30分钟，<0，服务器不主动断开；这是一种资源保护机制

    @Value("${log.reader.websocket.active.time:180000}")
    private long activeTime;    // 活跃连接时间，在指定时间内有日志输出则不断开，默认3分钟，<0，服务器不主动断开；这是一种资源保护机制

    public long getConnectionTime() {
        return connectionTime;
    }

    public void setConnectionTime(long connectionTime) {
        this.connectionTime = connectionTime;
    }

    public long getActiveTime() {
        return activeTime;
    }

    public void setActiveTime(long activeTime) {
        this.activeTime = activeTime;
    }

    @Bean
    public ServerEndpointExporter serverEndpointExporter() {
        return new ServerEndpointExporter();
    }
}
