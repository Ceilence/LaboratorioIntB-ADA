package org.example;

import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVRecord;
import java.io.FileReader;
import java.io.Reader;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.util.Random;

public class ImportatoreCSV {

    public static void importaDati() {
        // La query di inserimento (i punti interrogativi sono parametri che riempiremo dopo)
        String query = "INSERT INTO RistorantiTheKnife (nome, nazione, citta, indirizzo, latitudine, longitudine, fascia_prezzo, delivery, prenotazione_online, tipo_cucina) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
        Random random = new Random();

        // Apriamo la connessione al DB, il file CSV e prepariamo la query
        try (Connection conn = DatabaseManager.connect();
             Reader in = new FileReader("michelin_my_maps.csv");
             PreparedStatement pstmt = conn.prepareStatement(query)) {

            // Configuriamo il lettore CSV
            Iterable<CSVRecord> records = CSVFormat.DEFAULT.builder()
                    .setHeader()
                    .setSkipHeaderRecord(true)
                    .build()
                    .parse(in);

            int contatore = 0;

            System.out.println("Inizio importazione nel database. Attendi...");

            for (CSVRecord record : records) {
                // Estraiamo i dati dal CSV chiamandoli per nome colonna
                String nome = record.get("Name");
                String indirizzo = record.get("Address");
                double latitudine = Double.parseDouble(record.get("Latitude"));
                double longitudine = Double.parseDouble(record.get("Longitude"));
                String cucina = record.get("Cuisine");

                // Dividiamo la Location in Città e Nazione
                String[] location = record.get("Location").split(", ");
                String citta = location[0];
                String nazione = location.length > 1 ? location[1] : "Sconosciuta";

                // Gestione fascia di prezzo (conta i simboli € e li moltiplica per 25)
                String prezzoStr = record.get("Price");
                int prezzoMedio = (prezzoStr != null && !prezzoStr.isEmpty()) ? prezzoStr.length() * 25 : 30;

                // Riempiamo i punti interrogativi della query con i dati veri
                pstmt.setString(1, nome);
                pstmt.setString(2, nazione);
                pstmt.setString(3, citta);
                pstmt.setString(4, indirizzo);
                pstmt.setDouble(5, latitudine);
                pstmt.setDouble(6, longitudine);
                pstmt.setInt(7, prezzoMedio);

                // Servizi non presenti nel CSV generati randomicamente come booleani
                pstmt.setBoolean(8, random.nextBoolean());
                pstmt.setBoolean(9, random.nextBoolean());

                pstmt.setString(10, cucina);

                // Aggiungiamo la riga al "pacchetto" di dati da spedire
                pstmt.addBatch();
                contatore++;

                // Spediamo i dati a PostgreSQL a pacchetti di 1000 per non intasare la memoria
                if (contatore % 1000 == 0) {
                    pstmt.executeBatch();
                }
            }

            // Spediamo le ultime righe rimaste
            pstmt.executeBatch();
            System.out.println("\nImportazione di " + contatore + " ristoranti completata con successo nel DB!");

        } catch (Exception e) {
            System.out.println("Errore durante l'importazione:");
            e.printStackTrace();
        }
    }
}