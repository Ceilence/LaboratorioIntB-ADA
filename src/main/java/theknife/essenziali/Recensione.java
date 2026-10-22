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
 * Classe che rappresenta una recensione scritta da un cliente per un ristorante.
 * <p>
 * Mappa la tabella <code>Recensioni</code> del database e implementa <code>Serializable</code>.
 * @author Alessandro Frigerio
 * @author Antonio Pardo
 * @author Davide Moretti
 */
public class Recensione implements Serializable {

    /**
     * ID per la serializzazione <code>serialVersionUID</code>.
     */
    private static final long serialVersionUID = 1L;

    /**
     * ID univoco della recensione <code>idRecensione</code>.
     */
    private int idRecensione;

    /**
     * ID del cliente che ha scritto la recensione <code>idUtente</code>.
     */
    private int idUtente;

    /**
     * ID del ristorante recensito <code>idRistorante</code>.
     */
    private int idRistorante;

    /**
     * Voto in stelle da 1 a 5 <code>stelle</code>.
     */
    private int stelle;

    /**
     * Testo del commento scritto dal cliente <code>testo</code>.
     */
    private String testo;

    /**
     * Data in cui è stata scritta la recensione <code>dataRecensione</code>.
     */
    private Date dataRecensione;

    /**
     * Risposta del ristoratore alla recensione <code>rispostaRistoratore</code> (può essere null).
     */
    private String rispostaRistoratore;

    /**
     * Costruttore vuoto della classe <code>Recensione</code>.
     */
    public Recensione() {
    }

    /**
     * Costruttore completo per creare un oggetto <code>Recensione</code> letto dal database.
     * @param idRecensione ID della recensione
     * @param idUtente ID dell'utente autore
     * @param idRistorante ID del ristorante recensito
     * @param stelle voto da 1 a 5 stelle
     * @param testo commento della recensione
     * @param dataRecensione data della recensione
     * @param rispostaRistoratore risposta del ristoratore (oppure null)
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
     * Costruttore per creare una nuova recensione prima di salvarla nel database.
     * @param idUtente ID dell'utente autore
     * @param idRistorante ID del ristorante recensito
     * @param stelle voto da 1 a 5 stelle
     * @param testo commento della recensione
     * @param dataRecensione data della recensione
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
     * Restituisce l'ID della recensione.
     * @return il valore di <code>idRecensione</code>
     */
    public int getIdRecensione() {
        return idRecensione;
    }

    /**
     * Imposta l'ID della recensione.
     * @param idRecensione nuovo valore per <code>idRecensione</code>
     */
    public void setIdRecensione(int idRecensione) {
        this.idRecensione = idRecensione;
    }

    /**
     * Restituisce l'ID dell'utente che ha scritto la recensione.
     * @return il valore di <code>idUtente</code>
     */
    public int getIdUtente() {
        return idUtente;
    }

    /**
     * Imposta l'ID dell'utente autore.
     * @param idUtente nuovo valore per <code>idUtente</code>
     */
    public void setIdUtente(int idUtente) {
        this.idUtente = idUtente;
    }

    /**
     * Restituisce l'ID del ristorante recensito.
     * @return il valore di <code>idRistorante</code>
     */
    public int getIdRistorante() {
        return idRistorante;
    }

    /**
     * Imposta l'ID del ristorante recensito.
     * @param idRistorante nuovo valore per <code>idRistorante</code>
     */
    public void setIdRistorante(int idRistorante) {
        this.idRistorante = idRistorante;
    }

    /**
     * Restituisce il numero di stelle della recensione.
     * @return il valore di <code>stelle</code>
     */
    public int getStelle() {
        return stelle;
    }

    /**
     * Imposta il numero di stelle della recensione.
     * @param stelle nuovo valore per <code>stelle</code>
     */
    public void setStelle(int stelle) {
        this.stelle = stelle;
    }

    /**
     * Restituisce il testo della recensione.
     * @return il valore di <code>testo</code>
     */
    public String getTesto() {
        return testo;
    }

    /**
     * Imposta il testo della recensione.
     * @param testo nuovo valore per <code>testo</code>
     */
    public void setTesto(String testo) {
        this.testo = testo;
    }

    /**
     * Restituisce la data della recensione.
     * @return il valore di <code>dataRecensione</code>
     */
    public Date getDataRecensione() {
        return dataRecensione;
    }

    /**
     * Imposta la data della recensione.
     * @param dataRecensione nuovo valore per <code>dataRecensione</code>
     */
    public void setDataRecensione(Date dataRecensione) {
        this.dataRecensione = dataRecensione;
    }

    /**
     * Restituisce la risposta del ristoratore.
     * @return il valore di <code>rispostaRistoratore</code>, oppure <code>null</code> se non c'è risposta
     */
    public String getRispostaRistoratore() {
        return rispostaRistoratore;
    }

    /**
     * Imposta la risposta del ristoratore.
     * @param rispostaRistoratore nuovo valore per <code>rispostaRistoratore</code>
     */
    public void setRispostaRistoratore(String rispostaRistoratore) {
        this.rispostaRistoratore = rispostaRistoratore;
    }

    /**
     * Restituisce una stringa con i dati principali della recensione.
     * @return stringa con stelle, testo e data
     */
    @Override
    public String toString() {
        return stelle + "/5 stelle - \"" + testo + "\" (" + dataRecensione + ")";
    }
}