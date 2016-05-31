package common;

import java.lang.reflect.Method;

/**
 * Created by juemingzi on 16/5/31.
 */
public class ServerInvoker implements Invoker {
    private Class<?> clazz;

    public ServerInvoker(Class<?> clazz) {
        this.clazz = clazz;
    }

    @Override
    public Result invoke(Invocation invocation) throws Exception {
//        System.out.println("methodName is: " + invocation.getMethodName());
        Method method = clazz.getMethod(invocation.getMethodName(), invocation.getParameterTypes());
        Object instance = clazz.newInstance();
        Object result = method.invoke(instance, invocation.getArguments());

        return new Result(result);
    }
}
