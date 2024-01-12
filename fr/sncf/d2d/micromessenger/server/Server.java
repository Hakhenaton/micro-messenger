package fr.sncf.d2d.micromessenger.server;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.function.Consumer;

import static fr.sncf.d2d.micromessenger.common.Callbacks.*;
import fr.sncf.d2d.micromessenger.protocol.ClientIncomingMessage;

/**
 * Objet représentant un serveur de chat
 */
public class Server {

    private final ServerOptions options;

    private ServerSocket listeningSocket;

    private Consumer<ConnectedClientMessage> onClientMessage = ignore();
    private Consumer<Exception> onServerError = ignore();
    private Consumer<ConnectedClient> onConnect = ignore();
    private Consumer<ConnectedClient> onDisconnect = ignore();
    private Runnable onListen = noop();

    private final List<ConnectedClient> clients = new ArrayList<>();
   
    /**
     * Crée une nouvelle instance de serveur
     * @param options les options du serveur
     */
    public Server(ServerOptions options){
        this.options = options;
    }

    /**
     * @return l'adresse sur laquelle le serveur écoute
     */
    public String getHost(){
        return this.options.getAddress();
    }

    /**
     * @return le port sur lequel le serveur écoute
     */
    public int getPort(){
        return this.options.getPort();
    }

    /**
     * Définit l'action à effectuer lorsqu'un message est reçu
     * @param onClientMessage l'action à effectuer à partir du message
     */
    public void onClientMessage(Consumer<ConnectedClientMessage> onClientMessage) {
        this.onClientMessage = onClientMessage;
    }

    /**
     * Définit l'action à effectuer lorsqu'un client vient de se connecter
     * @param onConnect l'action à effectuer à partir du client
     */
    public void onClientConnect(Consumer<ConnectedClient> onConnect) {
        this.onConnect = onConnect;
    }

    /**
     * Définit l'action à effectuer lorsqu'un client s'est déconnecté
     * @param onDisconnect l'action à effectuer à partir du client
     */
    public void onClientDisconnect(Consumer<ConnectedClient> onDisconnect){
        this.onDisconnect = onDisconnect;
    }

    /**
     * Définit l'action à effectuer lorsque le serveur est en écoute
     * @param onListen l'action à effectuer
     */
    public void onListening(Runnable onListen) {
        this.onListen = onListen;
    }

    /**
     * Définit l'action à effectuer lorsqu'une erreur survient lors de l'écoute du serveur
     * @param onServerError l'action à effectuer à partir de l'erreur
     */
    public void onServerError(Consumer<Exception> onServerError){
        this.onServerError = onServerError;
    }

    /**
     * Lance l'écoute du serveur
     * @throws IOException si une erreur survient
     */
    public void listen() throws IOException {
        this.listeningSocket = new ServerSocket();
        this.listeningSocket.bind(
            new InetSocketAddress(
                this.options.getAddress(), 
                this.options.getPort()
            )
        );

        this.onListen.run();
        
        while (true){
            try {
                
                final var socket = this.listeningSocket.accept();
                
                final var client = new ConnectedClient(socket);
                
                client.onMessage(message -> this.handleClientIncomingMessage(client, message));
                client.onDisconnect(() -> this.handleClientDisconnect(client));
                
                client.accept(this.options.getBanner());
                
                this.clients.add(client);
                
                this.onConnect.accept(client);
            
            } catch (Exception e) {
                this.onServerError.accept(e);
            }
        }
    }

    private void handleClientIncomingMessage(ConnectedClient client, ClientIncomingMessage message){
        final var clientMessage = new ConnectedClientMessage(client, message.getMessage(), new Date());
        for (final var otherClient: this.clients){
            try {
                otherClient.sendMessage(clientMessage);
            } catch (Exception e){
                this.onServerError.accept(e);
            }
        }
        this.onClientMessage.accept(clientMessage);
    }

    private void handleClientDisconnect(ConnectedClient client){
        this.clients.remove(client);
        this.onDisconnect.accept(client);
    }
}
