package common;

import transport.ExchangeClient;

import java.util.concurrent.ExecutionException;

/**
 * Created by juemingzi on 16/5/24.
 */
public class ClientInvoker<T> implements Invoker{

    private ExchangeClient exchangeClient;

    private Class<T> clazz;

    public ClientInvoker(Class<T> clazz){
        this.clazz = clazz;
    }

    public ExchangeClient getExchangeClient() {
        return exchangeClient;
    }

    public void setExchangeClient(ExchangeClient exchangeClient) {
        this.exchangeClient = exchangeClient;
    }

    public Class<T> getClazz() {
        return clazz;
    }

    public void setClazz(Class<T> clazz) {
        this.clazz = clazz;
    }

    public Result invoke(Invocation invocation) throws ExecutionException, InterruptedException {

        ResponseFuture responseFuture = exchangeClient.send(invocation);

        return new Result(responseFuture.get());
    }

}
