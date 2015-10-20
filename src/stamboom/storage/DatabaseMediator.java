/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package stamboom.storage;

import java.io.IOException;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Properties;
import stamboom.domain.*;



public class DatabaseMediator implements IStorageMediator {

    private Properties props;
    private Connection conn;

    @Override
    public Administratie load() throws IOException {
        //todo opgave 4
        return null;
    }

    @Override
    public void save(Administratie admin) throws IOException {
        try {
            //todo opgave 4
            
            initConnection();
            ArrayList<String> queries = new ArrayList<String>();
            
            String insertInto = "insert into PERSOON(NR, VOORNAMEN, TUSSENVOEGSEL, ACHTERNAAM, GEBOORTEDATUM, GEBOORTEPLAATS, OUDERLIJKGEZIN, GESLACHT)\n" +
                    "values ";
            
            for (Persoon p : admin.getPersonen())
            {
                String query = insertInto + "(" + p.getNr() + ", " + p.getVoornamen() + ", " + p.getTussenvoegsel() + ", " + p.getAchternaam() + ", " + p.getGebDat() +", "+p.getGebPlaats() + ", " + p.getOuderlijkGezin() + ", " + p.getGeslacht() + ");";
                queries.add(query);
            }
            
            for(String q: queries)
            {
                try {
                    Statement st = conn.createStatement();
                    st.executeUpdate(q);
                } catch (SQLException ex) {
                    System.out.print(ex.toString());
                }
            }
            
            
            for(Gezin gezin : admin.getGezinnen()) {
                
            }
            closeConnection();
        } catch (SQLException ex) {
            System.out.print(ex.toString());
        }    
    }

    /**
     * Laadt de instellingen, in de vorm van een Properties bestand, en controleert
     * of deze in de correcte vorm is, en er verbinding gemaakt kan worden met
     * de database.
     * @param props
     * @return
     */
    @Override
    public final boolean configure(Properties props) {
        this.props = props;
        if (!isCorrectlyConfigured()) {
            System.err.println("props mist een of meer keys");
            return false;
        }

        try {
            initConnection();
            return true;
        } catch (SQLException ex) {
            System.err.println(ex.getMessage());
            this.props = null;
            return false;
        } finally {
            closeConnection();
        }
    }

    @Override
    public Properties config() {
        return props;
    }

    @Override
    public boolean isCorrectlyConfigured() {
        if (props == null) {
            return false;
        }
        if (!props.containsKey("driver")) {
            return false;
        }
        if (!props.containsKey("url")) {
            return false;
        }
        if (!props.containsKey("username")) {
            return false;
        }
        if (!props.containsKey("password")) {
            return false;
        }
        return true;
    }

    private void initConnection() throws SQLException {
        //opgave 4
    }

    private void closeConnection() {
        try {
            conn.close();
            conn = null;
        } catch (SQLException ex) {
            System.err.println(ex.getMessage());
        }
    }
}
