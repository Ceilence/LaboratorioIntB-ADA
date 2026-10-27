/*
 * Autori del progetto:
 * - Alessandro Frigerio, Matricola: 759926, Sede: CO
 * - Antonio Pardo, Matricola: 760613, Sede: CO
 * - Davide Moretti, Matricola: 762176, Sede: CO
 */
package theknife.dao;

import theknife.db.DatabaseManager;
import theknife.essenziali.Ristorante;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

/**
 * Classe DAO per gestire le query sul database relative ai ristoranti e ai preferiti.
 * <p>
 * Si occupa di inserire, cercare e filtrare i ristoranti nella tabella <code>RistorantiTheKnife</code>
 * e di gestire la tabella <code>Preferiti</code> per i clienti.
 * @author Alessandro Frigerio
 * @author Antonio Pardo
 * @author Davide Moretti
 */
public class RistoranteDAO {

    /**
     * Costruttore vuoto della classe <code>RistoranteDAO</code>.
     */
    public RistoranteDAO() {
    }

    /**
     * Aggiunge un nuovo ristorante nel database, controllando che l'utente sia un ristoratore.
     * @param r il nuovo <code>Ristorante</code> da inserire
     * @param idRistoratore l'ID dell'utente ristoratore che sta creando il locale
     * @return <code>true</code> se l'inserimento riesce, <code>false</code> se l'utente non è un ristoratore o c'è un errore
     */
    public boolean aggiungiRistorante(Ristorante r, int idRistoratore) {
        String query = "INSERT INTO RistorantiTheKnife (nome, nazione, citta, indirizzo, latitudine, longitudine, fascia_prezzo, delivery, prenotazione_online, tipo_cucina, id_ristoratore) " +
                "SELECT ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ? FROM Utenti " +
                "WHERE id_utente = ? AND LOWER(ruolo) = 'ristoratore'";

        try (Connection conn = DatabaseManager.connect();
             PreparedStatement pstmt = conn.prepareStatement(query)) {

            pstmt.setString(1, r.getNome());
            pstmt.setString(2, r.getNazione());
            pstmt.setString(3, r.getCitta());
            pstmt.setString(4, r.getIndirizzo());
            pstmt.setDouble(5, r.getLatitudine());
            pstmt.setDouble(6, r.getLongitudine());
            pstmt.setInt(7, r.getFasciaPrezzo());
            pstmt.setBoolean(8, r.isDelivery());
            pstmt.setBoolean(9, r.isPrenotazioneOnline());
            pstmt.setString(10, r.getTipoCucina());
            pstmt.setInt(11, idRistoratore);
            pstmt.setInt(12, idRistoratore);

            int righeInserite = pstmt.executeUpdate();
            if (righeInserite == 0) {
                System.err.println("Operazione negata: solo un utente con ruolo 'ristoratore' può aggiungere un ristorante.");
            }
            return righeInserite > 0;

        } catch (SQLException e) {
            System.err.println("Errore durante l'inserimento del ristorante:");
            e.printStackTrace();
            return false;
        }
    }

    /**
     * Cerca un ristorante nel database tramite il suo ID.
     * @param idRistorante l'ID del ristorante da cercare
     * @return l'oggetto <code>Ristorante</code> trovato, oppure <code>null</code> se non esiste
     */
    public Ristorante visualizzaRistorante(int idRistorante) {
        String query = "SELECT * FROM RistorantiTheKnife WHERE id_ristorante = ?";

        try (Connection conn = DatabaseManager.connect();
             PreparedStatement pstmt = conn.prepareStatement(query)) {

            pstmt.setInt(1, idRistorante);

            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    return estraiRistorante(rs);
                }
            }
        } catch (SQLException e) {
            System.err.println("Errore durante la ricerca del ristorante per ID:");
            e.printStackTrace();
        }
        return null;
    }

    /**
     * Cerca i ristoranti combinando il testo della barra di ricerca e i vari filtri.
     * <p>
     * Se un filtro viene passato come <code>null</code>, viene semplicemente ignorato nella query.
     * @param nomeRistorante nome o parola chiave cercata nella barra di ricerca (può essere <code>null</code>)
     * @param locazione città o nazione del ristorante (può essere <code>null</code>)
     * @param tipoCucina tipo di cucina cercata (può essere <code>null</code>)
     * @param prezzoMin prezzo minimo in euro (può essere <code>null</code>)
     * @param prezzoMax prezzo massimo in euro (può essere <code>null</code>)
     * @param delivery true per cercare solo ristoranti con delivery (può essere <code>null</code>)
     * @param prenotazioneOnline true per cercare solo ristoranti con prenotazione online (può essere <code>null</code>)
     * @param mediaStelleMin media minima delle stelle da 1 a 5 (può essere <code>null</code>)
     * @return lista di oggetti <code>Ristorante</code> che rispettano i filtri inseriti
     */
    public List<Ristorante> cercaRistorante(String nomeRistorante, String locazione, String tipoCucina,
                                            Integer prezzoMin, Integer prezzoMax, Boolean delivery,
                                            Boolean prenotazioneOnline, Double mediaStelleMin) {
        List<Ristorante> risultati = new ArrayList<>();

        StringBuilder query = new StringBuilder(
                "SELECT r.* FROM RistorantiTheKnife r " +
                        "LEFT JOIN Recensioni rec ON r.id_ristorante = rec.id_ristorante " +
                        "WHERE 1=1"
        );

        List<Object> parametri = new ArrayList<>();

        if (nomeRistorante != null && !nomeRistorante.trim().isEmpty()) {
            query.append(" AND LOWER(r.nome) LIKE LOWER(?)");
            parametri.add("%" + nomeRistorante.trim() + "%");
        }

        if (locazione != null && !locazione.trim().isEmpty()) {
            query.append(" AND (LOWER(r.citta) LIKE LOWER(?) OR LOWER(r.nazione) LIKE LOWER(?))");
            String locPattern = "%" + locazione.trim() + "%";
            parametri.add(locPattern);
            parametri.add(locPattern);
        }

        if (tipoCucina != null && !tipoCucina.trim().isEmpty()) {
            query.append(" AND LOWER(r.tipo_cucina) LIKE LOWER(?)");
            parametri.add("%" + tipoCucina.trim() + "%");
        }

        if (prezzoMin != null) {
            query.append(" AND r.fascia_prezzo >= ?");
            parametri.add(prezzoMin);
        }
        if (prezzoMax != null) {
            query.append(" AND r.fascia_prezzo <= ?");
            parametri.add(prezzoMax);
        }

        if (delivery != null && delivery) {
            query.append(" AND r.delivery = true");
        }

        if (prenotazioneOnline != null && prenotazioneOnline) {
            query.append(" AND r.prenotazione_online = true");
        }

        query.append(" GROUP BY r.id_ristorante");

        if (mediaStelleMin != null) {
            query.append(" HAVING COALESCE(AVG(rec.stelle), 0) >= ?");
            parametri.add(mediaStelleMin);
        }

        query.append(" ORDER BY r.nome ASC LIMIT 100");

        try (Connection conn = DatabaseManager.connect();
             PreparedStatement pstmt = conn.prepareStatement(query.toString())) {

            for (int i = 0; i < parametri.size(); i++) {
                pstmt.setObject(i + 1, parametri.get(i));
            }

            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    risultati.add(estraiRistorante(rs));
                }
            }
        } catch (SQLException e) {
            System.err.println("Errore durante l'esecuzione di cercaRistorante:");
            e.printStackTrace();
        }
        return risultati;
    }

    /**
     * Restituisce la lista dei ristoranti che appartengono a uno specifico ristoratore.
     * @param idRistoratore l'ID dell'utente ristoratore
     * @return lista dei ristoranti gestiti da quell'utente
     */
    public List<Ristorante> getRistorantiPerRistoratore(int idRistoratore) {
        List<Ristorante> mieiRistoranti = new ArrayList<>();
        String query = "SELECT * FROM RistorantiTheKnife WHERE id_ristoratore = ? ORDER BY nome ASC";

        try (Connection conn = DatabaseManager.connect();
             PreparedStatement pstmt = conn.prepareStatement(query)) {

            pstmt.setInt(1, idRistoratore);

            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    mieiRistoranti.add(estraiRistorante(rs));
                }
            }
        } catch (SQLException e) {
            System.err.println("Errore nel recupero dei ristoranti del ristoratore:");
            e.printStackTrace();
        }
        return mieiRistoranti;
    }

    /**
     * Aggiunge un ristorante alla lista dei preferiti di un cliente.
     * @param idCliente l'ID dell'utente cliente
     * @param idRistorante l'ID del ristorante da aggiungere ai preferiti
     * @return <code>true</code> se viene aggiunto, <code>false</code> se l'utente non è un cliente o c'è un errore
     */
    public boolean aggiungiPreferito(int idCliente, int idRistorante) {
        String query = "INSERT INTO Preferiti (id_utente, id_ristorante) " +
                "SELECT ?, ? FROM Utenti WHERE id_utente = ? AND LOWER(ruolo) = 'cliente'";

        try (Connection conn = DatabaseManager.connect();
             PreparedStatement pstmt = conn.prepareStatement(query)) {

            pstmt.setInt(1, idCliente);
            pstmt.setInt(2, idRistorante);
            pstmt.setInt(3, idCliente);

            return pstmt.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("Errore durante l'aggiunta ai preferiti:");
            e.printStackTrace();
            return false;
        }
    }

    /**
     * Rimuove un ristorante dalla lista dei preferiti di un cliente.
     * @param idCliente l'ID dell'utente cliente
     * @param idRistorante l'ID del ristorante da rimuovere
     * @return <code>true</code> se viene rimosso con successo, <code>false</code> altrimenti
     */
    public boolean rimuoviPreferito(int idCliente, int idRistorante) {
        String query = "DELETE FROM Preferiti WHERE id_utente = ? AND id_ristorante = ?";

        try (Connection conn = DatabaseManager.connect();
             PreparedStatement pstmt = conn.prepareStatement(query)) {

            pstmt.setInt(1, idCliente);
            pstmt.setInt(2, idRistorante);

            return pstmt.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("Errore durante la rimozione dai preferiti:");
            e.printStackTrace();
            return false;
        }
    }

    /**
     * Restituisce tutti i ristoranti preferiti salvati da un cliente.
     * @param idCliente l'ID dell'utente cliente
     * @return lista di oggetti <code>Ristorante</code> preferiti dal cliente
     */
    public List<Ristorante> visualizzaPreferiti(int idCliente) {
        List<Ristorante> preferiti = new ArrayList<>();
        String query = "SELECT r.* FROM RistorantiTheKnife r " +
                "JOIN Preferiti p ON r.id_ristorante = p.id_ristorante " +
                "WHERE p.id_utente = ? ORDER BY r.nome ASC";

        try (Connection conn = DatabaseManager.connect();
             PreparedStatement pstmt = conn.prepareStatement(query)) {

            pstmt.setInt(1, idCliente);

            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    preferiti.add(estraiRistorante(rs));
                }
            }
        } catch (SQLException e) {
            System.err.println("Errore nel recupero dei ristoranti preferiti:");
            e.printStackTrace();
        }
        return preferiti;
    }

    /**
     * Legge la riga corrente del <code>ResultSet</code> e crea un oggetto <code>Ristorante</code>.
     * @param rs il <code>ResultSet</code> della query
     * @return l'oggetto <code>Ristorante</code> creato con i dati letti
     * @throws SQLException se c'è un errore nella lettura delle colonne
     */
    private Ristorante estraiRistorante(ResultSet rs) throws SQLException {
        int idRist = rs.getInt("id_ristoratore");
        Integer idRistoratoreObj = rs.wasNull() ? null : idRist;

        return new Ristorante(
                rs.getInt("id_ristorante"),
                rs.getString("nome"),
                rs.getString("nazione"),
                rs.getString("citta"),
                rs.getString("indirizzo"),
                rs.getDouble("latitudine"),
                rs.getDouble("longitudine"),
                rs.getInt("fascia_prezzo"),
                rs.getBoolean("delivery"),
                rs.getBoolean("prenotazione_online"),
                rs.getString("tipo_cucina"),
                idRistoratoreObj
        );
    }

    /**
     * Modifica i dati di un ristorante esistente verificando che appartenga al ristoratore.
     * @param r l'oggetto <code>Ristorante</code> con i dati aggiornati
     * @param idRistoratore l'ID del ristoratore proprietario
     * @return <code>true</code> se la modifica riesce, <code>false</code> altrimenti
     */
    public boolean modificaRistorante(Ristorante r, int idRistoratore) {
        String query = "UPDATE RistorantiTheKnife SET nome = ?, nazione = ?, citta = ?, indirizzo = ?, " +
                "latitudine = ?, longitudine = ?, fascia_prezzo = ?, delivery = ?, prenotazione_online = ?, tipo_cucina = ? " +
                "WHERE id_ristorante = ? AND id_ristoratore = ?";

        try (Connection conn = DatabaseManager.connect();
             PreparedStatement pstmt = conn.prepareStatement(query)) {

            pstmt.setString(1, r.getNome());
            pstmt.setString(2, r.getNazione());
            pstmt.setString(3, r.getCitta());
            pstmt.setString(4, r.getIndirizzo());
            pstmt.setDouble(5, r.getLatitudine());
            pstmt.setDouble(6, r.getLongitudine());
            pstmt.setInt(7, r.getFasciaPrezzo());
            pstmt.setBoolean(8, r.isDelivery());
            pstmt.setBoolean(9, r.isPrenotazioneOnline());
            pstmt.setString(10, r.getTipoCucina());
            pstmt.setInt(11, r.getIdRistorante());
            pstmt.setInt(12, idRistoratore);

            return pstmt.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    /**
     * Elimina un ristorante dal database verificando che appartenga al ristoratore.
     * @param idRistorante l'ID del ristorante da eliminare
     * @param idRistoratore l'ID del ristoratore proprietario
     * @return <code>true</code> se viene eliminato, <code>false</code> altrimenti
     */
    public boolean eliminaRistorante(int idRistorante, int idRistoratore) {
        String query = "DELETE FROM RistorantiTheKnife WHERE id_ristorante = ? AND id_ristoratore = ?";

        try (Connection conn = DatabaseManager.connect();
             PreparedStatement pstmt = conn.prepareStatement(query)) {

            pstmt.setInt(1, idRistorante);
            pstmt.setInt(2, idRistoratore);

            return pstmt.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }
}