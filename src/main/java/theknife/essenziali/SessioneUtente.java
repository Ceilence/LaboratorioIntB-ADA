/*
 * Autori del progetto:
 * - Alessandro Frigerio, Matricola: 759926, Sede: CO
 * - Antonio Pardo, Matricola: 760613, Sede: CO
 * - Davide Moretti, Matricola: 762176, Sede: CO
 */
package theknife.essenziali;

/**
 * Classe che mantiene i dati dell'utente attualmente attivo nell'applicazione (loggato o Guest).
 * <p>
 * Permette all'interfaccia grafica di sapere in ogni momento se chi usa il programma
 * è entrato tramite il pulsante Guest, oppure se ha fatto il login come cliente o ristoratore.
 * @author Alessandro Frigerio
 * @author Antonio Pardo
 * @author Davide Moretti
 */
public class SessioneUtente {

    /**
     * L'utente attualmente attivo nella sessione <code>utenteCorrente</code>.
     */
    private Utente utenteCorrente;

    /**
     * Costruttore vuoto della classe <code>SessioneUtente</code>.
     */
    public SessioneUtente() {
        this.utenteCorrente = null;
    }

    /**
     * Imposta la sessione in modalità Guest quando l'utente clicca sul pulsante per entrare senza login.
     * @param luogo il nome del luogo inserito dall'utente Guest nella schermata iniziale
     * @return <code>true</code> se il luogo è valido e la sessione Guest viene avviata, <code>false</code> se il luogo è vuoto
     */
    public boolean entraComeGuest(String luogo) {
        if (luogo == null || luogo.trim().isEmpty()) {
            System.err.println("Errore: per entrare come Guest devi inserire il nome di un luogo.");
            return false;
        }
        this.utenteCorrente = Utente.creaGuest(luogo.trim());
        return true;
    }

    /**
     * Imposta la sessione con un utente registrato che ha appena effettuato il login.
     * @param utenteLoggato l'oggetto <code>Utente</code> restituito dal database dopo il login
     */
    public void impostaUtenteLoggato(Utente utenteLoggato) {
        this.utenteCorrente = utenteLoggato;
    }

    /**
     * Chiude la sessione corrente (logout) riportando l'utente a <code>null</code>.
     */
    public void logout() {
        this.utenteCorrente = null;
    }

    /**
     * Restituisce l'utente attivo nella sessione corrente.
     * @return l'oggetto <code>Utente</code> corrente (può essere un Guest o un utente registrato)
     */
    public Utente getUtenteCorrente() {
        return utenteCorrente;
    }

    /**
     * Restituisce il luogo di riferimento dell'utente attivo.
     * @return il nome del luogo associato alla sessione, oppure <code>null</code> se nessuno è entrato
     */
    public String getLuogoRiferimento() {
        if (utenteCorrente != null) {
            return utenteCorrente.getLuogoDomicilio();
        }
        return null;
    }

    /**
     * Indica se c'è un utente attivo nella sessione.
     * @return <code>true</code> se la sessione è attiva, <code>false</code> se si è ancora nel menù iniziale
     */
    public boolean isSessioneAttiva() {
        return utenteCorrente != null;
    }
}