import java.io.DataOutputStream;
import java.io.IOException;
import java.net.*;

public class Logger {

    public static void sendParam(String param) {

        try {
            URL url = new URL("http://" + Main.IP_ADDR + "/JavaDiscordLogger.php");
            URLConnection conn = url.openConnection();
            conn.setDoOutput(true);
            conn.setUseCaches(false);

            conn.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");

            try (DataOutputStream out = new DataOutputStream(conn.getOutputStream())) {
                out.writeBytes(param);
                out.flush();
            }

            conn.getInputStream();

        } catch (MalformedURLException e) {
            System.out.println("The URL address is incorrect.");
            e.printStackTrace();
        } catch (IOException e) {
            System.out.println("It can't connect to the web page.");
            e.printStackTrace();
        }
    }
}