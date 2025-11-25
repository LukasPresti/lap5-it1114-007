/**
 * UCID: lap5
 * Date: 2025-11-23
 * Summary: Manages a collection of connected clients (ServerThreads), handles message relaying, and room actions like join/leave.
 */
package Server;

import java.util.concurrent.ConcurrentHashMap;

import Common.Constants;
import Common.RoomAction;
import Exceptions.DuplicateRoomException;
import Exceptions.RoomNotFoundException;
import Common.TextFX;
import Common.TextFX.Color;
import Common.User;

public class Room implements AutoCloseable {
    private final String name;// unique name of the Room
    private volatile boolean isRunning = false;
    private final ConcurrentHashMap<Long, ServerThread> clientsInRoom = new ConcurrentHashMap<Long, ServerThread>();

    public final static String LOBBY = "lobby";

    // Game Logic
    private String gameState = Constants.GAME_STATE_LOBBY;
    private long roundTime = Constants.ROUND_TIME_MS;
    private java.util.Timer roundTimer;

    private void info(String message) {
        System.out.println(TextFX.colorize(String.format("Room[%s]: %s", name, message), Color.PURPLE));
    }

    public Room(String name) {
        this.name = name;
        isRunning = true;
        info("Created");
    }

    public String getName() {
        return this.name;
    }

    protected synchronized void addClient(ServerThread client) {
        if (!isRunning) { // block action if Room isn't running
            return;
        }
        if (clientsInRoom.containsKey(client.getClientId())) {
            info("Attempting to add a client that already exists in the room");
            return;
        }
        clientsInRoom.put(client.getClientId(), client);
        client.setCurrentRoom(this);
        client.sendResetUserList();
        syncExistingClients(client);
        // notify clients of someone joining
        joinStatusRelay(client, true);

    }

    protected synchronized void removeClient(ServerThread client) {
        if (!isRunning) { // block action if Room isn't running
            return;
        }
        if (!clientsInRoom.containsKey(client.getClientId())) {
            info("Attempting to remove a client that doesn't exist in the room");
            return;
        }
        ServerThread removedClient = clientsInRoom.get(client.getClientId());
        if (removedClient != null) {
            // notify clients of someone joining
            joinStatusRelay(removedClient, false);
            clientsInRoom.remove(client.getClientId());
            autoCleanup();
        }
    }

    private void syncExistingClients(ServerThread incomingClient) {
        clientsInRoom.values().forEach(serverThread -> {
            if (serverThread.getClientId() != incomingClient.getClientId()) {
                boolean failedToSync = !incomingClient.sendClientInfo(serverThread.getClientId(),
                        serverThread.getClientName(), RoomAction.JOIN, true);
                if (failedToSync) {
                    System.out.println(
                            String.format("Removing disconnected %s from list", serverThread.getDisplayName()));
                    disconnect(serverThread);
                }
            }
        });
    }

    private void joinStatusRelay(ServerThread client, boolean didJoin) {
        clientsInRoom.values().removeIf(serverThread -> {
            String formattedMessage = String.format("Room[%s] %s %s the room",
                    getName(),
                    client.getClientId() == serverThread.getClientId() ? "You"
                            : client.getDisplayName(),
                    didJoin ? "joined" : "left");
            final long senderId = client == null ? Constants.DEFAULT_CLIENT_ID : client.getClientId();
            // Share info of the client joining or leaving the room
            boolean failedToSync = !serverThread.sendClientInfo(client.getClientId(),
                    client.getClientName(), didJoin ? RoomAction.JOIN : RoomAction.LEAVE);
            // Send the server generated message to the current client
            boolean failedToSend = !serverThread.sendMessage(senderId, formattedMessage);
            if (failedToSend || failedToSync) {
                System.out.println(
                        String.format("Removing disconnected %s from list", serverThread.getDisplayName()));
                disconnect(serverThread);
            }
            return failedToSend;
        });
    }

    /**
     * Sends a basic String message from the sender to all connectedClients
     * Internally calls processCommand and evaluates as necessary.
     * Note: Clients that fail to receive a message get removed from
     * connectedClients.
     * Adding the synchronized keyword ensures that only one thread can execute
     * these methods at a time,
     * preventing concurrent modification issues and ensuring thread safety
     * 
     * @param message
     * @param sender  ServerThread (client) sending the message or null if it's a
     *                server-generated message
     */
    protected synchronized void relay(ServerThread sender, String message) {
        if (!isRunning) { // block action if Room isn't running
            return;
        }

        // Note: any desired changes to the message must be done before this line
        String senderString = sender == null ? String.format("Room[%s]", getName())
                : sender.getDisplayName();
        final long senderId = sender == null ? Constants.DEFAULT_CLIENT_ID : sender.getClientId();
        // Note: formattedMessage must be final (or effectively final) since outside
        // scope can't be changed inside a callback function (see removeIf() below)
        final String formattedMessage = String.format("%s: %s", senderString, message);

        // loop over clients and send out the message; remove client if message failed
        // to be sent
        // Note: this uses a lambda expression for each item in the values() collection,
        // it's one way we can safely remove items during iteration
        info(String.format("sending message to %s recipients: %s", clientsInRoom.size(), formattedMessage));

        clientsInRoom.values().removeIf(serverThread -> {
            boolean failedToSend = !serverThread.sendMessage(senderId, formattedMessage);
            if (failedToSend) {
                System.out.println(
                        String.format("Removing disconnected %s from list", serverThread.getDisplayName()));
                disconnect(serverThread);
            }
            return failedToSend;
        });
    }

    /**
     * Takes a ServerThread and removes them from the Server
     * Adding the synchronized keyword ensures that only one thread can execute
     * these methods at a time,
     * preventing concurrent modification issues and ensuring thread safety
     * 
     * @param client
     */
    private synchronized void disconnect(ServerThread client) {
        if (!isRunning) { // block action if Room isn't running
            return;
        }
        ServerThread disconnectingServerThread = clientsInRoom.remove(client.getClientId());
        if (disconnectingServerThread != null) {

            clientsInRoom.values().removeIf(serverThread -> {
                if (serverThread.getClientId() == disconnectingServerThread.getClientId()) {
                    return true;
                }
                boolean failedToSend = !serverThread.sendClientInfo(disconnectingServerThread.getClientId(),
                        disconnectingServerThread.getClientName(), RoomAction.LEAVE);
                if (failedToSend) {
                    System.out.println(
                            String.format("Removing disconnected %s from list", serverThread.getDisplayName()));
                    disconnect(serverThread);
                }
                return failedToSend;
            });
            relay(null, disconnectingServerThread.getDisplayName() + " disconnected");
            disconnectingServerThread.disconnect();
        }
        autoCleanup();
    }

    protected synchronized void disconnectAll() {
        info("Disconnect All triggered");
        if (!isRunning) {
            return;
        }
        clientsInRoom.values().removeIf(client -> {
            disconnect(client);
            return true;
        });
        info("Disconnect All finished");
    }

    /**
     * Attempts to close the room to free up resources if it's empty
     */
    private void autoCleanup() {
        if (!Room.LOBBY.equalsIgnoreCase(name) && clientsInRoom.isEmpty()) {
            close();
        }
    }

    @Override
    public void close() {
        // attempt to gracefully close and migrate clients
        if (!clientsInRoom.isEmpty()) {
            relay(null, "Room is shutting down, migrating to lobby");
            info(String.format("migrating %s clients", clientsInRoom.size()));
            clientsInRoom.values().removeIf(client -> {
                try {
                    Server.INSTANCE.joinRoom(Room.LOBBY, client);
                } catch (RoomNotFoundException e) {
                    e.printStackTrace();
                    // TODO, fill in, this shouldn't happen though
                }
                return true;
            });
        }
        Server.INSTANCE.removeRoom(this);
        isRunning = false;
        clientsInRoom.clear();
        info(String.format("closed"));
    }

    // start handle methods
    public void handleCreateRoom(ServerThread sender, String roomName) {
        try {
            Server.INSTANCE.createRoom(roomName);
            Server.INSTANCE.joinRoom(roomName, sender);
        } catch (RoomNotFoundException e) {
            info("Room wasn't found (this shouldn't happen)");
            e.printStackTrace();
        } catch (DuplicateRoomException e) {
            sender.sendMessage(Constants.DEFAULT_CLIENT_ID, String.format("Room %s already exists", roomName));
        }
    }

    public void handleJoinRoom(ServerThread sender, String roomName) {
        try {
            Server.INSTANCE.joinRoom(roomName, sender);
        } catch (RoomNotFoundException e) {
            sender.sendMessage(Constants.DEFAULT_CLIENT_ID, String.format("Room %s doesn't exist", roomName));
        }
    }

    protected synchronized void handleDisconnect(BaseServerThread sender) {
        handleDisconnect((ServerThread) sender);
    }

    /**
     * Expose access to the disconnect action
     * 
     * @param serverThread
     */
    protected synchronized void handleDisconnect(ServerThread sender) {
        disconnect(sender);
    }

    protected synchronized void handleReverseText(ServerThread sender, String text) {
        StringBuilder sb = new StringBuilder(text);
        sb.reverse();
        String rev = sb.toString();
        relay(sender, rev);
    }

    protected synchronized void handleReady(ServerThread sender) {
        if (!gameState.equals(Constants.GAME_STATE_LOBBY) && !gameState.equals(Constants.GAME_STATE_GAME_OVER)) {
            return; // Can only ready up in Lobby or Game Over
        }
        User user = sender.getUser();
        user.setReady(!user.isReady());
        relay(null, String.format("%s is %s", sender.getDisplayName(), user.isReady() ? "READY" : "NOT READY"));

        checkStartGame();
    }

    private void checkStartGame() {
        if (clientsInRoom.size() < 2)
            return; // Need at least 2 players
        boolean allReady = clientsInRoom.values().stream().map(ServerThread::getUser).allMatch(User::isReady);
        if (allReady) {
            startGame();
        }
    }

    private void startGame() {
        gameState = Constants.GAME_STATE_READY;
        relay(null, "All players ready! Game starting...");
        // Reset points and elimination
        clientsInRoom.values().forEach(client -> {
            client.getUser().setPoints(0);
            client.getUser().setEliminated(false);
            client.getUser().setCurrentChoice(null);
        });
        startRound();
    }

    private void startRound() {
        gameState = Constants.GAME_STATE_CHOOSING;
        relay(null, "Round Start! Pick your choice (R, P, S)!");
        // Reset choices for active players
        clientsInRoom.values().forEach(client -> {
            if (!client.getUser().isEliminated()) {
                client.getUser().setCurrentChoice(null);
            }
        });

        // Start Timer
        if (roundTimer != null) {
            roundTimer.cancel();
        }
        roundTimer = new java.util.Timer();
        roundTimer.schedule(new java.util.TimerTask() {
            @Override
            public void run() {
                endRound();
            }
        }, roundTime);
    }

    private void endRound() {
        // To be implemented
        info("Round Ended by Timer");
        // Need to handle thread safety if called from Timer
        // Ideally, we should synchronize or use a specific method
        processEndRound();
    }

    private synchronized void processEndRound() {
        if (roundTimer != null) {
            roundTimer.cancel();
            roundTimer = null;
        }
        gameState = Constants.GAME_STATE_ROUND_END;
        relay(null, "Round Ended! Processing results...");

        // Eliminate non-pickers
        clientsInRoom.values().forEach(client -> {
            if (!client.getUser().isEliminated() && client.getUser().getCurrentChoice() == null) {
                client.getUser().setEliminated(true);
                relay(null, String.format("%s eliminated for not picking", client.getDisplayName()));
            }
        });

        // Process Battles
        java.util.List<ServerThread> activePlayers = clientsInRoom.values().stream()
                .filter(c -> !c.getUser().isEliminated())
                .collect(java.util.stream.Collectors.toList());

        if (activePlayers.size() > 1) {
            StringBuilder battleResults = new StringBuilder("Battle Results:\n");
            java.util.Set<Long> eliminatedIds = new java.util.HashSet<>();

            for (int i = 0; i < activePlayers.size(); i++) {
                ServerThread p1 = activePlayers.get(i);
                ServerThread p2 = activePlayers.get((i + 1) % activePlayers.size());

                int result = calculateMatch(p1.getUser().getCurrentChoice(), p2.getUser().getCurrentChoice());

                String p1Name = p1.getDisplayName();
                String p2Name = p2.getDisplayName();
                String p1Choice = p1.getUser().getCurrentChoice();
                String p2Choice = p2.getUser().getCurrentChoice();

                battleResults.append(String.format("%s (%s) vs %s (%s): ", p1Name, p1Choice, p2Name, p2Choice));

                if (result == 1) { // P1 wins
                    p1.getUser().setPoints(p1.getUser().getPoints() + 1);
                    eliminatedIds.add(p2.getClientId()); // P2 lost defense
                    battleResults.append(String.format("%s Wins!\n", p1Name));
                } else if (result == -1) { // P2 wins
                    p2.getUser().setPoints(p2.getUser().getPoints() + 1);
                    eliminatedIds.add(p1.getClientId()); // P1 lost attack
                    battleResults.append(String.format("%s Wins!\n", p2Name));
                } else {
                    battleResults.append("Tie!\n");
                }
            }

            relay(null, battleResults.toString());

            // Apply eliminations
            activePlayers.forEach(p -> {
                if (eliminatedIds.contains(p.getClientId())) {
                    p.getUser().setEliminated(true);
                    relay(null, String.format("%s eliminated", p.getDisplayName()));
                }
            });
        }

        // Sync Points
        sendPoints();

        // Check Win Condition
        long activeCount = clientsInRoom.values().stream().filter(c -> !c.getUser().isEliminated()).count();
        if (activeCount <= 1) {
            endSession(activeCount == 1
                    ? clientsInRoom.values().stream().filter(c -> !c.getUser().isEliminated()).findFirst().orElse(null)
                    : null);
        } else {
            // Next Round
            new java.util.Timer().schedule(new java.util.TimerTask() {
                @Override
                public void run() {
                    startRound();
                }
            }, 5000); // 5 second delay before next round
            relay(null, "Next round starts in 5 seconds...");
        }
    }

    private int calculateMatch(String c1, String c2) {
        if (c1.equalsIgnoreCase(c2))
            return 0;
        if (c1.equalsIgnoreCase(Constants.ROCK)) {
            return c2.equalsIgnoreCase(Constants.SCISSORS) ? 1 : -1;
        }
        if (c1.equalsIgnoreCase(Constants.PAPER)) {
            return c2.equalsIgnoreCase(Constants.ROCK) ? 1 : -1;
        }
        if (c1.equalsIgnoreCase(Constants.SCISSORS)) {
            return c2.equalsIgnoreCase(Constants.PAPER) ? 1 : -1;
        }
        return 0;
    }

    private void sendPoints() {
        clientsInRoom.values().forEach(client -> {
            Common.PointsPayload payload = new Common.PointsPayload();
            payload.setClientId(client.getClientId());
            payload.setPoints(client.getUser().getPoints());
            relayPayload(payload);
        });
    }

    private void relayPayload(Common.Payload payload) {
        clientsInRoom.values().forEach(client -> client.sendToClient(payload));
    }

    private void endSession(ServerThread winner) {
        gameState = Constants.GAME_STATE_GAME_OVER;
        if (winner != null) {
            relay(null, String.format("Game Over! Winner: %s", winner.getDisplayName()));
        } else {
            relay(null, "Game Over! It's a Tie!");
        }

        // Scoreboard
        StringBuilder sb = new StringBuilder("Final Scores:\n");
        clientsInRoom.values().stream()
                .sorted((c1, c2) -> Integer.compare(c2.getUser().getPoints(), c1.getUser().getPoints()))
                .forEach(c -> sb.append(String.format("%s: %d\n", c.getDisplayName(), c.getUser().getPoints())));
        relay(null, sb.toString());

        // Reset
        clientsInRoom.values().forEach(c -> c.getUser().setReady(false));
        relay(null, "Session ended. Type /ready to start a new game.");
    }

    protected synchronized void handlePick(ServerThread sender, String choice) {
        if (!gameState.equals(Constants.GAME_STATE_CHOOSING)) {
            sender.sendMessage(Constants.DEFAULT_CLIENT_ID, "Not in choosing phase");
            return;
        }
        if (sender.getUser().isEliminated()) {
            sender.sendMessage(Constants.DEFAULT_CLIENT_ID, "You are eliminated");
            return;
        }
        // Validate choice
        if (!isValidChoice(choice)) {
            sender.sendMessage(Constants.DEFAULT_CLIENT_ID, "Invalid choice. Use R, P, or S");
            return;
        }

        sender.getUser().setCurrentChoice(choice);
        relay(null, String.format("%s made a choice", sender.getDisplayName()));

        // Check if all active players have picked
        boolean allPicked = clientsInRoom.values().stream()
                .filter(c -> !c.getUser().isEliminated())
                .allMatch(c -> c.getUser().getCurrentChoice() != null);

        if (allPicked) {
            processEndRound();
        }
    }

    private boolean isValidChoice(String choice) {
        return Constants.ROCK.equalsIgnoreCase(choice) ||
                Constants.PAPER.equalsIgnoreCase(choice) ||
                Constants.SCISSORS.equalsIgnoreCase(choice);
    }

    protected synchronized void handleMessage(ServerThread sender, String text) {
        relay(sender, text);
    }
    // end handle methods
}
