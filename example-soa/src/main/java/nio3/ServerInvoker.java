package nio3;

import example.soa.Invocation;

import java.lang.reflect.Method;

/**
 * Created by juemingzi on 16/6/4.
 */
public class ServerInvoker {
    private Class clazz;

    public ServerInvoker(Class clazz){
        this.clazz = clazz;
    }

    public Object invoke(Invocation invocation) throws Exception {
        Object object = clazz.newInstance();
        Method method = clazz.getMethod(invocation.getMethodName(), invocation.getParameterTypes());
        Object result = method.invoke(object, invocation.getArguments());

        return result;
    }
}
