package TheKnife.Essenziali;



import java.io.Serializable;

/**
 * Classe di dominio (Model) che rappresenta l'entit&agrave; Ristorante all'interno del sistema.
 * <p>
 * Mappa la struttura della tabella relazionale <code>RistorantiTheKnife</code> presente nel database
 * e implementa l'interfaccia <code>Serializable</code> per consentire il trasferimento degli oggetti
 * tra server e client.
 * @author Alessandro Frigerio
 */
public class Ristorante implements Serializable {

    /**
     * Identificativo univoco per la serializzazione della classe <code>serialVersionUID</code>.
     */
    private static final long serialVersionUID = 1L;

    /**
     * Identificativo numerico univoco del ristorante <code>idRistorante</code> (chiave primaria).
     */
    private int idRistorante;

    /**
     * Nome del ristorante <code>nome</code>.
     */
    private String nome;

    /**
     * Stato o nazione <code>nazione</code>.
     */
    private String nazione;

    /**
     * Citta in cui é ubicato il ristorante <code>citta</code>.
     */
    private String citta;

    /**
     * Indirizzo del ristorante <code>indirizzo</code>.
     */
    private String indirizzo;

    /**
     * Latitudine <code>latitudine</code>.
     */
    private double latitudine;

    /**
     * Longitudine <code>longitudine</code>.
     */
    private double longitudine;

    /**
     * Prezzo medio in euro <code>fasciaPrezzo</code>.
     */
    private int fasciaPrezzo;

    /**
     * Disponibilita del servizio di consegna a domicilio <code>delivery</code>.
     */
    private boolean delivery;

    /**
     * Disponibilita del servizio di prenotazione online <code>prenotazioneOnline</code>.
     */
    private boolean prenotazioneOnline;

    /**
     * Tipologia di cucina offerta dal ristorante <code>tipoCucina</code>.
     */
    private String tipoCucina;

    /**
     * Costruttore vuoto di default per la classe <code>Ristorante</code>.
     */
    public Ristorante() {
    }

    /**
     * Costruttore completo per istanziare un oggetto <code>Ristorante</code> gia presente nel database.
     * @param idRistorante identificativo univoco del ristorante
     * @param nome nome del ristorante
     * @param nazione nazione del ristorante
     * @param citta citta del ristorante
     * @param indirizzo indirizzo del ristorante
     * @param latitudine latitudine
     * @param longitudine longitudine
     * @param fasciaPrezzo prezzo medio in euro
     * @param delivery true se offre servizio delivery, false altrimenti
     * @param prenotazioneOnline true se accetta prenotazioni online, false altrimenti
     * @param tipoCucina descrizione della tipologia di cucina
     */
    public Ristorante(int idRistorante, String nome, String nazione, String citta, String indirizzo,
                      double latitudine, double longitudine, int fasciaPrezzo,
                      boolean delivery, boolean prenotazioneOnline, String tipoCucina) {
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
    }

    /**
     * Costruttore senza <code>idRistorante</code> per la creazione di un nuovo ristorante prima del salvataggio sul database.
     * @param nome nome del ristorante
     * @param nazione nazione del ristorante
     * @param citta citta del ristorante
     * @param indirizzo indirizzo fisico del ristorante
     * @param latitudine latitudine
     * @param longitudine longitudine
     * @param fasciaPrezzo prezzo medio in euro
     * @param delivery true se offre servizio delivery, false altrimenti
     * @param prenotazioneOnline true se accetta prenotazioni online, false altrimenti
     * @param tipoCucina descrizione della tipologia di cucina
     */
    public Ristorante(String nome, String nazione, String citta, String indirizzo,
                      double latitudine, double longitudine, int fasciaPrezzo,
                      boolean delivery, boolean prenotazioneOnline, String tipoCucina) {
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
    }

    /**
     * Restituisce l'identificativo univoco del ristorante.
     * @return il valore dell'attributo <code>idRistorante</code>
     */
    public int getIdRistorante() {
        return idRistorante;
    }

    /**
     * Imposta l'identificativo univoco del ristorante.
     * @param idRistorante il nuovo identificativo da assegnare a <code>idRistorante</code>
     */
    public void setIdRistorante(int idRistorante) {
        this.idRistorante = idRistorante;
    }

    /**
     * Restituisce il nome del ristorante.
     * @return il valore dell'attributo <code>nome</code>
     */
    public String getNome() {
        return nome;
    }

    /**
     * Imposta il nome del ristorante.
     * @param nome il nuovo nome da assegnare a <code>nome</code>
     */
    public void setNome(String nome) {
        this.nome = nome;
    }

    /**
     * Restituisce la nazione del ristorante.
     * @return il valore dell'attributo <code>nazione</code>
     */
    public String getNazione() {
        return nazione;
    }

    /**
     * Imposta la nazione del ristorante.
     * @param nazione la nuova nazione da assegnare a <code>nazione</code>
     */
    public void setNazione(String nazione) {
        this.nazione = nazione;
    }

    /**
     * Restituisce la citta del ristorante.
     * @return il valore dell'attributo <code>citta</code>
     */
    public String getCitta() {
        return citta;
    }

    /**
     * Imposta la citta del ristorante.
     * @param citta la nuova citta da assegnare a <code>citta</code>
     */
    public void setCitta(String citta) {
        this.citta = citta;
    }

    /**
     * Restituisce l'indirizzo del ristorante.
     * @return il valore dell'attributo <code>indirizzo</code>
     */
    public String getIndirizzo() {
        return indirizzo;
    }

    /**
     * Imposta l'indirizzo del ristorante.
     * @param indirizzo il nuovo indirizzo da assegnare a <code>indirizzo</code>
     */
    public void setIndirizzo(String indirizzo) {
        this.indirizzo = indirizzo;
    }

    /**
     * Restituisce la latitudine del ristorante.
     * @return il valore dell'attributo <code>latitudine</code>
     */
    public double getLatitudine() {
        return latitudine;
    }

    /**
     * Imposta la latitudine del ristorante.
     * @param latitudine la nuova coordinata da assegnare a <code>latitudine</code>
     */
    public void setLatitudine(double latitudine) {
        this.latitudine = latitudine;
    }

    /**
     * Restituisce la longitudine del ristorante.
     * @return il valore dell'attributo <code>longitudine</code>
     */
    public double getLongitudine() {
        return longitudine;
    }

    /**
     * Imposta la longitudine del ristorante.
     * @param longitudine la nuova coordinata da assegnare a <code>longitudine</code>
     */
    public void setLongitudine(double longitudine) {
        this.longitudine = longitudine;
    }

    /**
     * Restituisce la fascia di prezzo del ristorante.
     * @return il valore dell'attributo <code>fasciaPrezzo</code>
     */
    public int getFasciaPrezzo() {
        return fasciaPrezzo;
    }

    /**
     * Imposta la fascia di prezzo del ristorante.
     * @param fasciaPrezzo il nuovo prezzo medio da assegnare a <code>fasciaPrezzo</code>
     */
    public void setFasciaPrezzo(int fasciaPrezzo) {
        this.fasciaPrezzo = fasciaPrezzo;
    }

    /**
     * Indica se il servizio di delivery é disponibile.
     * @return <code>true</code> se il delivery é attivo, <code>false</code> altrimenti
     */
    public boolean isDelivery() {
        return delivery;
    }

    /**
     * Imposta la disponibilita del servizio di delivery.
     * @param delivery il nuovo stato da assegnare a <code>delivery</code>
     */
    public void setDelivery(boolean delivery) {
        this.delivery = delivery;
    }

    /**
     * Indica se la prenotazione online é disponibile.
     * @return <code>true</code> se la prenotazione online é attiva, <code>false</code> altrimenti
     */
    public boolean isPrenotazioneOnline() {
        return prenotazioneOnline;
    }

    /**
     * Imposta la disponibilita della prenotazione online.
     * @param prenotazioneOnline il nuovo stato da assegnare a <code>prenotazioneOnline</code>
     */
    public void setPrenotazioneOnline(boolean prenotazioneOnline) {
        this.prenotazioneOnline = prenotazioneOnline;
    }

    /**
     * Restituisce il tipo di cucina del ristorante.
     * @return il valore dell'attributo <code>tipoCucina</code>
     */
    public String getTipoCucina() {
        return tipoCucina;
    }

    /**
     * Imposta il tipo di cucina del ristorante.
     * @param tipoCucina la nuova tipologia da assegnare a <code>tipoCucina</code>
     */
    public void setTipoCucina(String tipoCucina) {
        this.tipoCucina = tipoCucina;
    }

    /**
     * Restituisce una rappresentazione testuale sintetica dell'oggetto <code>Ristorante</code>.
     * @return stringa contenente i dati principali del ristorante
     */
    @Override
    public String toString() {
        return nome + " (" + citta + ", " + nazione + ") - Cucina: " + tipoCucina + " - Prezzo: " + fasciaPrezzo + "€";
    }
}
