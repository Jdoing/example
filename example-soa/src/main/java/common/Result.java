package common;

import java.io.Serializable;

/**
 * Created by juemingzi on 16/5/28.
 */
public class Result implements Serializable{

    private static final long serialVersionUID = -3135030969999063172L;

    private Object data;

    public Result(Object data){
        this.data = data;
    }

    public Object getData() {
        return data;
    }

    public void setData(Object data) {
        this.data = data;
    }
}
