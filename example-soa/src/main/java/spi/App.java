package spi;

import java.util.ServiceLoader;

/**
 * Created by juemingzi on 16/5/18.
 */
public class App {

    public static void main(String[] args){
        ServiceLoader<HelloService> serviceLoader = ServiceLoader.load(HelloService.class);

        for (HelloService helloService : serviceLoader){

            helloService.sayHello();
        }
    }

}
