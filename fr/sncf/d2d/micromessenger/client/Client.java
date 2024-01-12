package fr.sncf.d2d.micromessenger.client;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.time.Duration;
import java.util.Optional;
import java.util.function.Consumer;

import static fr.sncf.d2d.micromessenger.common.Callbacks.*;
import fr.sncf.d2d.micromessenger.protocol.ClientConnection;
import fr.sncf.d2d.micromessenger.protocol.ClientIncomingMessage;
import fr.sncf.d2d.micromessenger.protocol.ServerAccept;
import fr.sncf.d2d.micromessenger.protocol.ServerIncomingMessage;

/**
 * classe représentant un client micro-messenger
 */
public class Client {

    private static final Duration SOCKET_OPERATION_TIMEOUT = Duration.ofSeconds(30);

    // options du client
    private final ClientOptions options;

    // le socket de la connexion sous-jacente
    private Socket connection;

    // on utilise des streams spécifiques pour envoyer/recevoir des objets Java sérialisés
    private ObjectOutputStream output;
    private ObjectInputStream input;

    // flag d'état de la connexion
    private boolean connected = false;

    private Thread messageReadsThread;

    private Consumer<ServerIncomingMessage> onMessage = ignore();
    private Consumer<Exception> onError = ignore();
    
    /**
     * Créer un nouveau client.
     * @param options Les options pour ce client.
     */
    public Client(ClientOptions options){
        this.options = options;
    }

    /**
     * Attacher un callback à appeler lors de la réception d'un message.
     * @param consumer La fonction de consommation du message.
     */
    public void onMessage(Consumer<ServerIncomingMessage> consumer){
        this.onMessage = consumer;
    }

    /**
     * Attacher un callback à appeler lors d'une erreur de réception de message.
     * @param consumer La fonction de consommation de l'erreur.
     */
    public void onError(Consumer<Exception> consumer){
        this.onError = consumer;
    }

    public ClientOptions getOptions(){
        return this.options;
    }

    /**
     * Se connecter au serveur. Cette méthode effectue une déconnexion si le client est déjà connecté.
     * @throws IOException En cas d'erreur de connexion ou d'entrées/sorties.
     * @return La bannière du serveur telle que lue lors de la connexion.
     */
    public Optional<String> connect() throws IOException, ClassNotFoundException, InterruptedException {

        // si déjà connecté, on se déconnecte d'abord
        if (this.connected)
            this.disconnect();

        // créer un Socket non connecté.
        this.connection = new Socket();
        
        // timeout mis en place pour attendre le message de type ServerConnected
        this.connection.setSoTimeout((int)SOCKET_OPERATION_TIMEOUT.toMillis());
        
        // on se connecte
        this.connection.connect(
            new InetSocketAddress(
                this.options.getHost(),
                this.options.getPort()
            ), 
            (int)SOCKET_OPERATION_TIMEOUT.toMillis()
        );
        
        this.output = new ObjectOutputStream(this.connection.getOutputStream());
        this.input = new ObjectInputStream(this.connection.getInputStream());

        this.output.writeObject(new ClientConnection(this.options.getUsername()));
        this.output.flush();

        final var connectedMessage = (ServerAccept)this.input.readObject();

        // plus besoin de timeout pour les opérations de lecture de messages.
        this.connection.setSoTimeout(0);

        this.messageReadsThread = new Thread(() -> {
            while(true){
                try {
                    final var message = (ServerIncomingMessage)this.input.readObject();
                    this.onMessage.accept(message);
                } catch (Exception e){
                    this.onError.accept(e);
                }
            }
        });

        this.messageReadsThread.start();

        this.connected = true;

        return Optional.ofNullable(connectedMessage.getBanner());
    }

    /**
     * Se déconnecter du serveur.
     * @throws IllegalStateException Si le client n'est pas connecté.
     */
    public void disconnect() throws IOException, InterruptedException {
        
        if (!this.connected)
            throw new IllegalStateException("client is not connected");

        this.messageReadsThread.join();
        this.input.close();
        this.output.close();
        this.connection.close();
        this.connected = false;
    }

    public boolean isConnected(){
        return this.connected;
    }

    public void sendMessage(String message) throws IOException {
        this.output.writeObject(new ClientIncomingMessage(message));
        this.output.flush();
    }
}
