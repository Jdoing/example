package chain;

/**
 * Created by juemingzi on 16/5/11.
 */
public class Order {
    private String id;

    private Type type;

    public enum Type{

        NORMAL("普通"),

        TAKEOUT("外卖"),

        PREPAY("预支付");

        private String name;

        private Type(String name){
            this.name = name;
        }
    }


    public Type getType(){
        return type;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public void setType(Type type) {
        this.type = type;
    }
}
