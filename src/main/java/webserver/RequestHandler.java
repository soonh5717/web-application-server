package webserver;

import db.DataBase;
import model.User;
import org.apache.commons.lang3.ObjectUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import util.HttpRequestUtils;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.util.Collection;
import java.util.Map;

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

            String path = httpRequest.getPath();
            if ("/user/create".equals(path)) {
                User user = buildUser(httpRequest.getParamMap());
                DataBase.addUser(user);
                log.info("create user : {}", user.toString());
                httpResponse.sendRedirect("/index.html");

            } else if ("/user/login".equals(path)) {
                String userId = httpRequest.getParamMap("userId");
                String password = httpRequest.getParamMap("password");
                log.info("login user - userId : {}, password : {}", userId, password);
                User user = DataBase.findUserById(userId);
                if (user == null || !password.equals(user.getPassword())) {
                    httpResponse.sendRedirect("/user/login_failed.html");
                }
                httpResponse.addHeader("Set-Cookie", "logined=true");
                httpResponse.sendRedirect("/index.html");

            } else if ("/user/list".equals(httpRequest.getPath())) {
                if (!isLogin(httpRequest.getHeaderMap("Cookie"))) {
                    httpResponse.sendRedirect("/user/login.html");
                    return;
                }

                Collection<User> userCollection = DataBase.findAll();
                StringBuilder sb = new StringBuilder();
                sb.append("<table border='1>");
                userCollection.forEach(user -> {
                    sb.append("'<tr>");
                    sb.append("<td>").append(user.getUserId()).append("</td>");
                    sb.append("<td>").append(user.getName()).append("</td>");
                    sb.append("<td>").append(user.getEmail()).append("</td>");
                    sb.append("'</tr>");
                });
                httpResponse.forwardBody(sb.toString());

            } else {
                httpResponse.forward(path);
            }
        } catch (IOException e) {
            log.error(e.getMessage());
        }
    }

    private User buildUser(Map<String, String> queryStringMap) {
        String userId = queryStringMap.get("userId");
        String password = queryStringMap.get("password");
        String name = queryStringMap.get("name");
        String email = queryStringMap.get("email");
        return new User(userId, password, name, email);
    }

    private boolean isLogin(String cookieValue) {
        Map<String, String> cookies = HttpRequestUtils.parseCookies(cookieValue);
        String value = cookies.get("logined");
        if (ObjectUtils.isEmpty(value)) {
            return false;
        }
        return Boolean.parseBoolean(value);
    }
}
