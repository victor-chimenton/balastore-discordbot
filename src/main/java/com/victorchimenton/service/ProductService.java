package com.victorchimenton.service;

import com.victorchimenton.model.Category;
import com.victorchimenton.model.Product;
import com.victorchimenton.util.JsonUtil;

import java.util.List;

public class ProductService {
    private static final String JSON_PATH = "data.json";

    public List<Product> getProductsByCategory(String categoryName) {
        var categories = JsonUtil.loadJson(JSON_PATH, "categories", Category.class);
        return categories.stream()
                .filter(cat -> cat.getName().equalsIgnoreCase(categoryName))
                .findFirst()
                .map(Category::getProducts)
                .orElse(List.of());
    }
}
