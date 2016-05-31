package common;

/**
 * Created by juemingzi on 16/5/31.
 */
public interface Invoker {

    Result invoke(Invocation invocation) throws Exception;
}
