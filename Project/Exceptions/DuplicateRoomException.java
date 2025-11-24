/**
 * UCID: lap5
 * Date: 2025-11-23
 * Summary: Exception thrown when attempting to create a room that already exists.
 */
package Exceptions;

public class DuplicateRoomException extends CustomIT114Exception {
    public DuplicateRoomException(String message) {
        super(message);
    }

    public DuplicateRoomException(String message, Throwable cause) {
        super(message, cause);
    }

}
