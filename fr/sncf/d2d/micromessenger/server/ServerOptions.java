package fr.sncf.d2d.micromessenger.server;

import fr.sncf.d2d.micromessenger.common.Validators;

public class ServerOptions {

    private final String address;
    
    private final Integer port;

    private final String banner;

    private ServerOptions(String address, Integer port, String banner){
        this.address = address;
        this.port = port;
        this.banner = banner;
    }

    public String getAddress() {
        return this.address;
    }

    public Integer getPort() {
        return this.port;
    }

    public String getBanner() {
        return this.banner;
    }

    public static ServerOptionsBuilder builder(){
        return new ServerOptionsBuilder();
    }

    public static class ServerOptionsBuilder {

        private String address;

        private Integer port;

        private String banner;

        private ServerOptionsBuilder(){}

        public ServerOptionsBuilder address(String address){
            
            if (!Validators.isValidHost(address))
                throw new IllegalArgumentException("listen address is invalid");

            this.address = address;
            
            return this;
        }

        public ServerOptionsBuilder port(int port){

            if (!Validators.isValidPort(port))
                throw new IllegalArgumentException("listen port is invalid");
        
            this.port = port;

            return this;
        }

        public ServerOptionsBuilder banner(String banner){

            if (banner.isBlank())
                throw new IllegalArgumentException("server banner must not be empty");

            this.banner = banner;

            return this;
        }

        public ServerOptions build(){
            return new ServerOptions(this.address, this.port, this.banner);
        }
    }
}
