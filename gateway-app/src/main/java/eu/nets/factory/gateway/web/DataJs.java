package eu.nets.factory.gateway.web;

import java.lang.annotation.Retention;

import static java.lang.annotation.RetentionPolicy.RUNTIME;

@Retention(RUNTIME)
public @interface DataJs {
    boolean ignore() default false;
}
