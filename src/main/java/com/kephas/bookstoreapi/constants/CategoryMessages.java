package com.kephas.bookstoreapi.constants;

public final class CategoryMessages {
    private CategoryMessages() {};

    public static final String CATEGORY_CREATED = "Category created successfully";
    public static final String CATEGORY_UPDATED = "Category updated successfully";
    public static final String CATEGORY_DELETED = "Category deleted successfully";

    public static final String CATEGORY_NOT_FOUND_BY_ID = "Category with id: %s does not exist";
    public static final String CATEGORY_NOT_FOUND = "Category does not exist";
    public static final String UNCATEGORIZED_NAME = "Uncategorized";
    public static final String UNCATEGORIZED_DESCRIPTION = "This category is used for books without a defined category.";

}
