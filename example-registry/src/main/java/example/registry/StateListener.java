package example.registry;

/**
 * Created by juemingzi on 16/6/13.
 */
public interface StateListener {

    void onChanged(StateEvent stateEvent) throws Exception;

}
