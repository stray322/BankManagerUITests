package com.simbirsoft.practicum.listeners;

import org.testng.IAnnotationTransformer;
import org.testng.annotations.ITestAnnotation;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.Constructor;
import java.lang.reflect.Method;

/**
 * Listener для настройки повторного запуска тестов
 */
public class TestRetryListener implements IAnnotationTransformer {
    private static final Logger logger = LoggerFactory.getLogger(TestRetryListener.class);

    @Override
    public void transform(ITestAnnotation annotation, Class testClass,
                          Constructor testConstructor, Method testMethod) {
        annotation.setRetryAnalyzer(TestRetryAnalyzer.class);
        logger.debug("Установлен ретрай анализатор для метода: {}", testMethod.getName());
    }
}
