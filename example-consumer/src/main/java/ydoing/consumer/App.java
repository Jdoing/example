package ydoing.consumer;

import org.springframework.context.support.ClassPathXmlApplicationContext;
import ydoing.service.ExampleService;

public class App
{

    public static void main( String[] args )
    {

        ClassPathXmlApplicationContext context = new ClassPathXmlApplicationContext(new String[] {"spring-config.xml"});
        context.start();

        ExampleService service = context.getBean("exampleService", ExampleService.class);
        String result = service.doSomething();
        System.out.println(result);
    }
}
