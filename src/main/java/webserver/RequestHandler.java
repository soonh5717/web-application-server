package webserver;

import db.DataBase;
import model.User;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import webserver.component.RequestBuilder;
import webserver.vo.RequestVO;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.Socket;
import java.nio.file.Files;
import java.util.Map;

public class RequestHandler extends Thread {
    private static final String URL_SEPARATOR = " ";
    private static final Logger log = LoggerFactory.getLogger(RequestHandler.class);

    private Socket connection;
    private RequestBuilder requestBuilder = new RequestBuilder();

    public RequestHandler(Socket connectionSocket) {
        this.connection = connectionSocket;
    }

    public void run() {
        log.debug("New Client Connect! Connected IP : {}, Port : {}", connection.getInetAddress(),
                connection.getPort());

        try (InputStream in = connection.getInputStream(); OutputStream out = connection.getOutputStream()) {
            BufferedReader br = new BufferedReader(new InputStreamReader(in, "UTF-8"));
            RequestVO requestVO = requestBuilder.buildRequestUrlVO(br);
            log.info("requestUrlVO : {}", requestVO.toString());

            // TODO 사용자 요청에 대한 처리는 이 곳에 구현하면 된다.
            DataOutputStream dos = new DataOutputStream(out);
            if ("/user/create".equals(requestVO.getUrl())) {
                User user = buildUser(requestVO.getParameterMap());
                log.info("create user : {}", user.toString());
                DataBase.addUser(user);
                String redirectUrl = "/index.html";
                response302Header(dos, redirectUrl, null);
            } else if ("/user/login".equals(requestVO.getUrl())) {
                String userId = requestVO.getParameterMap().get("userId");
                String password = requestVO.getParameterMap().get("password");
                log.info("login user - userId : {}, password : {}", userId, password);
                User user = DataBase.findUserById(userId);
                if (user == null || !password.equals(user.getPassword())) {
                    String redirectUrl = "/user/login_failed.html";
                    String cookie = "logined=false";
                    response302Header(dos, redirectUrl, cookie);
                }
                String redirectUrl = "/index.html";
                String cookie = "logined=true";
                response302Header(dos, redirectUrl, cookie);
            } else if ("/user/list".equals(requestVO.getUrl())) {
                boolean isLogin = "logined=true".equals(requestVO.getHeaderMap().get("Cookie"));
                log.info("isLogin : {}", isLogin);

            } else {
                //byte[] body = "Hello World".getBytes();
                byte[] body = Files.readAllBytes(new File("./webapp" + requestVO.getUrl()).toPath());
                response200Header(dos, getContentType(requestVO.getHeaderMap().get("Accept")), body.length);
                responseBody(dos, body);
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

    private String getContentType(String contentTypeString) {
        int index = contentTypeString.indexOf(",");
        if (index > -1) {
            return contentTypeString.substring(0, index);
        }
        return "text/html";
    }

    private void response200Header(DataOutputStream dos, String contentType, int lengthOfBodyContent) {
        try {
            dos.writeBytes("HTTP/1.1 200 OK \r\n");
            dos.writeBytes("Content-Type: " + contentType + ";charset=utf-8\r\n");
            dos.writeBytes("Content-Length: " + lengthOfBodyContent + "\r\n");
            dos.writeBytes("\r\n");
        } catch (IOException e) {
            log.error(e.getMessage());
        }
    }

    /*
      ref : https://en.wikipedia.org/wiki/HTTP_302
     */
    private void response302Header(DataOutputStream dos) {
        try {
            dos.writeBytes("HTTP/1.1 302 Found \r\n");
            dos.writeBytes("Location: /index.html \n");
            dos.writeBytes("\r\n");
        } catch (IOException e) {
            log.error(e.getMessage());
        }
    }

    private void response302Header(DataOutputStream dos, String url, String cookie) {
        try {
            dos.writeBytes("HTTP/1.1 302 Found \r\n");
            dos.writeBytes("Location: " + url + " \r\n");
            if (cookie != null) {
                dos.writeBytes("Set-Cookie: " + cookie + " \r\n");
            }
            dos.writeBytes("\r\n");
        } catch (IOException e) {
            log.error(e.getMessage());
        }
    }

    private void responseBody(DataOutputStream dos, byte[] body) {
        try {
            dos.write(body, 0, body.length);
            dos.flush();
        } catch (IOException e) {
            log.error(e.getMessage());
        }
    }
}
