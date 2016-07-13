package example.common;

import org.apache.commons.lang3.StringUtils;

/**
 * Created by juemingzi on 16/7/12.
 */
public class UrlUtils {

    public static boolean isMatch(URL consumer, URL provider) {
        if (StringUtils.equals(consumer.getServiceName(), provider.getServiceName())) {
            return true;
        } else {
            return false;
        }
    }

}
