package com.luo.log.log3;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;
import java.util.Set;

/**
 * @author pudding
 * @version 0.1.0
 * @design
 * @date 2018/12/14.10:09
 * @see
 */
@RestController
@RequestMapping("lccx/log/ws/manager")
public class LogWsManager {

    private static final Logger log = LoggerFactory.getLogger(LogWsManager.class);

    @RequestMapping("test")
    public void test() {
        log.info("It is a log msg for test !");
    }

    /**
     * 获取所有的websocket连接的信息
     * @return
     */
    @RequestMapping("sessions")
    public Set<String> sessions() {
        return WebsocketServer.sessions().keySet();
    }

    /**
     * 服务端关闭指定的会话
     * @param sid
     * @return
     * @throws IOException
     */
    @RequestMapping("close/{sid}")
    public String close(@PathVariable String sid) throws IOException {
        if (sid == null) return "Websocket server sid must not be null !";

        if (sid.trim().equals("_all")) {
            WebsocketServer.closeAll();
        } else {
            WebsocketServer.close(sid);
        }
        return "Websocket server with " + sid + " closed !";
    }
}
