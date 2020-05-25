import com.sun.net.httpserver.HttpServer;
import net.dv8tion.jda.api.AccountType;
import net.dv8tion.jda.api.JDABuilder;
import net.dv8tion.jda.api.OnlineStatus;
import net.dv8tion.jda.api.entities.Activity;

import javax.security.auth.login.LoginException;
import java.net.InetSocketAddress;
import java.sql.Connection;
import java.util.Random;
import java.util.concurrent.Executors;

public class Main {

    static final Connection con = SQL.initSQLConnection("discordjavabot");
    static final String IP_ADDR ="hurrhnn.xyz";
    static final int HTTP_PORT = initInternalHTTPServer();
    static String[] Blocklists = PortScannerBlocklistsSettings.read();

    public static void main(String[] args) throws LoginException {

        JDABuilder builder = new JDABuilder(AccountType.BOT);
        builder.setToken(SQL.getSQLData(con, "info", "token", null)[0]);
        builder.setStatus(OnlineStatus.ONLINE);
        builder.setAutoReconnect(true);
        builder.setActivity(Activity.watching("鬼滅の刃"));
        builder.addEventListeners(new TextListener());
        builder.build();
    }

    private static int initInternalHTTPServer() {
        Random rand = new Random();
        int HTTP_PORT = (rand.nextInt(4) + 1) * 10000 + (rand.nextInt(5000) + 1);

        try {
            InetSocketAddress inetSocketAddress = new InetSocketAddress(HTTP_PORT);
            //System.out.println("HTTP PORT: " + HTTP_PORT);
            HttpServer server = HttpServer.create(inetSocketAddress, 0);
            server.createContext("/", new HTTPHandler());
            server.setExecutor(Executors.newCachedThreadPool());
            server.start();
        }catch (Exception e) { System.out.println("HTTP ERROR: " + e.getMessage()); }
        return HTTP_PORT;
    }
}
