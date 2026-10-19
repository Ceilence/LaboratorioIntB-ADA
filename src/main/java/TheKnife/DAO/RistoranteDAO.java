package TheKnife.DAO;

import TheKnife.DB.DatabaseManager;
import TheKnife.Essenziali.Ristorante;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

/**
 * Classe Data Access Object (DAO) per la gestione della persistenza dell'entit&agrave; <code>Ristorante</code>.
 * <p>
 * Fornisce i metodi per eseguire le operazioni CRUD e le ricerche filtrate sulla tabella
 * <code>RistorantiTheKnife</code> del database PostgreSQL tramite JDBC.
 * @author Alessandro Frigerio
 */
public class RistoranteDAO {

    /**
     * Costruttore vuoto di default per la classe <code>RistoranteDAO</code>.
     */
    public RistoranteDAO() {
    }

    /**
     * Inserisce un nuovo ristorante all'interno della tabella <code>RistorantiTheKnife</code>.
     * @param r l'oggetto <code>Ristorante</code> contenente i dati da salvare
     * @return <code>true</code> se l'inserimento va a buon fine, <code>false</code> altrimenti
     */
    public boolean inserisciRistorante(Ristorante r) {
        String query = "INSERT INTO RistorantiTheKnife (nome, nazione, citta, indirizzo, latitudine, longitudine, fascia_prezzo, delivery, prenotazione_online, tipo_cucina) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";

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

            int righeInserite = pstmt.executeUpdate();
            return righeInserite > 0;

        } catch (SQLException e) {
            System.err.println("Errore durante l'inserimento del ristorante:");
            e.printStackTrace();
            return false;
        }
    }

    /**
     * Recupera un singolo ristorante dal database a partire dal suo identificativo univoco.
     * @param idRistorante l'identificativo univoco del ristorante da cercare
     * @return l'oggetto <code>Ristorante</code> corrispondente, oppure <code>null</code> se non trovato
     */
    public Ristorante getRistorantePerId(int idRistorante) {
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
     * Ricerca i ristoranti nel database combinando dinamicamente la barra di ricerca e i filtri selezionati.
     * <p>
     * Utilizza la clausola base <code>WHERE 1=1</code> per permettere la concatenazione libera di qualsiasi
     * combinazione di criteri (inclusa la ricerca per solo nome e prezzo) ed esegue un <code>LEFT JOIN</code>
     * con la tabella <code>Recensioni</code> per il filtro sulla media delle stelle.
     * @param nomeRistorante parola chiave o nome del ristorante inserito nella barra di ricerca (pu&ograve; essere <code>null</code>)
     * @param locazione citt&agrave; o nazione del ristorante (pu&ograve; essere <code>null</code>)
     * @param tipoCucina tipologia di cucina desiderata (pu&ograve; essere <code>null</code>)
     * @param prezzoMin prezzo minimo in euro, es. maggiore di 30 euro (pu&ograve; essere <code>null</code>)
     * @param prezzoMax prezzo massimo in euro, es. minore di 50 euro (pu&ograve; essere <code>null</code>)
     * @param delivery se <code>true</code> filtra i ristoranti con consegna a domicilio (pu&ograve; essere <code>null</code>)
     * @param prenotazioneOnline se <code>true</code> filtra i ristoranti con prenotazione online (pu&ograve; essere <code>null</code>)
     * @param mediaStelleMin valutazione media minima di stelle da 1 a 5 (pu&ograve; essere <code>null</code>)
     * @return una lista di oggetti <code>Ristorante</code> che soddisfano tutti i filtri specificati
     */
    public List<Ristorante> cercaRistorante(String nomeRistorante, String locazione, String tipoCucina,
                                            Integer prezzoMin, Integer prezzoMax, Boolean delivery,
                                            Boolean prenotazioneOnline, Double mediaStelleMin) {
        List<Ristorante> risultati = new ArrayList<>();

        // WHERE 1=1 permette di attaccare qualsiasi filtro con "AND" in totale libertà
        StringBuilder query = new StringBuilder(
                "SELECT r.* FROM RistorantiTheKnife r " +
                        "LEFT JOIN Recensioni rec ON r.id_ristorante = rec.id_ristorante " +
                        "WHERE 1=1"
        );

        List<Object> parametri = new ArrayList<>();

        // 1. Barra di ricerca (cerca nel nome del ristorante)
        if (nomeRistorante != null && !nomeRistorante.trim().isEmpty()) {
            query.append(" AND LOWER(r.nome) LIKE LOWER(?)");
            parametri.add("%" + nomeRistorante.trim() + "%");
        }

        // 2. Filtro locazione geografica (cerca sia in città che in nazione)
        if (locazione != null && !locazione.trim().isEmpty()) {
            query.append(" AND (LOWER(r.citta) LIKE LOWER(?) OR LOWER(r.nazione) LIKE LOWER(?))");
            String locPattern = "%" + locazione.trim() + "%";
            parametri.add(locPattern);
            parametri.add(locPattern);
        }

        // 3. Filtro tipologia di cucina
        if (tipoCucina != null && !tipoCucina.trim().isEmpty()) {
            query.append(" AND LOWER(r.tipo_cucina) LIKE LOWER(?)");
            parametri.add("%" + tipoCucina.trim() + "%");
        }

        // 4. Filtro fascia di prezzo:
        // - Solo prezzoMin valorizzato (es. 31 o 50): tutti quelli che costano più di 30€ (da €€ in su)
        // - Solo prezzoMax valorizzato (es. 30): tutti quelli che costano meno di 30€ (solo € = 25€)
        // - Entrambi valorizzati: fascia compresa tra prezzoMin e prezzoMax
        if (prezzoMin != null) {
            query.append(" AND r.fascia_prezzo >= ?");
            parametri.add(prezzoMin);
        }
        if (prezzoMax != null) {
            query.append(" AND r.fascia_prezzo <= ?");
            parametri.add(prezzoMax);
        }

        // 5. Filtro servizio delivery
        if (delivery != null && delivery) {
            query.append(" AND r.delivery = true");
        }

        // 6. Filtro prenotazione online
        if (prenotazioneOnline != null && prenotazioneOnline) {
            query.append(" AND r.prenotazione_online = true");
        }

        // Raggruppamento per calcolare la media delle recensioni di ogni ristorante
        query.append(" GROUP BY r.id_ristorante");

        // 7. Filtro media stelle
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
     * Metodo di utilit&agrave; privato che converte una riga del <code>ResultSet</code> in un oggetto <code>Ristorante</code>.
     * @param rs il <code>ResultSet</code> posizionato sulla riga corrente da convertire
     * @return l'oggetto <code>Ristorante</code> popolato con i dati della riga
     * @throws SQLException se si verifica un errore nell'accesso alle colonne del <code>ResultSet</code>
     */
    private Ristorante estraiRistorante(ResultSet rs) throws SQLException {
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
                rs.getString("tipo_cucina")
        );
    }
}
