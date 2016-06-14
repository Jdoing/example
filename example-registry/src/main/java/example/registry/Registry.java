package example.registry;

/**
 * Created by juemingzi on 16/6/12.
 */
public interface Registry {

    void register(String path) throws Exception;

    void subscribe(String path) throws Exception;
}
