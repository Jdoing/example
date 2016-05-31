package chain;

import java.io.Serializable;

/**
 * Created by juemingzi on 16/5/11.
 */
public class OrderDto implements Serializable {
    private String id;

    private String customerId;

    private int orderFrom;

    public int getOrderFrom() {
        return orderFrom;
    }

    public void setOrderFrom(int orderFrom) {
        this.orderFrom = orderFrom;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getCustomerId() {
        return customerId;
    }

    public void setCustomerId(String customerId) {
        this.customerId = customerId;
    }
}
