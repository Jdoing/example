package example.registry;


import example.common.URL;

import java.util.List;

/**注册中心
 * Created by juemingzi on 16/6/12.
 */
public interface Registry {

    void register(URL url) throws Exception;

    void subscribe(URL url, NotifyListener listener) throws Exception;

    List<URL> lookup(URL url) throws Exception;
}
