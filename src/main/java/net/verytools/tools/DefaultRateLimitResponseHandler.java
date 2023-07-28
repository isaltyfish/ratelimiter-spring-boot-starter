package net.verytools.tools;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

public class DefaultRateLimitResponseHandler implements RateLimitResponseHandler {
    @Override
    public void handle(HttpServletResponse response) throws IOException {
        renderString(response, "Rate Limit exceeded");
    }

    public static void renderString(HttpServletResponse response, String string) throws IOException {
        response.setStatus(429);
        response.setContentType("application/json");
        response.setCharacterEncoding("utf-8");
        response.getWriter().print(string);
    }
}
