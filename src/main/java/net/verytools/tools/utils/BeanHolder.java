package net.verytools.tools.utils;

import org.springframework.beans.factory.NoSuchBeanDefinitionException;
import org.springframework.context.ApplicationContext;

public class BeanHolder<T> {

    private volatile T obj;
    private volatile boolean resolved = false;
    private final Object lock = new Object();

    public T get(Class<T> clazz, ApplicationContext ctx) {
        if (obj != null || resolved) {
            return obj;
        }
        synchronized (lock) {
            if (obj != null) {
                return obj;
            }
            try {
                obj = ctx.getBean(clazz);
            } catch (NoSuchBeanDefinitionException ex) {
                // ignore
            }
            resolved = true;
        }
        return obj;
    }

}
