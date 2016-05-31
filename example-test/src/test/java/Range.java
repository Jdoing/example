import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

/**
 * Created by juemingzi on 16/5/17.
 */
public class Range {
    //不变性条件: low < high
    private int low;
    private int high;

    Lock lockLow = new ReentrantLock();
    Lock locHigh = new ReentrantLock();

    public void setLow(int low){
        lockLow.lock();
        if(low < high){
            this.low = low;
        }
        lockLow.unlock();
    }

    public void setHigh(int high){
        locHigh.lock();
        if(low < high){
            this.high = high;
        }
        locHigh.unlock();
    }
}
