package theknife.db;

import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVRecord;
import java.io.FileReader;
import java.io.Reader;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.util.Random;

/**
 * Classe responsabile dell'importazione dei dati da file CSV al database.
 * <p>
 * Implementa la lettura formattata tramite la libreria <em>Apache Commons CSV</em>
 * e l'inserimento all'interno della tabella <code>RistorantiTheKnife</code> usando JDBC.
 *
 * @author Alessandro Frigerio, Antonio Pardo, Davide Moretti
 */
public class ImportatoreCSV {


    /**
     * Legge il dataset CSV e popola le tabelle del database
     * <p>
     * Estrae i campi richiesti, suddivide dinamicamente la colonna location in citta e nazione
     * e implementa una logica di conversione testuale per calcolare la <code>fascia_prezzo</code>.
     * L'inserimento su PostgreSQL avviene a blocchi di 1000 record per non saturare la memoria.
     */
    public static void importaDati(String percorsoFile) {

        String query = "INSERT INTO RistorantiTheKnife (nome, nazione, citta, indirizzo, latitudine, longitudine, fascia_prezzo, delivery, prenotazione_online, tipo_cucina) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
        Random random = new Random();


        try (Connection conn = DatabaseManager.connect();
             Reader in = new FileReader(percorsoFile);
             PreparedStatement pstmt = conn.prepareStatement(query)) {

            try (PreparedStatement checkStmt = conn.prepareStatement("SELECT COUNT(*) FROM RistorantiTheKnife");
                 java.sql.ResultSet rs = checkStmt.executeQuery()) {
                if (rs.next() && rs.getInt(1) > 0) {
                    System.out.println("Database già popolato (" + rs.getInt(1) + " ristoranti presenti).");
                    return;
                }
            }

            Iterable<CSVRecord> records = CSVFormat.DEFAULT.builder()
                    .setHeader()
                    .setSkipHeaderRecord(true)
                    .build()
                    .parse(in);

            int contatore = 0;

            System.out.println("Importazione nel database...");

            for (CSVRecord record : records) {

                String nome = record.get("Name");
                String indirizzo = record.get("Address");
                double latitudine = Double.parseDouble(record.get("Latitude"));
                double longitudine = Double.parseDouble(record.get("Longitude"));
                String cucina = record.get("Cuisine");


                String[] location = record.get("Location").split(", ");
                String citta = location[0];
                String nazione = location.length > 1 ? location[1] : "Sconosciuta";

                String prezzoStr = record.get("Price");
                int prezzoMedio = (prezzoStr != null && !prezzoStr.isEmpty()) ? prezzoStr.length() * 25 : 30;


                pstmt.setString(1, nome);
                pstmt.setString(2, nazione);
                pstmt.setString(3, citta);
                pstmt.setString(4, indirizzo);
                pstmt.setDouble(5, latitudine);
                pstmt.setDouble(6, longitudine);
                pstmt.setInt(7, prezzoMedio);


                pstmt.setBoolean(8, random.nextBoolean());
                pstmt.setBoolean(9, random.nextBoolean());

                pstmt.setString(10, cucina);


                pstmt.addBatch();
                contatore++;


                if (contatore % 1000 == 0) {
                    pstmt.executeBatch();
                }
            }

            pstmt.executeBatch();
            System.out.println("\nImportazione di " + contatore + " ristoranti completata con successo nel DB!");

        } catch (Exception e) {
            System.out.println("Errore durante l'importazione:");
            e.printStackTrace();
        }
    }
}