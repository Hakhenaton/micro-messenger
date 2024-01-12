package fr.sncf.d2d.micromessenger.client;

import java.util.Optional;

import javax.xml.validation.Validator;

import fr.sncf.d2d.micromessenger.common.Validators;

public class ClientOptions {

    private static final int DEFAULT_PORT = 19337;
    private static final String DEFAULT_USERNAME = "anonymous";

    private final String host;

    private final int port;

    private final String username;

    private ClientOptions(String host, int port, String username){
        this.host = host;
        this.port = Optional.ofNullable(port)
            .orElse(DEFAULT_PORT);
        this.username = Optional.ofNullable(username)
            .orElse(DEFAULT_USERNAME);
    }

    public static ClientOptionsBuilder builder(){
        return new ClientOptionsBuilder();
    }

    public String getHost() {
        return this.host;
    }

    public int getPort() {
        return this.port;
    }

    public String getUsername() {
        return this.username;
    }

    public static class ClientOptionsBuilder {

        private String host;

        private int port;

        private String username;

        private ClientOptionsBuilder(){}

        public void host(String host) {

            if (!Validators.isValidHost(host))
                throw new IllegalArgumentException("host cannot be empty");

            this.host = host;
        }

        public void port(int port) {

            if (!Validators.isValidPort(port))
                throw new IllegalArgumentException("port must be between 1 and 65535");

            this.port = port;
        }

        public void username(String username) {

            if (username.trim().length() == 0)
                throw new IllegalArgumentException("username cannot be empty");

            this.username = username;
        }

        public ClientOptions build(){
            return new ClientOptions(
                this.host, 
                this.port, 
                this.username
            );
        }
    }
}
