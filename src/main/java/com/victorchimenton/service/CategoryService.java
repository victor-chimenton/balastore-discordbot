package com.victorchimenton.service;

import com.victorchimenton.model.Category;
import com.victorchimenton.util.JsonUtil;

import java.util.List;

public class CategoryService {
    private static final String JSON_PATH = "data.json";

    public List<Category> getAllCategories() {
        return JsonUtil.loadJson(JSON_PATH, "categories", Category.class);
    }
}
