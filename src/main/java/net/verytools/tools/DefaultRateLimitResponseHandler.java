package net.verytools.tools;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

public class DefaultRateLimitResponseHandler implements RateLimitResponseHandler {
    @Override
    public void handle(HttpServletResponse response) throws IOException {
        renderString(response, "you are rate limited");
    }

    public static void renderString(HttpServletResponse response, String string) throws IOException {
        response.setStatus(200);
        response.setContentType("application/json");
        response.setCharacterEncoding("utf-8");
        response.getWriter().print(string);
    }
}
