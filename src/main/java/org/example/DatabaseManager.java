package org.example; // Ricorda poi di usare un package come theknife.server[cite: 21]

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DatabaseManager {

    // URL di connessione: protocollo://host:porta/nome_database
    private static final String URL = "jdbc:postgresql://localhost:5432/the_knife";
    private static final String USER = "postgres"; // Il tuo utente di pgAdmin
    private static final String PASSWORD = "theknife"; // Inserisci la password che usi su pgAdmin

    public static Connection connect() {
        Connection conn = null;
        try {
            conn = DriverManager.getConnection(URL, USER, PASSWORD);
            System.out.println("Connessione al database the_knife stabilita con successo!");
        } catch (SQLException e) {
            System.out.println("Errore durante la connessione al database.");
            e.printStackTrace();
        }
        return conn;
    }

    public static void main(String[] args) {
        // Test rapido per verificare il collegamento
        connect();
    }
}
