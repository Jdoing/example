package chain;

/**
 * Created by juemingzi on 16/5/11.
 */
public class CashierObserver implements Observer{
    @Override
    public void notify(Message message) {
        System.out.println("向收银发送消息");
    }
}
