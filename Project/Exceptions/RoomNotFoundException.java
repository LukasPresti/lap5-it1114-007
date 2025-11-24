/**
 * UCID: lap5
 * Date: 2025-11-23
 * Summary: Exception thrown when a requested room cannot be found.
 */
package Exceptions;

public class RoomNotFoundException extends CustomIT114Exception {

    public RoomNotFoundException(String message) {
        super(message);
    }

    public RoomNotFoundException(String message, Throwable cause) {
        super(message, cause);
    }

}
