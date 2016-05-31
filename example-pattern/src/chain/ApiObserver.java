package chain;

/**
 * Created by juemingzi on 16/5/11.
 */
public class ApiObserver implements Observer{
    @Override
    public void notify(Message message) {
        System.out.println("向api发送消息!");
    }
}
