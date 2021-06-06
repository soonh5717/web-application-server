package controller;

import db.DataBase;
import model.User;
import org.apache.commons.lang3.ObjectUtils;
import util.HttpRequestUtils;
import webserver.HttpRequest;
import webserver.HttpResponse;

import java.util.Collection;
import java.util.Map;

public class ListUserController implements Controller {
    @Override
    public void service(HttpRequest httpRequest, HttpResponse httpResponse) {
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
