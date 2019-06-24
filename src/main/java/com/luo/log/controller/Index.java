package com.luo.log.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;

/**
 * @author luosen
 * @version 0.0.1
 * @date 2018/12/19
 * @time 13:42
 * @function 功能:
 * @describe 版本描述:
 * @modifyLog 修改日志:
 */
@Controller
@RequestMapping("index")
public class Index {

    private static final Logger log = LoggerFactory.getLogger(Index.class);

    @RequestMapping("/log")
    public String helloHtml(HttpServletRequest request, String url) {
        request.setAttribute("url", url);
        return "/log_ws";
    }

    @RequestMapping("/savelog")
    @ResponseBody
    public String savelog(String name) {
        log.info(name);
        log.warn(name);
        log.error(name);
        return name;
    }
}
