package example.directory;

import example.soa.Invocation;
import example.soa.Invoker;

import java.util.List;

/**
 * Created by juemingzi on 16/7/8.
 */
public interface Directory<T> {

    List<Invoker<T>> list(Invocation invocation);

}
