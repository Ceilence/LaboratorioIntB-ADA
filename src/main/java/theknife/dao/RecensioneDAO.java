/*
 * Autori del progetto:
 * - Alessandro Frigerio, Matricola: 759926, Sede: CO
 * - Antonio Pardo, Matricola: 760613, Sede: CO
 * - Davide Moretti, Matricola: 762176, Sede: CO
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
 * Classe DAO per gestire le query sul database relative alle recensioni.
 * <p>
 * Permette ai clienti di aggiungere, modificare ed eliminare recensioni e ai ristoratori
 * di rispondere alle recensioni dei propri locali.
 * @author Alessandro Frigerio
 * @author Antonio Pardo
 * @author Davide Moretti
 */
public class RecensioneDAO {

    /**
     * Costruttore vuoto della classe <code>RecensioneDAO</code>.
     */
    public RecensioneDAO() {
    }

    /**
     * Salva una nuova recensione nel database, controllando che l'autore sia un cliente.
     * @param r l'oggetto <code>Recensione</code> da inserire
     * @return <code>true</code> se l'inserimento va a buon fine, <code>false</code> altrimenti
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
                System.err.println("Operazione negata: solo un 'cliente' può scrivere recensioni.");
            }
            return righeInserite > 0;

        } catch (SQLException e) {
            System.err.println("Errore durante l'aggiunta della recensione (o recensione già presente):");
            e.printStackTrace();
            return false;
        }
    }

    /**
     * Modifica le stelle e il testo di una recensione, verificando che l'utente sia il cliente che l'ha scritta.
     * @param idRecensione l'ID della recensione da modificare
     * @param idCliente l'ID del cliente autore della recensione
     * @param nuoveStelle il nuovo voto in stelle (da 1 a 5)
     * @param nuovoTesto il nuovo testo del commento
     * @return <code>true</code> se la modifica riesce, <code>false</code> altrimenti
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

            return pstmt.executeUpdate() > 0;

        } catch (SQLException e) {
            System.err.println("Errore durante la modifica della recensione:");
            e.printStackTrace();
            return false;
        }
    }

    /**
     * Elimina una recensione dal database, verificando che l'utente sia il cliente che l'ha scritta.
     * @param idRecensione l'ID della recensione da eliminare
     * @param idCliente l'ID del cliente autore della recensione
     * @return <code>true</code> se la recensione viene eliminata, <code>false</code> altrimenti
     */
    public boolean eliminaRecensione(int idRecensione, int idCliente) {
        String query = "DELETE FROM Recensioni rec USING Utenti u " +
                "WHERE rec.id_recensione = ? AND rec.id_utente = ? " +
                "AND u.id_utente = rec.id_utente AND LOWER(u.ruolo) = 'cliente'";

        try (Connection conn = DatabaseManager.connect();
             PreparedStatement pstmt = conn.prepareStatement(query)) {

            pstmt.setInt(1, idRecensione);
            pstmt.setInt(2, idCliente);

            return pstmt.executeUpdate() > 0;

        } catch (SQLException e) {
            System.err.println("Errore durante l'eliminazione della recensione:");
            e.printStackTrace();
            return false;
        }
    }

    /**
     * Permette al ristoratore di rispondere a una recensione di un suo ristorante (massimo una risposta).
     * <p>
     * Controlla che l'utente abbia il ruolo <code>ristoratore</code>, che sia il proprietario del
     * ristorante (tramite <code>id_ristoratore</code>) e che non abbia già risposto.
     * @param idRecensione l'ID della recensione a cui rispondere
     * @param idRistoratore l'ID del ristoratore che sta scrivendo la risposta
     * @param risposta il testo della risposta
     * @return <code>true</code> se la risposta viene salvata, <code>false</code> altrimenti
     */
    public boolean rispostaRecensioni(int idRecensione, int idRistoratore, String risposta) {
        if (risposta == null || risposta.trim().isEmpty()) {
            return false;
        }

        String query = "UPDATE Recensioni rec SET risposta_ristoratore = ? " +
                "FROM RistorantiTheKnife r, Utenti u " +
                "WHERE rec.id_recensione = ? " +
                "AND rec.id_ristorante = r.id_ristorante " +
                "AND r.id_ristoratore = ? " +
                "AND u.id_utente = ? " +
                "AND LOWER(u.ruolo) = 'ristoratore' " +
                "AND rec.risposta_ristoratore IS NULL";

        try (Connection conn = DatabaseManager.connect();
             PreparedStatement pstmt = conn.prepareStatement(query)) {

            pstmt.setString(1, risposta.trim());
            pstmt.setInt(2, idRecensione);
            pstmt.setInt(3, idRistoratore);
            pstmt.setInt(4, idRistoratore);

            int righeAggiornate = pstmt.executeUpdate();
            if (righeAggiornate == 0) {
                System.err.println("Impossibile rispondere: controlla di essere il proprietario del ristorante e di non aver già risposto.");
            }
            return righeAggiornate > 0;

        } catch (SQLException e) {
            System.err.println("Errore durante il salvataggio della risposta del ristoratore:");
            e.printStackTrace();
            return false;
        }
    }

    /**
     * Restituisce tutte le recensioni di un ristorante, ordinate dalla più recente.
     * @param idRistorante l'ID del ristorante
     * @return lista di oggetti <code>Recensione</code> del ristorante
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
            System.err.println("Errore nel recupero delle recensioni del ristorante:");
            e.printStackTrace();
        }
        return recensioni;
    }

    /**
     * Restituisce tutte le recensioni scritte da un singolo cliente.
     * @param idCliente l'ID del cliente
     * @return lista di oggetti <code>Recensione</code> scritti dal cliente
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
            System.err.println("Errore nel recupero delle recensioni del cliente:");
            e.printStackTrace();
        }
        return recensioni;
    }

    /**
     * Calcola il numero totale di recensioni e la media delle stelle di un ristorante.
     * @param idRistorante l'ID del ristorante
     * @return array di 2 elementi: in posizione <code>[0]</code> il numero di recensioni, in <code>[1]</code> la media stelle
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
            System.err.println("Errore nel calcolo del riepilogo recensioni:");
            e.printStackTrace();
        }
        return riepilogo;
    }

    /**
     * Legge la riga corrente del <code>ResultSet</code> e crea un oggetto <code>Recensione</code>.
     * @param rs il <code>ResultSet</code> della query
     * @return l'oggetto <code>Recensione</code> creato con i dati letti
     * @throws SQLException se c'è un errore nella lettura delle colonne
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