package chain;

/**
 * Created by juemingzi on 16/5/11.
 */
public abstract class DefaultHandler implements Handler{

    private Handler nextHandler;

    @Override
    public Handler getNextHandler() {
        return nextHandler;
    }

    protected void setNextHandler(Handler nextHandler){
        this.nextHandler = nextHandler;
    }

}
