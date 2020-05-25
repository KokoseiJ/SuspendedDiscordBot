import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.TextChannel;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import javax.annotation.Nullable;
import java.sql.*;

public class SQL {

    public static Connection initSQLConnection(String database) {
        Connection connection = null;

        String server = Main.IP_ADDR;
        String user_name = "MySQLuser";
        String password = "hurrhnn0516!";

        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
        } catch (ClassNotFoundException e) {
            System.err.println("<JDBC 오류> Driver load 오류: " + e.getMessage());
        }
        // 2.연결
        try {
            connection = DriverManager.getConnection("jdbc:mysql://" + server + "/" + database + "?autoReconnect=true", user_name, password);
        } catch (SQLException e) {
            System.err.println("SQL 연결 오류! 프로그램을 종료합니다!");
            System.exit(0);
        }
        return connection;
    }

    public static String[] getSQLData(Connection connection, String table, String column, @Nullable MessageReceivedEvent event) {

        try {
            String query = "select * from " + table + ";";
            Statement statement = connection.createStatement();
            ResultSet result = statement.executeQuery(query);

            StringBuilder resultSum = new StringBuilder();
            while (result.next()) resultSum.append(result.getString(column)).append("\u200B");
            return resultSum.toString().split("\u200B");

        } catch (Exception e) {
            if(event != null) errSQLConnection(e.getMessage(), event);
            else System.err.println(e.getMessage());
        }
        return null;
    }

    public static void insertSQLData(Connection connection, String table, String[] datas, MessageReceivedEvent event) {

        try {
            Statement statement = connection.createStatement();
            StringBuilder query = new StringBuilder("insert into " + table + " values(");

            for (String data : datas) {
                query.append("'").append(data).append("', ");
            }
            query.deleteCharAt(query.length() - 1).deleteCharAt(query.length() - 1).append(")");
            statement.executeUpdate(query.toString());

        } catch (Exception e) {
            errSQLConnection(e.getMessage(), event);
        }
    }

    public static void dropSQLData(Connection connection, String table, String column, String value, MessageReceivedEvent event) {

        try {
            Statement statement = connection.createStatement();
            statement.executeUpdate("delete from " + table + " where " + column + "='" + value + "';");
        } catch (Exception e) {
            errSQLConnection(e.getMessage(), event);
        }
    }

    public static void errSQLConnection(String errMessage, MessageReceivedEvent event) {
        TextChannel textChannel = event.getTextChannel();
        User user = event.getAuthor();

        EmbedBuilder embedBuilder = new EmbedBuilder();
        embedBuilder.setTitle("DB서버와 통신 중 오류가 발생했습니다!");
        embedBuilder.setDescription("```Java\n" + errMessage + "\n```");
        embedBuilder.setFooter("요청자 : " + user.getAsTag(), user.getAvatarUrl());

        textChannel.sendMessage(embedBuilder.build()).queue();
    }

    public static void finSQLConnection(Connection connection)
    {
        try {
            if(connection != null)
                connection.close();
        } catch (SQLException ignored) {}
    }
}
