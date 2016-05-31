package chain;

/**
 * Created by juemingzi on 16/5/11.
 */
public class OrderService {

    private Order convert(OrderDto orderDto){
        Order order = new Order();

        order.setId(orderDto.getId());

        return order;
    }

    public Result submit(OrderDto orderDto){

        Order order = convert(orderDto);

        CheckHandler checkHandler = new CheckHandler();
        DaoHandler daoHandler = new DaoHandler();

        daoHandler.addObservers(new ApiObserver());
        daoHandler.addObservers(new CashierObserver());

        checkHandler.setNextHandler(daoHandler);

        return checkHandler.handleOrder(new Context(), order);
    }


    public static void main(String[] args){
        OrderService orderService = new OrderService();

        OrderDto orderDto = new OrderDto();
        orderDto.setId("1234");

        orderService.submit(orderDto);
    }

}
