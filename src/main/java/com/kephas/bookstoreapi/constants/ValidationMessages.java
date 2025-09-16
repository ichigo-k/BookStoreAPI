package com.kephas.bookstoreapi.constants;

public final class ValidationMessages {
    private ValidationMessages(){};

    // Book validations
    public static final String TITLE_NOT_BLANK = "Book title cannot be blank";
    public static final String ISBN_NOT_BLANK = "Book isbn cannot be blank";
    public static final String PRICE_NOT_NULL = "Book price cannot be null";
    public static final String PUBLICATION_DATE_NOT_NULL = "Publication date cannot be blank";
    public static final String DESCRIPTION_NOT_BLANK = "Book description cannot be blank";
    public static final String DESCRIPTION_SIZE = "Description must be 10 to 255 characters long";
    public static final String CATEGORY_REQUIRED = "Category is required";
    public static final String AUTHOR_REQUIRED = "Author is required";

    // Category validations
    public static final String CATEGORY_NAME_NOT_BLANK = "Category name cannot be blank";

    // Author validations
    public static final String AUTHOR_NAME_NOT_BLANK = "Author name is required";

    // Name validations
    public static final String USER_NAME_REQUIRED = "Name is required";

    // Email validations
    public static final String EMAIL_REQUIRED = "Email is required";
    public static final String EMAIL_INVALID = "Please provide a valid email";

    // Password validations
    public static final String PASSWORD_REQUIRED = "Password is required";
    public static final String PASSWORD_MIN_LENGTH = "Password must be at least 8 characters long";

    // New password validations
    public static final String NEW_PASSWORD_REQUIRED = "New password is required";
    public static final String NEW_PASSWORD_MIN_LENGTH = "New password must be at least 8 characters long";



}
