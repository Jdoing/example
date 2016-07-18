package example.registry;

import example.common.URL;

import java.util.List;

/**
 * Created by juemingzi on 16/7/18.
 */
public interface ZkClient {

    void create(URL url) throws Exception;

    List<String> getChildNode(String path) throws Exception;

    List<String> addNodeListener(final URL url, final ChildListener listener) throws Exception;

    void addStateListener(StateListener stateListener);
}
