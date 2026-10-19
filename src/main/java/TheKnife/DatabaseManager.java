package TheKnife;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DatabaseManager {

    private static final String URL = "jdbc:postgresql://localhost:5432/the_knife";
    private static final String USER = "postgres";
    private static final String PASSWORD = "theknife";

    public static Connection connect() {
        Connection conn = null;
        try {
            conn = DriverManager.getConnection(URL, USER, PASSWORD);
            System.out.println("Connessione al database stabilita con successo");
        } catch (SQLException e) {
            System.out.println("Errore durante la connessione al database");
            e.printStackTrace();
        }
        return conn;
    }

    public static void main(String[] args) {
        connect();
    }
}
