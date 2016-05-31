package rabbitmq;

import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;

import java.io.IOException;
import java.util.Random;
import java.util.concurrent.ExecutorCompletionService;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeoutException;

/**
 * Created by jiangyu on 16/1/30.
 */
public class Producer2 {
    private static final String EXCHANGE_NAME = "test";
    private static final ExecutorService exec = Executors.newFixedThreadPool(10);
    private static final ExecutorCompletionService executorCompletionService = new ExecutorCompletionService(exec);
    private static final String ROUTING_KEY = "foo";

    private static class Worker implements Runnable{
        ConnectionFactory factory;
        int count;
        public Worker(ConnectionFactory factory, int count){
            this.factory = factory;
            this.count = count;
        }

        public void run() {
            Connection connection = null;
            try {
                connection = factory.newConnection();
                Channel channel = connection.createChannel();

                channel.exchangeDeclare(EXCHANGE_NAME, "direct");

                Random random = new Random();
                while(true) {

                    channel.basicPublish(EXCHANGE_NAME, ROUTING_KEY, null, Integer.valueOf(random.nextInt()).toString().getBytes());
                }
            } catch (IOException e) {
                e.printStackTrace();
            } catch (TimeoutException e) {
                e.printStackTrace();
            }
        }
    }

    public static void main(String[] args){
        final ConnectionFactory factory = new ConnectionFactory();
        factory.setHost("localhost");
        for(int i = 0; i < 10; i++){
            exec.submit(new Worker(factory, 1000000));
        }
    }


}
