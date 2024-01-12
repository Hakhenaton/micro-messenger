package fr.sncf.d2d.micromessenger.cli;

import java.util.Optional;

public enum Option {

    CONNECT( "connect", 'c'),
    LISTEN("listen", 'l'),
    HELP("help"),
    
    HOST("host", 'h'),
    PORT("port", 'p'),
    ADDR("addr", 'a'),
    
    USERNAME("username", 'u'),
    BANNER("banner", 'b');

    private static final String LONG_PREFIX = "--";
    private static final String SHORT_PREFIX = "-";

    private final String name;

    private final Character shortName;

    private Option(String name){
        this(name, null);
    }

    private Option(String name, Character shortName){
        this.name = name;
        this.shortName = shortName;
    }

    public boolean matches(String argument){
        return (LONG_PREFIX + this.name).equals(argument)
            || Optional.ofNullable(this.shortName)
                .map(shortName -> (SHORT_PREFIX + shortName).equals(argument))
                .orElse(false);
    }
}
