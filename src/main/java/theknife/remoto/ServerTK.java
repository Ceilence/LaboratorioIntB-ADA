/*
 * Autori del progetto:
 * - Alessandro Frigerio, Matricola: 759926, Sede: CO
 * - Antonio Pardo, Matricola: 760613, Sede: CO
 * - Davide Moretti, Matricola: 762176, Sede: CO
 */
package theknife.remoto;

import theknife.dao.RecensioneDAO;
import theknife.dao.RistoranteDAO;
import theknife.dao.UtenteDAO;
import theknife.db.DatabaseManager;
import theknife.db.ImportatoreCSV;
import theknife.essenziali.Recensione;
import theknife.essenziali.Ristorante;
import theknife.essenziali.Utente;

import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.UnicastRemoteObject;
import java.sql.Connection;
import java.util.List;
import java.util.Scanner;

/**
 * Classe che implementa il modulo <code>serverTK</code> della piattaforma TheKnife.
 * <p>
 * All'avvio richiede l'host e le credenziali del database PostgreSQL, avvia il registro RMI
 * e rimane in attesa di richieste concorrenti da parte dei client <code>clientTK</code>.
 * @author Alessandro Frigerio
 * @author Antonio Pardo
 * @author Davide Moretti
 */
public class ServerTK extends UnicastRemoteObject implements ServiziTheKnife {

    /**
     * ID per la serializzazione <code>serialVersionUID</code>.
     */
    private static final long serialVersionUID = 1L;

    /**
     * DAO per la gestione dei ristoranti <code>ristoranteDAO</code>.
     */
    private final RistoranteDAO ristoranteDAO;

    /**
     * DAO per la gestione degli utenti <code>utenteDAO</code>.
     */
    private final UtenteDAO utenteDAO;

    /**
     * DAO per la gestione delle recensioni <code>recensioneDAO</code>.
     */
    private final RecensioneDAO recensioneDAO;

    /**
     * Costruttore che inizializza l'oggetto remoto <code>ServerTK</code> e i relativi DAO.
     * @throws RemoteException se si verifica un errore nell'esportazione dell'oggetto RMI
     */
    public ServerTK() throws RemoteException {
        super();
        this.ristoranteDAO = new RistoranteDAO();
        this.utenteDAO = new UtenteDAO();
        this.recensioneDAO = new RecensioneDAO();
    }

    /**
     * Registra un nuovo utente in modo thread-safe per gestire gli accessi concorrenti.
     * @param nuovoUtente oggetto <code>Utente</code> da registrare
     * @return <code>true</code> se la registrazione riesce, <code>false</code> altrimenti
     * @throws RemoteException in caso di errore RMI
     */
    @Override
    public synchronized boolean registrazione(Utente nuovoUtente) throws RemoteException {
        return utenteDAO.registraUtente(nuovoUtente);
    }

    /**
     * Effettua il login di un utente.
     * @param username lo username dell'utente
     * @param password la password in chiaro
     * @return l'oggetto <code>Utente</code> autenticato, oppure <code>null</code>
     * @throws RemoteException in caso di errore RMI
     */
    @Override
    public Utente login(String username, String password) throws RemoteException {
        return utenteDAO.login(username, password);
    }

    /**
     * Esegue la ricerca filtrata dei ristoranti sul database.
     * @param nomeRistorante nome o parola chiave
     * @param locazione città o nazione
     * @param tipoCucina tipo di cucina
     * @param prezzoMin prezzo minimo
     * @param prezzoMax prezzo massimo
     * @param delivery filtro delivery
     * @param prenotazioneOnline filtro prenotazione online
     * @param mediaStelleMin filtro media stelle
     * @return lista dei ristoranti corrispondenti ai criteri
     * @throws RemoteException in caso di errore RMI
     */
    @Override
    public List<Ristorante> cercaRistorante(String nomeRistorante, String locazione, String tipoCucina,
                                            Integer prezzoMin, Integer prezzoMax, Boolean delivery,
                                            Boolean prenotazioneOnline, Double mediaStelleMin) throws RemoteException {
        return ristoranteDAO.cercaRistorante(
                nomeRistorante, locazione, tipoCucina,
                prezzoMin, prezzoMax, delivery, prenotazioneOnline, mediaStelleMin
        );
    }

    /**
     * Restituisce i dettagli di un singolo ristorante.
     * @param idRistorante l'ID del ristorante
     * @return l'oggetto <code>Ristorante</code> trovato, oppure <code>null</code>
     * @throws RemoteException in caso di errore RMI
     */
    @Override
    public Ristorante visualizzaRistorante(int idRistorante) throws RemoteException {
        return ristoranteDAO.visualizzaRistorante(idRistorante);
    }

    /**
     * Restituisce le recensioni di un ristorante oscurando l'ID dell'autore per l'anonimato.
     * @param idRistorante l'ID del ristorante
     * @return lista di oggetti <code>Recensione</code>
     * @throws RemoteException in caso di errore RMI
     */
    @Override
    public List<Recensione> visualizzaRecensioni(int idRistorante) throws RemoteException {
        List<Recensione> recensioni = recensioneDAO.visualizzaRecensioni(idRistorante);
        for (Recensione rec : recensioni) {
            rec.setIdUtente(0);
        }
        return recensioni;
    }

    /**
     * Restituisce il riepilogo (numero recensioni e media stelle) di un ristorante.
     * @param idRistorante l'ID del ristorante
     * @return array con totale recensioni e media stelle
     * @throws RemoteException in caso di errore RMI
     */
    @Override
    public double[] visualizzaRiepilogo(int idRistorante) throws RemoteException {
        return recensioneDAO.visualizzaRiepilogo(idRistorante);
    }

    /**
     * Aggiunge un ristorante ai preferiti di un cliente in modo sincronizzato.
     * @param idCliente l'ID del cliente
     * @param idRistorante l'ID del ristorante
     * @return <code>true</code> se aggiunto, <code>false</code> altrimenti
     * @throws RemoteException in caso di errore RMI
     */
    @Override
    public synchronized boolean aggiungiPreferito(int idCliente, int idRistorante) throws RemoteException {
        return ristoranteDAO.aggiungiPreferito(idCliente, idRistorante);
    }

    /**
     * Rimuove un ristorante dai preferiti di un cliente in modo sincronizzato.
     * @param idCliente l'ID del cliente
     * @param idRistorante l'ID del ristorante
     * @return <code>true</code> se rimosso, <code>false</code> altrimenti
     * @throws RemoteException in caso di errore RMI
     */
    @Override
    public synchronized boolean rimuoviPreferito(int idCliente, int idRistorante) throws RemoteException {
        return ristoranteDAO.rimuoviPreferito(idCliente, idRistorante);
    }

    /**
     * Restituisce i ristoranti preferiti di un cliente.
     * @param idCliente l'ID del cliente
     * @return lista dei ristoranti preferiti
     * @throws RemoteException in caso di errore RMI
     */
    @Override
    public List<Ristorante> visualizzaPreferiti(int idCliente) throws RemoteException {
        return ristoranteDAO.visualizzaPreferiti(idCliente);
    }

    /**
     * Salva una nuova recensione in modo sincronizzato.
     * @param nuovaRecensione l'oggetto <code>Recensione</code> da salvare
     * @return <code>true</code> se salvata, <code>false</code> altrimenti
     * @throws RemoteException in caso di errore RMI
     */
    @Override
    public synchronized boolean aggiungiRecensione(Recensione nuovaRecensione) throws RemoteException {
        return recensioneDAO.aggiungiRecensione(nuovaRecensione);
    }

    /**
     * Modifica una recensione di un cliente in modo sincronizzato.
     * @param idRecensione l'ID della recensione
     * @param idCliente l'ID del cliente
     * @param nuoveStelle il nuovo voto
     * @param nuovoTesto il nuovo commento
     * @return <code>true</code> se modificata, <code>false</code> altrimenti
     * @throws RemoteException in caso di errore RMI
     */
    @Override
    public synchronized boolean modificaRecensione(int idRecensione, int idCliente, int nuoveStelle, String nuovoTesto) throws RemoteException {
        return recensioneDAO.modificaRecensione(idRecensione, idCliente, nuoveStelle, nuovoTesto);
    }

    /**
     * Elimina una recensione di un cliente in modo sincronizzato.
     * @param idRecensione l'ID della recensione
     * @param idCliente l'ID del cliente
     * @return <code>true</code> se eliminata, <code>false</code> altrimenti
     * @throws RemoteException in caso di errore RMI
     */
    @Override
    public synchronized boolean eliminaRecensione(int idRecensione, int idCliente) throws RemoteException {
        return recensioneDAO.eliminaRecensione(idRecensione, idCliente);
    }

    /**
     * Restituisce tutte le recensioni scritte da un cliente.
     * @param idCliente l'ID del cliente
     * @return lista delle recensioni del cliente
     * @throws RemoteException in caso di errore RMI
     */
    @Override
    public List<Recensione> visualizzaRecensioniPerCliente(int idCliente) throws RemoteException {
        return recensioneDAO.visualizzaRecensioniPerCliente(idCliente);
    }

    /**
     * Aggiunge un nuovo ristorante creato da un ristoratore in modo sincronizzato.
     * @param nuovoRistorante l'oggetto <code>Ristorante</code> da inserire
     * @param idRistoratore l'ID del ristoratore proprietario
     * @return <code>true</code> se inserito, <code>false</code> altrimenti
     * @throws RemoteException in caso di errore RMI
     */
    @Override
    public synchronized boolean aggiungiRistorante(Ristorante nuovoRistorante, int idRistoratore) throws RemoteException {
        return ristoranteDAO.aggiungiRistorante(nuovoRistorante, idRistoratore);
    }

    /**
     * Registra la risposta del ristoratore a una recensione in modo sincronizzato.
     * @param idRecensione l'ID della recensione
     * @param idRistoratore l'ID del ristoratore
     * @param risposta il testo della risposta
     * @return <code>true</code> se registrata, <code>false</code> altrimenti
     * @throws RemoteException in caso di errore RMI
     */
    @Override
    public synchronized boolean rispostaRecensioni(int idRecensione, int idRistoratore, String risposta) throws RemoteException {
        return recensioneDAO.rispostaRecensioni(idRecensione, idRistoratore, risposta);
    }

    /**
     * Restituisce i ristoranti gestiti da uno specifico ristoratore.
     * @param idRistoratore l'ID del ristoratore
     * @return lista dei suoi ristoranti
     * @throws RemoteException in caso di errore RMI
     */
    @Override
    public List<Ristorante> getRistorantiPerRistoratore(int idRistoratore) throws RemoteException {
        return ristoranteDAO.getRistorantiPerRistoratore(idRistoratore);
    }

    /**
     * Metodo principale di avvio di <code>serverTK</code>.
     * <p>
     * Richiede all'utente l'host e le credenziali del database, verifica la connessione
     * e pubblica il server sul registro RMI sulla porta <code>1099</code>.
     * @param args argomenti da riga di comando
     */
    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);

        System.out.println("=== AVVIO MODULO serverTK (TheKnife) ===");
        System.out.print("Inserisci l'host del DB PostgreSQL [premi Invio per 'localhost']: ");
        String host = scanner.nextLine();

        System.out.print("Inserisci l'username del DB [premi Invio per 'postgres']: ");
        String dbUser = scanner.nextLine();

        System.out.print("Inserisci la password del DB [premi Invio per usare quella di default]: ");
        String dbPass = scanner.nextLine();

        DatabaseManager.configuraDatabase(
                host.isEmpty() ? "localhost" : host,
                dbUser.isEmpty() ? "postgres" : dbUser,
                dbPass.isEmpty() ? null : dbPass
        );

        // Verifica che le credenziali siano valide prima di avviare il server
        try (Connection testConn = DatabaseManager.connect()) {
            if (testConn == null) {
                System.err.println("Impossibile avviare serverTK: credenziali o host del database non validi.");
                return;
            }
            System.out.println("Connessione al database PostgreSQL verificata con successo!");
        } catch (Exception e) {
            e.printStackTrace();
            return;
        }

        // Controlla se serve popolare il DB dal CSV
        ImportatoreCSV.importaDati("michelin_my_maps.csv");

        try {
            ServerTK server = new ServerTK();
            Registry registry = LocateRegistry.createRegistry(1099);
            registry.rebind("TheKnifeService", server);

            System.out.println("\n>>> serverTK ATTIVO E IN ATTESA DI CONNESSIONI DAI CLIENT (Porta 1099) <<<");
        } catch (RemoteException e) {
            System.err.println("Errore durante l'avvio del server RMI:");
            e.printStackTrace();
        }
    }
}