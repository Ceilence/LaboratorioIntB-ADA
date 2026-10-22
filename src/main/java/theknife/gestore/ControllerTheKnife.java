/*
 * Autori del progetto:
 * - Alessandro Frigerio, Matricola: 759926, Sede: CO
 * - Antonio Pardo, Matricola: 760613, Sede: CO
 * - Davide Moretti, Matricola: 762176, Sede: CO
 */
package theknife.gestore;

import theknife.dao.RecensioneDAO;
import theknife.dao.RistoranteDAO;
import theknife.dao.UtenteDAO;
import theknife.essenziali.Recensione;
import theknife.essenziali.Ristorante;
import theknife.essenziali.SessioneUtente;
import theknife.essenziali.Utente;

import java.util.ArrayList;
import java.util.List;

/**
 * Classe Controller che gestisce tutte le operazioni dell'applicazione in base al ruolo dell'utente.
 * <p>
 * Collega la grafica (o il client) con le classi DAO, verificando tramite <code>SessioneUtente</code>
 * se chi esegue un'operazione è un utente Guest, un cliente registrato o un ristoratore.
 * @author Alessandro Frigerio
 * @author Antonio Pardo
 * @author Davide Moretti
 */
public class ControllerTheKnife {

    /**
     * Oggetto che mantiene lo stato dell'utente attivo <code>sessione</code>.
     */
    private final SessioneUtente sessione;

    /**
     * DAO per le operazioni sui ristoranti e preferiti <code>ristoranteDAO</code>.
     */
    private final RistoranteDAO ristoranteDAO;

    /**
     * DAO per le operazioni sugli utenti <code>utenteDAO</code>.
     */
    private final UtenteDAO utenteDAO;

    /**
     * DAO per le operazioni sulle recensioni <code>recensioneDAO</code>.
     */
    private final RecensioneDAO recensioneDAO;

    /**
     * Costruttore che inizializza la sessione e i DAO.
     */
    public ControllerTheKnife() {
        this.sessione = new SessioneUtente();
        this.ristoranteDAO = new RistoranteDAO();
        this.utenteDAO = new UtenteDAO();
        this.recensioneDAO = new RecensioneDAO();
    }

    /**
     * Restituisce la sessione utente corrente.
     * @return l'oggetto <code>SessioneUtente</code> attivo
     */
    public SessioneUtente getSessione() {
        return sessione;
    }

    // =========================================================================
    // 1. FUNZIONALITÀ DI ACCESSO: PULSANTE GUEST, LOGIN E REGISTRAZIONE
    // =========================================================================

    /**
     * Metodo che verrà chiamato dal pulsante "Entra come Guest" della schermata iniziale.
     * <p>
     * Avvia una sessione Guest salvando il luogo indicato e restituisce subito l'elenco
     * dei ristoranti vicini a quel luogo, come richiesto dalla Slide 14.
     * @param luogo il luogo inserito dall'utente Guest
     * @return la lista dei ristoranti vicini al luogo indicato
     */
    public List<Ristorante> accediComeGuest(String luogo) {
        if (sessione.entraComeGuest(luogo)) {
            return getRistorantiVicini();
        }
        return new ArrayList<>();
    }

    /**
     * Effettua il login di un utente registrato (cliente o ristoratore) e avvia la sua sessione.
     * @param username lo username dell'utente
     * @param password la password in chiaro
     * @return <code>true</code> se il login riesce, <code>false</code> se le credenziali sono errate
     */
    public boolean login(String username, String password) {
        Utente u = utenteDAO.login(username, password);
        if (u != null) {
            sessione.impostaUtenteLoggato(u);
            return true;
        }
        return false;
    }

    /**
     * Registra un nuovo utente sulla piattaforma (accessibile senza login o da Guest).
     * <p>
     * Corrisponde alla funzionalità <code>registrazione()</code> della Slide 11.
     * @param nuovoUtente l'oggetto <code>Utente</code> con i dati compilati nel form di registrazione
     * @return <code>true</code> se la registrazione va a buon fine, <code>false</code> altrimenti
     */
    public boolean registrazione(Utente nuovoUtente) {
        return utenteDAO.registraUtente(nuovoUtente);
    }

    /**
     * Effettua il logout riportando l'applicazione allo stato iniziale.
     */
    public void logout() {
        sessione.logout();
    }

    // =========================================================================
    // 2. FUNZIONALITÀ ACCESSIBILI A TUTTI (GUEST, CLIENTI E RISTORATORI)
    // =========================================================================

    /**
     * Restituisce i ristoranti vicini al luogo indicato dal Guest o al domicilio dell'utente loggato.
     * <p>
     * Supporta la schermata iniziale dopo l'accesso descritta nella Slide 14.
     * @return lista di oggetti <code>Ristorante</code> vicini all'utente corrente
     */
    public List<Ristorante> getRistorantiVicini() {
        if (!sessione.isSessioneAttiva()) {
            System.err.println("Errore: devi prima accedere come Guest o fare il login.");
            return new ArrayList<>();
        }
        String luogo = sessione.getLuogoRiferimento();
        return ristoranteDAO.cercaRistorante(null, luogo, null, null, null, null, null, null);
    }

    /**
     * Cerca i ristoranti combinando i filtri selezionati (accessibile anche al Guest).
     * <p>
     * Corrisponde alla funzionalità <code>cercaRistorante()</code> della Slide 10.
     * Se il parametro <code>locazione</code> viene lasciato vuoto dall'utente, usa automaticamente
     * il luogo indicato dal Guest o il domicilio dell'utente loggato.
     * @param nomeRistorante parola chiave o nome del ristorante (può essere <code>null</code>)
     * @param locazione città o nazione in cui cercare (se <code>null</code> usa il luogo della sessione)
     * @param tipoCucina tipo di cucina desiderata (può essere <code>null</code>)
     * @param prezzoMin prezzo minimo in euro (può essere <code>null</code>)
     * @param prezzoMax prezzo massimo in euro (può essere <code>null</code>)
     * @param delivery filtro consegna a domicilio (può essere <code>null</code>)
     * @param prenotazioneOnline filtro prenotazione online (può essere <code>null</code>)
     * @param mediaStelleMin filtro media minima stelle (può essere <code>null</code>)
     * @return lista dei ristoranti che soddisfano i criteri
     */
    public List<Ristorante> cercaRistorante(String nomeRistorante, String locazione, String tipoCucina,
                                            Integer prezzoMin, Integer prezzoMax, Boolean delivery,
                                            Boolean prenotazioneOnline, Double mediaStelleMin) {
        String luogoDaUsare = (locazione != null && !locazione.trim().isEmpty())
                ? locazione
                : sessione.getLuogoRiferimento();

        return ristoranteDAO.cercaRistorante(
                nomeRistorante, luogoDaUsare, tipoCucina,
                prezzoMin, prezzoMax, delivery, prenotazioneOnline, mediaStelleMin
        );
    }

    /**
     * Restituisce i dettagli completi di un ristorante selezionato (accessibile anche al Guest).
     * <p>
     * Corrisponde alla funzionalità <code>visualizzaRistorante()</code> della Slide 11.
     * @param idRistorante l'ID del ristorante selezionato
     * @return l'oggetto <code>Ristorante</code> completo, oppure <code>null</code>
     */
    public Ristorante visualizzaRistorante(int idRistorante) {
        return ristoranteDAO.visualizzaRistorante(idRistorante);
    }

    /**
     * Restituisce le recensioni di un ristorante in forma anonima (accessibile anche al Guest).
     * <p>
     * Corrisponde alla funzionalità <code>visualizzaRecensioni()</code> delle Slide 8, 11 e 13.
     * Per garantire l'anonimato richiesto dalla Slide 8, l'ID dell'utente autore viene azzerato (impostato a 0)
     * prima di restituire la lista alla vista, così nessun dato personale del cliente viene esposto.
     * @param idRistorante l'ID del ristorante di cui leggere le recensioni
     * @return lista di oggetti <code>Recensione</code> resi anonimi
     */
    public List<Recensione> visualizzaRecensioni(int idRistorante) {
        List<Recensione> recensioni = recensioneDAO.visualizzaRecensioni(idRistorante);
        for (Recensione rec : recensioni) {
            rec.setIdUtente(0);
        }
        return recensioni;
    }

    /**
     * Restituisce il numero totale di recensioni e la media delle stelle di un ristorante.
     * <p>
     * Corrisponde alla funzionalità <code>visualizzaRiepilogo()</code> delle Slide 11 e 13.
     * @param idRistorante l'ID del ristorante
     * @return array con numero recensioni in <code>[0]</code> e media stelle in <code>[1]</code>
     */
    public double[] visualizzaRiepilogo(int idRistorante) {
        return recensioneDAO.visualizzaRiepilogo(idRistorante);
    }

    // =========================================================================
    // 3. FUNZIONALITÀ RISERVATE ESCLUSIVAMENTE AI CLIENTI LOGGATI (Slide 12)
    // =========================================================================

    /**
     * Aggiunge un ristorante ai preferiti dell'utente corrente (bloccato per Guest e Ristoratori).
     * @param idRistorante l'ID del ristorante da aggiungere ai preferiti
     * @return <code>true</code> se l'utente è un cliente e l'aggiunta riesce, <code>false</code> altrimenti
     */
    public boolean aggiungiPreferito(int idRistorante) {
        if (!verificaPermessoCliente("aggiungere un ristorante ai preferiti")) {
            return false;
        }
        return ristoranteDAO.aggiungiPreferito(sessione.getUtenteCorrente().getIdUtente(), idRistorante);
    }

    /**
     * Rimuove un ristorante dai preferiti dell'utente corrente (bloccato per Guest e Ristoratori).
     * @param idRistorante l'ID del ristorante da rimuovere
     * @return <code>true</code> se l'utente è un cliente e la rimozione riesce, <code>false</code> altrimenti
     */
    public boolean rimuoviPreferito(int idRistorante) {
        if (!verificaPermessoCliente("rimuovere un ristorante dai preferiti")) {
            return false;
        }
        return ristoranteDAO.rimuoviPreferito(sessione.getUtenteCorrente().getIdUtente(), idRistorante);
    }

    /**
     * Visualizza la lista dei ristoranti preferiti del cliente loggato (bloccato per Guest e Ristoratori).
     * @return lista dei ristoranti preferiti del cliente, oppure lista vuota se non autorizzato
     */
    public List<Ristorante> visualizzaPreferiti() {
        if (!verificaPermessoCliente("visualizzare i ristoranti preferiti")) {
            return new ArrayList<>();
        }
        return ristoranteDAO.visualizzaPreferiti(sessione.getUtenteCorrente().getIdUtente());
    }

    /**
     * Aggiunge una recensione per un ristorante a nome del cliente loggato (bloccato per Guest e Ristoratori).
     * @param idRistorante l'ID del ristorante da recensire
     * @param stelle voto da 1 a 5 stelle
     * @param testo commento testuale della recensione
     * @return <code>true</code> se il cliente è autorizzato e l'inserimento riesce, <code>false</code> altrimenti
     */
    public boolean aggiungiRecensione(int idRistorante, int stelle, String testo) {
        if (!verificaPermessoCliente("inserire una recensione")) {
            return false;
        }
        Recensione nuova = new Recensione(sessione.getUtenteCorrente().getIdUtente(), idRistorante, stelle, testo, null);
        return recensioneDAO.aggiungiRecensione(nuova);
    }

    /**
     * Modifica una recensione scritta dal cliente loggato (bloccato per Guest e Ristoratori).
     * @param idRecensione l'ID della recensione da modificare
     * @param nuoveStelle il nuovo voto da 1 a 5 stelle
     * @param nuovoTesto il nuovo testo della recensione
     * @return <code>true</code> se la modifica riesce, <code>false</code> altrimenti
     */
    public boolean modificaRecensione(int idRecensione, int nuoveStelle, String nuovoTesto) {
        if (!verificaPermessoCliente("modificare una recensione")) {
            return false;
        }
        return recensioneDAO.modificaRecensione(idRecensione, sessione.getUtenteCorrente().getIdUtente(), nuoveStelle, nuovoTesto);
    }

    /**
     * Elimina una recensione scritta dal cliente loggato (bloccato per Guest e Ristoratori).
     * @param idRecensione l'ID della recensione da eliminare
     * @return <code>true</code> se l'eliminazione riesce, <code>false</code> altrimenti
     */
    public boolean eliminaRecensione(int idRecensione) {
        if (!verificaPermessoCliente("eliminare una recensione")) {
            return false;
        }
        return recensioneDAO.eliminaRecensione(idRecensione, sessione.getUtenteCorrente().getIdUtente());
    }

    /**
     * Restituisce tutte le recensioni scritte dal cliente attualmente loggato (Slide 15).
     * @return lista delle recensioni del cliente, oppure lista vuota se non autorizzato
     */
    public List<Recensione> visualizzaMieRecensioniCliente() {
        if (!verificaPermessoCliente("visualizzare le proprie recensioni")) {
            return new ArrayList<>();
        }
        return recensioneDAO.visualizzaRecensioniPerCliente(sessione.getUtenteCorrente().getIdUtente());
    }

    // =========================================================================
    // 4. FUNZIONALITÀ RISERVATE ESCLUSIVAMENTE AI RISTORATORI LOGGATI (Slide 13)
    // =========================================================================

    /**
     * Crea e salva un nuovo ristorante associandolo al ristoratore loggato (bloccato per Guest e Clienti).
     * @param nuovoRistorante l'oggetto <code>Ristorante</code> con i dati del locale
     * @return <code>true</code> se l'utente è un ristoratore e il salvataggio riesce, <code>false</code> altrimenti
     */
    public boolean aggiungiRistorante(Ristorante nuovoRistorante) {
        if (!verificaPermessoRistoratore("aggiungere un nuovo ristorante")) {
            return false;
        }
        int idRistoratore = sessione.getUtenteCorrente().getIdUtente();
        nuovoRistorante.setIdRistoratore(idRistoratore);
        return ristoranteDAO.aggiungiRistorante(nuovoRistorante, idRistoratore);
    }

    /**
     * Permette al ristoratore loggato di rispondere a una recensione di un suo ristorante (bloccato per Guest e Clienti).
     * @param idRecensione l'ID della recensione a cui rispondere
     * @param testoRisposta il testo della risposta
     * @return <code>true</code> se la risposta viene salvata, <code>false</code> altrimenti
     */
    public boolean rispostaRecensioni(int idRecensione, String testoRisposta) {
        if (!verificaPermessoRistoratore("rispondere a una recensione")) {
            return false;
        }
        return recensioneDAO.rispostaRecensioni(idRecensione, sessione.getUtenteCorrente().getIdUtente(), testoRisposta);
    }

    /**
     * Restituisce l'elenco dei ristoranti inseriti dal ristoratore attualmente loggato (Slide 15).
     * @return lista dei ristoranti gestiti dal ristoratore, oppure lista vuota se non autorizzato
     */
    public List<Ristorante> visualizzaMieiRistoranti() {
        if (!verificaPermessoRistoratore("visualizzare i propri ristoranti gestiti")) {
            return new ArrayList<>();
        }
        return ristoranteDAO.getRistorantiPerRistoratore(sessione.getUtenteCorrente().getIdUtente());
    }

    // =========================================================================
    // METODI PRIVATI DI CONTROLLO DEI PERMESSI (GUEST / CLIENTE / RISTORATORE)
    // =========================================================================

    /**
     * Controlla che nella sessione ci sia un utente loggato con ruolo <code>cliente</code>.
     * @param azione descrizione dell'azione richiesta per stampare un messaggio chiaro
     * @return <code>true</code> se è un cliente registrato, <code>false</code> se è un Guest o un ristoratore
     */
    private boolean verificaPermessoCliente(String azione) {
        Utente u = sessione.getUtenteCorrente();
        if (u == null || u.isGuest()) {
            System.err.println("Operazione bloccata: un utente Guest non può " + azione + ". Effettua il login come cliente.");
            return false;
        }
        if (!u.isCliente()) {
            System.err.println("Operazione bloccata: l'utente '" + u.getUsername() + "' ha ruolo '" + u.getRuolo() + "' e non può " + azione + ".");
            return false;
        }
        return true;
    }

    /**
     * Controlla che nella sessione ci sia un utente loggato con ruolo <code>ristoratore</code>.
     * @param azione descrizione dell'azione richiesta per stampare un messaggio chiaro
     * @return <code>true</code> se è un ristoratore registrato, <code>false</code> se è un Guest o un cliente
     */
    private boolean verificaPermessoRistoratore(String azione) {
        Utente u = sessione.getUtenteCorrente();
        if (u == null || u.isGuest()) {
            System.err.println("Operazione bloccata: un utente Guest non può " + azione + ". Effettua il login come ristoratore.");
            return false;
        }
        if (!u.isRistoratore()) {
            System.err.println("Operazione bloccata: l'utente '" + u.getUsername() + "' ha ruolo '" + u.getRuolo() + "' e non può " + azione + ".");
            return false;
        }
        return true;
    }
}