package example.soa;

/**
 * Created by juemingzi on 16/5/31.
 */
public interface Invoker<T> {

    Class<T> getInterface();

    Result invoke(Invocation invocation) throws Exception;

}
