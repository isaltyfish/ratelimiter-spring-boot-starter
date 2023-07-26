package net.verytools.tools;

import javax.servlet.http.HttpServletRequest;

public interface KeyResolver {

    String resolve(HttpServletRequest exchange);

}
