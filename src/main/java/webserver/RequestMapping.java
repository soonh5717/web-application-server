package webserver;

import controller.Controller;
import controller.CreateUserController;
import controller.ListUserController;
import controller.LoginUserController;

import java.util.HashMap;
import java.util.Map;

public class RequestMapping {
    private static Map<String, Controller> controllerMap = new HashMap<String, Controller>();

    static {
        controllerMap.put("/user/create", new CreateUserController());
        controllerMap.put("/user/login", new LoginUserController());
        controllerMap.put("/user/list", new ListUserController());
    }

    public static Controller getController(String requestUrl) {
        return controllerMap.get(requestUrl);
    }
}
