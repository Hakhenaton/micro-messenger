package fr.sncf.d2d.micromessenger.cli;

import java.util.Arrays;
import java.util.Optional;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Supplier;
import java.util.stream.IntStream;

import fr.sncf.d2d.micromessenger.client.ClientOptions;
import fr.sncf.d2d.micromessenger.common.ExecutionMode;
import fr.sncf.d2d.micromessenger.server.ServerOptions;

public class CommandLine {

    public static final String USAGE = "Usage:\n" +
        "Client mode: micro-messenger -c -h <host> [-p <port>] [-u <username>]\n" +
        "Server mode: micro-messenger -l -a <addr> -p <port> -b <banner>\n";

    private final Function<Option, Boolean> getBoolean;

    private final Function<Option, Optional<String>> getString;

    public CommandLine(String[] args){

        this.getBoolean = option -> Arrays.stream(args)
            .filter(option::matches)
            .findFirst()
            .isPresent();

        this.getString = option -> IntStream.range(0, args.length - 1)
            .filter(i -> option.matches(args[i]))
            .mapToObj(i -> args[i + 1])
            .findFirst();
    }

    public ExecutionMode getExecutionMode(){
        
        if (getBoolean.apply(Option.CONNECT))
            return ExecutionMode.CLIENT;

        if (getBoolean.apply(Option.LISTEN))
            return ExecutionMode.SERVER;

        if (getBoolean.apply(Option.HELP))
            return ExecutionMode.HELP;

        throw new IllegalStateException("execution mode must be provided");
    }

    public ClientOptions getClientOptions(){

        final var clientOptions = ClientOptions.builder();

        this.getString.apply(Option.HOST)
            .ifPresent(clientOptions::host);

        this.getString.apply(Option.PORT)
            .map(Integer::valueOf)
            .ifPresent(clientOptions::port);

        this.getString.apply(Option.USERNAME)
            .ifPresent(clientOptions::username);

        return clientOptions.build();
    }

    public ServerOptions getServerOptions(){
        
        final var serverOptions = ServerOptions.builder();

        this.getString.apply(Option.ADDR)
            .ifPresent(serverOptions::address);

        this.getString.apply(Option.PORT)
            .map(Integer::valueOf)
            .ifPresent(serverOptions::port);

        this.getString.apply(Option.BANNER)
            .ifPresent(serverOptions::banner);

        return serverOptions.build();
    }
}
