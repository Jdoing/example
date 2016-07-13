package example.soa;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;

/**
 * Created by juemingzi on 16/5/28.
 */
public class ProxyFactory {

    public <T> T getProxy(final ClientInvoker<T> invoker){
        return  (T) Proxy.newProxyInstance(Thread.currentThread().getContextClassLoader(), new Class<?>[]{invoker.getClass()}, new InvocationHandler(){
            @Override
            public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
                Invocation invocation = new Invocation();
                invocation.setMethodName(method.getName());
                invocation.setParameterTypes(method.getParameterTypes());
                invocation.setArguments(args);

                return invoker.invoke(invocation);
            }
        });
    }
}
