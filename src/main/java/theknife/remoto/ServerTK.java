/*
 * Autori del progetto:
 * Alessandro Frigerio, Matricola: 759926, Sede: CO
 * Antonio Pardo, Matricola: 760613, Sede: CO
 * Davide Moretti, Matricola: 762176, Sede: CO
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
import theknife.gui.StileTheKnife;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.io.File;
import java.net.URL;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.UnicastRemoteObject;
import java.sql.Connection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Classe che implementa il modulo <code>serverTK</code> della piattaforma TheKnife.
 * <p>
 * All'avvio richiede tramite interfaccia grafica l'host e le credenziali del database PostgreSQL,
 * avvia il registro RMI e rimane in attesa di richieste concorrenti da parte dei client <code>clientTK</code>.
 *
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
    private final java.util.concurrent.atomic.AtomicLong versioneDati = new java.util.concurrent.atomic.AtomicLong(1);

    /** Mappa per salvare in cache le immagini già caricate e ridimensionate. */
    private static final Map<String, ImageIcon> CACHE_IMMAGINI = new HashMap<>();

    /**
     * Costruttore che inizializza l'oggetto remoto <code>ServerTK</code> e i relativi DAO.
     *
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
     *
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
     *
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
     *
     * @param nomeRistorante     nome o parola chiave
     * @param locazione          città o nazione
     * @param tipoCucina         tipo di cucina
     * @param prezzoMin          prezzo minimo
     * @param prezzoMax          prezzo massimo
     * @param delivery           filtro delivery
     * @param prenotazioneOnline filtro prenotazione online
     * @param mediaStelleMin     filtro media stelle
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
     *
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
     *
     * @param idRistorante l'ID del ristorante
     * @return lista di oggetti <code>Recensione</code>
     * @throws RemoteException in caso di errore RMI
     */
    @Override
    public List<Recensione> visualizzaRecensioni(int idRistorante) throws RemoteException {
        return recensioneDAO.visualizzaRecensioni(idRistorante);
    }

    /**
     * Restituisce il riepilogo (numero recensioni e media stelle) di un ristorante.
     *
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
     *
     * @param idCliente    l'ID del cliente
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
     *
     * @param idCliente    l'ID del cliente
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
     *
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
     *
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
     *
     * @param idRecensione l'ID della recensione
     * @param idCliente    l'ID del cliente
     * @param nuoveStelle  il nuovo voto
     * @param nuovoTesto   il nuovo commento
     * @return <code>true</code> se modificata, <code>false</code> altrimenti
     * @throws RemoteException in caso di errore RMI
     */
    @Override
    public synchronized boolean modificaRecensione(int idRecensione, int idCliente, int nuoveStelle, String nuovoTesto) throws RemoteException {
        return recensioneDAO.modificaRecensione(idRecensione, idCliente, nuoveStelle, nuovoTesto);
    }

    /**
     * Elimina una recensione di un cliente in modo sincronizzato.
     *
     * @param idRecensione l'ID della recensione
     * @param idCliente    l'ID del cliente
     * @return <code>true</code> se eliminata, <code>false</code> altrimenti
     * @throws RemoteException in caso di errore RMI
     */
    @Override
    public synchronized boolean eliminaRecensione(int idRecensione, int idCliente) throws RemoteException {
        return recensioneDAO.eliminaRecensione(idRecensione, idCliente);
    }

    /**
     * Restituisce tutte le recensioni scritte da un cliente.
     *
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
     *
     * @param nuovoRistorante l'oggetto <code>Ristorante</code> da inserire
     * @param idRistoratore   l'ID del ristoratore proprietario
     * @return <code>true</code> se inserito, <code>false</code> altrimenti
     * @throws RemoteException in caso di errore RMI
     */
    @Override
    public synchronized boolean aggiungiRistorante(Ristorante nuovoRistorante, int idRistoratore) throws RemoteException {
        return ristoranteDAO.aggiungiRistorante(nuovoRistorante, idRistoratore);
    }

    /**
     * Registra la risposta del ristoratore a una recensione in modo sincronizzato.
     *
     * @param idRecensione  l'ID della recensione
     * @param idRistoratore l'ID del ristoratore
     * @param risposta      il testo della risposta
     * @return <code>true</code> se registrata, <code>false</code> altrimenti
     * @throws RemoteException in caso di errore RMI
     */
    @Override
    public synchronized boolean rispostaRecensioni(int idRecensione, int idRistoratore, String risposta) throws RemoteException {
        return recensioneDAO.rispostaRecensioni(idRecensione, idRistoratore, risposta);
    }

    /**
     * Restituisce i ristoranti gestiti da uno specifico ristoratore.
     *
     * @param idRistoratore l'ID del ristoratore
     * @return lista dei suoi ristoranti
     * @throws RemoteException in caso di errore RMI
     */
    @Override
    public List<Ristorante> getRistorantiPerRistoratore(int idRistoratore) throws RemoteException {
        return ristoranteDAO.getRistorantiPerRistoratore(idRistoratore);
    }

    /**
     * Aggiorna i dati di un ristorante esistente nel database in modo sincronizzato,
     * verificando che l'utente richiedente sia l'effettivo ristoratore proprietario.
     *
     * @param r             L'oggetto <code>Ristorante</code> contenente i dati aggiornati.
     * @param idRistoratore L'ID del ristoratore che richiede la modifica.
     * @return <code>true</code> se la modifica è andata a buon fine, <code>false</code> altrimenti.
     * @throws RemoteException in caso di errori di comunicazione RMI.
     */
    @Override
    public synchronized boolean modificaRistorante(Ristorante r, int idRistoratore) throws RemoteException {
        return ristoranteDAO.modificaRistorante(r, idRistoratore);
    }

    /**
     * Elimina un ristorante dal database in modo sincronizzato,
     * assicurandosi che il richiedente sia il ristoratore proprietario.
     *
     * @param idRistorante  L'identificativo univoco del ristorante da eliminare.
     * @param idRistoratore L'ID del ristoratore che richiede l'eliminazione.
     * @return <code>true</code> se l'eliminazione è avvenuta con successo, <code>false</code> altrimenti.
     * @throws RemoteException in caso di errori di comunicazione RMI.
     */
    @Override
    public synchronized boolean eliminaRistorante(int idRistorante, int idRistoratore) throws RemoteException {
        return ristoranteDAO.eliminaRistorante(idRistorante, idRistoratore);
    }

    /**
     * Aggiorna il profilo di un utente nel database in modo sincronizzato.
     * Permette di modificare i dati personali e le credenziali di accesso.
     *
     * @param u              L'oggetto <code>Utente</code> con i dati aggiornati.
     * @param cambiaPassword Flag booleano; se <code>true</code> indica che l'utente ha richiesto
     *                       l'aggiornamento della password e che questa deve essere salvata.
     * @return <code>true</code> se il profilo è stato aggiornato correttamente, <code>false</code> altrimenti.
     * @throws RemoteException in caso di errori di comunicazione RMI.
     */
    @Override
    public synchronized boolean aggiornaUtente(Utente u, boolean cambiaPassword) throws RemoteException {
        return utenteDAO.aggiornaUtente(u, cambiaPassword);
    }

    /**
     * Restituisce la versione corrente dei dati gestiti dal server.
     * Questo metodo è utile per i client per verificare se ci sono stati aggiornamenti.
     *
     * @return un intero (<code>long</code>) che rappresenta il numero di versione attuale dei dati.
     * @throws RemoteException in caso di errori di comunicazione RMI.
     */
    @Override
    public long getVersioneDati() throws RemoteException {
        return versioneDati.get();
    }

    /**
     * Metodo principale di avvio di <code>serverTK</code>.
     * <p>
     * Mostra una finestra grafica in cui premendo Invio sui campi Host e Username vengono
     * confermati i valori predefiniti (<code>localhost</code> e <code>postgres</code>), mentre
     * la password viene richiesta obbligatoriamente. Verifica la connessione al DB e avvia il
     * registro RMI sulla porta <code>1099</code>.
     *
     * @param args argomenti da riga di comando
     */
    public static void main(String[] args) {
        SwingUtilities.invokeLater(ServerTK::mostraFinestraAvvioServer);
    }

    /**
     * Costruisce e visualizza l'interfaccia grafica di configurazione del ServerTK.
     */
    private static void mostraFinestraAvvioServer() {
        JFrame frame = new JFrame("TheKnife - Avvio ServerTK");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(460, 360);
        frame.setLocationRelativeTo(null);
        frame.setResizable(false);

        JPanel mainPanel = new JPanel(new BorderLayout(10, 10));
        mainPanel.setBorder(new EmptyBorder(15, 20, 15, 20));

        JLabel lblTitolo = new JLabel("Configurazione database PostgreSQL", SwingConstants.CENTER);
        lblTitolo.setFont(new Font("SansSerif", Font.BOLD, 16));
        mainPanel.add(lblTitolo, BorderLayout.NORTH);

        // Pannello centrale con i 3 campi
        JPanel formPanel = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(6, 6, 6, 6);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        // Host
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.weightx = 0.35;
        formPanel.add(new JLabel("Host DB [Invio = localhost]:"), gbc);

        gbc.gridx = 1;
        gbc.weightx = 0.65;
        JTextField txtHost = new JTextField("localhost");
        formPanel.add(txtHost, gbc);

        // Username
        gbc.gridx = 0;
        gbc.gridy = 1;
        gbc.weightx = 0.35;
        formPanel.add(new JLabel("Username [Invio = postgres]:"), gbc);

        gbc.gridx = 1;
        gbc.weightx = 0.65;
        JTextField txtUser = new JTextField("postgres");
        formPanel.add(txtUser, gbc);

        // Password
        gbc.gridx = 0;
        gbc.gridy = 2;
        gbc.weightx = 0.35;
        formPanel.add(new JLabel("Password DB (obbligatoria):"), gbc);

        gbc.gridx = 1;
        gbc.weightx = 0.65;
        JPasswordField txtPass = new JPasswordField();
        formPanel.add(txtPass, gbc);

        mainPanel.add(formPanel, BorderLayout.CENTER);

        // Pulsanti
        JPanel bottomPanel = new JPanel(new BorderLayout(8, 8));

        JTextArea areaLog = new JTextArea(4, 30);
        areaLog.setEditable(false);
        areaLog.setFont(new Font("Monospaced", Font.PLAIN, 12));
        areaLog.setText("Premi invio su Host e Username per i valori standard,\ninserisci la password e premi invio per avviare.");
        JScrollPane scrollLog = new JScrollPane(areaLog);
        bottomPanel.add(scrollLog, BorderLayout.CENTER);

        JPanel btnPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        JButton btnAvvia = new JButton("Avvia server");
        JButton btnChiudi = new JButton("Esci");
        btnPanel.add(btnAvvia);
        btnPanel.add(btnChiudi);
        bottomPanel.add(btnPanel, BorderLayout.SOUTH);

        mainPanel.add(bottomPanel, BorderLayout.SOUTH);
        frame.setContentPane(mainPanel);

        // Compilazione automatica
        txtHost.addActionListener(e -> {
            if (txtHost.getText().trim().isEmpty()) {
                txtHost.setText("localhost");
            }
            txtUser.requestFocusInWindow();
            txtUser.selectAll();
        });

        txtUser.addActionListener(e -> {
            if (txtUser.getText().trim().isEmpty()) {
                txtUser.setText("postgres");
            }
            txtPass.requestFocusInWindow();
        });

        // Connessione e avvio
        Runnable azioneConnetti = () -> {
            String host = txtHost.getText().trim();
            if (host.isEmpty()) {
                host = "localhost";
                txtHost.setText(host);
            }

            String dbUser = txtUser.getText().trim();
            if (dbUser.isEmpty()) {
                dbUser = "postgres";
                txtUser.setText(dbUser);
            }

            String dbPass = new String(txtPass.getPassword());
            if (dbPass.isEmpty()) {
                JOptionPane.showMessageDialog(frame,
                        "Devi inserire la password del database per procedere.",
                        "Password richiesta", JOptionPane.WARNING_MESSAGE);
                txtPass.requestFocusInWindow();
                return;
            }

            btnAvvia.setEnabled(false);
            txtHost.setEnabled(false);
            txtUser.setEnabled(false);
            txtPass.setEnabled(false);
            areaLog.setText(" Connessione a PostgreSQL su '" + host + "' con utente '" + dbUser + "'...");

            final String finalHost = host;
            final String finalUser = dbUser;

            // Connessione e avvio RMI in un thread separato per non bloccare la GUI
            new Thread(() -> {
                DatabaseManager.configuraDatabase(finalHost, finalUser, dbPass);

                try (Connection testConn = DatabaseManager.connect()) {
                    if (testConn != null) {
                        SwingUtilities.invokeLater(() ->
                                areaLog.append("\nConnessione al DB stabilita\nControllo dati CSV in corso...")
                        );

                        // Controlla se serve popolare il DB dal CSV
                        ImportatoreCSV.importaDati("../src/risorse/michelin_my_maps.csv");

                        ServerTK server = new ServerTK();
                        Registry registry = LocateRegistry.createRegistry(1099);
                        registry.rebind("TheKnifeService", server);

                        SwingUtilities.invokeLater(() -> {
                            areaLog.append("\nServerTK attivo sulla porta 1099 e in attesa del client.");
                            btnChiudi.setText("Arresta Server");
                        });
                    } else {
                        ripristinaCampiPerErrore(frame, txtHost, txtUser, txtPass, btnAvvia, areaLog,
                                "Password errata o database non raggiungibile su '" + finalHost + "'.");
                    }
                } catch (Exception ex) {
                    ripristinaCampiPerErrore(frame, txtHost, txtUser, txtPass, btnAvvia, areaLog,
                            "Errore durante l'avvio: " + ex.getMessage());
                }
            }).start();
        };

        txtPass.addActionListener(e -> azioneConnetti.run());
        btnAvvia.addActionListener(e -> azioneConnetti.run());
        btnChiudi.addActionListener(e -> System.exit(0));

        StileTheKnife.applicaIconaFinestra(frame);
        frame.setVisible(true);
        txtHost.requestFocusInWindow();
        txtHost.selectAll();
    }

    /**
     * Riabilita i campi della finestra in caso di password errata o errore di connessione,
     * posizionando direttamente il cursore sul campo della password.
     */
    private static void ripristinaCampiPerErrore(JFrame frame, JTextField txtHost, JTextField txtUser,
                                                 JPasswordField txtPass, JButton btnAvvia,
                                                 JTextArea areaLog, String messaggioErrore) {
        SwingUtilities.invokeLater(() -> {
            areaLog.setText("Errore: " + messaggioErrore + "\nRiprova a inserire la password.");
            JOptionPane.showMessageDialog(frame, messaggioErrore, "Errore di connessione", JOptionPane.ERROR_MESSAGE);
            txtHost.setEnabled(true);
            txtUser.setEnabled(true);
            txtPass.setEnabled(true);
            btnAvvia.setEnabled(true);
            txtPass.setText("");
            txtPass.requestFocusInWindow();
        });
    }
}