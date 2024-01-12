package fr.sncf.d2d.micromessenger.client;

import java.io.IOException;
import java.time.Duration;
import java.util.List;
import java.util.Scanner;

import fr.sncf.d2d.micromessenger.common.MessageFormatter;

public class ClientOperations {

    private static final Duration RECONNECT_TIMEOUT = Duration.ofSeconds(5);
    
    public static void start(Client client) throws Exception {

        while(true){
            
            try {
                
                System.out.println(String.format(
                    "connecting to %s:%s with username \"%s\"", 
                    client.getOptions().getHost(), 
                    client.getOptions().getPort(),
                    client.getOptions().getUsername()
                ));

                client.onMessage(message -> {
                    System.out.println(MessageFormatter.format(
                        message.getSentAt(), 
                        message.getUsername(), 
                        message.getMessage()
                    ));
                });

                client.onError(error -> {
                    System.err.println(String.format(
                        "client error (%s): %s", 
                        error.getClass().getSimpleName(), 
                        error.getMessage()
                    ));
                    try {
                        client.disconnect();
                    } catch (Exception e) {
                        System.out.println(String.format(
                            "error while disconnecting (%s): %s",
                            error.getClass().getSimpleName(), 
                            error.getMessage()
                        ));
                    } 
                });

                final var banner = client.connect();

                System.out.println(String.format(
                    "you are now connected to %s:%d !", 
                    client.getOptions().getHost(), 
                    client.getOptions().getPort()
                ));

                banner.ifPresent(System.out::println);

                try (final var input = new Scanner(System.in)){
                    while (true){
                        client.sendMessage(input.nextLine());
                    }
                }

            } catch (IOException exception){
                System.err.println(String.format("I/O error: %s", exception.getMessage()));
                if (client.isConnected()){
                    System.err.println("client is still connected. disconnecting and reconnecting...");
                } else {
                    System.err.println("client is disconnected, reconnecting...");
                }
                Thread.sleep(RECONNECT_TIMEOUT.toMillis());
            }

        }
    }
}
