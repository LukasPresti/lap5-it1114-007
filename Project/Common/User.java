/**
 * UCID: lap5
 * Date: 2025-11-23
 * Summary: Represents a user/client with an ID and name.
 */
package Common;

public class User {
    private long clientId = Constants.DEFAULT_CLIENT_ID;
    private String clientName;
    private int points = 0;
    private String currentChoice = null;
    private boolean isEliminated = false;
    private boolean isReady = false;

    /**
     * @return the clientId
     */
    public long getClientId() {
        return clientId;
    }

    /**
     * @param clientId the clientId to set
     */
    public void setClientId(long clientId) {
        this.clientId = clientId;
    }

    /**
     * @return the username
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

    public String getDisplayName() {
        return String.format("%s(%s)", clientName, clientId);
    }

    public int getPoints() {
        return points;
    }

    public void setPoints(int points) {
        this.points = points;
    }

    public String getCurrentChoice() {
        return currentChoice;
    }

    public void setCurrentChoice(String currentChoice) {
        this.currentChoice = currentChoice;
    }

    public boolean isEliminated() {
        return isEliminated;
    }

    public void setEliminated(boolean isEliminated) {
        this.isEliminated = isEliminated;
    }

    public boolean isReady() {
        return isReady;
    }

    public void setReady(boolean isReady) {
        this.isReady = isReady;
    }

    public void reset() {
        this.clientName = null;
        this.clientId = Constants.DEFAULT_CLIENT_ID;
        this.points = 0;
        this.currentChoice = null;
        this.isEliminated = false;
        this.isReady = false;
    }
}
