import com.google.common.util.concurrent.Futures;
import com.google.common.util.concurrent.ListenableFuture;
import com.google.common.util.concurrent.ListeningExecutorService;
import com.google.common.util.concurrent.MoreExecutors;
import org.junit.Test;

import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Created by juemingzi on 16/5/17.
 */
public class GuavaTest {

    @Test
    public void test() throws ExecutionException, InterruptedException {

        ListeningExecutorService service = MoreExecutors.listeningDecorator(Executors.newFixedThreadPool(10));

        final AtomicInteger atomicInteger = new AtomicInteger(0);

        ListenableFuture<?> f1 = service.submit(new Callable<Integer>() {
            public Integer call() {
                return atomicInteger.incrementAndGet();
            }
        });


        ListenableFuture<?> f2 = service.submit(new Callable<Integer>() {
            public Integer call() throws Exception {


                return atomicInteger.incrementAndGet();
            }
        });

        ListenableFuture<?> f3 = service.submit(new Runnable() {
            public void run() {
                System.out.println("OK");

            }
        });

        ListenableFuture<List<Object>> results = Futures.allAsList(f1, f2, f3);

//        List re = results.get();
//        if(re instanceof List){
//            List res = List.class.cast(re);
//            System.out.println("list :" +res);
//        }
//
//        System.out.println(results.get());
        for (Object obj : results.get()) {
            if (obj != null)
                System.out.println(obj);
        }


    }


}
