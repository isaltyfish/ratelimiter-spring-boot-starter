package net.verytools.tools;

import java.lang.reflect.Method;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class MethodSignatureCache {

    private final Map<Method, String> mp = new ConcurrentHashMap<>();
    private final Object lock = new Object();

    public String getSig(Method method) {
        String s = mp.get(method);
        if (s == null) {
            synchronized (lock) {
                s = mp.get(method);
                if (s != null) {
                    return s;
                }
                s = method.toString();
                mp.put(method, s);
            }
        }
        return s;
    }
}
