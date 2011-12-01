package com.lithium3141.OpenWarp;

/**
 * Exception thrown when encountering a permissions-based error.
 */
public class OWPermissionException extends Exception {
    /**
     * Create a new OWPermissionException.
     */
    public OWPermissionException() {
        super();
    }

    /**
     * Create a new OWPermissionException with the given message.
     *
     * @param message The message to include in this OWPermissionException.
     */
    public OWPermissionException(String message) {
        super(message);
    }
}
