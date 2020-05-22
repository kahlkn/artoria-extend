package artoria.servlet;

import artoria.util.ArrayUtils;
import artoria.util.CollectionUtils;
import artoria.util.MapUtils;
import artoria.util.StringUtils;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import java.util.*;

import static artoria.common.Constants.COMMA;
import static artoria.common.Constants.UNKNOWN;

/**
 * Request tools.
 * @author Kahle
 */
public class RequestUtils {

    public static String getReferer(HttpServletRequest request) {
        if (request == null) { return null; }
        String referer = request.getHeader("Referer");
        return StringUtils.isBlank(referer) ? null : referer;
    }

    public static String getUserAgent(HttpServletRequest request) {
        if (request == null) { return null; }
        String userAgent = request.getHeader("User-Agent");
        return StringUtils.isBlank(userAgent) ? null : userAgent;
    }

    public static String getRealAddress(HttpServletRequest request) {
        String remoteAddr = RequestUtils.getRemoteAddress(request);
        if (StringUtils.isBlank(remoteAddr)) { return remoteAddr; }
        if (!remoteAddr.contains(COMMA)) { return remoteAddr; }
        String[] split = remoteAddr.trim().split(COMMA);
        remoteAddr = split[split.length - 1];
        if (StringUtils.isNotBlank(remoteAddr)) {
            remoteAddr = remoteAddr.trim();
        }
        return remoteAddr;
    }

    public static String getRemoteAddress(HttpServletRequest request) {
        if (request == null) { return null; }
        String address = request.getHeader("X-Forwarded-For");
        if(StringUtils.isBlank(address) || UNKNOWN.equalsIgnoreCase(address)) {
            address = request.getHeader("X-Real-IP");
        }
        if(StringUtils.isBlank(address) || UNKNOWN.equalsIgnoreCase(address)) {
            address = request.getHeader("Proxy-Client-IP");
        }
        if(StringUtils.isBlank(address) || UNKNOWN.equalsIgnoreCase(address)) {
            address = request.getHeader("WL-Proxy-Client-IP");
        }
        if(StringUtils.isBlank(address) || UNKNOWN.equalsIgnoreCase(address)) {
            address = request.getHeader("HTTP_CLIENT_IP");
        }
        if(StringUtils.isBlank(address) || UNKNOWN.equalsIgnoreCase(address)) {
            address = request.getHeader("HTTP_X_FORWARDED_FOR");
        }
        if(StringUtils.isBlank(address) || UNKNOWN.equalsIgnoreCase(address)) {
            address = request.getRemoteAddr();
        }
        return address;
    }

    public static Map<String, String> getCookieMap(Cookie[] cookies) {
        Map<String, String> cookieMap = new LinkedHashMap<String, String>();
        if (ArrayUtils.isEmpty(cookies)) { return cookieMap; }
        for (Cookie cookie : cookies) {
            if (cookie == null) { continue; }
            String cookieValue = cookie.getValue();
            String cookieName = cookie.getName();
            cookieMap.put(cookieName, cookieValue);
        }
        return cookieMap;
    }

    public static RequestBean getRequestBean(HttpServletRequest request) {
        if (request == null) { return null; }
        RequestBean requestBean = new RequestBean();
        requestBean.setReceiveTime(new Date());
        StringBuffer requestURL = request.getRequestURL();
        requestBean.setRequestURL(String.valueOf(requestURL));
        requestBean.setMethod(request.getMethod());
        requestBean.setContentType(request.getContentType());
        requestBean.setRemoteAddress(request.getRemoteAddr());
        int remotePort = request.getRemotePort();
        requestBean.setRemotePort(String.valueOf(remotePort));
        requestBean.setCharacterEncoding(request.getCharacterEncoding());
        Map<String, String> cookieMap = getCookieMap(request.getCookies());
        requestBean.setCookies(cookieMap);
        Map<String, List<String>> headers = new LinkedHashMap<String, List<String>>();
        Enumeration<String> headerNameEnumeration = request.getHeaderNames();
        while (headerNameEnumeration.hasMoreElements()) {
            String headerName = headerNameEnumeration.nextElement();
            if (StringUtils.isBlank(headerName)) { continue; }
            Enumeration<String> valEnumeration = request.getHeaders(headerName);
            List<String> list = new ArrayList<String>();
            CollectionUtils.addAll(list, valEnumeration);
            headers.put(headerName, list);
        }
        requestBean.setHeaders(headers);
        Map<String, List<String>> parameters = new LinkedHashMap<String, List<String>>();
        Map<String, String[]> parameterMap = request.getParameterMap();
        if (MapUtils.isNotEmpty(parameterMap)) {
            for (Map.Entry<String, String[]> entry : parameterMap.entrySet()) {
                if (entry == null) { continue; }
                String[] val = entry.getValue();
                String key = entry.getKey();
                List<String> list = new ArrayList<String>();
                Collections.addAll(list, val);
                parameters.put(key, list);
            }
        }
        requestBean.setParameters(parameters);
        return requestBean;
    }

    public static String findCookieValue(HttpServletRequest request, String cookieName) {
        if (request == null) { return null; }
        if (StringUtils.isBlank(cookieName)) {
            return null;
        }
        Cookie[] cookies = request.getCookies();
        if (ArrayUtils.isEmpty(cookies)) {
            return null;
        }
        for (Cookie cookie : cookies) {
            if (cookie == null) { continue; }
            String name = cookie.getName();
            if (cookieName.equalsIgnoreCase(name)) {
                return cookie.getValue();
            }
        }
        return null;
    }

}
