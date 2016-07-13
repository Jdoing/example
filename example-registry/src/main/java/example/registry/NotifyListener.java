package example.registry;

import example.common.URL;

import java.util.List;

/**
 * Created by juemingzi on 16/7/11.
 */
public interface NotifyListener {

    void notify(List<URL> urls) throws Exception;

}
