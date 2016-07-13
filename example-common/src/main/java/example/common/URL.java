package example.common;

import java.io.Serializable;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.TreeMap;

/**
 * Created by juemingzi on 16/7/8.
 */
public class URL implements Serializable {
    private final String ROOT = "/example";

    private String protocol;

    private String host;

    private int port;

    private final String path;

    private String serviceName;

    private Map<String, String> parameters = new HashMap<>();

    public URL(String protocol, String serviceName) {
        this.serviceName = serviceName;
        this.protocol = protocol;
        this.host = Constant.LOCAL_HOST;
        this.port = Constant.PORT;
        this.path = buildString();
    }

    public URL(String protocol, String host, int port, String serviceName, Map<String, String> parameters) {
        this.protocol = protocol;
        this.host = host;
        this.port = port;
        this.parameters = parameters;
        this.serviceName = serviceName;

        this.path = buildString();
    }

    public URL addParameter(String key, String value) {
        parameters.put(key, value);
        return new URL(protocol, host, port, serviceName, getParameters());
    }

    public String getRegisterPath() throws UnsupportedEncodingException {
        return ROOT + '/' + serviceName + '/' + protocol + '/' + URLEncoder.encode(buildString(), "UTF-8");
    }

    public String getSubscribePath() {
        return ROOT + '/' + serviceName + '/' + ServiceType.PROVIDER.getLabel();
    }

    private String buildString() {
        StringBuilder buf = new StringBuilder();

        if (protocol != null && protocol.length() > 0) {
            buf.append(protocol);
            buf.append("://");
        }

        buf.append(host).append(':').append(port).append('/').append(serviceName).append(buildParameters());

        return buf.toString();
    }

    private String buildParameters() {
        if (parameters.size() == 0)
            return "";

        StringBuilder buf = new StringBuilder();

        boolean first = true;
        for (Map.Entry<String, String> entry : new TreeMap<>(getParameters()).entrySet()) {
            if (first) {
                buf.append("?");

                first = false;
            } else {
                buf.append("&");
            }
            buf.append(entry.getKey());
            buf.append("=");
            buf.append(entry.getValue() == null ? "" : entry.getValue().trim());
        }

        return buf.toString();
    }

    public Map<String, String> getParameters() {
        return parameters;
    }

    public String getServiceName() {
        return serviceName;
    }

    public static String decode(String value) {
        if (value == null || value.length() == 0) {
            return "";
        }
        try {
            return URLDecoder.decode(value, "UTF-8");
        } catch (UnsupportedEncodingException e) {
            throw new RuntimeException(e.getMessage(), e);
        }
    }

    //provider://10.1.51.239:20880/com.alibaba.dubbo.demo.DemoService?anyhost=true&application=demo-provider
    public static URL valueOf(String url) {
        if (url == null || (url = url.trim()).length() == 0) {
            throw new IllegalArgumentException("url == null");
        }
        String protocol = null;
        String host = null;
        int port = 0;
        String serviceName = null;
        Map<String, String> parameters = new HashMap<>();
        int i = url.indexOf("?"); // seperator between body and parameters
        if (i >= 0) {
            String[] parts = url.substring(i + 1).split("\\&");
            for (String part : parts) {
                part = part.trim();
                if (part.length() > 0) {
                    int j = part.indexOf('=');
                    if (j >= 0) {
                        parameters.put(part.substring(0, j), part.substring(j + 1));
                    } else {
                        parameters.put(part, part);
                    }
                }
            }
            url = url.substring(0, i);
        }

        i = url.indexOf("://");
        if (i >= 0) {
            if (i == 0) throw new IllegalStateException("url missing protocol: \"" + url + "\"");
            protocol = url.substring(0, i);
            url = url.substring(i + 3);
        }

        i = url.indexOf("/");
        if (i >= 0) {
            serviceName = url.substring(i + 1);
            url = url.substring(0, i);
        }

        i = url.indexOf(":");
        if (i >= 0 && i < url.length() - 1) {
            port = Integer.parseInt(url.substring(i + 1));
            url = url.substring(0, i);
        }
        if (url.length() > 0) host = url;
        return new URL(protocol, host, port, serviceName, parameters);
    }

    public String getPath() {
        return path;
    }

    public String getHost() {
        return host;
    }

    public int getPort() {
        return port;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        URL url = (URL) o;
        return Objects.equals(path, url.path);
    }

    @Override
    public int hashCode() {
        return Objects.hash(path);
    }
}
