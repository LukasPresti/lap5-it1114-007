/**
 * UCID: lap5
 * Date: 2025-11-24
 * Summary: Payload for syncing player points to clients.
 */
package Common;

public class PointsPayload extends Payload {
    private long clientId;
    private int points;

    public PointsPayload() {
        setPayloadType(PayloadType.SYNC_PAYLOAD);
    }

    public long getClientId() {
        return clientId;
    }

    public void setClientId(long clientId) {
        this.clientId = clientId;
    }

    public int getPoints() {
        return points;
    }

    public void setPoints(int points) {
        this.points = points;
    }

    @Override
    public String toString() {
        return String.format("PointsPayload[clientId=%d, points=%d]", clientId, points);
    }
}
