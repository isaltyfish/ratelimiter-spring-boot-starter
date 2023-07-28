package net.verytools.tools;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

public interface RateLimitResponseHandler {

    void handle(HttpServletResponse response) throws IOException;

}
