package rabbitmq;

import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;

import java.io.IOException;
import java.util.concurrent.TimeoutException;

/**
 * Created by jiangyu on 16/1/30.
 */
public class BasicTest {

    private final static String EXCHANGE_NAME= "logs";

    public static void main(String[] args) throws IOException, TimeoutException {
        ConnectionFactory factory = new ConnectionFactory();
        factory.setHost("localhost");
        Connection connection = factory.newConnection();

        Channel channel = connection.createChannel();

        String msg = "info:run here";

        //定义交换器
        channel.exchangeDeclare(EXCHANGE_NAME, "fanout");
        channel.basicPublish(EXCHANGE_NAME, "", null, msg.getBytes());

        System.out.println("send success");
        channel.close();
        connection.close();
    }

}
