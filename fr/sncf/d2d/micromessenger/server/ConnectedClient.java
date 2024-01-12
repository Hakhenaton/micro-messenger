package fr.sncf.d2d.micromessenger.server;

import java.io.EOFException;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.time.Duration;
import java.util.function.Consumer;

import static fr.sncf.d2d.micromessenger.common.Callbacks.*;
import fr.sncf.d2d.micromessenger.protocol.ClientConnection;
import fr.sncf.d2d.micromessenger.protocol.ClientIncomingMessage;
import fr.sncf.d2d.micromessenger.protocol.ServerAccept;
import fr.sncf.d2d.micromessenger.protocol.ServerIncomingMessage;

/**
 * Objet représentant un client connecté
 */
public class ConnectedClient {

    private static final Duration CLIENT_OPERATION_TIMEOUT = Duration.ofSeconds(30);
    private String username;

    private final Socket connection;

    private Consumer<ClientIncomingMessage> onMessage = ignore();
    private Consumer<Exception> onError = ignore();
    private Runnable onDisconnect = noop();

    private Thread messagesReadThread;

    private final ObjectInputStream input;
    private final ObjectOutputStream output;

    /**
     * Crée une nouvelle instance de client connecté à partir d'une connexion
     * @param connection la connexion
     * @throws IOException si une erreur d'entrée/sortie survient
     */
    public ConnectedClient(Socket connection) throws IOException {
        this.connection = connection;
        this.output = new ObjectOutputStream(connection.getOutputStream());
        this.input = new ObjectInputStream(connection.getInputStream());
    }

    /**
     * Effectue les opérations d'acceptation du client sur le serveur.
     * - Réception du message de connexion
     * - Envoie du message d'acceptation
     * - Mise en place de la boucle de lecture des messages
     * @param banner le message de bienvenue à envoyer au client
     * @throws IOException si une erreur d'entrée/sortie survient
     * @throws ClassNotFoundException si une erreur de classe survient
     */
    public void accept(String banner) throws IOException, ClassNotFoundException {

        connection.setSoTimeout((int)CLIENT_OPERATION_TIMEOUT.toMillis());

        final var clientConnection = (ClientConnection)this.input.readObject();

        connection.setSoTimeout(0);

        this.username = clientConnection.getUsername();

        this.output.writeObject(new ServerAccept(banner));
        this.output.flush();

        this.messagesReadThread = new Thread(() -> {
            while (true){
                try {
                    final var message = (ClientIncomingMessage)this.input.readObject();
                    this.onMessage.accept(message);
                } catch(Exception ex){
                    if (ex instanceof EOFException && this.onDisconnect != null){
                        this.onDisconnect.run();
                    } else {
                        this.onError.accept(ex);
                    }
                    break;
                }
            }
        });

        this.messagesReadThread.start();
    }

    /**
     * Définit l'action à effectuer lorsqu'une erreur survient lors de la lecture d'un message
     * @param consumer La fonction de consommation de l'erreur.
     */
    public void onError(Consumer<Exception> consumer){
        this.onError = consumer;
    }

    /**
     * Définit l'action à effectuer lorsqu'un client s'est déconnecté
     * @param runnable l'action à effectuer
     */
    public void onDisconnect(Runnable runnable){
        this.onDisconnect = runnable;
    }

    /**
     * Définit l'action à effectuer lorsqu'un message est reçu
     * @param consumer La fonction de consommation du message
     */
    public void onMessage(Consumer<ClientIncomingMessage> consumer){
        this.onMessage = consumer;
    }
    
    /**
     * Envoie un message au client
     * @param message le message à envoyer
     * @throws IOException si une erreur d'entrée/sortie survient
     */
    public void sendMessage(ConnectedClientMessage message) throws IOException {
        this.output.writeObject(new ServerIncomingMessage(
            message.getMessage(), 
            message.getClient().username, 
            message.getSentAt()
        ));
        this.output.flush();
    }

    /**
     * @return le nom d'utilisateur du client
     */
    public String getUsername() {
        return username;
    }
}
