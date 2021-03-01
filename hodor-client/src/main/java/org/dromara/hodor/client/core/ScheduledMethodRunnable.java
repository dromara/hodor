package org.dromara.hodor.client.core;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.UndeclaredThrowableException;
import org.springframework.util.ReflectionUtils;

/**
 * @author tomgs
 * @since 2021/3/1
 * @see org.springframework.scheduling.support.ScheduledMethodRunnable
 */
public class ScheduledMethodRunnable implements Runnable {

    private final Object target;

    private final Method method;

    private final boolean hasArg;

    private final ThreadLocal<Object[]> argsThreadLocal = new ThreadLocal<>();

    /**
     * Create a {@code ScheduledMethodRunnable} for the given target instance,
     * calling the specified method.
     * @param target the target instance to call the method on
     * @param method the target method to call
     */
    public ScheduledMethodRunnable(Object target, Method method, boolean hasArg) {
        this.target = target;
        this.method = method;
        this.hasArg = hasArg;
    }

    /**
     * Create a {@code ScheduledMethodRunnable} for the given target instance,
     * calling the specified method by name.
     * @param target the target instance to call the method on
     * @param methodName the name of the target method
     * @throws NoSuchMethodException if the specified method does not exist
     */
    public ScheduledMethodRunnable(Object target, String methodName, boolean hasArg) throws NoSuchMethodException {
        this.target = target;
        this.method = target.getClass().getMethod(methodName);
        this.hasArg = hasArg;
    }


    /**
     * Return the target instance to call the method on.
     */
    public Object getTarget() {
        return this.target;
    }

    /**
     * Return the target method to call.
     */
    public Method getMethod() {
        return this.method;
    }

    public Object[] getArgs() {
        return argsThreadLocal.get();
    }

    public void setArgs(Object... args) {
        this.argsThreadLocal.set(args);
    }

    public boolean isHasArg() {
        return hasArg;
    }

    @Override
    public void run() {
        try {
            ReflectionUtils.makeAccessible(this.method);
            //this.method.invoke(this.target);
            if (this.hasArg) {
                this.method.invoke(this.target, getArgs());
            } else {
                this.method.invoke(this.target);
            }
        }
        catch (InvocationTargetException ex) {
            ReflectionUtils.rethrowRuntimeException(ex.getTargetException());
        }
        catch (IllegalAccessException ex) {
            throw new UndeclaredThrowableException(ex);
        }
    }

    @Override
    public String toString() {
        return this.method.getDeclaringClass().getName() + "." + this.method.getName();
    }
}
