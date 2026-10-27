/*
 * Autori del progetto:
 * - Alessandro Frigerio, Matricola: 759926, Sede: CO
 * - Antonio Pardo, Matricola: 760613, Sede: CO
 * - Davide Moretti, Matricola: 762176, Sede: CO
 */
package theknife.db;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

/**
 * Classe che gestisce la connessione al database PostgreSQL tramite JDBC.
 * <p>
 * Non memorizza alcuna password di default nel codice per motivi di sicurezza,
 * richiedendo la configurazione dinamica all'avvio di <code>serverTK</code>.
 * @author Alessandro Frigerio
 * @author Antonio Pardo
 * @author Davide Moretti
 */
public class DatabaseManager {

    /**
     * URL JDBC per la connessione al database <code>url</code>.
     */
    private static String url = "jdbc:postgresql://localhost:5432/the_knife";

    /**
     * Nome utente per accedere a PostgreSQL <code>user</code>.
     */
    private static String user = "postgres";

    /**
     * Password per accedere a PostgreSQL impostata a stringa vuota di default <code>password</code>.
     */
    private static String password = "";

    /**
     * Costruttore vuoto della classe <code>DatabaseManager</code>.
     */
    public DatabaseManager() {
    }

    /**
     * Configura i parametri di connessione al database richiesti all'avvio di <code>serverTK</code>.
     * @param host indirizzo IP o nome host del database (es. <code>localhost</code>)
     * @param dbUser username per l'accesso al DBMS PostgreSQL
     * @param dbPassword password per l'accesso al DBMS PostgreSQL
     */
    public static void configuraDatabase(String host, String dbUser, String dbPassword) {
        if (host != null && !host.trim().isEmpty()) {
            url = "jdbc:postgresql://" + host.trim() + ":5432/the_knife";
        }
        if (dbUser != null && !dbUser.trim().isEmpty()) {
            user = dbUser.trim();
        }
        if (dbPassword != null) {
            password = dbPassword;
        }
    }

    /**
     * Apre e restituisce una nuova connessione al database PostgreSQL.
     * @return l'oggetto <code>Connection</code> attivo, oppure <code>null</code> se le credenziali o l'host sono errati
     */
    public static Connection connect() {
        try {
            return DriverManager.getConnection(url, user, password);
        } catch (SQLException e) {
            return null;
        }
    }
}