package com.sample.app2.test.base;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;


/*
 * Test case methods marked with this annotation will be called in a background thread.
 * These method should properly handle thread interruption (by checking
 * Thread.currentThread.isInterrupted() or handling InterruptedException)
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
public @interface Timeout {
    long millis() default 0;
}
