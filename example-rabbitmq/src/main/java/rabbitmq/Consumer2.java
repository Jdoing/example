package rabbitmq;

import com.rabbitmq.client.*;

import java.io.IOException;
import java.util.concurrent.*;

/**
 * Created by jiangyu on 16/1/31.
 */
public class Consumer2 {
    private static final String EXCHANGE_NAME = "test";
    private static final int WORK_NUMBER = 10;
    private static final ExecutorService exec = Executors.newFixedThreadPool(WORK_NUMBER);
    private static final ExecutorCompletionService executorCompletionService = new ExecutorCompletionService(exec);
    private static final CyclicBarrier barrier = new CyclicBarrier(WORK_NUMBER);
    private static final int COUNT = 1000;
    private static final ConnectionFactory factory;
    private static final String ROUTING_KEY = "foo";

    static {
        factory = new ConnectionFactory();
        factory.setHost("localhost");
    }

    private static class Worker implements Callable<Integer> {

        public Integer call() throws Exception {
            Connection connection = factory.newConnection();
            final Channel channel = connection.createChannel();

            //定义交换器
            channel.exchangeDeclare(EXCHANGE_NAME, "direct");
            String queueName = channel.queueDeclare().getQueue();

            boolean ack = false; //打开应答机制
            channel.queueBind(queueName, EXCHANGE_NAME, ROUTING_KEY);

            com.rabbitmq.client.Consumer consumer = new DefaultConsumer(channel) {
                public void handleDelivery(String consumerTag,
                                           Envelope envelope,
                                           AMQP.BasicProperties properties,
                                           byte[] body)
                        throws IOException {
                    String message = new String(body, "UTF-8");
                    System.out.println(" [x] Received '" + envelope.getRoutingKey() + "':'" + message + "'");
                }
            };

            channel.basicConsume(queueName, true, consumer);
            return COUNT;
        }
    }

    public static void execute(){

    }


    public static void main(String[] args){
        for(int i = 0; i < WORK_NUMBER; i ++){
            exec.submit(new Worker());
        }

        System.out.println("exit!");
    }

}
