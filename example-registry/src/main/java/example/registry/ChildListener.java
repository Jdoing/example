package example.registry;

import java.util.List;

/**
 * Created by juemingzi on 16/7/11.
 */
public interface ChildListener {

    void childChanged(String path, List<String> children) throws Exception;

}
