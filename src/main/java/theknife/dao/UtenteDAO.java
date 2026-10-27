package theknife.dao;

import theknife.db.DatabaseManager;
import theknife.essenziali.Utente;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Types;

/**
 * Classe Data Access Object (DAO) per la gestione della persistenza dell'entitá <code>Utente</code>.
 * <p>
 * Fornisce i metodi per la registrazione, l'autenticazione con password cifrata in SHA-256
 * e la ricerca degli utenti all'interno della tabella <code>Utenti</code> del database.
 * @author Alessandro Frigerio, Antonio Pardo, Davide Moretti
 */
public class UtenteDAO {

    /**
     * Costruttore vuoto di default per la classe <code>UtenteDAO</code>.
     */
    public UtenteDAO() {
    }

    /**
     * Registra un nuovo utente nel database cifrando automaticamente la sua password.
     * <p>
     * Gestisce anche il caso in cui l'attributo opzionale <code>dataNascita</code> sia <code>null</code>.
     * @param u l'oggetto <code>Utente</code> contenente i dati da registrare
     * @return <code>true</code> se la registrazione va a buon fine, <code>false</code> se lo username esiste giá o si verifica un errore
     */
    public boolean registraUtente(Utente u) {
        if (esisteUsername(u.getUsername())) {
            System.err.println("Errore: lo username '" + u.getUsername() + "' è già in uso.");
            return false;
        }

        String query = "INSERT INTO Utenti (nome, cognome, username, password, data_nascita, luogo_domicilio, ruolo) VALUES (?, ?, ?, ?, ?, ?, ?)";

        try (Connection conn = DatabaseManager.connect();
             PreparedStatement pstmt = conn.prepareStatement(query)) {

            pstmt.setString(1, u.getNome());
            pstmt.setString(2, u.getCognome());
            pstmt.setString(3, u.getUsername());
            pstmt.setString(4, cifraPassword(u.getPassword()));

            if (u.getDataNascita() != null) {
                pstmt.setDate(5, u.getDataNascita());
            } else {
                pstmt.setNull(5, Types.DATE);
            }

            pstmt.setString(6, u.getLuogoDomicilio());
            pstmt.setString(7, u.getRuolo().toLowerCase());

            int righeInserite = pstmt.executeUpdate();
            return righeInserite > 0;

        } catch (SQLException e) {
            System.err.println("Errore durante la registrazione dell'utente:");
            e.printStackTrace();
            return false;
        }
    }

    /**
     * Effettua l'autenticazione di un utente verificando username e password.
     * <p>
     * La password viene cifrata in SHA-256 prima di essere confrontata
     * con l'hash memorizzato nella tabella <code>Utenti</code>.
     * @param username lo username inserito dall'utente
     * @param password la password inserita dall'utente
     * @return l'oggetto <code>Utente</code> corrispondente se le credenziali sono corrette, <code>null</code> altrimenti
     */
    public Utente login(String username, String password) {
        String query = "SELECT * FROM Utenti WHERE username = ? AND password = ?";

        try (Connection conn = DatabaseManager.connect();
             PreparedStatement pstmt = conn.prepareStatement(query)) {

            pstmt.setString(1, username);
            pstmt.setString(2, cifraPassword(password));

            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    return estraiUtente(rs);
                }
            }
        } catch (SQLException e) {
            System.err.println("Errore durante il login dell'utente:");
            e.printStackTrace();
        }
        return null;
    }

    /**
     * Verifica se uno specifico username é giá presente nel database.
     * @param username lo username da controllare
     * @return <code>true</code> se lo username é giá registrato, <code>false</code> altrimenti
     */
    public boolean esisteUsername(String username) {
        String query = "SELECT 1 FROM Utenti WHERE username = ?";

        try (Connection conn = DatabaseManager.connect();
             PreparedStatement pstmt = conn.prepareStatement(query)) {

            pstmt.setString(1, username);

            try (ResultSet rs = pstmt.executeQuery()) {
                return rs.next();
            }
        } catch (SQLException e) {
            System.err.println("Errore durante la verifica dello username:");
            e.printStackTrace();
            return false;
        }
    }

    /**
     * Recupera un utente dal database a partire dal suo identificativo univoco.
     * @param idUtente l'identificativo numerico dell'utente
     * @return l'oggetto <code>Utente</code> trovato, oppure <code>null</code> se non esiste
     */
    public Utente getUtentePerId(int idUtente) {
        String query = "SELECT * FROM Utenti WHERE id_utente = ?";

        try (Connection conn = DatabaseManager.connect();
             PreparedStatement pstmt = conn.prepareStatement(query)) {

            pstmt.setInt(1, idUtente);

            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    return estraiUtente(rs);
                }
            }
        } catch (SQLException e) {
            System.err.println("Errore durante il recupero dell'utente per ID:");
            e.printStackTrace();
        }
        return null;
    }

    /**
     * Cifra una stringa di testo utilizzando l'algoritmo di hashing SHA-256.
     * @param password la password da cifrare
     * @return la stringa esadecimale corrispondente all'hash SHA-256 della password
     */
    private String cifraPassword(String password) {
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] hashBytes = digest.digest(password.getBytes(StandardCharsets.UTF_8));
            StringBuilder hexString = new StringBuilder();
            for (byte b : hashBytes) {
                String hex = Integer.toHexString(0xff & b);
                if (hex.length() == 1) {
                    hexString.append('0');
                }
                hexString.append(hex);
            }
            return hexString.toString();
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException("Errore: algoritmo SHA-256 non disponibile", e);
        }
    }

    /**
     * Estrae i dati dalla riga corrente del <code>ResultSet</code> e costruisce un oggetto <code>Utente</code>.
     * @param rs il <code>ResultSet</code> posizionato sulla riga da leggere
     * @return l'oggetto <code>Utente</code> popolato con i dati letti dal database
     * @throws SQLException se si verifica un errore nella lettura delle colonne del <code>ResultSet</code>
     */
    private Utente estraiUtente(ResultSet rs) throws SQLException {
        return new Utente(
                rs.getInt("id_utente"),
                rs.getString("nome"),
                rs.getString("cognome"),
                rs.getString("username"),
                rs.getString("password"),
                rs.getDate("data_nascita"),
                rs.getString("luogo_domicilio"),
                rs.getString("ruolo")
        );
    }

    /**
     * Aggiorna i dati anagrafici, lo username e (opzionalmente) la password di un utente.
     * @param u l'oggetto <code>Utente</code> con i nuovi dati
     * @param cambiaPassword <code>true</code> se l'utente ha inserito una nuova password da cifrare, <code>false</code> per mantenere quella attuale
     * @return <code>true</code> se l'aggiornamento riesce, <code>false</code> altrimenti
     */
    public boolean aggiornaUtente(Utente u, boolean cambiaPassword) {
        String query = cambiaPassword
                ? "UPDATE Utenti SET nome = ?, cognome = ?, username = ?, password = ?, luogo_domicilio = ? WHERE id_utente = ?"
                : "UPDATE Utenti SET nome = ?, cognome = ?, username = ?, luogo_domicilio = ? WHERE id_utente = ?";

        try (Connection conn = DatabaseManager.connect();
             PreparedStatement pstmt = conn.prepareStatement(query)) {

            pstmt.setString(1, u.getNome());
            pstmt.setString(2, u.getCognome());
            pstmt.setString(3, u.getUsername());
            if (cambiaPassword) {
                pstmt.setString(4, cifraPassword(u.getPassword()));
                pstmt.setString(5, u.getLuogoDomicilio());
                pstmt.setInt(6, u.getIdUtente());
            } else {
                pstmt.setString(4, u.getLuogoDomicilio());
                pstmt.setInt(5, u.getIdUtente());
            }

            return pstmt.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("Errore durante l'aggiornamento del profilo utente:");
            e.printStackTrace();
            return false;
        }
    }
}
