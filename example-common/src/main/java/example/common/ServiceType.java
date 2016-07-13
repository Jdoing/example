package example.common;

/**
 * Created by juemingzi on 16/6/28.
 */
public enum ServiceType {

    PROVIDER("provider"),

    CONSUMER("consumer");

    private String label;

    ServiceType(String label) {
        this.label = label;
    }

    public String getLabel() {
        return label;
    }
}
