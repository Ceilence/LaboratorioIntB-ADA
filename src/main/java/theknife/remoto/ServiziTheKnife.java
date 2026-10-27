/*
 * Autori del progetto:
 * - Alessandro Frigerio, Matricola: 759926, Sede: CO
 * - Antonio Pardo, Matricola: 760613, Sede: CO
 * - Davide Moretti, Matricola: 762176, Sede: CO
 */
package theknife.remoto;

import theknife.essenziali.Recensione;
import theknife.essenziali.Ristorante;
import theknife.essenziali.Utente;

import java.rmi.Remote;
import java.rmi.RemoteException;
import java.util.List;

/**
 * Interfaccia remota RMI che definisce tutti i servizi offerti dal modulo <code>serverTK</code> ai client.
 * <p>
 * Permette a più istanze di <code>clientTK</code> di interagire in parallelo con il server.
 * @author Alessandro Frigerio
 * @author Antonio Pardo
 * @author Davide Moretti
 */
public interface ServiziTheKnife extends Remote {

    /**
     * Registra un nuovo utente nel database.
     * @param nuovoUtente oggetto <code>Utente</code> da registrare
     * @return <code>true</code> se la registrazione va a buon fine, <code>false</code> altrimenti
     * @throws RemoteException in caso di errore di comunicazione in rete
     */
    boolean registrazione(Utente nuovoUtente) throws RemoteException;

    /**
     * Autentica un utente tramite username e password.
     * @param username lo username inserito
     * @param password la password in chiaro
     * @return l'oggetto <code>Utente</code> loggato, oppure <code>null</code> se le credenziali sono errate
     * @throws RemoteException in caso di errore di comunicazione in rete
     */
    Utente login(String username, String password) throws RemoteException;

    /**
     * Cerca i ristoranti nel database applicando i filtri selezionati.
     * @param nomeRistorante nome o parola chiave (può essere <code>null</code>)
     * @param locazione città o nazione (può essere <code>null</code>)
     * @param tipoCucina tipo di cucina (può essere <code>null</code>)
     * @param prezzoMin prezzo minimo in euro (può essere <code>null</code>)
     * @param prezzoMax prezzo massimo in euro (può essere <code>null</code>)
     * @param delivery filtro consegna a domicilio (può essere <code>null</code>)
     * @param prenotazioneOnline filtro prenotazione online (può essere <code>null</code>)
     * @param mediaStelleMin filtro media minima stelle (può essere <code>null</code>)
     * @return lista dei ristoranti trovati
     * @throws RemoteException in caso di errore di comunicazione in rete
     */
    List<Ristorante> cercaRistorante(String nomeRistorante, String locazione, String tipoCucina,
                                     Integer prezzoMin, Integer prezzoMax, Boolean delivery,
                                     Boolean prenotazioneOnline, Double mediaStelleMin) throws RemoteException;

    /**
     * Restituisce tutte le informazioni di uno specifico ristorante.
     * @param idRistorante l'ID del ristorante selezionato
     * @return l'oggetto <code>Ristorante</code> trovato, oppure <code>null</code>
     * @throws RemoteException in caso di errore di comunicazione in rete
     */
    Ristorante visualizzaRistorante(int idRistorante) throws RemoteException;

    /**
     * Restituisce la lista delle recensioni di un ristorante in forma anonima.
     * @param idRistorante l'ID del ristorante
     * @return lista di oggetti <code>Recensione</code>
     * @throws RemoteException in caso di errore di comunicazione in rete
     */
    List<Recensione> visualizzaRecensioni(int idRistorante) throws RemoteException;

    /**
     * Restituisce numero totale di recensioni e media stelle di un ristorante.
     * @param idRistorante l'ID del ristorante
     * @return array con numero recensioni in <code>[0]</code> e media stelle in <code>[1]</code>
     * @throws RemoteException in caso di errore di comunicazione in rete
     */
    double[] visualizzaRiepilogo(int idRistorante) throws RemoteException;

    /**
     * Aggiunge un ristorante alla lista dei preferiti di un cliente.
     * @param idCliente l'ID del cliente
     * @param idRistorante l'ID del ristorante
     * @return <code>true</code> se l'aggiunta riesce, <code>false</code> altrimenti
     * @throws RemoteException in caso di errore di comunicazione in rete
     */
    boolean aggiungiPreferito(int idCliente, int idRistorante) throws RemoteException;

    /**
     * Rimuove un ristorante dalla lista dei preferiti di un cliente.
     * @param idCliente l'ID del cliente
     * @param idRistorante l'ID del ristorante
     * @return <code>true</code> se la rimozione riesce, <code>false</code> altrimenti
     * @throws RemoteException in caso di errore di comunicazione in rete
     */
    boolean rimuoviPreferito(int idCliente, int idRistorante) throws RemoteException;

    /**
     * Restituisce la lista dei ristoranti preferiti di un cliente.
     * @param idCliente l'ID del cliente
     * @return lista di oggetti <code>Ristorante</code>
     * @throws RemoteException in caso di errore di comunicazione in rete
     */
    List<Ristorante> visualizzaPreferiti(int idCliente) throws RemoteException;

    /**
     * Inserisce una nuova recensione scritta da un cliente.
     * @param nuovaRecensione l'oggetto <code>Recensione</code> da salvare
     * @return <code>true</code> se l'inserimento riesce, <code>false</code> altrimenti
     * @throws RemoteException in caso di errore di comunicazione in rete
     */
    boolean aggiungiRecensione(Recensione nuovaRecensione) throws RemoteException;

    /**
     * Modifica una recensione esistente di un cliente.
     * @param idRecensione l'ID della recensione da modificare
     * @param idCliente l'ID del cliente autore
     * @param nuoveStelle il nuovo numero di stelle
     * @param nuovoTesto il nuovo testo della recensione
     * @return <code>true</code> se la modifica riesce, <code>false</code> altrimenti
     * @throws RemoteException in caso di errore di comunicazione in rete
     */
    boolean modificaRecensione(int idRecensione, int idCliente, int nuoveStelle, String nuovoTesto) throws RemoteException;

    /**
     * Elimina una recensione scritta da un cliente.
     * @param idRecensione l'ID della recensione da eliminare
     * @param idCliente l'ID del cliente autore
     * @return <code>true</code> se l'eliminazione riesce, <code>false</code> altrimenti
     * @throws RemoteException in caso di errore di comunicazione in rete
     */
    boolean eliminaRecensione(int idRecensione, int idCliente) throws RemoteException;

    /**
     * Restituisce tutte le recensioni scritte da uno specifico cliente.
     * @param idCliente l'ID del cliente
     * @return lista di oggetti <code>Recensione</code> del cliente
     * @throws RemoteException in caso di errore di comunicazione in rete
     */
    List<Recensione> visualizzaRecensioniPerCliente(int idCliente) throws RemoteException;

    /**
     * Aggiunge un nuovo ristorante creato da un ristoratore.
     * @param nuovoRistorante l'oggetto <code>Ristorante</code> da salvare
     * @param idRistoratore l'ID del ristoratore proprietario
     * @return <code>true</code> se l'inserimento riesce, <code>false</code> altrimenti
     * @throws RemoteException in caso di errore di comunicazione in rete
     */
    boolean aggiungiRistorante(Ristorante nuovoRistorante, int idRistoratore) throws RemoteException;

    /**
     * Salva la risposta del ristoratore a una recensione.
     * @param idRecensione l'ID della recensione
     * @param idRistoratore l'ID del ristoratore
     * @param risposta il testo della risposta
     * @return <code>true</code> se il salvataggio riesce, <code>false</code> altrimenti
     * @throws RemoteException in caso di errore di comunicazione in rete
     */
    boolean rispostaRecensioni(int idRecensione, int idRistoratore, String risposta) throws RemoteException;

    /**
     * Restituisce la lista dei ristoranti inseriti da uno specifico ristoratore.
     * @param idRistoratore l'ID del ristoratore
     * @return lista di oggetti <code>Ristorante</code> gestiti dal ristoratore
     * @throws RemoteException in caso di errore di comunicazione in rete
     */
    List<Ristorante> getRistorantiPerRistoratore(int idRistoratore) throws RemoteException;

    boolean modificaRistorante(Ristorante r, int idRistoratore) throws RemoteException;
    boolean eliminaRistorante(int idRistorante, int idRistoratore) throws RemoteException;
    boolean aggiornaUtente(Utente u, boolean cambiaPassword) throws RemoteException;
}