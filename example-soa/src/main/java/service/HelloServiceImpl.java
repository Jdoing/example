package service;

/**
 * Created by juemingzi on 16/5/13.
 */
public class HelloServiceImpl implements HelloService {
    public String hello(String name) {
        return "Hello " + name;
    }

}
