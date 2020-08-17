package artoria.event;

import artoria.beans.BeanMap;
import artoria.beans.BeanUtils;
import artoria.exception.ExceptionUtils;
import artoria.user.UserInfo;
import artoria.user.UserUtils;
import artoria.util.ArrayUtils;
import artoria.util.CollectionUtils;
import artoria.util.StringUtils;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.Signature;
import org.aspectj.lang.annotation.AfterThrowing;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.aspectj.lang.reflect.MethodSignature;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.lang.reflect.Method;
import java.util.*;

import static artoria.common.Constants.FOUR;
import static artoria.common.Constants.ZERO;

@Aspect
public class EventRecordAspect {
    private static ThreadLocal<Map<Method, Long>> accessTimeMapLocal = new ThreadLocal<Map<Method, Long>>();
    private static Logger log = LoggerFactory.getLogger(EventRecordAspect.class);

    private List<Object> getArguments(JoinPoint joinPoint) {
        if (joinPoint == null) { return null; }
        Object[] args = joinPoint.getArgs();
        if (ArrayUtils.isEmpty(args)) {
            return Collections.emptyList();
        }
        List<Object> result = new ArrayList<Object>();
        for (Object arg : args) {
            if (arg instanceof HttpServletRequest) {
                continue;
            }
            if (arg instanceof HttpServletResponse) {
                continue;
            }
            result.add(arg);
        }
        return result;
    }

    private String tryExtract(List<Object> arguments) {
        if (CollectionUtils.isEmpty(arguments)) { return null; }
        for (Object argument : arguments) {
            if (argument == null) { continue; }
            BeanMap beanMap = BeanUtils.createBeanMap(argument);
            if (beanMap.containsKey("userId") &&
                    beanMap.get("userId") != null) {
                return String.valueOf(beanMap.get("userId"));
            }
            if (beanMap.containsKey("uid") &&
                    beanMap.get("uid") != null) {
                return String.valueOf(beanMap.get("uid"));
            }
            if (beanMap.containsKey("user") &&
                    beanMap.get("user") != null) {
                return "{\"user\":\"" + beanMap.get("user") + "\"}";
            }
            if (beanMap.containsKey("account") &&
                    beanMap.get("account") != null) {
                return "{\"account\":\"" + beanMap.get("account") + "\"}";
            }
            if (beanMap.containsKey("username") &&
                    beanMap.get("username") != null) {
                return "{\"username\":\"" + beanMap.get("username") + "\"}";
            }
            if (beanMap.containsKey("userName") &&
                    beanMap.get("userName") != null) {
                return "{\"userName\":\"" + beanMap.get("userName") + "\"}";
            }
        }
        return null;
    }

    private Method getMethod(JoinPoint joinPoint) {
        if (joinPoint == null) { return null; }
        Signature signature = joinPoint.getSignature();
        if (!(signature instanceof MethodSignature)) {
            return null;
        }
        MethodSignature methodSign = (MethodSignature) signature;
        return methodSign.getMethod();
    }

    private void setupAccessTime(Method method) {
        if (method == null) { return; }
        Map<Method, Long> accessTimeMap = accessTimeMapLocal.get();
        if (accessTimeMap == null) {
            accessTimeMap = new HashMap<Method, Long>(FOUR);
            accessTimeMapLocal.set(accessTimeMap);
        }
        accessTimeMap.put(method, System.nanoTime());
    }

    private void clearAccessTime(Method method) {
        Map<Method, Long> accessTimeMap = accessTimeMapLocal.get();
        if (accessTimeMap == null) { return; }
        if (method != null) {
            accessTimeMap.remove(method);
        }
        if (accessTimeMap.size() == ZERO) {
            accessTimeMapLocal.remove();
        }
    }

    private Long calcProcessTime(Method method) {
        if (method == null) { return null; }
        Map<Method, Long> accessTimeMap = accessTimeMapLocal.get();
        if (accessTimeMap == null) { return null; }
        Long accessTime = accessTimeMap.get(method);
        if (accessTime == null) { return null; }
        return (System.nanoTime() - accessTime) / 1000000;
    }

    private void addEvent(JoinPoint joinPoint, Map<String, Object> properties, Object result) {
        Signature signature = joinPoint.getSignature();
        MethodSignature methodSign = (MethodSignature) signature;
        Method method = methodSign.getMethod();

        EventRecord eventRecord = method.getAnnotation(EventRecord.class);
        String event = eventRecord.event();
        String type = eventRecord.type();
        boolean input = eventRecord.input();
        boolean output = eventRecord.output();

        List<Object> args = getArguments(joinPoint);
        if (output) {
            properties.put("output", result);
        }
        if (input) {
            properties.put("input", args);
        }

        Object target = joinPoint.getTarget();
        String signName = signature.getName();
        String methodName = target.getClass().getName() + "." + signName + "()";
        properties.put("methodName", methodName);

        UserInfo userInfo = UserUtils.getUserInfo();
        String userId = userInfo != null ? userInfo.getId() : null;
        if (StringUtils.isBlank(userId)) { userId = tryExtract(args); }
        EventUtils.addEvent(event, type, userId, null, properties);
    }

    @Pointcut("@annotation(artoria.event.EventRecord)")
    public void eventPointcut() {

    }

    @Around("eventPointcut()")
    public Object eventAround(ProceedingJoinPoint joinPoint) throws Throwable {
        Method method = null;
        try {
            method = getMethod(joinPoint);
            setupAccessTime(method);
        }
        catch (Exception e) {
            log.error(getClass().getSimpleName() + ": An error has occurred. ", e);
        }

        Object result = joinPoint.proceed();

        try {
            Long processTime = calcProcessTime(method);
            clearAccessTime(method);
            Map<String, Object> properties = new LinkedHashMap<String, Object>();
            properties.put("processTime", processTime);
            addEvent(joinPoint, properties, result);
        }
        catch (Exception e) {
            log.error(getClass().getSimpleName() + ": An error has occurred. ", e);
        }

        return result;
    }

    @AfterThrowing(pointcut = "eventPointcut()", throwing = "th")
    public void eventAfterThrowing(JoinPoint joinPoint, Throwable th) {
        try {
            Method method = getMethod(joinPoint);
            Long processTime = calcProcessTime(method);
            clearAccessTime(method);
            Map<String, Object> properties = new LinkedHashMap<String, Object>();
            properties.put("processTime", processTime);
            properties.put("errorMessage", ExceptionUtils.toString(th));
            addEvent(joinPoint, properties, null);
        }
        catch (Exception e) {
            log.error(getClass().getSimpleName() + ": An error has occurred. ", e);
        }
    }

}
