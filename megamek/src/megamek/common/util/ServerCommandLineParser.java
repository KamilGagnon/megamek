package megamek.common.util;

import megamek.MegaMek;
import megamek.server.Server;
import org.apache.logging.log4j.LogManager;

import java.util.Locale;
import java.util.ResourceBundle;
import java.util.logging.Logger;

public  class ServerCommandLineParser extends AbstractCommandLineParser {

    public enum ServerCommandLineFlag {
        //region Enum Declarations
        PORT("port","set which port the server listens to. Defautls to "+ Server.DEFAULT_PORT),
        PASSWORD("password","set which port the server listens to. Defautls to "+ Server.DEFAULT_PORT),
        ANNOUNCE("announce","set which port the server listens to. Defautls to "+ Server.DEFAULT_PORT),
        MAIL("mail","set which port the server listens to. Defautls to "+ Server.DEFAULT_PORT);
        //endregion Enum Declarations

        private final String name;
        private final String toolTipText;

        //region Constructors
        ServerCommandLineFlag(final String name, final String toolTipText) {
//            final ResourceBundle resources = ResourceBundle.getBundle("mekhq.resources.Finances",
//                    MegaMek.getMekHQOptions().getLocale(), new EncodeControl());
            this.name = name;
            this.toolTipText = toolTipText; //resources.getString(toolTipText);
        }
        //endregion Constructors
        //region File I/O
        /**
         * This allows for the legacy parsing method of financial durations, outdated in 0.49.X
         */
        public static ServerCommandLineFlag parseFromString(final String text) {
            try {
                return valueOf(text.toUpperCase(Locale.ROOT));
            } catch (Exception ex) {
                LogManager.getLogger().error("Failed to parse the ServerCommandLineFlag from text " + text);
                throw(ex);
            }
        }
        //endregion File I/O

        @Override
        public String toString() {
            return name;
        }
    }

    private String gameFilename;
    private int port;
    private String password;
    private String announceUrl = "";
    private String mailProperties;


    // Options
//    private static final String OPTION_PORT = "port";
//    private static final String OPTION_PASSWORD = "password";
//    private static final String OPTION_ANNOUNCE = "announce";
//    private static final String OPTION_MAIL = "mail";

    public ServerCommandLineParser(String[] args) {
        super(args);
    }

    /**
     * @return port option value or <code>-1</code> if it wasn't set
     */
    public int getPort() {
        return port;
    }

    /**
     * @return the password option value, will be null if not set.
     */
    public String getPassword() {
        return password;
    }

    public String getAnnounceUrl() {
        return announceUrl;
    }

    public boolean getRegister() {
        return (announceUrl != null) && (!announceUrl.isBlank());
    }

    public String getMailProperties() {
        return mailProperties;
    }

    /**
     * @return the game file name option value or <code>null</code> if it wasn't set
     */
    public String getGameFilename() {
        return gameFilename;
    }

    @Override
    protected void start() throws ParseException {
        while (hasNext()) {
            int tokType = getToken();
            switch (tokType) {
                case TOK_OPTION:
                    try {
                        switch ( ServerCommandLineFlag.parseFromString(getTokenValue())) {
                            case PORT:
                                nextToken();
                                parsePort();
                                break;
                            case ANNOUNCE:
                                nextToken();
                                parseAnnounce();
                                break;
                            case PASSWORD:
                                nextToken();
                                parsePassword();
                                break;
                            case MAIL:
                                nextToken();
                                parseMail();
                                break;
                        }
                    } catch (Exception ex) {
                        //ignore or fail?
                    }
                    break;
                case TOK_LITERAL:
                    gameFilename = getTokenValue();
                    nextToken();
                    break;
                case TOK_EOF:
                    // Do nothing, although this shouldn't happen
                    break;
                default:
                    throw new ParseException("unexpected input");
            }
            nextToken();
        }
//        while (hasNext()) {
//            int tokType = getToken();
//            switch (tokType) {
//                case TOK_OPTION:
//                    switch (getTokenValue()) {
//                        case OPTION_PORT:
//                            nextToken();
//                            parsePort();
//                            break;
//                        case OPTION_ANNOUNCE:
//                            nextToken();
//                            parseAnnounce();
//                            break;
//                        case OPTION_PASSWORD:
//                            nextToken();
//                            parsePassword();
//                            break;
//                        case OPTION_MAIL:
//                            nextToken();
//                            parseMail();
//                            break;
//                    }
//                    break;
//                case TOK_LITERAL:
//                    gameFilename = getTokenValue();
//                    nextToken();
//                    break;
//                case TOK_EOF:
//                    // Do nothing, although this shouldn't happen
//                    break;
//                default:
//                    throw new ParseException("unexpected input");
//            }
//            nextToken();
//        }
    }

    private void parsePort() throws ParseException {
        if (getToken() == TOK_LITERAL) {
            int newPort = -1;
            try {
                newPort = Integer.decode(getTokenValue());
            } catch (NumberFormatException ignored) {
                //ignore, leave at -1
            }
            if ((newPort < 0) || (newPort > 65535)) {
                throw new ParseException("invalid port number");
            }
            port = newPort;
        } else {
            throw new ParseException("port number expected");
        }
    }

    private void parseAnnounce() throws ParseException {
        if (getToken() == TOK_LITERAL) {
            announceUrl = getTokenValue();
        } else {
            throw new ParseException("meta server announce URL expected");
        }
    }

    private void parsePassword() throws ParseException {
        if (getToken() == TOK_LITERAL) {
            password = getTokenValue();
        } else {
            throw new ParseException("password expected");
        }
    }

    private void parseMail() throws ParseException {
        if (getToken() == TOK_LITERAL) {
            mailProperties = getTokenValue();
        } else {
            throw new ParseException("mail properties expected");
        }
    }
}