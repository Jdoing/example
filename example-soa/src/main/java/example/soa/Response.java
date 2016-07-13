package example.soa;

import java.io.Serializable;

/**
 * Created by juemingzi on 16/5/25.
 */
public class Response implements Serializable{

    private static final long serialVersionUID = 6764938524077537666L;

    private long msgId;

    private Object result;

    public Object getResult() {
        return result;
    }

    public void setResult(Object result) {
        this.result = result;
    }

    public long getMsgId() {
        return msgId;
    }

    public void setMsgId(long msgId) {
        this.msgId = msgId;
    }


}
