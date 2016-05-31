package common;

import java.io.Serializable;
import java.util.concurrent.atomic.AtomicLong;

/**
 * Created by juemingzi on 16/5/25.
 */
public class Request implements Serializable{
    private static final AtomicLong GEN_ID = new  AtomicLong(0);
    private static final long serialVersionUID = -6599632286252577338L;

    private long msgId;

    private Invocation invocation;

    public Request(){
        msgId = newMsgId();
    }

    public Invocation getInvocation() {
        return invocation;
    }

    public void setInvocation(Invocation invocation) {
        this.invocation = invocation;
    }

    public long getMsgId() {
        return msgId;
    }

    public void setMsgId(long msgId) {
        this.msgId = msgId;
    }

    public static long newMsgId(){
        return GEN_ID.incrementAndGet();
    }

    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder("Request{");
        sb.append("msgId=").append(msgId);
        sb.append(", invocation=").append(invocation);
        sb.append('}');
        return sb.toString();
    }
}
