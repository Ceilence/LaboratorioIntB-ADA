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
import java.util.HashMap;
import java.util.Map;

/**
 * Classe di utilità grafica con cache in memoria per le bandiere, validazione password e stile moderno.
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

    /**
     * Giallo dorato per tutte le stelle (sia in anteprima hover sia confermate/pubblicate) <code>GIALLO_STELLA</code>.
     */
    public static final Color GIALLO_STELLA = new Color(245, 176, 0);

    public static final Color BIANCO = Color.WHITE;
    public static final Color GRIGIO_SFONDO = new Color(244, 247, 246);
    public static final Color TESTO_SCURO = new Color(30, 40, 45);
    public static final Color TESTO_GRIGIO = new Color(100, 115, 120);
    public static final Color GRIGIO_BORDO = new Color(215, 222, 220);

    public static final Font FONT_BRAND = new Font("Georgia", Font.BOLD | Font.ITALIC, 22);
    public static final Font FONT_BRAND_GRANDE = new Font("Georgia", Font.BOLD | Font.ITALIC, 32);
    public static final Font FONT_TITOLO = new Font("SansSerif", Font.BOLD, 24);
    public static final Font FONT_SOTTOTITOLO = new Font("SansSerif", Font.BOLD, 16);
    public static final Font FONT_NORMALE = new Font("SansSerif", Font.PLAIN, 14);
    public static final Font FONT_BOTTONE = new Font("SansSerif", Font.BOLD, 14);

    /**
     * Cache in memoria per non ricaricare da disco le immagini delle bandiere durante lo scroll.
     */
    private static final Map<String, ImageIcon> CACHE_IMMAGINI = new HashMap<>();

    public StileTheKnife() {
    }

    /**
     * Controlla che la password rispetti i criteri di sicurezza:
     * almeno 8 caratteri, almeno una maiuscola, almeno una minuscola e almeno un numero.
     * @param password la password da controllare
     * @return <code>null</code> se la password è valida, oppure il messaggio di errore da mostrare
     */
    public static String validaPassword(String password) {
        if (password == null || password.length() < 8) {
            return "La password deve contenere almeno 8 caratteri.";
        }
        boolean haMaiuscola = false;
        boolean haMinuscola = false;
        boolean haNumero = false;
        for (char c : password.toCharArray()) {
            if (Character.isUpperCase(c)) haMaiuscola = true;
            else if (Character.isLowerCase(c)) haMinuscola = true;
            else if (Character.isDigit(c)) haNumero = true;
        }
        if (!haMaiuscola || !haMinuscola || !haNumero) {
            return "La password deve contenere almeno:\n- 8 caratteri\n- Una lettera maiuscola (A-Z)\n- Una lettera minuscola (a-z)\n- Un numero (0-9)";
        }
        return null;
    }

    /**
     * Carica un'immagine usando la cache in RAM per evitare rallentamenti o crash con molti ristoranti.
     * @param nomeFile nome del file PNG
     * @param larghezza larghezza in pixel
     * @param altezza altezza in pixel
     * @return <code>ImageIcon</code> ridimensionata
     */
    public static ImageIcon caricaImmagine(String nomeFile, int larghezza, int altezza) {
        String chiaveCache = nomeFile + "_" + larghezza + "x" + altezza;
        if (CACHE_IMMAGINI.containsKey(chiaveCache)) {
            return CACHE_IMMAGINI.get(chiaveCache);
        }

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
                ImageIcon scalata = new ImageIcon(img.getScaledInstance(larghezza, altezza, Image.SCALE_SMOOTH));
                CACHE_IMMAGINI.put(chiaveCache, scalata);
                return scalata;
            }
        } catch (Exception ignored) {
        }
        CACHE_IMMAGINI.put(chiaveCache, null);
        return null;
    }

    public static JLabel creaLabelLogo(int dimensione) {
        JLabel lbl = new JLabel();
        ImageIcon icon = caricaImmagine("logo.png", dimensione, dimensione);
        if (icon != null) {
            lbl.setIcon(icon);
        }
        return lbl;
    }

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

    /**
     * Restituisce una stringa HTML con le stelle piene gialle e quelle vuote grigie.
     * @param stelle numero di stelle da 1 a 5
     * @return stringa HTML con stelle gialle
     */
    public static String stelleGialleHtml(int stelle) {
        int piene = Math.max(0, Math.min(5, stelle));
        String sPiene = "★".repeat(piene);
        String sVuote = "☆".repeat(5 - piene);
        return "<span style='color:#F5B000; font-size:14pt;'>" + sPiene + "</span>"
                + "<span style='color:#B0BEC5; font-size:14pt;'>" + sVuote + "</span>";
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