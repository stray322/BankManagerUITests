package com.simbirsoft.practicum.enums;

import java.util.Comparator;

public enum SortOrder {
    ASCENDING(Comparator.naturalOrder(), "По возрастанию"),
    DESCENDING(Comparator.reverseOrder(), "По убыванию");

    private final Comparator<String> comparator;
    private final String description;

    SortOrder(Comparator<String> comparator, String description) {
        this.comparator = comparator;
        this.description = description;
    }

    public Comparator<String> getComparator() {
        return comparator;
    }

    public String getDescription() {
        return description;
    }

    @Override
    public String toString() {
        return description;
    }

    /**
     * Получить противоположный порядок сортировки
     */
    public SortOrder getOpposite() {
        return this == ASCENDING ? DESCENDING : ASCENDING;
    }
}
