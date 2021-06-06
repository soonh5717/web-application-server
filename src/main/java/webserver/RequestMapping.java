package webserver;

import controller.Controller;
import controller.CreateUserController;
import controller.ListUserController;
import controller.LoginUserController;

import java.util.HashMap;
import java.util.Map;

public class RequestMapping {
     private final static Map<String, Controller> controllerMap = new HashMap<String, Controller>();

    // 위 선언과 동시에 수행한다.
    static {
        controllerMap.put("/user/create", new CreateUserController());
        controllerMap.put("/user/login", new LoginUserController());
        controllerMap.put("/user/list", new ListUserController());
    }

    public static Controller getController(String requestUrl) {
        return controllerMap.get(requestUrl);
    }
}
