package example.registry.zookeeper;

/**
 * Created by juemingzi on 16/7/12.
 */
public class DemoServiceImp implements DemoService {
    @Override
    public String call(String msg) {
        return msg;
    }
}
