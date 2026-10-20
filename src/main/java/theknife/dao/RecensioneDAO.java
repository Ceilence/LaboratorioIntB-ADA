/*
 * Autori del progetto:
 * - Alessandro Frigerio, Matricola: [INSERISCI_MATRICOLA], Sede: CO
 * - [Nome Cognome Membro 2], Matricola: [MATRICOLA], Sede: CO
 */
package theknife.dao;

import theknife.db.DatabaseManager;
import theknife.essenziali.Recensione;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

/**
 * Classe Data Access Object (DAO) per la gestione della persistenza dell'entit&agrave; <code>Recensione</code>.
 * <p>
 * Implementa le operazioni per clienti e gestori sulla tabella <code>Recensioni</code>,
 * applicando i controlli di autorizzazione sul <code>ruolo</code> dell'utente e sulla propriet&agrave;
 * del ristorante direttamente a livello di query SQL.
 * @author Alessandro Frigerio
 */
public class RecensioneDAO {

    /**
     * Costruttore vuoto di default per la classe <code>RecensioneDAO</code>.
     */
    public RecensioneDAO() {
    }

    /**
     * Inserisce una nuova recensione verificando che l'autore abbia il ruolo di <code>cliente</code>.
     * <p>
     * Corrisponde alla funzionalit&agrave; di specifica <code>aggiungiRecensione()</code>.
     * @param r l'oggetto <code>Recensione</code> contenente id utente, id ristorante, stelle e testo
     * @return <code>true</code> se l'utente &egrave; un cliente e l'inserimento riesce, <code>false</code> altrimenti
     */
    public boolean aggiungiRecensione(Recensione r) {
        String query = "INSERT INTO Recensioni (id_utente, id_ristorante, stelle, testo, data_recensione) " +
                "SELECT ?, ?, ?, ?, CURRENT_DATE FROM Utenti " +
                "WHERE id_utente = ? AND LOWER(ruolo) = 'cliente'";

        try (Connection conn = DatabaseManager.connect();
             PreparedStatement pstmt = conn.prepareStatement(query)) {

            pstmt.setInt(1, r.getIdUtente());
            pstmt.setInt(2, r.getIdRistorante());
            pstmt.setInt(3, r.getStelle());
            pstmt.setString(4, r.getTesto());
            pstmt.setInt(5, r.getIdUtente());

            int righeInserite = pstmt.executeUpdate();
            if (righeInserite == 0) {
                System.err.println("Accesso negato: solo un utente con ruolo 'cliente' può inserire recensioni.");
            }
            return righeInserite > 0;

        } catch (SQLException e) {
            System.err.println("Errore durante l'aggiunta della recensione (o recensione già presente):");
            e.printStackTrace();
            return false;
        }
    }

    /**
     * Modifica una recensione verificando che l'utente sia l'autore originale e abbia il ruolo di <code>cliente</code>.
     * <p>
     * Corrisponde alla funzionalit&agrave; di specifica <code>modificaRecensione()</code>.
     * @param idRecensione l'identificativo univoco della recensione da modificare
     * @param idCliente l'identificativo del cliente che richiede la modifica
     * @param nuoveStelle la nuova valutazione in stelle (da 1 a 5)
     * @param nuovoTesto il nuovo testo della recensione
     * @return <code>true</code> se la modifica viene applicata, <code>false</code> se l'utente non &egrave; autorizzato o in caso di errore
     */
    public boolean modificaRecensione(int idRecensione, int idCliente, int nuoveStelle, String nuovoTesto) {
        String query = "UPDATE Recensioni rec SET stelle = ?, testo = ?, data_recensione = CURRENT_DATE " +
                "FROM Utenti u " +
                "WHERE rec.id_recensione = ? AND rec.id_utente = ? " +
                "AND u.id_utente = rec.id_utente AND LOWER(u.ruolo) = 'cliente'";

        try (Connection conn = DatabaseManager.connect();
             PreparedStatement pstmt = conn.prepareStatement(query)) {

            pstmt.setInt(1, nuoveStelle);
            pstmt.setString(2, nuovoTesto);
            pstmt.setInt(3, idRecensione);
            pstmt.setInt(4, idCliente);

            int righeAggiornate = pstmt.executeUpdate();
            return righeAggiornate > 0;

        } catch (SQLException e) {
            System.err.println("Errore durante la modifica della recensione:");
            e.printStackTrace();
            return false;
        }
    }

    /**
     * Elimina una recensione verificando che l'utente sia l'autore originale e abbia il ruolo di <code>cliente</code>.
     * <p>
     * Corrisponde alla funzionalit&agrave; di specifica <code>eliminaRecensione()</code>.
     * @param idRecensione l'identificativo univoco della recensione da eliminare
     * @param idCliente l'identificativo del cliente autore della recensione
     * @return <code>true</code> se la cancellazione va a buon fine, <code>false</code> se non autorizzato o in caso di errore
     */
    public boolean eliminaRecensione(int idRecensione, int idCliente) {
        String query = "DELETE FROM Recensioni rec USING Utenti u " +
                "WHERE rec.id_recensione = ? AND rec.id_utente = ? " +
                "AND u.id_utente = rec.id_utente AND LOWER(u.ruolo) = 'cliente'";

        try (Connection conn = DatabaseManager.connect();
             PreparedStatement pstmt = conn.prepareStatement(query)) {

            pstmt.setInt(1, idRecensione);
            pstmt.setInt(2, idCliente);

            int righeEliminate = pstmt.executeUpdate();
            return righeEliminate > 0;

        } catch (SQLException e) {
            System.err.println("Errore durante l'eliminazione della recensione:");
            e.printStackTrace();
            return false;
        }
    }

    /**
     * Registra la risposta del gestore a una recensione, verificando ruolo e propriet&agrave; del ristorante.
     * <p>
     * Corrisponde alla funzionalit&agrave; di specifica <code>rispostaRecensioni()</code>.
     * Controlla che l'utente sia un <code>ristoratore</code> (o <code>gestore</code>), che sia il proprietario
     * del ristorante recensito (<code>id_gestore</code>) e che non esista gi&agrave; una risposta.
     * @param idRecensione l'identificativo della recensione a cui rispondere
     * @param idRistoratore l'identificativo del ristoratore che sta rispondendo
     * @param risposta il testo della risposta formulata dal ristoratore
     * @return <code>true</code> se la risposta viene salvata, <code>false</code> se non autorizzato o se gi&agrave; presente
     */
    public boolean rispostaRecensioni(int idRecensione, int idRistoratore, String risposta) {
        if (risposta == null || risposta.trim().isEmpty()) {
            return false;
        }

        String query = "UPDATE Recensioni rec SET risposta_ristoratore = ? " +
                "FROM RistorantiTheKnife r, Utenti u " +
                "WHERE rec.id_recensione = ? " +
                "AND rec.id_ristorante = r.id_ristorante " +
                "AND r.id_gestore = ? " +
                "AND u.id_utente = ? " +
                "AND LOWER(u.ruolo) IN ('ristoratore') " +
                "AND rec.risposta_ristoratore IS NULL";

        try (Connection conn = DatabaseManager.connect();
             PreparedStatement pstmt = conn.prepareStatement(query)) {

            pstmt.setString(1, risposta.trim());
            pstmt.setInt(2, idRecensione);
            pstmt.setInt(3, idRistoratore);
            pstmt.setInt(4, idRistoratore);

            int righeAggiornate = pstmt.executeUpdate();
            if (righeAggiornate == 0) {
                System.err.println("Impossibile rispondere: verifica di essere il gestore di questo ristorante e di non aver già risposto.");
            }
            return righeAggiornate > 0;

        } catch (SQLException e) {
            System.err.println("Errore durante l'inserimento della risposta del ristoratore:");
            e.printStackTrace();
            return false;
        }
    }

    /**
     * Recupera tutte le recensioni associate a uno specifico ristorante, ordinate dalla pi&ugrave; recente.
     * <p>
     * Corrisponde alla funzionalit&agrave; di specifica <code>visualizzaRecensioni()</code> (accessibile a tutti).
     * @param idRistorante l'identificativo univoco del ristorante di cui leggere le recensioni
     * @return una lista di oggetti <code>Recensione</code> relativi al ristorante specificato
     */
    public List<Recensione> visualizzaRecensioni(int idRistorante) {
        List<Recensione> recensioni = new ArrayList<>();
        String query = "SELECT * FROM Recensioni WHERE id_ristorante = ? ORDER BY data_recensione DESC, id_recensione DESC";

        try (Connection conn = DatabaseManager.connect();
             PreparedStatement pstmt = conn.prepareStatement(query)) {

            pstmt.setInt(1, idRistorante);

            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    recensioni.add(estraiRecensione(rs));
                }
            }
        } catch (SQLException e) {
            System.err.println("Errore durante il recupero delle recensioni del ristorante:");
            e.printStackTrace();
        }
        return recensioni;
    }

    /**
     * Recupera tutte le recensioni scritte da uno specifico cliente.
     * @param idCliente l'identificativo univoco del cliente
     * @return una lista di oggetti <code>Recensione</code> pubblicati dal cliente
     */
    public List<Recensione> visualizzaRecensioniPerCliente(int idCliente) {
        List<Recensione> recensioni = new ArrayList<>();
        String query = "SELECT rec.* FROM Recensioni rec JOIN Utenti u ON rec.id_utente = u.id_utente " +
                "WHERE rec.id_utente = ? AND LOWER(u.ruolo) = 'cliente' ORDER BY rec.data_recensione DESC";

        try (Connection conn = DatabaseManager.connect();
             PreparedStatement pstmt = conn.prepareStatement(query)) {

            pstmt.setInt(1, idCliente);

            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    recensioni.add(estraiRecensione(rs));
                }
            }
        } catch (SQLException e) {
            System.err.println("Errore durante il recupero delle recensioni del cliente:");
            e.printStackTrace();
        }
        return recensioni;
    }

    /**
     * Calcola il riepilogo statistico delle recensioni di un ristorante (numero totale e media stelle).
     * <p>
     * Corrisponde alla funzionalit&agrave; di specifica <code>visualizzaRiepilogo()</code>.
     * @param idRistorante l'identificativo del ristorante di cui calcolare il riepilogo
     * @return un array di <code>double</code> di dimensione 2: in <code>[0]</code> il numero recensioni, in <code>[1]</code> la media stelle
     */
    public double[] visualizzaRiepilogo(int idRistorante) {
        double[] riepilogo = new double[]{0.0, 0.0};
        String query = "SELECT COUNT(*) AS totale, COALESCE(AVG(stelle), 0.0) AS media FROM Recensioni WHERE id_ristorante = ?";

        try (Connection conn = DatabaseManager.connect();
             PreparedStatement pstmt = conn.prepareStatement(query)) {

            pstmt.setInt(1, idRistorante);

            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    riepilogo[0] = rs.getInt("totale");
                    riepilogo[1] = Math.round(rs.getDouble("media") * 10.0) / 10.0;
                }
            }
        } catch (SQLException e) {
            System.err.println("Errore durante il calcolo del riepilogo recensioni:");
            e.printStackTrace();
        }
        return riepilogo;
    }

    /**
     * Estrae i dati dalla riga corrente del <code>ResultSet</code> e costruisce un oggetto <code>Recensione</code>.
     * @param rs il <code>ResultSet</code> posizionato sulla riga da leggere
     * @return l'oggetto <code>Recensione</code> popolato con i dati letti dal database
     * @throws SQLException se si verifica un errore nella lettura delle colonne del <code>ResultSet</code>
     */
    private Recensione estraiRecensione(ResultSet rs) throws SQLException {
        return new Recensione(
                rs.getInt("id_recensione"),
                rs.getInt("id_utente"),
                rs.getInt("id_ristorante"),
                rs.getInt("stelle"),
                rs.getString("testo"),
                rs.getDate("data_recensione"),
                rs.getString("risposta_ristoratore")
        );
    }
}