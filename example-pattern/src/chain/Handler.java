package chain;

/**
 * Created by juemingzi on 16/5/11.
 */
public interface Handler {

    Result handleOrder(Context context, Order order);

    Handler getNextHandler();
}
