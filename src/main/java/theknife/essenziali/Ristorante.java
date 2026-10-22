/*
 * Autori del progetto:
 * - Alessandro Frigerio, Matricola: 759926, Sede: CO
 * - Antonio Pardo, Matricola: 760613, Sede: CO
 * - Davide Moretti, Matricola: 762176, Sede: CO
 */
package theknife.essenziali;

import java.io.Serializable;

/**
 * Classe che rappresenta un ristorante all'interno dell'applicazione.
 * <p>
 * Contiene i dati della tabella <code>RistorantiTheKnife</code> e implementa
 * <code>Serializable</code> per passare gli oggetti tra client e server.
 * @author Alessandro Frigerio
 * @author Antonio Pardo
 * @author Davide Moretti
 */
public class Ristorante implements Serializable {

    /**
     * ID per la serializzazione <code>serialVersionUID</code>.
     */
    private static final long serialVersionUID = 1L;

    /**
     * ID univoco del ristorante <code>idRistorante</code>.
     */
    private int idRistorante;

    /**
     * Nome del ristorante <code>nome</code>.
     */
    private String nome;

    /**
     * Nazione in cui si trova il ristorante <code>nazione</code>.
     */
    private String nazione;

    /**
     * Città in cui si trova il ristorante <code>citta</code>.
     */
    private String citta;

    /**
     * Indirizzo del ristorante <code>indirizzo</code>.
     */
    private String indirizzo;

    /**
     * Coordinata di latitudine <code>latitudine</code>.
     */
    private double latitudine;

    /**
     * Coordinata di longitudine <code>longitudine</code>.
     */
    private double longitudine;

    /**
     * Prezzo medio in euro <code>fasciaPrezzo</code>.
     */
    private int fasciaPrezzo;

    /**
     * Indica se il ristorante fa consegne a domicilio <code>delivery</code>.
     */
    private boolean delivery;

    /**
     * Indica se il ristorante accetta prenotazioni online <code>prenotazioneOnline</code>.
     */
    private boolean prenotazioneOnline;

    /**
     * Tipo di cucina del ristorante <code>tipoCucina</code>.
     */
    private String tipoCucina;

    /**
     * ID dell'utente ristoratore proprietario del locale <code>idRistoratore</code> (può essere null).
     */
    private Integer idRistoratore;

    /**
     * Costruttore vuoto della classe <code>Ristorante</code>.
     */
    public Ristorante() {
    }

    /**
     * Costruttore completo per creare un oggetto <code>Ristorante</code> letto dal database.
     * @param idRistorante ID del ristorante
     * @param nome nome del ristorante
     * @param nazione nazione del ristorante
     * @param citta città del ristorante
     * @param indirizzo indirizzo del ristorante
     * @param latitudine latitudine geografica
     * @param longitudine longitudine geografica
     * @param fasciaPrezzo prezzo medio in euro
     * @param delivery true se ha il delivery, false altrimenti
     * @param prenotazioneOnline true se ha la prenotazione online, false altrimenti
     * @param tipoCucina tipo di cucina offerta
     * @param idRistoratore ID del ristoratore proprietario (può essere null)
     */
    public Ristorante(int idRistorante, String nome, String nazione, String citta, String indirizzo,
                      double latitudine, double longitudine, int fasciaPrezzo,
                      boolean delivery, boolean prenotazioneOnline, String tipoCucina, Integer idRistoratore) {
        this.idRistorante = idRistorante;
        this.nome = nome;
        this.nazione = nazione;
        this.citta = citta;
        this.indirizzo = indirizzo;
        this.latitudine = latitudine;
        this.longitudine = longitudine;
        this.fasciaPrezzo = fasciaPrezzo;
        this.delivery = delivery;
        this.prenotazioneOnline = prenotazioneOnline;
        this.tipoCucina = tipoCucina;
        this.idRistoratore = idRistoratore;
    }

    /**
     * Costruttore senza ID ristorante, utile quando un ristoratore crea un nuovo locale da salvare nel DB.
     * @param nome nome del ristorante
     * @param nazione nazione del ristorante
     * @param citta città del ristorante
     * @param indirizzo indirizzo del ristorante
     * @param latitudine latitudine geografica
     * @param longitudine longitudine geografica
     * @param fasciaPrezzo prezzo medio in euro
     * @param delivery true se ha il delivery, false altrimenti
     * @param prenotazioneOnline true se ha la prenotazione online, false altrimenti
     * @param tipoCucina tipo di cucina offerta
     * @param idRistoratore ID del ristoratore che sta creando il ristorante
     */
    public Ristorante(String nome, String nazione, String citta, String indirizzo,
                      double latitudine, double longitudine, int fasciaPrezzo,
                      boolean delivery, boolean prenotazioneOnline, String tipoCucina, Integer idRistoratore) {
        this.nome = nome;
        this.nazione = nazione;
        this.citta = citta;
        this.indirizzo = indirizzo;
        this.latitudine = latitudine;
        this.longitudine = longitudine;
        this.fasciaPrezzo = fasciaPrezzo;
        this.delivery = delivery;
        this.prenotazioneOnline = prenotazioneOnline;
        this.tipoCucina = tipoCucina;
        this.idRistoratore = idRistoratore;
    }

    /**
     * Restituisce l'ID del ristorante.
     * @return il valore di <code>idRistorante</code>
     */
    public int getIdRistorante() {
        return idRistorante;
    }

    /**
     * Imposta l'ID del ristorante.
     * @param idRistorante nuovo valore per <code>idRistorante</code>
     */
    public void setIdRistorante(int idRistorante) {
        this.idRistorante = idRistorante;
    }

    /**
     * Restituisce il nome del ristorante.
     * @return il valore di <code>nome</code>
     */
    public String getNome() {
        return nome;
    }

    /**
     * Imposta il nome del ristorante.
     * @param nome nuovo valore per <code>nome</code>
     */
    public void setNome(String nome) {
        this.nome = nome;
    }

    /**
     * Restituisce la nazione del ristorante.
     * @return il valore di <code>nazione</code>
     */
    public String getNazione() {
        return nazione;
    }

    /**
     * Imposta la nazione del ristorante.
     * @param nazione nuovo valore per <code>nazione</code>
     */
    public void setNazione(String nazione) {
        this.nazione = nazione;
    }

    /**
     * Restituisce la città del ristorante.
     * @return il valore di <code>citta</code>
     */
    public String getCitta() {
        return citta;
    }

    /**
     * Imposta la città del ristorante.
     * @param citta nuovo valore per <code>citta</code>
     */
    public void setCitta(String citta) {
        this.citta = citta;
    }

    /**
     * Restituisce l'indirizzo del ristorante.
     * @return il valore di <code>indirizzo</code>
     */
    public String getIndirizzo() {
        return indirizzo;
    }

    /**
     * Imposta l'indirizzo del ristorante.
     * @param indirizzo nuovo valore per <code>indirizzo</code>
     */
    public void setIndirizzo(String indirizzo) {
        this.indirizzo = indirizzo;
    }

    /**
     * Restituisce la latitudine del ristorante.
     * @return il valore di <code>latitudine</code>
     */
    public double getLatitudine() {
        return latitudine;
    }

    /**
     * Imposta la latitudine del ristorante.
     * @param latitudine nuovo valore per <code>latitudine</code>
     */
    public void setLatitudine(double latitudine) {
        this.latitudine = latitudine;
    }

    /**
     * Restituisce la longitudine del ristorante.
     * @return il valore di <code>longitudine</code>
     */
    public double getLongitudine() {
        return longitudine;
    }

    /**
     * Imposta la longitudine del ristorante.
     * @param longitudine nuovo valore per <code>longitudine</code>
     */
    public void setLongitudine(double longitudine) {
        this.longitudine = longitudine;
    }

    /**
     * Restituisce la fascia di prezzo del ristorante.
     * @return il valore di <code>fasciaPrezzo</code>
     */
    public int getFasciaPrezzo() {
        return fasciaPrezzo;
    }

    /**
     * Imposta la fascia di prezzo del ristorante.
     * @param fasciaPrezzo nuovo valore per <code>fasciaPrezzo</code>
     */
    public void setFasciaPrezzo(int fasciaPrezzo) {
        this.fasciaPrezzo = fasciaPrezzo;
    }

    /**
     * Restituisce true se il ristorante fa consegne a domicilio.
     * @return il valore di <code>delivery</code>
     */
    public boolean isDelivery() {
        return delivery;
    }

    /**
     * Imposta il servizio di consegna a domicilio.
     * @param delivery nuovo valore per <code>delivery</code>
     */
    public void setDelivery(boolean delivery) {
        this.delivery = delivery;
    }

    /**
     * Restituisce true se il ristorante accetta prenotazioni online.
     * @return il valore di <code>prenotazioneOnline</code>
     */
    public boolean isPrenotazioneOnline() {
        return prenotazioneOnline;
    }

    /**
     * Imposta il servizio di prenotazione online.
     * @param prenotazioneOnline nuovo valore per <code>prenotazioneOnline</code>
     */
    public void setPrenotazioneOnline(boolean prenotazioneOnline) {
        this.prenotazioneOnline = prenotazioneOnline;
    }

    /**
     * Restituisce il tipo di cucina del ristorante.
     * @return il valore di <code>tipoCucina</code>
     */
    public String getTipoCucina() {
        return tipoCucina;
    }

    /**
     * Imposta il tipo di cucina del ristorante.
     * @param tipoCucina nuovo valore per <code>tipoCucina</code>
     */
    public void setTipoCucina(String tipoCucina) {
        this.tipoCucina = tipoCucina;
    }

    /**
     * Restituisce l'ID del ristoratore proprietario.
     * @return il valore di <code>idRistoratore</code>, oppure <code>null</code> se non ha un proprietario
     */
    public Integer getIdRistoratore() {
        return idRistoratore;
    }

    /**
     * Imposta l'ID del ristoratore proprietario.
     * @param idRistoratore nuovo valore per <code>idRistoratore</code>
     */
    public void setIdRistoratore(Integer idRistoratore) {
        this.idRistoratore = idRistoratore;
    }

    /**
     * Restituisce una stringa con le informazioni principali del ristorante.
     * @return stringa descrittiva del ristorante
     */
    @Override
    public String toString() {
        return nome + " (" + citta + ", " + nazione + ") - Cucina: " + tipoCucina + " - Prezzo: " + fasciaPrezzo + "€";
    }
}