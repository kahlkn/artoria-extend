package artoria.event;

import artoria.exception.ExceptionUtils;
import artoria.user.UserInfo;
import artoria.user.UserUtils;
import artoria.util.ArrayUtils;
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

    private void addEvent(JoinPoint joinPoint, Object result) {
        Signature signature = joinPoint.getSignature();
        MethodSignature methodSign = (MethodSignature) signature;
        Method method = methodSign.getMethod();

        EventRecord eventRecord = method.getAnnotation(EventRecord.class);
        String eventName = eventRecord.name();
        String eventType = eventRecord.type();
        boolean input = eventRecord.input();
        boolean output = eventRecord.output();

        List<Object> args = getArguments(joinPoint);
        if (output) {
            EventUtils.record().setProperty("output", result);
        }
        if (input) {
            EventUtils.record().setProperty("input", args);
        }

        Object target = joinPoint.getTarget();
        String signName = signature.getName();
        String methodName = target.getClass().getName() + "." + signName + "()";
        EventUtils.record().setProperty("methodName", methodName);

        UserInfo userInfo = UserUtils.getUserInfo();
        String userId = userInfo != null ? userInfo.getId() : null;
        EventUtils.record()
                .setName(eventName)
                .setType(eventType)
                .setDistinctId(userId)
                .submit();
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
            EventUtils.record()
                    .setProperty("processTime", processTime);
            addEvent(joinPoint, result);
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
            EventUtils.record()
                    .setProperty("processTime", processTime)
                    .setProperty("errorMessage", ExceptionUtils.toString(th));
            addEvent(joinPoint, null);
        }
        catch (Exception e) {
            log.error(getClass().getSimpleName() + ": An error has occurred. ", e);
        }
    }

}
