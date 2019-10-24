package artoria.feign;

import artoria.spring.RequestContextUtils;
import artoria.util.CollectionUtils;
import artoria.util.StringUtils;
import feign.RequestInterceptor;
import feign.RequestTemplate;

import javax.servlet.http.HttpServletRequest;
import java.util.*;

/**
 * Feign request header transfer interceptor.
 * @author Kahle
 */
public class FeignRequestTransferInterceptor implements RequestInterceptor {

    @Override
    public void apply(RequestTemplate requestTemplate) {
        HttpServletRequest request = RequestContextUtils.getRequest();
        if (request == null) { return; }
        // Transfer request headers.
        Enumeration<String> headerNames = request.getHeaderNames();
        if (CollectionUtils.isEmpty(headerNames)) { return; }
        Map<String, Collection<String>> headers = new LinkedHashMap<String, Collection<String>>();
        while (headerNames.hasMoreElements()) {
            String name = headerNames.nextElement();
            if (StringUtils.isBlank(name)) { continue; }
            String value = request.getHeader(name);
            Collection<String> collection = headers.get(name);
            if (collection == null) {
                collection = new ArrayList<String>();
                headers.put(name, collection);
            }
            collection.add(value);
        }
        headers.putAll(requestTemplate.headers());
        requestTemplate.headers(headers);
    }

}
