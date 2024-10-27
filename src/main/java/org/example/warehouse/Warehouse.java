package org.example.warehouse;

import java.math.BigDecimal;
import java.util.*;

public class Warehouse {
    private static final Map<String, Warehouse> instances = new HashMap<>();
    private final Map<UUID, ProductRecord> products = new LinkedHashMap<>();
    private final Set<ProductRecord> changedProducts = new LinkedHashSet<>();  // Track the original ProductRecord instances
    private final String name;

    // Private constructor to prevent public instantiation
    private Warehouse(String name) {
        this.name = name;
    }

    // Singleton factory method to get or create a warehouse instance by name
    public static Warehouse getInstance(String name) {
        if (name.isEmpty()) {
            return new Warehouse(name);
        }
        return instances.computeIfAbsent(name, Warehouse::new);
    }

    // Default instance method
    public static Warehouse getInstance() {
        return getInstance("DefaultWarehouse");
    }

    // Method to clear all instances - used for test isolation
    public static void reset() {
        instances.clear();
    }

    // Check if the warehouse is empty
    public boolean isEmpty() {
        return products.isEmpty();
    }

    // Get all products in an unmodifiable list
    public List<ProductRecord> getProducts() {
        return Collections.unmodifiableList(new ArrayList<>(products.values()));
    }

    // Add a product to the warehouse
    public ProductRecord addProduct(UUID uuid, String name, Category category, BigDecimal price) {
        if (name == null || name.trim().isEmpty()) {
            throw new IllegalArgumentException("Product name can't be null or empty.");
        }
        if (category == null) {
            throw new IllegalArgumentException("Category can't be null.");
        }
        if (uuid == null) {
            uuid = UUID.randomUUID();
        }
        if (price == null) {
            price = BigDecimal.ZERO;
        }
        if (products.containsKey(uuid)) {
            throw new IllegalArgumentException("Product with that id already exists, use updateProduct for updates.");
        }

        ProductRecord newProduct = new ProductRecord(uuid, name, category, price);
        products.put(uuid, newProduct);
        return newProduct;
    }

    // Update the price of a product and track changes by the original ProductRecord instance
    public void updateProductPrice(UUID uuid, BigDecimal newPrice) {
        ProductRecord existingProduct = products.get(uuid);
        if (existingProduct == null) {
            throw new IllegalArgumentException("Product with that id doesn't exist.");
        }

        // Only update if the price is different
        if (!existingProduct.price().equals(newPrice)) {
            ProductRecord updatedProduct = new ProductRecord(uuid, existingProduct.name(), existingProduct.category(), newPrice);
            products.put(uuid, updatedProduct);
            changedProducts.add(existingProduct);  // Track the original product instance
        }
    }

    // Retrieve changed products by their original instances and clear the list after retrieval
    public List<ProductRecord> getChangedProducts() {
        List<ProductRecord> result = new ArrayList<>(changedProducts);
        changedProducts.clear();  // Clear after retrieval to simulate one-time view
        return Collections.unmodifiableList(result);
    }

    // Get a product by its UUID
    public Optional<ProductRecord> getProductById(UUID uuid) {
        return Optional.ofNullable(products.get(uuid));
    }

    // Get all products by category in an unmodifiable list
    public List<ProductRecord> getProductsBy(Category category) {
        List<ProductRecord> filteredProducts = new ArrayList<>();
        for (ProductRecord product : products.values()) {
            if (product.category().equals(category)) {
                filteredProducts.add(product);
            }
        }
        return Collections.unmodifiableList(filteredProducts);
    }

    // Group products by category
    public Map<Category, List<ProductRecord>> getProductsGroupedByCategories() {
        Map<Category, List<ProductRecord>> groupedProducts = new LinkedHashMap<>();
        for (ProductRecord product : products.values()) {
            groupedProducts.computeIfAbsent(product.category(), k -> new ArrayList<>()).add(product);
        }

        // Make each list in the map unmodifiable
        groupedProducts.replaceAll((category, productList) -> Collections.unmodifiableList(productList));
        return groupedProducts;
    }
}
