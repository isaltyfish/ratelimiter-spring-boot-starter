package net.verytools.tools;

import javax.servlet.http.HttpServletRequest;

public class EmptyResolver implements KeyResolver {
    @Override
    public String resolve(HttpServletRequest exchange) {
        return null;
    }
}
