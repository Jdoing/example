package chain;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by juemingzi on 16/5/11.
 */
public class DaoHandler extends DefaultHandler {

    private List<Observer> observers = new ArrayList<>();

    @Override
    public Result handleOrder(Context context, Order order) {

        insert(order);
        onChange(order);
        return Result.successResult();
    }

    private void insert(Order order){
        System.out.println("将order插入数据库......");
    }

    private void onChange(Order order){

        for(Observer observer : observers){

            observer.notify(new Message("insert order:" + order.getId()));
        }
    }

    public void addObservers(Observer observer) {
        observers.add(observer);
    }
}
