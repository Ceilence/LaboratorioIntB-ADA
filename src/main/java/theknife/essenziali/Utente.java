/*
 * Autori del progetto:
 * - Alessandro Frigerio, Matricola: 759926, Sede: CO
 * - Antonio Pardo, Matricola: 760613, Sede: CO
 * - Davide Moretti, Matricola: 762176, Sede: CO
 */
package theknife.essenziali;

import java.io.Serializable;
import java.sql.Date;

/**
 * Classe che rappresenta l'entità Utente all'interno del sistema.
 * <p>
 * Mappa la struttura della tabella relazionale <code>Utenti</code> del database e gestisce
 * sia i profili di tipo cliente sia quelli di tipo ristoratore tramite l'attributo <code>ruolo</code>.
 * Implementa <code>Serializable</code> per il trasferimento dati tra client e server.
 * @author Alessandro Frigerio
 * @author Antonio Pardo
 * @author Davide Moretti
 */
public class Utente implements Serializable {

    /**
     * Identificativo univoco per la serializzazione della classe <code>serialVersionUID</code>.
     */
    private static final long serialVersionUID = 1L;

    /**
     * Identificativo numerico univoco dell'utente <code>idUtente</code> (chiave primaria).
     */
    private int idUtente;

    /**
     * Nome dell'utente <code>nome</code>.
     */
    private String nome;

    /**
     * Cognome dell'utente <code>cognome</code>.
     */
    private String cognome;

    /**
     * Nome utente univoco utilizzato per l'autenticazione <code>username</code>.
     */
    private String username;

    /**
     * Password associata all'account dell'utente <code>password</code>.
     */
    private String password;

    /**
     * Data di nascita dell'utente <code>dataNascita</code>.
     */
    private Date dataNascita;

    /**
     * Città o indirizzo di domicilio dell'utente <code>luogoDomicilio</code>.
     */
    private String luogoDomicilio;

    /**
     * Ruolo dell'utente nel sistema <code>ruolo</code>.
     */
    private String ruolo;

    /**
     * Costruttore vuoto di default per la classe <code>Utente</code>.
     */
    public Utente() {
    }

    /**
     * Costruttore completo per istanziare un oggetto <code>Utente</code> giá registrato nel database.
     * @param idUtente identificativo univoco dell'utente
     * @param nome nome dell'utente
     * @param cognome cognome dell'utente
     * @param username username univoco dell'utente
     * @param password password cifrata dell'utente
     * @param dataNascita data di nascita dell'utente
     * @param luogoDomicilio luogo di domicilio dell'utente
     * @param ruolo ruolo dell'utente nel sistema (cliente o ristoratore)
     */
    public Utente(int idUtente, String nome, String cognome, String username, String password,
                  Date dataNascita, String luogoDomicilio, String ruolo) {
        this.idUtente = idUtente;
        this.nome = nome;
        this.cognome = cognome;
        this.username = username;
        this.password = password;
        this.dataNascita = dataNascita;
        this.luogoDomicilio = luogoDomicilio;
        this.ruolo = ruolo;
    }

    /**
     * Costruttore senza <code>idUtente</code> per la registrazione di un nuovo utente prima del salvataggio sul database.
     * @param nome nome dell'utente
     * @param cognome cognome dell'utente
     * @param username username univoco dell'utente
     * @param password password cifrata dell'utente
     * @param dataNascita data di nascita dell'utente
     * @param luogoDomicilio luogo di domicilio dell'utente
     * @param ruolo ruolo dell'utente nel sistema (cliente o ristoratore)
     */
    public Utente(String nome, String cognome, String username, String password,
                  Date dataNascita, String luogoDomicilio, String ruolo) {
        this.nome = nome;
        this.cognome = cognome;
        this.username = username;
        this.password = password;
        this.dataNascita = dataNascita;
        this.luogoDomicilio = luogoDomicilio;
        this.ruolo = ruolo;
    }

    /**
     * Restituisce l'identificativo univoco dell'utente.
     * @return il valore dell'attributo <code>idUtente</code>
     */
    public int getIdUtente() {
        return idUtente;
    }

    /**
     * Imposta l'identificativo univoco dell'utente.
     * @param idUtente il nuovo identificativo da assegnare a <code>idUtente</code>
     */
    public void setIdUtente(int idUtente) {
        this.idUtente = idUtente;
    }

    /**
     * Restituisce il nome dell'utente.
     * @return il valore dell'attributo <code>nome</code>
     */
    public String getNome() {
        return nome;
    }

    /**
     * Imposta il nome dell'utente.
     * @param nome il nuovo nome da assegnare a <code>nome</code>
     */
    public void setNome(String nome) {
        this.nome = nome;
    }

    /**
     * Restituisce il cognome dell'utente.
     * @return il valore dell'attributo <code>cognome</code>
     */
    public String getCognome() {
        return cognome;
    }

    /**
     * Imposta il cognome dell'utente.
     * @param cognome il nuovo cognome da assegnare a <code>cognome</code>
     */
    public void setCognome(String cognome) {
        this.cognome = cognome;
    }

    /**
     * Restituisce lo username dell'utente.
     * @return il valore dell'attributo <code>username</code>
     */
    public String getUsername() {
        return username;
    }

    /**
     * Imposta lo username dell'utente.
     * @param username il nuovo username da assegnare a <code>username</code>
     */
    public void setUsername(String username) {
        this.username = username;
    }

    /**
     * Restituisce la password cifrata dell'utente.
     * @return il valore dell'attributo <code>password</code>
     */
    public String getPassword() {
        return password;
    }

    /**
     * Imposta la password dell'utente.
     * @param password la nuova password da assegnare a <code>password</code>
     */
    public void setPassword(String password) {
        this.password = password;
    }

    /**
     * Restituisce la data di nascita dell'utente.
     * @return il valore dell'attributo <code>dataNascita</code>
     */
    public Date getDataNascita() {
        return dataNascita;
    }

    /**
     * Imposta la data di nascita dell'utente.
     * @param dataNascita la nuova data da assegnare a <code>dataNascita</code>
     */
    public void setDataNascita(Date dataNascita) {
        this.dataNascita = dataNascita;
    }

    /**
     * Restituisce il luogo di domicilio dell'utente.
     * @return il valore dell'attributo <code>luogoDomicilio</code>
     */
    public String getLuogoDomicilio() {
        return luogoDomicilio;
    }

    /**
     * Imposta il luogo di domicilio dell'utente.
     * @param luogoDomicilio il nuovo domicilio da assegnare a <code>luogoDomicilio</code>
     */
    public void setLuogoDomicilio(String luogoDomicilio) {
        this.luogoDomicilio = luogoDomicilio;
    }

    /**
     * Restituisce il ruolo dell'utente.
     * @return il valore dell'attributo <code>ruolo</code>
     */
    public String getRuolo() {
        return ruolo;
    }

    /**
     * Imposta il ruolo dell'utente.
     * @param ruolo il nuovo ruolo da assegnare a <code>ruolo</code>
     */
    public void setRuolo(String ruolo) {
        this.ruolo = ruolo;
    }

    /**
     * Crea un oggetto <code>Utente</code> temporaneo per chi accede con il tasto Guest.
     * <p>
     * L'utente Guest non viene salvato nel database, ha ID pari a <code>-1</code>,
     * ruolo impostato a <code>guest</code> e memorizza solo il luogo scelto all'avvio.
     * @param luogoIndicato il nome della città o luogo inserito dall'utente Guest
     * @return un oggetto <code>Utente</code> configurato come Guest
     */
    public static Utente creaGuest(String luogoIndicato) {
        return new Utente(-1, "Ospite", "Guest", "guest", "", null, luogoIndicato, "guest");
    }

    /**
     * Controlla se l'utente attuale sta usando l'applicazione come Guest (non registrato).
     * @return <code>true</code> se il ruolo è <code>guest</code>, <code>false</code> altrimenti
     */
    public boolean isGuest() {
        return "guest".equalsIgnoreCase(this.ruolo);
    }

    /**
     * Controlla se l'utente attuale è un cliente registrato.
     * @return <code>true</code> se il ruolo è <code>cliente</code>, <code>false</code> altrimenti
     */
    public boolean isCliente() {
        return "cliente".equalsIgnoreCase(this.ruolo);
    }

    /**
     * Controlla se l'utente attuale è un ristoratore/gestore registrato.
     * @return <code>true</code> se il ruolo è <code>ristoratore</code> o <code>gestore</code>, <code>false</code> altrimenti
     */
    public boolean isRistoratore() {
        return "ristoratore".equalsIgnoreCase(this.ruolo) || "gestore".equalsIgnoreCase(this.ruolo);
    }

    // Metodo toString per testing.
    @Override
    public String toString() {
        return username + " (" + nome + " " + cognome + " - " + ruolo + ")";
    }

}
