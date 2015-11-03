/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package stamboom.storage;

import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;
import stamboom.domain.*;



public class DatabaseMediator implements IStorageMediator {

    private Properties props;
    private Connection conn;
   
    
    @Override
    public Administratie load() throws IOException {
        Administratie admin = new Administratie();
        admin.setObservable();
        
        try
        {
            initConnection();
            String query = "select nr, voornamen, tussenvoegsel, achternamen, gebdat, gebplaats, gezinnr, geslacht from persoon order by nr asc";
            Statement statement = conn.createStatement();
            ResultSet rs = statement.executeQuery(query);
            
            while(rs.next())
            {
                int nr = rs.getInt("nr");
                String voornaam = rs.getString("voornamen");
                String [] vnamen = voornaam.split("\\s+");
                String tussenvoegsel = rs.getString("tussenvoegsel");
                String achternaam = rs.getString("achternamen");
                String gebdat = rs.getString("gebdat");
                String gebplaats = rs.getString("gebplaats");
                int gezinnr = rs.getInt("gezinnr");
                String geslacht = rs.getString("geslacht");
                
                DateFormat formatter = new SimpleDateFormat("dd-MM-yyyy");
                Date date = formatter.parse(gebdat);
                Calendar c = Calendar.getInstance();
                c.setTime(date);
                Geslacht sex;
                
                switch (geslacht)
                {
                    case "MAN":
                        sex = Geslacht.MAN;
                        break;
                    case "VROUW":
                        sex = Geslacht.VROUW;
                        break;
                    default:
                        sex = Geslacht.MAN;
                }
                
                admin.addPersoon(sex, vnamen, achternaam, tussenvoegsel, c, gebplaats, null);
            }
            
            query = "select nr, ouder1, ouder2, huwelijksdatum, scheidingsdatum from gezin order by nr asc";
            statement = conn.createStatement();
            rs = statement.executeQuery(query);
            
            while(rs.next())
            {
                int ouder1 = rs.getInt("ouder1");
                int ouder2 = rs.getInt("ouder2");
                int nr = rs.getInt("nr");
                
                Persoon o1 = admin.getPersoon(ouder1);
                Persoon o2 = admin.getPersoon(ouder2);
                
                boolean huwelijk = false;
                Calendar c;
                Calendar c2;
                
                String huwdate = rs.getString("huwelijksdatum");
                Date huwelijkdate = null;
                //Check for marriage date
                if (huwdate != null && huwdate != "" && huwdate != "null")
                {
                    huwelijk = true;
                    DateFormat formatter = new SimpleDateFormat("dd-MM-yyyy", Locale.ENGLISH);
                    huwelijkdate = formatter.parse(huwdate);
                    c = Calendar.getInstance();
                    c.setTime(huwelijkdate);
                }
                
                //Check for divorce
                boolean scheiding = false;
                Date scheidingsdate = null;
                boolean testbool = false;
                
                String scheiddatum = rs.getString("scheidingsdatum");
                testbool = scheiddatum.isEmpty();
                if (scheiddatum != null && !scheiddatum.equals("") && !scheiddatum.equals("null"))
                {
                    scheiding = true;
                    DateFormat formatter = new SimpleDateFormat("dd-MM-yyyy");
                    scheidingsdate = formatter.parse(scheiddatum);
                    c2 = Calendar.getInstance();
                    c2.setTime(scheidingsdate);
                }
                
                Gezin g = admin.addOngehuwdGezin(o1, o2);
                
                if (huwelijk)
                {
                    Calendar huwDatum = Calendar.getInstance();
                    huwDatum.setTime(huwelijkdate);
                    admin.setHuwelijk(g, huwDatum);
                }
                
                if (scheiding)
                {
                    Calendar scheidingdatum = Calendar.getInstance();
                    scheidingdatum.setTime(scheidingsdate);
                    admin.setScheiding(g, scheidingdatum);
                }
                
            }
            
            query = "select nr, voornamen, tussenvoegsel, achternamen, gebdat, gebplaats, gezinnr, geslacht from persoon where gezinnr is not null";
            statement = conn.createStatement();
            rs = statement.executeQuery(query);
            
            while (rs.next()) //adding children to families
            {
                int nr = rs.getInt("nr");
                int gezinnr = rs.getInt("gezinnr");
                Persoon child = admin.getPersoon(nr);
                Gezin parents = admin.getGezin(gezinnr);
                if(gezinnr > 0)
                {
                admin.setOuders(child, parents);
                }
            }
            
        } catch (SQLException | ParseException ex) {
            Logger.getLogger(DatabaseMediator.class.getName()).log(Level.SEVERE, null, ex);
        }
       
        return admin;
            
    }

    @Override
    public void save(Administratie admin) throws IOException {
        try {
            //todo opgave 4
            emptyDatabase();
            initConnection();
            ArrayList<String> queries = new ArrayList<String>();
            String query = "";
            
            String insertIntoPersoon = "insert into persoon(nr, voornamen, tussenvoegsel, achternamen, gebdat, gebplaats, gezinnr, geslacht) " +
                    "values ";
            String insertIntoGezin = "insert into gezin(nr, ouder1, ouder2, huwelijksdatum, scheidingsdatum) " + "values ";
            
            for (Persoon p : admin.getPersonen())
            {
                query = insertIntoPersoon + "('" + p.getNr() + "', '" + p.getVoornamen() + "', '" + p.getTussenvoegsel() + "', '" + p.getAchternaam() + "', '" + p.getGebDatString() +"', '"+p.getGebPlaats() + "', '" + p.getGezinnr() + "' , '" + p.getGeslacht() + "')";
                query.replace(";", "");
                queries.add(query);
            }
            
            for(Gezin gezin : admin.getGezinnen()) {
                String sdatum = gezin.getSdDatum();
                query = insertIntoGezin + "('" + gezin.getNr() + "', '" + gezin.getOuder1().getNr() + "', '" + gezin.getOuder2().getNr() + "', '" + gezin.getHwDatum() + "', '" + gezin.getSdDatum() + "')";
                query.replace(";", "");
                queries.add(query);
            }
            
            for(String q: queries)
            {
                try {
                    PreparedStatement st = conn.prepareStatement(q);
                    st.executeUpdate();
                } catch (SQLException ex) {
                    System.out.print(ex.toString());
                }
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
        //conn = DriverManager.getConnection("jdbc:sqlite://C:/Users/rvanduijnhoven/Documents/GitHub/Stamboom/stamboomdb.sqlite");
        conn = DriverManager.getConnection("jdbc:sqlite://Users/JulesKreutzer/Documents/Github/Stamboom/stamboomdb.sqlite");
    }

    private void closeConnection() {
        try {
            conn.close();
        } catch (SQLException ex) {
            System.err.println(ex.getMessage());
        }
    }
    
    private void emptyDatabase() throws SQLException
    {
        initConnection();
        
        Statement statement = conn.createStatement();
        statement.setQueryTimeout(30);
        
        statement.executeUpdate("delete from persoon");
        statement.executeUpdate("delete from gezin");
        
        closeConnection();
    }
}
