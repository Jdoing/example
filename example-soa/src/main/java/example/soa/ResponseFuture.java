package example.soa;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

/**
 * Created by juemingzi on 16/5/28.
 */
public class ResponseFuture {
    private static final Map<Long, ResponseFuture> FUTURES = new ConcurrentHashMap<>();

    private Lock lock = new ReentrantLock();

    private Condition done = lock.newCondition();

    private Response response;

    public ResponseFuture(long msgId){
        FUTURES.put(msgId, this);
    }

    public Response get() throws InterruptedException {
        if(isDone()){
            return response;
        }

        lock.lock();
        try{
            while (!isDone()){
                done.await();
            }

            return response;
        }finally {
            lock.unlock();
        }

    }

    public void receive(Response response){
        lock.lock();
        try {
            this.response = response;
            done.signal();
        }finally {
            lock.unlock();
        }
    }

    public boolean isDone(){
        return response != null;
    }

    public static ResponseFuture getFuture(long msgId){

        return FUTURES.get(msgId);
    }


}
