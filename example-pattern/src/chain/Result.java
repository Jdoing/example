package chain;

/**
 * Created by juemingzi on 16/5/11.
 */
public class Result {

    private boolean success;

    private String message;

    private Object model;

    public Result(boolean success){
        this.success = success;
    }

    public static Result failResult(){
        return new Result(false);
    }

    public static Result successResult(){
        return new Result(true);
    }


    public boolean isSuccess() {
        return success;
    }

    public void setSuccess(boolean success) {
        this.success = success;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public Object getModel() {
        return model;
    }

    public void setModel(Object model) {
        this.model = model;
    }
}
