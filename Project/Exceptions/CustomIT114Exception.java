/**
 * UCID: lap5
 * Date: 2025-11-23
 * Summary: Base custom exception class for the project.
 */
package Exceptions;

public abstract class CustomIT114Exception extends Exception {
    public CustomIT114Exception(String message) {
        super(message);
    }

    public CustomIT114Exception(String message, Throwable cause) {
        super(message, cause);
    }
}
