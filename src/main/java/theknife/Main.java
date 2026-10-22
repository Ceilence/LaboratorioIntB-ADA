/*
 * Autori del progetto:
 * - Alessandro Frigerio, Matricola: 759926, Sede: CO
 * - Antonio Pardo, Matricola: 760613, Sede: CO
 * - Davide Moretti, Matricola: 762176, Sede: CO
 */
package theknife;

import theknife.db.ImportatoreCSV;
import theknife.essenziali.Recensione;
import theknife.essenziali.Ristorante;
import theknife.essenziali.Utente;
import theknife.gestore.ControllerTheKnife;

import java.util.List;

/**
 * Classe principale per testare i permessi dei metodi tra utente Guest, Cliente e Ristoratore.
 * @author Alessandro Frigerio
 * @author Antonio Pardo
 * @author Davide Moretti
 */
public class Main {

    /**
     * Costruttore vuoto della classe <code>Main</code>.
     */
    public Main() {
    }

    /**
     * Esegue i test di autorizzazione e funzionamento tramite il <code>ControllerTheKnife</code>.
     * @param args argomenti da riga di comando
     */
    public static void main(String[] args) {

        ImportatoreCSV.importaDati("michelin_my_maps.csv");
        ControllerTheKnife controller = new ControllerTheKnife();

        // Assicuriamoci che nel DB esistano un cliente (mrossi) e un ristoratore (ccracco)
        controller.registrazione(new Utente("Mario", "Rossi", "mrossi", "Segreta123!", null, "Como", "cliente"));
        controller.registrazione(new Utente("Carlo", "Cracco", "ccracco", "Chef123!", null, "Milano", "ristoratore"));

        // =====================================================================
        // PROVA 1: SIMULAZIONE PRESSIONE DEL TASTO "ENTRA COME GUEST"
        // =====================================================================
        System.out.println("\n--- 1. TEST UTENTE GUEST (Luogo: Vienna) ---");
        List<Ristorante> viciniGuest = controller.accediComeGuest("Vienna");
        System.out.println("Utente attivo in sessione: " + controller.getSessione().getUtenteCorrente());
        System.out.println("Ristoranti vicini a Vienna trovati per il Guest: " + viciniGuest.size());

        // Il Guest fa una ricerca filtrata e visualizza un ristorante (CONSENTITO)
        List<Ristorante> filtrati = controller.cercaRistorante(null, null, "Modern", 25, 100, null, null, null);
        int idRist = filtrati.isEmpty() ? 1 : filtrati.get(0).getIdRistorante();
        System.out.println("Dettaglio ristorante letto dal Guest: " + controller.visualizzaRistorante(idRist));

        // Il Guest prova a usare metodi riservati al Cliente e al Ristoratore (DEVONO FALLIRE TUTTI)
        System.out.println("Guest prova ad aggiungere preferito -> " + controller.aggiungiPreferito(idRist) + " (atteso: false)");
        System.out.println("Guest prova a scrivere recensione -> " + controller.aggiungiRecensione(idRist, 5, "Top!") + " (atteso: false)");
        Ristorante nuovoLocale = new Ristorante("Ristorante Test", "Italy", "Milano", "Via Dante 5", 45.46, 9.18, 50, true, true, "Italian", null);
        System.out.println("Guest prova a creare ristorante -> " + controller.aggiungiRistorante(nuovoLocale) + " (atteso: false)");

        // =====================================================================
        // PROVA 2: LOGIN COME CLIENTE ("mrossi")
        // =====================================================================
        System.out.println("\n--- 2. TEST UTENTE CLIENTE (mrossi) ---");
        controller.login("mrossi", "Segreta123!");
        System.out.println("Utente attivo in sessione: " + controller.getSessione().getUtenteCorrente());

        // Azioni consentite al Cliente
        System.out.println("Cliente aggiunge ai preferiti -> " + controller.aggiungiPreferito(idRist));
        System.out.println("Lista preferiti del cliente: " + controller.visualizzaPreferiti().size() + " ristoranti");
        System.out.println("Cliente rimuove dai preferiti -> " + controller.rimuoviPreferito(idRist));

        // Azione vietata al Cliente (creare un ristorante)
        System.out.println("Cliente prova a creare ristorante -> " + controller.aggiungiRistorante(nuovoLocale) + " (atteso: false)");

        // =====================================================================
        // PROVA 3: LOGIN COME RISTORATORE ("ccracco")
        // =====================================================================
        System.out.println("\n--- 3. TEST UTENTE RISTORATORE (ccracco) ---");
        controller.login("ccracco", "Chef123!");
        System.out.println("Utente attivo in sessione: " + controller.getSessione().getUtenteCorrente());

        // Azioni vietate al Ristoratore (aggiungere preferiti o scrivere recensioni)
        System.out.println("Ristoratore prova ad aggiungere preferito -> " + controller.aggiungiPreferito(idRist) + " (atteso: false)");
        System.out.println("Ristoratore prova a scrivere recensione -> " + controller.aggiungiRecensione(idRist, 5, "Autovoto!") + " (atteso: false)");

        // Azioni consentite al Ristoratore (vedere i propri ristoranti e rispondere alle recensioni)
        List<Ristorante> mieiLocali = controller.visualizzaMieiRistoranti();
        System.out.println("Ristoranti gestiti da ccracco: " + mieiLocali.size());

        if (!mieiLocali.isEmpty()) {
            int idMioLocale = mieiLocali.get(0).getIdRistorante();

            // Facciamo scrivere una recensione al cliente mrossi su questo locale per testare la risposta
            controller.login("mrossi", "Segreta123!");
            controller.aggiungiRecensione(idMioLocale, 4, "Molto buono, tornerò!");

            // Torniamo al ristoratore ccracco per rispondere
            controller.login("ccracco", "Chef123!");
            List<Recensione> recDelMioLocale = controller.visualizzaRecensioni(idMioLocale);
            if (!recDelMioLocale.isEmpty()) {
                int idRec = recDelMioLocale.get(0).getIdRecensione();
                System.out.println("Ristoratore risponde alla recensione -> " + controller.rispostaRecensioni(idRec, "Grazie mille per la visita!"));
            }
        }
    }
}