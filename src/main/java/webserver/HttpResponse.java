package webserver;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.DataOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.OutputStream;
import java.nio.file.Files;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

public class HttpResponse {
    private static final Logger log = LoggerFactory.getLogger(HttpResponse.class);
    private static final String HEADER_CONTENT_TYPE = "Content-Type";
    private static final String HEADER_CONTENT_LENGTH = "Content-Length";
    private DataOutputStream dos = null;
    private Map<String, String> headerMap = new HashMap<String, String>();

    public HttpResponse(OutputStream os) {
        dos = new DataOutputStream(os);
    }

    public void addHeader(String key, String value) {
        headerMap.put(key, value);
    }

    public void forward(String url) {
        try {
            byte[] body = Files.readAllBytes(new File("./webapp" + url).toPath());
            if (url.endsWith(".css")) {
                headerMap.put(HEADER_CONTENT_TYPE, "text/css");
            } else if (url.endsWith(".js")) {
                headerMap.put(HEADER_CONTENT_TYPE, "application/javascript");
            } else {
                headerMap.put(HEADER_CONTENT_TYPE, "text/html;charset=utf-8");
            }

            headerMap.put(HEADER_CONTENT_LENGTH, body.length + "");
            response200Header();
            responseBody(body);
        } catch (IOException e) {
            log.error(e.getMessage());
        }
    }

    public void forwardBody(String body) {
        byte[] contents = body.getBytes();
        headerMap.put(HEADER_CONTENT_TYPE, "text/html;charset=utf-8");
        headerMap.put(HEADER_CONTENT_LENGTH, contents.length + "");
        response200Header();
        responseBody(contents);
    }

    public void sendRedirect(String redirectUrl) {
        try {
            dos.writeBytes("HTTP/1.1 302 Found \r\n");
            processHeaders();
            dos.writeBytes("Location: " + redirectUrl + " \r\n");
            dos.writeBytes("\r\n");
        } catch (IOException e) {
            log.error(e.getMessage());
        }
    }

    private void response200Header() {
        try {
            dos.writeBytes("HTTP/1.1 200 OK \r\n");
            processHeaders();
            dos.writeBytes("\r\n");
        } catch (IOException e) {
            log.error(e.getMessage());
        }
    }

    private void responseBody(byte[] body) {
        try {
            dos.write(body, 0, body.length);
            dos.writeBytes("\r\n");
            dos.flush();
        } catch (IOException e) {
            log.error(e.getMessage());
        }
    }

    private void processHeaders() {
        try {
            Set<String> keys = headerMap.keySet();
            for (String key : keys) {
                dos.writeBytes(key + ": " + headerMap.get(key) + " \r\n");
            }
        } catch (IOException e) {
            log.error(e.getMessage());
        }
    }
}
