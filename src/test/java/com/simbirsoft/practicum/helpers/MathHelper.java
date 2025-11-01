package com.simbirsoft.practicum.helpers;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Collections;
import java.util.List;
import java.util.TreeMap;
import java.util.function.ToDoubleFunction;
import java.util.stream.Collectors;

public class MathHelper {
    private static final Logger logger = LoggerFactory.getLogger(MathHelper.class);

    private MathHelper() {
    }

    /**
     * Вычисляет среднее значение для числовых свойств элементов
     */
    public static <T> double calculateAverage(List<T> items, ToDoubleFunction<T> mapper) {
        logger.debug("Вычисление среднего значения для {} элементов", items.size());

        if (items == null || items.isEmpty()) {
            logger.error("Список элементов не может быть пустым");
            throw new IllegalArgumentException("Список элементов не может быть пустым");
        }

        double average = items.stream()
                .mapToDouble(mapper)
                .average()
                .orElseThrow();

        logger.debug("Среднее значение: {}", average);
        return average;
    }

    /**
     * Находит элементы с минимальным отклонением от целевого значения
     */
    public static <T> List<T> findClosestItems(List<T> items, double target, ToDoubleFunction<T> valueExtractor) {
        logger.debug("Поиск ближайших элементов к значению: {}", target);

        if (items == null || items.isEmpty()) {
            logger.warn("Список элементов пуст, возвращаем пустой список");
            return Collections.emptyList();
        }

        TreeMap<Double, List<T>> differences = items.stream()
                .collect(Collectors.groupingBy(
                        item -> Math.abs(valueExtractor.applyAsDouble(item) - target),
                        TreeMap::new,
                        Collectors.toList()
                ));

        List<T> closestItems = differences.firstEntry().getValue();
        logger.debug("Найдено ближайших элементов: {} со значением различия: {}",
                closestItems.size(), differences.firstKey());
        return closestItems;
    }

    /**
     * Вычисляет среднюю длину строк в списке
     */
    public static double calculateAverageStringLength(List<String> strings) {
        logger.debug("Вычисление средней длины строк из {} элементов", strings.size());

        if (strings == null || strings.isEmpty()) {
            logger.error("Список строк не может быть пустым");
            throw new IllegalArgumentException("Список строк не может быть пустым");
        }

        double averageLength = strings.stream()
                .mapToInt(String::length)
                .average()
                .orElseThrow();

        logger.debug("Средняя длина строк: {}", averageLength);
        return averageLength;
    }
}
