package chain;

/**
 * Created by juemingzi on 16/5/11.
 */
public class CheckHandler extends DefaultHandler {
    @Override
    public Result handleOrder(Context context, Order order) {
        System.out.println("检查重复订单......");

        if (order.getId().equals("test"))

            return Result.failResult();

        return getNextHandler().handleOrder(context, order);
    }
}
