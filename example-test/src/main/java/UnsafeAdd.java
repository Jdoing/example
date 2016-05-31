import javax.servlet.*;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by juemingzi on 16/4/16.
 */
public class UnsafeAdd {
    private volatile int value;

    public int getNext(){
        return value++;
    }
}


class SafeServlet implements Servlet {
    public void init(ServletConfig config) throws ServletException {

    }

    public ServletConfig getServletConfig() {
        return null;
    }

    public void service(ServletRequest req, ServletResponse res) throws ServletException, IOException {

    }

    public String getServletInfo() {
        return null;
    }

    public void destroy()        {

    }
}

class UnsafeCache {
    private Map<String, Integer> map = new HashMap<String, Integer>();

    public void put(String key, Integer value){
        if(map.containsKey(key))
            map.put(key, value);
    }

    public Integer get(String key){
        return map.get(key);
    }
}

class Resource{

}

class DoubleCheckedLocking{
    private static Resource resource;

    public static Resource getInstance(){
        if(resource == null){
            synchronized ((DoubleCheckedLocking.class)){
                if(resource == null)
                    resource = new Resource();
            }
        }
        return resource;
    }
}

class DeadLock{
    private Object A = new Object();
    private Object B = new Object();
    private Object C = new Object();

    public void holdA(){
        synchronized (A){
            synchronized (B){
                doSomething();
            }
        }
    }

    private void doSomething() {
    }

    public void holdB(){
        synchronized (B){
            synchronized (C){
                doSomething();
            }
        }
    }

    public void holdC(){
        synchronized (C){
            synchronized (A){
                doSomething();
            }
        }
    }
}