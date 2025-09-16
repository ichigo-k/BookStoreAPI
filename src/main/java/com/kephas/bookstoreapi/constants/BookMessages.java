package com.kephas.bookstoreapi.constants;

public final class BookMessages {

    private BookMessages() {}

    public static final String BOOK_CREATED = "Book created successfully";
    public static final String BOOK_DELETED = "Book deleted successfully";
    public static final String BOOK_UPDATED = "Book updated successfully";

    public static final String NO_BOOKS_FOUND = "No books found matching the search criteria";
    public static final String BOOKS_FOUND = "Found %d book%s matching the search criteria";

    public static final String BOOK_EXISTS_BY_ISBN = "Book with ISBN already exists";
    public static final String BOOK_NOT_FOUND_BY_ID = "Book by id: %s does not exist";
    public static final String AUTHOR_NOT_FOUND = "Author does not exist";
    public static final String CATEGORY_NOT_FOUND = "Category does not exist";

}
