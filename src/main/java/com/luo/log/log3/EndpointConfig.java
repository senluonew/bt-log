package com.luo.log.log3;


import com.luo.log.util.ReflectUtil;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.context.annotation.Configuration;

import javax.annotation.Resource;
import javax.websocket.server.ServerEndpointConfig;
import java.lang.annotation.Annotation;
import java.lang.reflect.Field;

/**
 * <P>
 *     用来解决{@link javax.websocket.server.ServerEndpoint}下field无法注入的问题
 * </P>
 * @author pudding
 * @version 0.1.0
 * @design
 * @date 2018/12/14.14:47
 * @see
 */
@Configuration
public class EndpointConfig extends ServerEndpointConfig.Configurator implements ApplicationContextAware {

    private static volatile BeanFactory context;

    @Override
    public <T> T getEndpointInstance(Class<T> clazz) throws InstantiationException {
        // 校验class
        for (Annotation annotation : clazz.getAnnotations()) {
            if (annotation.annotationType().getName().startsWith("javax.websocket")) {
                T t = super.getEndpointInstance(clazz);
                for (Field field : ReflectUtil.getFields(clazz)) {
                    for (Annotation fa : field.getAnnotations()) {
                        Class faType = fa.annotationType();
                        if (faType == Resource.class || faType == Autowired.class) {    // 主动赋值
                            ReflectUtil.setFieldValueF(field, t, context.getBean(field.getType()));
                        }
                    }
                }
                return t;
            }
        }
        return context.getBean(clazz);
    }

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        context = applicationContext;
    }
}
