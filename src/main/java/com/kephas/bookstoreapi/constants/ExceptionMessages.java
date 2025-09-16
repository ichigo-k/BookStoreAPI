package com.kephas.bookstoreapi.constants;

public final class ExceptionMessages {
    private ExceptionMessages() {}

    public static final String RESOURCE_NOT_FOUND = "Resource with id: %s does not exist";
    public static final String ENDPOINT_NOT_FOUND = "Endpoint '%s' not found";
    public static final String INVALID_REQUEST_BODY = "Invalid Request Body";
    public static final String METHOD_NOT_SUPPORTED = "Request method '%s' is not supported on this endpoint";
    public static final String CONFLICT = "Conflict: %s";
    public static final String INVALID_EMAIL_OR_PASSWORD = "Invalid email or password";
    public static final String ACCESS_DENIED = "You do not have the required role to access this resource";
    public static final String INTERNAL_SERVER_ERROR = "Internal Server Error: %s";
    public static final String MALFORMED_JSON = "Malformed JSON request";
    public static final String INVALID_PARAM_TYPE = "Invalid value for parameter '%s': expected type %s";
    public static final String INVALID_FIELD_VALUE = "Invalid value for field '%s': expected a valid %s";


    public static final String JWT_EXPIRED = "JWT token is expired";
    public static final String JWT_MALFORMED = "Malformed JWT token";
    public static final String JWT_INVALID_SIGNATURE = "Invalid JWT signature";
    public static final String AUTH_FAILED = "Authentication failed";

}
