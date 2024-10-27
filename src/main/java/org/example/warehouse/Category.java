package org.example.warehouse;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public class Category {
    private final String name;  // Make `name` final for immutability
    private static final Map<String, Category> categoryCache = new HashMap<>();

    // Private constructor to prevent public instantiation
    private Category(String name) {
        this.name = capitalize(name);
    }

    // Factory method for creating or retrieving instances
    public static Category of(String name) {
        if (name == null) {
            throw new IllegalArgumentException("Category name can't be null");
        }
        return categoryCache.computeIfAbsent(capitalize(name), Category::new);
    }

    // Capitalize the first letter of the category name
    private static String capitalize(String name) {
        if (name == null || name.isEmpty()) {
            return name;
        }
        return name.substring(0, 1).toUpperCase() + name.substring(1).toLowerCase();
    }

    // Getter for name
    public String getName() {
        return name;
    }

    // Override equals and hashCode based on `name`
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Category)) return false;
        Category category = (Category) o;
        return name.equals(category.name);
    }

    @Override
    public int hashCode() {
        return Objects.hash(name);
    }
}
