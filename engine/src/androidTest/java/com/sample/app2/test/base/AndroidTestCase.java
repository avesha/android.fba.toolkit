package com.sample.app2.test.base;

import android.content.Context;
import android.test.InstrumentationTestCase;
import android.util.Log;

import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.FutureTask;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

import ru.profi1c.engine.Const;
import ru.profi1c.engine.util.ReflectionHelper;

public abstract class AndroidTestCase extends InstrumentationTestCase {

    private static final String TEST_TAG = "fba-toolkit-test";

    private static final String formatMsg(String tag, String text) {
        return String.format("%s.%s", tag, text);
    }

    protected static void log(String text) {
        Log.d(Const.APP, text);
    }

    protected static void log(String TAG, String text) {
        log(formatMsg(TAG, text));
    }

    public static void log(String tag, String format, Object... args) {
        log(tag, String.format(format, args));
    }

    public Context getContext() {
        return getInstrumentation().getContext();
    }

    public Context getTargetContext() {
        return getInstrumentation().getTargetContext();
    }

    @Override
    protected void runTest() throws Throwable {

        String fName = getName();
        assertNotNull(fName);
        Method method = null;
        try {
            // use getMethod to get all public inherited
            // methods. getDeclaredMethods returns all
            // methods of this class but excludes the
            // inherited ones.
            method = getClass().getMethod(fName, (Class[]) null);
        } catch (NoSuchMethodException e) {
            fail("Method \"" + fName + "\" not found");
        }

        if (!Modifier.isPublic(method.getModifiers())) {
            fail("Method \"" + fName + "\" should be public");
        }

        if (method.isAnnotationPresent(Timeout.class)) {
            long timeoutMillis = method.getAnnotation(Timeout.class).millis();
            if (timeoutMillis != 0) {
                runMethod(method, timeoutMillis);
                return;
            }
        }

        super.runTest();
    }

    private <V> V runMethod(final Method method, long timeoutMs)
            throws InterruptedException, ExecutionException, TimeoutException {
        return runMethod(new Callable<V>() {
            @SuppressWarnings("unchecked")
            @Override
            public V call() throws Exception {
                return (V) method.invoke(AndroidTestCase.this, (Object[]) new Class[0]);
            }
        }, timeoutMs);
    }

    protected <V> V runMethod(Callable<V> method, long timeoutMs)
            throws InterruptedException, ExecutionException, TimeoutException {
        V result = null;
        FutureTask<V> task = new FutureTask<V>(method);

        Thread t = new Thread(task);
        t.start();

        try {
            result = task.get(timeoutMs, TimeUnit.MILLISECONDS);

        } catch (InterruptedException e) {
            t.interrupt();
            t.join();
            throw e;
        } catch (ExecutionException e) {
            t.interrupt();
            t.join();
            throw e;
        } catch (TimeoutException e) {
            t.interrupt();
            t.join();
            throw e;
        } catch (Exception e) {
            t.interrupt();
            t.join();
            throw new RuntimeException(e);
        }

        t.join();
        return result;
    }

    protected void runTestMethod(final String testName, long timeoutMs)
            throws InterruptedException, ExecutionException, TimeoutException {
        runMethod(new Callable<Void>() {

            @Override
            public Void call() throws Exception {
                ReflectionHelper.callMethod(AndroidTestCase.this, testName);
                return null;
            }
        }, timeoutMs);
    }


}
