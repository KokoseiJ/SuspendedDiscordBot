import net.dv8tion.jda.api.events.message.MessageReceivedEvent;

import java.sql.ResultSet;
import java.sql.Statement;
import java.util.Arrays;

public class PortScannerBlocklistsSettings {

    public static void add(String args, MessageReceivedEvent event) {
        SQL.insertSQLData(Main.con, "nmap_blocklist", (args + " ").split(" "), event);
    }

    public static boolean remove(String args, MessageReceivedEvent event) {
        Main.Blocklists = PortScannerBlocklistsSettings.read();
        SQL.getSQLData(Main.con,"nmap_blocklist", "hosts", event);

        boolean isBlocklistChanged = false;
        for(String str : Main.Blocklists)
        {
            if(str.equals(args))
            {
                SQL.dropSQLData(Main.con, "nmap_blocklist", "hosts", args, event);
                isBlocklistChanged = true;
            }
        }
        return isBlocklistChanged;
    }

    public static String[] read() {

        try {
            StringBuilder str = new StringBuilder();

            String Query = "select * from nmap_blocklist;";
            Statement statement = Main.con.createStatement();
            ResultSet result = statement.executeQuery(Query);

            while (result.next())
            {
                str.append(result.getString("hosts")).append(" ");
            }
            str.deleteCharAt(str.toString().length() - 1);
            String[] Blocklists = str.toString().split(" ");
            Arrays.sort(Blocklists);
            return Blocklists;

        }catch (Exception e)
        {
            System.err.println(e.getMessage());
            return null;
        }
    }
}
