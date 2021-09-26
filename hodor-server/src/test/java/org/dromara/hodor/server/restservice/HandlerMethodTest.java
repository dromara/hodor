package org.dromara.hodor.server.restservice;

import java.lang.reflect.Method;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;
import org.dromara.hodor.common.utils.GsonUtils;
import org.junit.Test;

public class HandlerMethodTest {

    @Test
    public void testListParameter() throws Exception {
        TestClass testClass = new TestClass();
        List<TestClass> list = new ArrayList<>();
        list.add(testClass);

        String request = GsonUtils.getGson().toJson(list);

        Method[] declaredMethods = testClass.getClass().getDeclaredMethods();
        HandlerMethod handlerMethod = new HandlerMethod(testClass, declaredMethods[0]);

        Object[] args = new Object[1];
        for (HandlerMethod.MethodParameter parameter : handlerMethod.getParameter()) {
            Type parameterizedType = parameter.getParameterizedType();
            Object o = GsonUtils.getGson().fromJson(request, parameterizedType);
            args[0] = o;
        }
        Object invoke = handlerMethod.invoke(args);
        System.out.println(invoke);
    }

    @Test
    public void testObjectParameter() throws Exception {
        TestClass testClass = new TestClass();
        String request = GsonUtils.getGson().toJson(testClass);

        Method[] declaredMethods = testClass.getClass().getDeclaredMethods();
        HandlerMethod handlerMethod = new HandlerMethod(testClass, declaredMethods[1]);

        Object[] args = new Object[1];
        for (HandlerMethod.MethodParameter parameter : handlerMethod.getParameter()) {
            Type parameterizedType = parameter.getParameterizedType();
            Object o = GsonUtils.getGson().fromJson(request, parameterizedType);
            args[0] = o;
        }
        Object invoke = handlerMethod.invoke(args);
        System.out.println(invoke);
    }

    static class TestClass {

        public void test(List<TestClass> testClassList) {
            for (TestClass testClass : testClassList) {
                System.out.println(testClass);
            }
        }

        public void test1(TestClass testClass) {
            System.out.println(testClass);
        }

    }

}
