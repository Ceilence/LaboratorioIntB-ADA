/*
 * Autori del progetto:
 * - Alessandro Frigerio, Matricola: 759926, Sede: CO
 * - Antonio Pardo, Matricola: 760613, Sede: CO
 * - Davide Moretti, Matricola: 762176, Sede: CO
 */
package theknife.gui;

import theknife.essenziali.Ristorante;

import javax.swing.BorderFactory;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JSplitPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.border.Border;
import javax.swing.plaf.basic.BasicSplitPaneDivider;
import javax.swing.plaf.basic.BasicSplitPaneUI;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.JTableHeader;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Image;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.File;
import java.net.URL;

/**
 * Classe di utilità grafica con palette <code>VERDE_THEKNIFE</code>, font dedicato per il brand,
 * caricamento automatico di logo e bandiere nazionali e finestre di dialogo personalizzate.
 * @author Alessandro Frigerio
 * @author Antonio Pardo
 * @author Davide Moretti
 */
public class StileTheKnife {

    public static final Color VERDE_THEKNIFE = new Color(0, 102, 102);
    public static final Color VERDE_PRIMARIO = VERDE_THEKNIFE;
    public static final Color VERDE_BOTTONE = VERDE_THEKNIFE;
    public static final Color VERDE_HOVER = new Color(10, 160, 140);
    public static final Color VERDE_CHIARO_SFONDO = new Color(224, 242, 241);
    public static final Color GIALLO_STELLA = new Color(245, 166, 35);
    public static final Color BIANCO = Color.WHITE;
    public static final Color GRIGIO_SFONDO = new Color(244, 247, 246);
    public static final Color TESTO_SCURO = new Color(38, 50, 56);
    public static final Color TESTO_GRIGIO = new Color(100, 115, 120);
    public static final Color GRIGIO_BORDO = new Color(215, 222, 220);

    /**
     * Font distintivo ed elegante specifico per la scritta "THE KNIFE" <code>FONT_BRAND</code>.
     */
    public static final Font FONT_BRAND = new Font("Georgia", Font.BOLD | Font.ITALIC, 22);

    /**
     * Font grande per il titolo "THE KNIFE" nella schermata di benvenuto <code>FONT_BRAND_GRANDE</code>.
     */
    public static final Font FONT_BRAND_GRANDE = new Font("Georgia", Font.BOLD | Font.ITALIC, 32);

    public static final Font FONT_TITOLO = new Font("SansSerif", Font.BOLD, 24);
    public static final Font FONT_SOTTOTITOLO = new Font("SansSerif", Font.BOLD, 16);
    public static final Font FONT_NORMALE = new Font("SansSerif", Font.PLAIN, 14);
    public static final Font FONT_BOTTONE = new Font("SansSerif", Font.BOLD, 14);

    public StileTheKnife() {
    }

    /**
     * Carica un'immagine cercandola nella cartella del progetto o in <code>src/main/resources</code>.
     * @param nomeFile nome del file (es. <code>logo.png</code> o <code>Flag_of_Italy.png</code>)
     * @param larghezza larghezza desiderata in pixel
     * @param altezza altezza desiderata in pixel
     * @return oggetto <code>ImageIcon</code> scalato, oppure <code>null</code> se non trovato
     */
    public static ImageIcon caricaImmagine(String nomeFile, int larghezza, int altezza) {
        try {
            Image img = null;
            File[] possibiliPercorsi = {
                    new File(nomeFile),
                    new File("src/main/resources/" + nomeFile),
                    new File("resources/" + nomeFile),
                    new File("img/" + nomeFile)
            };
            for (File f : possibiliPercorsi) {
                if (f.exists()) {
                    img = new ImageIcon(f.getAbsolutePath()).getImage();
                    break;
                }
            }
            if (img == null) {
                URL urlRisorsa = StileTheKnife.class.getResource("/" + nomeFile);
                if (urlRisorsa != null) {
                    img = new ImageIcon(urlRisorsa).getImage();
                }
            }
            if (img != null) {
                return new ImageIcon(img.getScaledInstance(larghezza, altezza, Image.SCALE_SMOOTH));
            }
        } catch (Exception ignored) {
        }
        return null;
    }

    /**
     * Crea un'etichetta con il logo ufficiale dell'app.
     * @param dimensione lato in pixel
     * @return <code>JLabel</code> con il logo
     */
    public static JLabel creaLabelLogo(int dimensione) {
        JLabel lbl = new JLabel();
        ImageIcon icon = caricaImmagine("logo.png", dimensione, dimensione);
        if (icon != null) {
            lbl.setIcon(icon);
        }
        return lbl;
    }

    /**
     * Restituisce un <code>JLabel</code> con l'immagine della bandiera corrispondente alla nazione del ristorante.
     * <p>
     * Supporta China, France, Germany, Italy, Japan, Spain, United States e usa <code>Globe.png</code> per tutti gli altri stati.
     * @param nazione stringa della nazione del ristorante
     * @param larghezza larghezza della bandiera in pixel
     * @param altezza altezza della bandiera in pixel
     * @return <code>JLabel</code> contenente la bandiera
     */
    public static JLabel creaLabelBandiera(String nazione, int larghezza, int altezza) {
        String fileBandiera = determinaFileBandiera(nazione);
        JLabel lbl = new JLabel();
        lbl.setPreferredSize(new Dimension(larghezza, altezza));
        ImageIcon icon = caricaImmagine(fileBandiera, larghezza, altezza);
        if (icon == null && !fileBandiera.equals("Globe.png")) {
            icon = caricaImmagine("Globe.png", larghezza, altezza);
        }
        if (icon != null) {
            lbl.setIcon(icon);
        } else {
            lbl.setOpaque(true);
            lbl.setBackground(VERDE_CHIARO_SFONDO);
            lbl.setForeground(VERDE_THEKNIFE);
            lbl.setFont(new Font("SansSerif", Font.BOLD, 11));
            lbl.setHorizontalAlignment(JLabel.CENTER);
            String sigla = (nazione != null && nazione.length() >= 2) ? nazione.substring(0, 2).toUpperCase() : "🌐";
            lbl.setText(sigla);
            lbl.setBorder(BorderFactory.createLineBorder(GRIGIO_BORDO));
        }
        return lbl;
    }

    /**
     * Associa il nome della nazione al file PNG richiesto.
     * @param nazione nazione del ristorante
     * @return nome del file immagine corrispondente
     */
    public static String determinaFileBandiera(String nazione) {
        if (nazione == null) {
            return "Globe.png";
        }
        String n = nazione.trim().toLowerCase();
        if (n.contains("italy") || n.contains("italia")) {
            return "Flag_of_Italy.png";
        } else if (n.contains("france") || n.contains("francia")) {
            return "Flag_of_France.png";
        } else if (n.contains("germany") || n.contains("germania")) {
            return "Flag_of_Germany.png";
        } else if (n.contains("spain") || n.contains("spagna")) {
            return "Flag_of_Spain.png";
        } else if (n.contains("japan") || n.contains("giappone")) {
            return "Flag_of_Japan.png";
        } else if (n.contains("china") || n.contains("cina") || n.contains("hong kong") || n.contains("macau")) {
            return "Flag_of_China.png";
        } else if (n.contains("united states") || n.equals("usa") || n.contains("stati uniti")) {
            return "Flag_of_United_States.png";
        }
        return "Globe.png";
    }

    /**
     * Genera una descrizione ricca e veritiera per la scheda dettagli del ristorante.
     * @param r l'oggetto <code>Ristorante</code>
     * @return testo descrittivo del ristorante
     */
    public static String generaDescrizioneRistorante(Ristorante r) {
        if (r == null) {
            return "";
        }
        String servizi = (r.isDelivery() && r.isPrenotazioneOnline())
                ? "Il locale offre sia il comodo servizio di consegna a domicilio (Delivery) sia la prenotazione online del tavolo."
                : (r.isDelivery()
                   ? "Il locale mette a disposizione il servizio di consegna a domicilio (Delivery)."
                   : (r.isPrenotazioneOnline()
                      ? "È disponibile il servizio di prenotazione online direttamente dalla piattaforma."
                      : "Il locale accoglie i clienti direttamente in sede per un'esperienza gastronomica tradizionale."));

        return "Situato a " + r.getCitta() + " (" + r.getNazione() + ") in " + r.getIndirizzo() + ", "
                + r.getNome() + " è un ristorante selezionato che propone una cucina di tipo " + r.getTipoCucina() + ". "
                + "La fascia di prezzo media indicativa è di circa " + r.getFasciaPrezzo() + " € a persona, "
                + "con particolare attenzione alla qualità delle materie prime e all'accoglienza. " + servizi;
    }

    public static JButton creaBottonePrimario(String testo) {
        JButton btn = new JButton(testo);
        btn.setFont(FONT_BOTTONE);
        btn.setBackground(VERDE_THEKNIFE);
        btn.setForeground(BIANCO);
        btn.setFocusPainted(false);
        btn.setBorderPainted(false);
        btn.setContentAreaFilled(false);
        btn.setOpaque(true);
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btn.setBorder(BorderFactory.createEmptyBorder(10, 20, 10, 20));

        btn.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) {
                if (btn.isEnabled()) {
                    btn.setBackground(VERDE_HOVER);
                }
            }

            @Override
            public void mouseExited(MouseEvent e) {
                if (btn.isEnabled()) {
                    btn.setBackground(VERDE_THEKNIFE);
                }
            }
        });
        return btn;
    }

    public static JButton creaBottoneSecondario(String testo) {
        JButton btn = new JButton(testo);
        btn.setFont(FONT_BOTTONE);
        btn.setBackground(VERDE_CHIARO_SFONDO);
        btn.setForeground(VERDE_THEKNIFE);
        btn.setFocusPainted(false);
        btn.setBorderPainted(false);
        btn.setContentAreaFilled(false);
        btn.setOpaque(true);
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btn.setBorder(BorderFactory.createEmptyBorder(10, 18, 10, 18));

        btn.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) {
                if (btn.isEnabled()) {
                    btn.setBackground(VERDE_HOVER);
                    btn.setForeground(BIANCO);
                }
            }

            @Override
            public void mouseExited(MouseEvent e) {
                if (btn.isEnabled()) {
                    btn.setBackground(VERDE_CHIARO_SFONDO);
                    btn.setForeground(VERDE_THEKNIFE);
                }
            }
        });
        return btn;
    }

    public static JTextField creaCampoTesto(int colonne) {
        JTextField txt = new JTextField(colonne);
        txt.setFont(FONT_NORMALE);
        txt.setForeground(TESTO_SCURO);
        txt.setBackground(BIANCO);
        txt.setBorder(creaBordoInput());
        return txt;
    }

    public static JPasswordField creaCampoPassword(int colonne) {
        JPasswordField pwd = new JPasswordField(colonne);
        pwd.setFont(FONT_NORMALE);
        pwd.setEchoChar('\u2022');
        pwd.setForeground(TESTO_SCURO);
        pwd.setBackground(BIANCO);
        pwd.setBorder(creaBordoInput());
        return pwd;
    }

    public static JLabel creaEtichetta(String testo) {
        JLabel lbl = new JLabel(testo);
        lbl.setFont(FONT_NORMALE);
        lbl.setForeground(TESTO_SCURO);
        return lbl;
    }

    public static JPanel creaCardBianca() {
        JPanel card = new JPanel();
        card.setBackground(BIANCO);
        card.setBorder(BorderFactory.createEmptyBorder(25, 28, 25, 28));
        return card;
    }

    public static Border creaBordoInput() {
        return BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(GRIGIO_BORDO, 1),
                BorderFactory.createEmptyBorder(7, 10, 7, 10)
        );
    }

    public static void fissaDimensioneInput(JComponent comp, int larghezza) {
        Dimension d = new Dimension(larghezza, 38);
        comp.setPreferredSize(d);
        comp.setMinimumSize(d);
        comp.setMaximumSize(d);
    }

    public static void applicaSplitVerdeContinuo(JSplitPane split) {
        split.setDividerSize(6);
        split.setBorder(BorderFactory.createMatteBorder(0, 4, 4, 4, VERDE_THEKNIFE));
        split.setBackground(VERDE_THEKNIFE);
        split.setUI(new BasicSplitPaneUI() {
            @Override
            public BasicSplitPaneDivider createDefaultDivider() {
                return new BasicSplitPaneDivider(this) {
                    @Override
                    public void paint(Graphics g) {
                        g.setColor(VERDE_THEKNIFE);
                        g.fillRect(0, 0, getSize().width, getSize().height);
                    }
                };
            }
        });
    }

    public static void applicaStileTabella(JTable tabella) {
        tabella.setFont(FONT_NORMALE);
        tabella.setRowHeight(34);
        tabella.setGridColor(GRIGIO_BORDO);
        tabella.setSelectionBackground(VERDE_CHIARO_SFONDO);
        tabella.setSelectionForeground(TESTO_SCURO);
        tabella.setShowVerticalLines(false);
        tabella.getTableHeader().setReorderingAllowed(false);

        JTableHeader header = tabella.getTableHeader();
        header.setPreferredSize(new Dimension(header.getWidth(), 38));

        DefaultTableCellRenderer headerRenderer = new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable table, Object value,
                                                           boolean isSelected, boolean hasFocus, int row, int column) {
                JLabel lbl = (JLabel) super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
                lbl.setBackground(VERDE_THEKNIFE);
                lbl.setForeground(BIANCO);
                lbl.setFont(FONT_BOTTONE);
                lbl.setBorder(BorderFactory.createEmptyBorder(0, 10, 0, 10));
                lbl.setOpaque(true);
                return lbl;
            }
        };

        for (int i = 0; i < tabella.getColumnModel().getColumnCount(); i++) {
            tabella.getColumnModel().getColumn(i).setHeaderRenderer(headerRenderer);
        }
    }

    /**
     * Crea una finestra di dialogo modale stilizzata con barra superiore <code>VERDE_THEKNIFE</code>,
     * logo e pulsanti Conferma/Annulla moderni (sostituisce i JOptionPane grigi di sistema).
     * @param parent finestra chiamante
     * @param titolo titolo nell'header verde
     * @param contenuto pannello centrale del form
     * @param testoConferma testo del bottone verde di conferma
     * @param larghezza larghezza del dialogo
     * @param altezza altezza del dialogo
     * @return <code>true</code> se l'utente ha premuto Conferma, <code>false</code> se ha premuto Annulla
     */
    public static boolean mostraDialogoStilizzato(JFrame parent, String titolo, JPanel contenuto,
                                                  String testoConferma, int larghezza, int altezza) {
        final boolean[] confermato = {false};
        JDialog dialog = new JDialog(parent, titolo, true);
        dialog.setSize(larghezza, altezza);
        dialog.setLocationRelativeTo(parent);
        dialog.setResizable(false);
        dialog.setLayout(new BorderLayout());

        JPanel header = new JPanel(new FlowLayout(FlowLayout.LEFT, 15, 12));
        header.setBackground(VERDE_THEKNIFE);
        header.add(creaLabelLogo(32));
        JLabel lblTitolo = new JLabel(titolo);
        lblTitolo.setFont(FONT_SOTTOTITOLO);
        lblTitolo.setForeground(BIANCO);
        header.add(lblTitolo);
        dialog.add(header, BorderLayout.NORTH);

        contenuto.setBackground(BIANCO);
        contenuto.setBorder(BorderFactory.createEmptyBorder(18, 24, 18, 24));
        dialog.add(contenuto, BorderLayout.CENTER);

        JPanel footer = new JPanel(new FlowLayout(FlowLayout.RIGHT, 12, 12));
        footer.setBackground(GRIGIO_SFONDO);
        footer.setBorder(BorderFactory.createMatteBorder(1, 0, 0, 0, GRIGIO_BORDO));

        JButton btnAnnulla = creaBottoneSecondario("Annulla");
        JButton btnOk = creaBottonePrimario(testoConferma);

        btnAnnulla.addActionListener(e -> dialog.dispose());
        btnOk.addActionListener(e -> {
            confermato[0] = true;
            dialog.dispose();
        });

        footer.add(btnAnnulla);
        footer.add(btnOk);
        dialog.add(footer, BorderLayout.SOUTH);

        dialog.setVisible(true);
        return confermato[0];
    }

    public static String formattaFasciaPrezzo(int prezzo) {
        int numSimboli = Math.max(1, Math.min(4, Math.round(prezzo / 25.0f)));
        return "€".repeat(numSimboli) + " (" + prezzo + "€)";
    }
}