package fr.sncf.d2d.micromessenger;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.ObjectOutputStream;
import java.util.List;
import java.util.Optional;
import java.util.Random;
import java.util.function.BiFunction;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Supplier;

import fr.sncf.d2d.micromessenger.cli.CommandLine;
import fr.sncf.d2d.micromessenger.client.Client;
import fr.sncf.d2d.micromessenger.client.ClientOperations;
import fr.sncf.d2d.micromessenger.protocol.ClientConnection;
import fr.sncf.d2d.micromessenger.server.Server;
import fr.sncf.d2d.micromessenger.server.ServerOperations;

public class MicroMessenger {

    public static void main(String[] args) {

        try {
        
            final var commandLine = new CommandLine(args);

            switch (commandLine.getExecutionMode()){
                case CLIENT:
                    final var client = new Client(commandLine.getClientOptions());
                    ClientOperations.start(client);
                    break;
                case SERVER:
                    final var server = new Server(commandLine.getServerOptions());
                    ServerOperations.start(server);
                    break;
                case HELP:
                    displayHelp();
                    break;
            }

        } catch (Exception ex){

            System.err.println(String.format("an unknown error occured: %s", ex.getMessage()));
            ex.printStackTrace();
            System.exit(1);
        }
    }

    private static void displayHelp(){
        System.out.println(CommandLine.USAGE);
    }
}