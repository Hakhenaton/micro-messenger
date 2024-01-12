package fr.sncf.d2d.micromessenger.server;

import fr.sncf.d2d.micromessenger.common.MessageFormatter;

/**
 * Procédures effecutées par le serveur
 */
public class ServerOperations {
    
    /**
     * Met en place les listeners du serveur et lance l'écoute
     * @param server le serveur à lancer
     * @throws Exception si une erreur survient
     */
    public static void start(Server server) throws Exception {

        server.onClientConnect(client -> {
            System.out.println("new client has connected: " + client.getUsername());
        });

        server.onClientDisconnect(client -> {
            System.out.println(client.getUsername() + " has disconnected");
        });

        server.onClientMessage(message -> {
            System.out.println(MessageFormatter.format(message.getSentAt(), message.getClient().getUsername(), message.getMessage()));
        });
        
        server.onServerError(ex -> {
            System.out.println(String.format(
                "Server error (%s): %s", 
                ex.getClass().getSimpleName(), 
                ex.getMessage()
            ));
        });

        server.onListening(() -> {
            System.out.println(String.format("Server listening on %s:%d", server.getHost(), server.getPort()));
        });

        server.listen();
    }
}
