package net.verytools.tools;

import org.springframework.context.ApplicationContext;

public class BeanHolder<T> {

    private volatile T obj;
    private final Object lock = new Object();

    public T get(Class<T> clazz, ApplicationContext ctx) {
        if (obj != null) {
            return obj;
        }
        synchronized (lock) {
            if (obj != null) {
                return obj;
            }
            obj = ctx.getBean(clazz);
        }
        return obj;
    }

}
