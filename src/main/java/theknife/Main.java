package theknife;


import theknife.dao.RistoranteDAO;
import theknife.dao.UtenteDAO;
import theknife.db.ImportatoreCSV;
import theknife.essenziali.Utente;

public class Main {
    static void main() {
        ImportatoreCSV.importaDati("michelin_my_maps.csv");
        RistoranteDAO dao = new RistoranteDAO();


        UtenteDAO utenteDAO = new UtenteDAO();

// 1. Creiamo un utente di prova (con data di nascita impostata a oggi o null)
        Utente nuovo = new Utente("Mario", "Rossi", "mrossi", "Segreta123!", null, "Como", "cliente");

// 2. Registrazione
        if (utenteDAO.registraUtente(nuovo)) {
            System.out.println("Registrazione riuscita!");
        }

// 3. Test Login
        Utente loggato = utenteDAO.login("mrossi", "Segreta123!");
        if (loggato != null) {
            System.out.println("Login effettuato con successo: " + loggato);
        } else {
            System.out.println("Credenziali errate!");
        }
        //System.out.println(dao.cercaRistorante(null, "Vienna", null, 20, null, true, null, null));
    }
}
