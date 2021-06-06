package controller;

import db.DataBase;
import model.User;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import webserver.HttpRequest;
import webserver.HttpResponse;

public class LoginUserController implements Controller {
    private static final Logger log = LoggerFactory.getLogger(LoginUserController.class);

    @Override
    public void service(HttpRequest httpRequest, HttpResponse httpResponse) {
        String userId = httpRequest.getParamMap("userId");
        String password = httpRequest.getParamMap("password");
        log.info("login user - userId : {}, password : {}", userId, password);
        User user = DataBase.findUserById(userId);
        if (user == null || !password.equals(user.getPassword())) {
            httpResponse.sendRedirect("/user/login_failed.html");
        }
        httpResponse.addHeader("Set-Cookie", "logined=true");
        httpResponse.sendRedirect("/index.html");
    }
}
