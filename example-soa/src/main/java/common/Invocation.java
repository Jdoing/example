package common;

import java.io.Serializable;

/**
 * Created by juemingzi on 16/5/23.
 */
public class Invocation implements Serializable{

    private static final long serialVersionUID = 1L;

    private Class<?> clazz;

    private String methodName;

    private Class<?>[] parameterTypes;

    private Object[] arguments;

    public Class<?> getClazz() {
        return clazz;
    }

    public void setClazz(Class<?> clazz) {
        this.clazz = clazz;
    }

    public String getMethodName() {
        return methodName;
    }

    public void setMethodName(String methodName) {
        this.methodName = methodName;
    }

    public Class<?>[] getParameterTypes() {
        return parameterTypes;
    }

    public void setParameterTypes(Class<?>[] parameterTypes) {
        this.parameterTypes = parameterTypes;
    }

    public Object[] getArguments() {
        return arguments;
    }

    public void setArguments(Object[] arguments) {
        this.arguments = arguments;
    }

    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder("Invocation{");
        sb.append("methodName='").append(methodName).append('\'');
        sb.append(", clazz=").append(clazz);
        sb.append('}');
        return sb.toString();
    }
}
