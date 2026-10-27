/*
 * Autori del progetto:
 * - Alessandro Frigerio, Matricola: 759926, Sede: CO
 * - Antonio Pardo, Matricola: 760613, Sede: CO
 * - Davide Moretti, Matricola: 762176, Sede: CO
 */
package theknife.gestore;

import theknife.essenziali.Recensione;
import theknife.essenziali.Ristorante;
import theknife.essenziali.SessioneUtente;
import theknife.essenziali.Utente;
import theknife.remoto.ServiziTheKnife;

import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.util.ArrayList;
import java.util.List;

/**
 * Classe Controller lato Client (<code>clientTK</code>) che comunica in rete con <code>serverTK</code>.
 * <p>
 * Mantiene la <code>SessioneUtente</code> locale (Guest, Cliente o Ristoratore), verifica i permessi
 * e inoltra le richieste al server tramite Java RMI.
 * @author Alessandro Frigerio
 * @author Antonio Pardo
 * @author Davide Moretti
 */
public class ControllerTheKnife {

    /**
     * Oggetto che mantiene lo stato dell'utente attivo sul client <code>sessione</code>.
     */
    private final SessioneUtente sessione;

    /**
     * Riferimento remoto ai servizi esposti da <code>serverTK</code> tramite RMI <code>serverRemoto</code>.
     */
    private ServiziTheKnife serverRemoto;

    /**
     * Costruttore che inizializza la sessione e si connette a <code>serverTK</code> su <code>localhost</code>.
     */
    public ControllerTheKnife() {
        this("localhost");
    }

    /**
     * Costruttore che inizializza la sessione e si connette a <code>serverTK</code> sull'host specificato.
     * @param hostServer indirizzo IP o nome host su cui è in esecuzione <code>serverTK</code>
     */
    public ControllerTheKnife(String hostServer) {
        this.sessione = new SessioneUtente();
        connettiAlServer(hostServer);
    }

    /**
     * Stabilisce la connessione RMI con il modulo <code>serverTK</code> sulla porta <code>1099</code>.
     * @param hostServer indirizzo IP o host del server
     * @return <code>true</code> se il collegamento con il server riesce, <code>false</code> altrimenti
     */
    public final boolean connettiAlServer(String hostServer) {
        try {
            String host = (hostServer == null || hostServer.trim().isEmpty()) ? "localhost" : hostServer.trim();
            Registry registry = LocateRegistry.getRegistry(host, 1099);
            this.serverRemoto = (ServiziTheKnife) registry.lookup("TheKnifeService");
            return true;
        } catch (Exception e) {
            System.err.println("Errore: impossibile connettersi a serverTK. Assicurati che ServerTK sia avviato!");
            return false;
        }
    }

    /**
     * Restituisce la sessione utente corrente del client.
     * @return l'oggetto <code>SessioneUtente</code> attivo
     */
    public SessioneUtente getSessione() {
        return sessione;
    }

    /**
     * Metodo chiamato dal pulsante "Entra come Guest" della GUI iniziale.
     * <p>
     * Imposta la sessione in modalità Guest con il luogo scelto e richiede al server
     * la lista dei ristoranti vicini.
     * @param luogo il nome del luogo inserito dall'utente Guest
     * @return lista dei ristoranti vicini al luogo indicato
     */
    public List<Ristorante> accediComeGuest(String luogo) {
        if (sessione.entraComeGuest(luogo)) {
            return getRistorantiVicini();
        }
        return new ArrayList<>();
    }

    /**
     * Invia le credenziali al server per effettuare il login e, se valide, avvia la sessione.
     * @param username lo username dell'utente
     * @param password la password in chiaro
     * @return <code>true</code> se il login riesce, <code>false</code> altrimenti
     */
    public boolean login(String username, String password) {
        try {
            Utente u = serverRemoto.login(username, password);
            if (u != null) {
                sessione.impostaUtenteLoggato(u);
                return true;
            }
        } catch (Exception e) {
            System.err.println("Errore di comunicazione con il server durante il login:");
            e.printStackTrace();
        }
        return false;
    }

    /**
     * Richiede al server la registrazione di un nuovo utente (cliente o ristoratore).
     * @param nuovoUtente l'oggetto <code>Utente</code> da registrare
     * @return <code>true</code> se la registrazione va a buon fine, <code>false</code> altrimenti
     */
    public boolean registrazione(Utente nuovoUtente) {
        try {
            return serverRemoto.registrazione(nuovoUtente);
        } catch (Exception e) {
            System.err.println("Errore di comunicazione con il server durante la registrazione:");
            e.printStackTrace();
            return false;
        }
    }

    /**
     * Chiude la sessione corrente sul client effettuando il logout.
     */
    public void logout() {
        sessione.logout();
    }

    /**
     * Richiede al server i ristoranti vicini al luogo del Guest o al domicilio dell'utente loggato.
     * @return lista di oggetti <code>Ristorante</code> vicini
     */
    public List<Ristorante> getRistorantiVicini() {
        if (!sessione.isSessioneAttiva()) {
            System.err.println("Errore: devi prima accedere come Guest o fare il login.");
            return new ArrayList<>();
        }
        return cercaRistorante(null, sessione.getLuogoRiferimento(), null, null, null, null, null, null);
    }

    /**
     * Richiede al server la ricerca dei ristoranti combinando i filtri selezionati.
     * @param nomeRistorante parola chiave o nome del ristorante (può essere <code>null</code>)
     * @param locazione città o nazione (se <code>null</code> usa il luogo della sessione attiva)
     * @param tipoCucina tipo di cucina desiderata (può essere <code>null</code>)
     * @param prezzoMin prezzo minimo in euro (può essere <code>null</code>)
     * @param prezzoMax prezzo massimo in euro (può essere <code>null</code>)
     * @param delivery filtro consegna a domicilio (può essere <code>null</code>)
     * @param prenotazioneOnline filtro prenotazione online (può essere <code>null</code>)
     * @param mediaStelleMin filtro media minima stelle (può essere <code>null</code>)
     * @return lista dei ristoranti restituiti dal server
     */
    public List<Ristorante> cercaRistorante(String nomeRistorante, String locazione, String tipoCucina,
                                            Integer prezzoMin, Integer prezzoMax, Boolean delivery,
                                            Boolean prenotazioneOnline, Double mediaStelleMin) {
        try {
            String luogoDaUsare = (locazione != null && !locazione.trim().isEmpty())
                    ? locazione
                    : sessione.getLuogoRiferimento();

            return serverRemoto.cercaRistorante(
                    nomeRistorante, luogoDaUsare, tipoCucina,
                    prezzoMin, prezzoMax, delivery, prenotazioneOnline, mediaStelleMin
            );
        } catch (Exception e) {
            System.err.println("Errore di comunicazione con il server durante cercaRistorante:");
            e.printStackTrace();
            return new ArrayList<>();
        }
    }

    /**
     * Richiede al server i dettagli completi di un ristorante selezionato.
     * @param idRistorante l'ID del ristorante
     * @return l'oggetto <code>Ristorante</code> ricevuto dal server, oppure <code>null</code>
     */
    public Ristorante visualizzaRistorante(int idRistorante) {
        try {
            return serverRemoto.visualizzaRistorante(idRistorante);
        } catch (Exception e) {
            System.err.println("Errore di comunicazione con il server durante visualizzaRistorante:");
            e.printStackTrace();
            return null;
        }
    }

    /**
     * Richiede al server la lista delle recensioni di un ristorante in forma anonima.
     * @param idRistorante l'ID del ristorante
     * @return lista di oggetti <code>Recensione</code>
     */
    public List<Recensione> visualizzaRecensioni(int idRistorante) {
        try {
            return serverRemoto.visualizzaRecensioni(idRistorante);
        } catch (Exception e) {
            System.err.println("Errore di comunicazione con il server durante visualizzaRecensioni:");
            e.printStackTrace();
            return new ArrayList<>();
        }
    }

    /**
     * Richiede al server il numero totale di recensioni e la media stelle di un ristorante.
     * @param idRistorante l'ID del ristorante
     * @return array con totale recensioni in <code>[0]</code> e media stelle in <code>[1]</code>
     */
    public double[] visualizzaRiepilogo(int idRistorante) {
        try {
            return serverRemoto.visualizzaRiepilogo(idRistorante);
        } catch (Exception e) {
            System.err.println("Errore di comunicazione con il server durante visualizzaRiepilogo:");
            e.printStackTrace();
            return new double[]{0.0, 0.0};
        }
    }

    /**
     * Richiede al server di aggiungere un ristorante ai preferiti del cliente loggato.
     * @param idRistorante l'ID del ristorante
     * @return <code>true</code> se l'aggiunta riesce, <code>false</code> altrimenti
     */
    public boolean aggiungiPreferito(int idRistorante) {
        if (!verificaPermessoCliente("aggiungere un ristorante ai preferiti")) {
            return false;
        }
        try {
            return serverRemoto.aggiungiPreferito(sessione.getUtenteCorrente().getIdUtente(), idRistorante);
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    /**
     * Richiede al server di rimuovere un ristorante dai preferiti del cliente loggato.
     * @param idRistorante l'ID del ristorante
     * @return <code>true</code> se la rimozione riesce, <code>false</code> altrimenti
     */
    public boolean rimuoviPreferito(int idRistorante) {
        if (!verificaPermessoCliente("rimuovere un ristorante dai preferiti")) {
            return false;
        }
        try {
            return serverRemoto.rimuoviPreferito(sessione.getUtenteCorrente().getIdUtente(), idRistorante);
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    /**
     * Richiede al server la lista dei ristoranti preferiti del cliente loggato.
     * @return lista dei ristoranti preferiti
     */
    public List<Ristorante> visualizzaPreferiti() {
        if (!verificaPermessoCliente("visualizzare i ristoranti preferiti")) {
            return new ArrayList<>();
        }
        try {
            return serverRemoto.visualizzaPreferiti(sessione.getUtenteCorrente().getIdUtente());
        } catch (Exception e) {
            e.printStackTrace();
            return new ArrayList<>();
        }
    }

    /**
     * Invia al server una nuova recensione scritta dal cliente loggato.
     * @param idRistorante l'ID del ristorante
     * @param stelle voto da 1 a 5 stelle
     * @param testo commento della recensione
     * @return <code>true</code> se l'inserimento riesce, <code>false</code> altrimenti
     */
    public boolean aggiungiRecensione(int idRistorante, int stelle, String testo) {
        if (!verificaPermessoCliente("inserire una recensione")) {
            return false;
        }
        try {
            Recensione nuova = new Recensione(sessione.getUtenteCorrente().getIdUtente(), idRistorante, stelle, testo, null);
            return serverRemoto.aggiungiRecensione(nuova);
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    /**
     * Richiede al server di modificare una recensione del cliente loggato.
     * @param idRecensione l'ID della recensione
     * @param nuoveStelle il nuovo voto da 1 a 5
     * @param nuovoTesto il nuovo commento
     * @return <code>true</code> se la modifica riesce, <code>false</code> altrimenti
     */
    public boolean modificaRecensione(int idRecensione, int nuoveStelle, String nuovoTesto) {
        if (!verificaPermessoCliente("modificare una recensione")) {
            return false;
        }
        try {
            return serverRemoto.modificaRecensione(idRecensione, sessione.getUtenteCorrente().getIdUtente(), nuoveStelle, nuovoTesto);
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    /**
     * Richiede al server di eliminare una recensione del cliente loggato.
     * @param idRecensione l'ID della recensione
     * @return <code>true</code> se l'eliminazione riesce, <code>false</code> altrimenti
     */
    public boolean eliminaRecensione(int idRecensione) {
        if (!verificaPermessoCliente("eliminare una recensione")) {
            return false;
        }
        try {
            return serverRemoto.eliminaRecensione(idRecensione, sessione.getUtenteCorrente().getIdUtente());
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    /**
     * Richiede al server tutte le recensioni scritte dal cliente loggato.
     * @return lista delle recensioni del cliente
     */
    public List<Recensione> visualizzaMieRecensioniCliente() {
        if (!verificaPermessoCliente("visualizzare le proprie recensioni")) {
            return new ArrayList<>();
        }
        try {
            return serverRemoto.visualizzaRecensioniPerCliente(sessione.getUtenteCorrente().getIdUtente());
        } catch (Exception e) {
            e.printStackTrace();
            return new ArrayList<>();
        }
    }

    /**
     * Invia al server un nuovo ristorante creato dal ristoratore loggato.
     * @param nuovoRistorante l'oggetto <code>Ristorante</code> da salvare
     * @return <code>true</code> se il salvataggio riesce, <code>false</code> altrimenti
     */
    public boolean aggiungiRistorante(Ristorante nuovoRistorante) {
        if (!verificaPermessoRistoratore("aggiungere un nuovo ristorante")) {
            return false;
        }
        try {
            int idRistoratore = sessione.getUtenteCorrente().getIdUtente();
            nuovoRistorante.setIdRistoratore(idRistoratore);
            return serverRemoto.aggiungiRistorante(nuovoRistorante, idRistoratore);
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    /**
     * Invia al server la risposta del ristoratore loggato a una recensione.
     * @param idRecensione l'ID della recensione
     * @param testoRisposta il testo della risposta
     * @return <code>true</code> se il salvataggio riesce, <code>false</code> altrimenti
     */
    public boolean rispostaRecensioni(int idRecensione, String testoRisposta) {
        if (!verificaPermessoRistoratore("rispondere a una recensione")) {
            return false;
        }
        try {
            return serverRemoto.rispostaRecensioni(idRecensione, sessione.getUtenteCorrente().getIdUtente(), testoRisposta);
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    /**
     * Richiede al server la lista dei ristoranti inseriti dal ristoratore loggato.
     * @return lista dei ristoranti del ristoratore
     */
    public List<Ristorante> visualizzaMieiRistoranti() {
        if (!verificaPermessoRistoratore("visualizzare i propri ristoranti gestiti")) {
            return new ArrayList<>();
        }
        try {
            return serverRemoto.getRistorantiPerRistoratore(sessione.getUtenteCorrente().getIdUtente());
        } catch (Exception e) {
            e.printStackTrace();
            return new ArrayList<>();
        }
    }

    /**
     * Verifica che l'utente attivo nella sessione sia un cliente registrato.
     * @param azione nome dell'operazione per il messaggio d'errore
     * @return <code>true</code> se è un cliente, <code>false</code> altrimenti
     */
    private boolean verificaPermessoCliente(String azione) {
        Utente u = sessione.getUtenteCorrente();
        if (u == null || u.isGuest()) {
            System.err.println("Operazione bloccata: un utente Guest non può " + azione + ".");
            return false;
        }
        if (!u.isCliente()) {
            System.err.println("Operazione bloccata: solo un cliente può " + azione + ".");
            return false;
        }
        return true;
    }

    /**
     * Verifica che l'utente attivo nella sessione sia un ristoratore registrato.
     * @param azione nome dell'operazione per il messaggio d'errore
     * @return <code>true</code> se è un ristoratore, <code>false</code> altrimenti
     */
    private boolean verificaPermessoRistoratore(String azione) {
        Utente u = sessione.getUtenteCorrente();
        if (u == null || u.isGuest()) {
            System.err.println("Operazione bloccata: un utente Guest non può " + azione + ".");
            return false;
        }
        if (!u.isRistoratore()) {
            System.err.println("Operazione bloccata: solo un ristoratore può " + azione + ".");
            return false;
        }
        return true;
    }

    /**
     * Aggiorna i dati e le credenziali dell'utente attualmente loggato.
     * @param nuovoNome nuovo nome
     * @param nuovoCognome nuovo cognome
     * @param nuovoUsername nuovo username
     * @param nuovoDomicilio nuovo luogo di domicilio
     * @param nuovaPasswordInChiaro nuova password (lascia vuoto per non cambiarla)
     * @return <code>true</code> se l'aggiornamento riesce, <code>false</code> altrimenti
     */
    public boolean aggiornaProfiloUtente(String nuovoNome, String nuovoCognome, String nuovoUsername,
                                         String nuovoDomicilio, String nuovaPasswordInChiaro) {
        Utente corrente = sessione.getUtenteCorrente();
        if (corrente == null || corrente.isGuest()) {
            return false;
        }
        try {
            boolean cambiaPass = nuovaPasswordInChiaro != null && !nuovaPasswordInChiaro.trim().isEmpty();
            Utente modificato = new Utente(
                    corrente.getIdUtente(), nuovoNome, nuovoCognome, nuovoUsername,
                    cambiaPass ? nuovaPasswordInChiaro : corrente.getPassword(),
                    corrente.getDataNascita(), nuovoDomicilio, corrente.getRuolo()
            );
            if (serverRemoto.aggiornaUtente(modificato, cambiaPass)) {
                sessione.impostaUtenteLoggato(modificato);
                return true;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    /**
     * Modifica un ristorante esistente del ristoratore loggato.
     * @param ristoranteModificato l'oggetto <code>Ristorante</code> aggiornato
     * @return <code>true</code> se la modifica riesce, <code>false</code> altrimenti
     */
    public boolean modificaRistorante(Ristorante ristoranteModificato) {
        if (!verificaPermessoRistoratore("modificare un ristorante")) {
            return false;
        }
        try {
            int idRist = sessione.getUtenteCorrente().getIdUtente();
            ristoranteModificato.setIdRistoratore(idRist);
            return serverRemoto.modificaRistorante(ristoranteModificato, idRist);
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    /**
     * Elimina un ristorante gestito dal ristoratore loggato.
     * @param idRistorante l'ID del ristorante da eliminare
     * @return <code>true</code> se viene eliminato, <code>false</code> altrimenti
     */
    public boolean eliminaRistorante(int idRistorante) {
        if (!verificaPermessoRistoratore("eliminare un ristorante")) {
            return false;
        }
        try {
            return serverRemoto.eliminaRistorante(idRistorante, sessione.getUtenteCorrente().getIdUtente());
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }
}