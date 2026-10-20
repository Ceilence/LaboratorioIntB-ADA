package theknife.essenziali;

import java.io.Serializable;
import java.sql.Date;

/**
 * Classe che rappresenta l'entitá Recensione all'interno del sistema.
 * <p>
 * Mappa la struttura della tabella relazionale <code>Recensioni</code> presente nel database,
 * associando la valutazione di un utente a uno specifico ristorante e includendo l'eventuale
 * risposta del ristoratore. Implementa <code>Serializable</code> per il trasferimento dati tra client e server.
 * @author Alessandro Frigerio, Antonio Pardo, Davide Moretti
 */
public class Recensione implements Serializable {

    /**
     * Identificativo univoco per la serializzazione della classe <code>serialVersionUID</code>.
     */
    private static final long serialVersionUID = 1L;

    /**
     * Identificativo numerico univoco della recensione <code>idRecensione</code> (chiave primaria).
     */
    private int idRecensione;

    /**
     * Identificativo dell'utente autore della recensione <code>idUtente</code> (chiave esterna).
     */
    private int idUtente;

    /**
     * Identificativo del ristorante recensito <code>idRistorante</code> (chiave esterna).
     */
    private int idRistorante;

    /**
     * Valutazione numerica espressa in stelle da 1 a 5 <code>stelle</code>.
     */
    private int stelle;

    /**
     * Contenuto testuale della recensione scritta dal cliente <code>testo</code>.
     */
    private String testo;

    /**
     * Data di pubblicazione della recensione <code>dataRecensione</code>.
     */
    private Date dataRecensione;

    /**
     * Eventuale replica del gestore del ristorante alla recensione <code>rispostaRistoratore</code>.
     */
    private String rispostaRistoratore;

    /**
     * Costruttore vuoto di default per la classe <code>Recensione</code>.
     */
    public Recensione() {
    }

    /**
     * Costruttore completo per istanziare un oggetto <code>Recensione</code> letto dal database.
     * @param idRecensione identificativo univoco della recensione
     * @param idUtente identificativo dell'utente autore
     * @param idRistorante identificativo del ristorante recensito
     * @param stelle numero di stelle assegnate (da 1 a 5)
     * @param testo commento testuale della recensione
     * @param dataRecensione data in cui é stata scritta la recensione
     * @param rispostaRistoratore eventuale risposta del gestore
     */
    public Recensione(int idRecensione, int idUtente, int idRistorante, int stelle,
                      String testo, Date dataRecensione, String rispostaRistoratore) {
        this.idRecensione = idRecensione;
        this.idUtente = idUtente;
        this.idRistorante = idRistorante;
        this.stelle = stelle;
        this.testo = testo;
        this.dataRecensione = dataRecensione;
        this.rispostaRistoratore = rispostaRistoratore;
    }

    /**
     * Costruttore senza <code>idRecensione</code> e senza risposta per creare una nuova recensione da salvare sul database.
     * @param idUtente identificativo dell'utente autore
     * @param idRistorante identificativo del ristorante recensito
     * @param stelle numero di stelle assegnate (da 1 a 5)
     * @param testo commento testuale della recensione
     * @param dataRecensione data in cui é stata scritta la recensione
     */
    public Recensione(int idUtente, int idRistorante, int stelle, String testo, Date dataRecensione) {
        this.idUtente = idUtente;
        this.idRistorante = idRistorante;
        this.stelle = stelle;
        this.testo = testo;
        this.dataRecensione = dataRecensione;
        this.rispostaRistoratore = null;
    }

    /**
     * Restituisce l'identificativo univoco della recensione.
     * @return il valore dell'attributo <code>idRecensione</code>
     */
    public int getIdRecensione() {
        return idRecensione;
    }

    /**
     * Imposta l'identificativo univoco della recensione.
     * @param idRecensione il nuovo identificativo da assegnare a <code>idRecensione</code>
     */
    public void setIdRecensione(int idRecensione) {
        this.idRecensione = idRecensione;
    }

    /**
     * Restituisce l'identificativo dell'utente autore della recensione.
     * @return il valore dell'attributo <code>idUtente</code>
     */
    public int getIdUtente() {
        return idUtente;
    }

    /**
     * Imposta l'identificativo dell'utente autore della recensione.
     * @param idUtente il nuovo identificativo utente da assegnare a <code>idUtente</code>
     */
    public void setIdUtente(int idUtente) {
        this.idUtente = idUtente;
    }

    /**
     * Restituisce l'identificativo del ristorante recensito.
     * @return il valore dell'attributo <code>idRistorante</code>
     */
    public int getIdRistorante() {
        return idRistorante;
    }

    /**
     * Imposta l'identificativo del ristorante recensito.
     * @param idRistorante il nuovo identificativo ristorante da assegnare a <code>idRistorante</code>
     */
    public void setIdRistorante(int idRistorante) {
        this.idRistorante = idRistorante;
    }

    /**
     * Restituisce il numero di stelle assegnate nella recensione.
     * @return il valore dell'attributo <code>stelle</code>
     */
    public int getStelle() {
        return stelle;
    }

    /**
     * Imposta il numero di stelle assegnate nella recensione.
     * @param stelle il nuovo numero di stelle da assegnare a <code>stelle</code>
     */
    public void setStelle(int stelle) {
        this.stelle = stelle;
    }

    /**
     * Restituisce il testo della recensione.
     * @return il valore dell'attributo <code>testo</code>
     */
    public String getTesto() {
        return testo;
    }

    /**
     * Imposta il testo della recensione.
     * @param testo il nuovo commento testuale da assegnare a <code>testo</code>
     */
    public void setTesto(String testo) {
        this.testo = testo;
    }

    /**
     * Restituisce la data di pubblicazione della recensione.
     * @return il valore dell'attributo <code>dataRecensione</code>
     */
    public Date getDataRecensione() {
        return dataRecensione;
    }

    /**
     * Imposta la data di pubblicazione della recensione.
     * @param dataRecensione la nuova data da assegnare a <code>dataRecensione</code>
     */
    public void setDataRecensione(Date dataRecensione) {
        this.dataRecensione = dataRecensione;
    }

    /**
     * Restituisce la risposta del ristoratore alla recensione.
     * @return il valore dell'attributo <code>rispostaRistoratore</code>, oppure <code>null</code> se non presente
     */
    public String getRispostaRistoratore() {
        return rispostaRistoratore;
    }

    /**
     * Imposta la risposta del ristoratore alla recensione.
     * @param rispostaRistoratore la risposta del gestore da assegnare a <code>rispostaRistoratore</code>
     */
    public void setRispostaRistoratore(String rispostaRistoratore) {
        this.rispostaRistoratore = rispostaRistoratore;
    }

    /**
     * Restituisce una rappresentazione testuale sintetica dell'oggetto <code>Recensione</code>.
     * @return stringa contenente valutazione in stelle, testo e data della recensione
     */
    @Override
    public String toString() {
        return stelle + "/5 stelle - \"" + testo + "\" (" + dataRecensione + ")";
    }
}
