/**
 * UCID: lap5
 * Date: 2025-11-23
 * Summary: Payload subclass for handling connection-related data like client name.
 */
package Common;

public class ConnectionPayload extends Payload {
    private String clientName;

    /**
     * @return the clientName
     */
    public String getClientName() {
        return clientName;
    }

    /**
     * @param clientName the clientName to set
     */
    public void setClientName(String clientName) {
        this.clientName = clientName;
    }

    @Override
    public String toString() {
        return super.toString() +
                String.format(" ClientName: [%s]",
                        getClientName());
    }

}
