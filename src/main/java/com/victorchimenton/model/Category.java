package com.victorchimenton.model;

import lombok.Data;

import java.util.List;

@Data
public class Category {
    private String name;
    private String description;
    private List<Product> products;
}
