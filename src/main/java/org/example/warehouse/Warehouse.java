package org.example.warehouse;

import java.math.BigDecimal;
import java.util.*;

public class Warehouse {
    // Use LinkedHashMap to maintain insertion order
    private final Map<UUID, ProductRecord> products = new LinkedHashMap<>();
    private final Set<ProductRecord> changedProducts = new HashSet<>();
    private String name;

    // Singleton instance
    private static final Map<String, Warehouse> instances = new HashMap<>();

    // Private constructor to prevent public instantiation
    private Warehouse(String name) {
        this.name = name;
        this.products.clear();
        this.changedProducts.clear();
    }

    // Method to reset the singleton instance map for testing purposes
    public static void reset() {
        instances.clear();
    }

    // Factory method to get or create a warehouse
    public static Warehouse getInstance(String name) {
        return instances.computeIfAbsent(name, Warehouse::new);
    }

    public static Warehouse getInstance() {
        return getInstance("DefaultWarehouse");
    }

    // Check if the warehouse is empty
    public boolean isEmpty() {
        return products.isEmpty();
    }

    // Get all products (unmodifiable list)
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

        // Generate a new UUID if it's null
        if (uuid == null) {
            uuid = UUID.randomUUID();
        }

        // Assign price as 0 if it's null
        if (price == null) {
            price = BigDecimal.ZERO;
        }

        // Prevent adding a product with an existing ID
        if (products.containsKey(uuid)) {
            throw new IllegalArgumentException("Product with that id already exists, use updateProduct for updates.");
        }

        ProductRecord newProduct = new ProductRecord(uuid, name, category, price);
        products.put(uuid, newProduct);  // Add the product to the map
        return newProduct;
    }


    public void updateProductPrice(UUID uuid, BigDecimal newPrice) {
        ProductRecord product = products.get(uuid);
        if (product == null) {
            throw new IllegalArgumentException("Product with that id doesn't exist.");
        }

        // Track the original product before updating
        if (!changedProducts.contains(product)) {
            changedProducts.add(product);  // Track the product before it's updated
        }

        // Only update if the price is different
        if (!product.price().equals(newPrice)) {
            ProductRecord updatedProduct = new ProductRecord(uuid, product.name(), product.category(), newPrice);
            products.put(uuid, updatedProduct);
        }
    }


    // Get changed products and clear the set after retrieval
    public List<ProductRecord> getChangedProducts() {
        List<ProductRecord> result = new ArrayList<>(changedProducts);
        changedProducts.clear();  // Clear after retrieving changed products
        return Collections.unmodifiableList(result);
    }

    // Get a product by its UUID
    public Optional<ProductRecord> getProductById(UUID uuid) {
        // Use Optional to handle cases where the product may not exist
        return Optional.ofNullable(products.get(uuid));
    }

    // Get products by category
    public List<ProductRecord> getProductsBy(Category category) {
        List<ProductRecord> result = new ArrayList<>();
        for (ProductRecord product : products.values()) {
            if (product.category().equals(category)) {
                result.add(product);
            }
        }
        return Collections.unmodifiableList(result);
    }

    // Group products by category
    public Map<Category, List<ProductRecord>> getProductsGroupedByCategories() {
        Map<Category, List<ProductRecord>> groupedProducts = new LinkedHashMap<>();
        for (ProductRecord product : products.values()) {
            groupedProducts
                    .computeIfAbsent(product.category(), k -> new ArrayList<>())
                    .add(product);
        }
        return groupedProducts;
    }
}
