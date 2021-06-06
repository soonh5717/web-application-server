package controller;

import db.DataBase;
import model.User;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import webserver.HttpRequest;
import webserver.HttpResponse;

import java.util.Map;

public class CreateUserController implements Controller {
    private static final Logger log = LoggerFactory.getLogger(CreateUserController.class);

    @Override
    public void service(HttpRequest httpRequest, HttpResponse httpResponse) {
        User user = buildUser(httpRequest.getParamMap());
        DataBase.addUser(user);
        log.info("create user : {}", user.toString());
        httpResponse.sendRedirect("/index.html");
    }

    private User buildUser(Map<String, String> queryStringMap) {
        String userId = queryStringMap.get("userId");
        String password = queryStringMap.get("password");
        String name = queryStringMap.get("name");
        String email = queryStringMap.get("email");
        return new User(userId, password, name, email);
    }
}
