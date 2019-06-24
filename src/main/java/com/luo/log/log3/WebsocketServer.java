package com.luo.log.log3;


import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import javax.websocket.*;

import javax.websocket.server.PathParam;
import javax.websocket.server.ServerEndpoint;
import java.io.Closeable;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.HashMap;
import java.util.Map;
import java.util.WeakHashMap;

/**
 * @author pudding
 * @version 0.1.0
 * @design
 * @date 2018/12/13.17:37
 * @see
 */
@ServerEndpoint(value = "/lccx/log/ws/{sid}", configurator = EndpointConfig.class)   // 一个sid一个对象
@Component
public class WebsocketServer implements Closeable {

    private static final Logger log = LoggerFactory.getLogger(WebsocketServer.class);

    private static final Map<Session, WebsocketServer> servers = new WeakHashMap<>();
    private static final Map<String, WebsocketServer> sidServers = new WeakHashMap<>();

    @Resource
    private WebsocketConfig websocketConfig;

    private Session session;
    String sid;
    long openTime;
    private LogSender logSender;

    @OnOpen
    public void onOpen(Session session, @PathParam("sid") String sid) {
        servers.put(session, this);
        sidServers.put(sid, this);
        openTime = System.currentTimeMillis();
        this.sid = sid;
        this.session = session;
        init();
        log.info("Log reader websocket {} open !", sid);
    }

    private void init() {
        this.logSender = new LogSender(this);
        this.logSender.setWebsocketConfig(websocketConfig);
    }

    @OnClose
    public void onClose(Session closeSession) {
        recycle();
        logSender.recycle();
        log.info("Log reader websocket {} close !", sid);
    }

    @OnMessage
    public void onMessage(Session session, String params) throws IOException {
        log.info("Log reader websocket {} message is {}", sid, params);
        if (params != null && params.trim().equals("-f")) {
            logSender.init();
        }
    }

    @OnError
    public void onError(Session session, Throwable e) throws Throwable {
        try {
            log.warn("Log reader websocket {} error for {}, {} !", sid, e, e.getMessage());
            e.printStackTrace();
            close();
        } catch (IOException e1) {
            log.warn("Log reader websocket {} close failed for {}, {}", sid, e1, e1.getMessage());
            throw e1;
        }
        throw e;
    }

    public void sendText(String text) throws IOException {
        session.getBasicRemote().sendText(text);
    }

    public void sendBinary(ByteBuffer data) throws IOException {
        session.getBasicRemote().sendBinary(data);
    }

    /**
     * 主动校验是否关闭
     */
    public boolean closeCheck() throws IOException {
        if (! session.isOpen()) {
            log.info("Websocket session {} is not open !", session);
            close();
            return true;
        }
        return false;
    }

    @Override
    public void close() throws IOException {
        if (session != null) session.close();
    }

    public void recycle() {
        servers.remove(session);
        sidServers.remove(sid);
    }


    public static Map<String, Session> sessions() {
        Map<String,Session> map = new HashMap<>();
        servers.forEach((session, websocketServer) -> map.put(websocketServer.sid, websocketServer.session));
        return map;
    }

    public static void close(String sid) throws IOException {
        WebsocketServer websocketServer = sidServers.get(sid);
        if (websocketServer != null) websocketServer.close();
    }

    public static void closeAll() throws IOException {
        for (Map.Entry<String, WebsocketServer> entry : sidServers.entrySet()) {
            WebsocketServer websocketServer = entry.getValue();
            websocketServer.close();
        }
    }
}
