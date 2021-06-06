package webserver;

import controller.Controller;
import org.apache.commons.lang3.ObjectUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;

public class RequestHandler extends Thread {
    private static final Logger log = LoggerFactory.getLogger(RequestHandler.class);

    private Socket connection;
    public RequestHandler(Socket connectionSocket) {
        this.connection = connectionSocket;
    }

    public void run() {
        log.debug("New Client Connect! Connected IP : {}, Port : {}", connection.getInetAddress(),
                connection.getPort());

        try (InputStream in = connection.getInputStream(); OutputStream out = connection.getOutputStream()) {
            HttpRequest httpRequest = new HttpRequest(in);
            HttpResponse httpResponse = new HttpResponse(out);

            Controller controller = RequestMapping.getController(httpRequest.getPath());
            log.info("path : {} / controller is null : {}", httpRequest.getPath(), ObjectUtils.isEmpty(controller));
            if (ObjectUtils.isEmpty(controller)) {
                String path = getDefaultPath(httpRequest.getPath());
                httpResponse.forward(path);
            } else {
                controller.service(httpRequest, httpResponse);
            }
        } catch (IOException e) {
            log.error(e.getMessage());
        }
    }

    private String getDefaultPath(String path) {
        if ("/".equals(path)) {
            return "/index.html";
        }
        return path;
    }
}
