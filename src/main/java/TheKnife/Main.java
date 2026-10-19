package TheKnife;


import TheKnife.DAO.RistoranteDAO;
import TheKnife.DB.ImportatoreCSV;

public class Main {
    static void main() {
        ImportatoreCSV.importaDati("michelin_my_maps.csv");
        RistoranteDAO dao = new RistoranteDAO();
        System.out.println(dao.cercaRistorante(null, "Vienna", null, 20, null, null, null, null));
    }

}
