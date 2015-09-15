/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package stamboom.storage;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Properties;
import stamboom.domain.Administratie;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

public class SerializationMediator implements IStorageMediator {

    /**
     * bevat de bestandslocatie. Properties is een subclasse van HashTable, een
     * alternatief voor een List. Het verschil is dat een List een volgorde heeft,
     * en een HashTable een key/value index die wordt opgevraagd niet op basis van
     * positie, maar op key.
     */
    private Properties props;

    /**
     * creation of a non configured serialization mediator
     */
    public SerializationMediator() {
        props = null;
    }

    @Override
    public Administratie load() throws IOException {
        if (!isCorrectlyConfigured()) {
            throw new RuntimeException("Serialization mediator isn't initialized correctly.");
        }
        Administratie admin = null;
        try{
            FileInputStream fileIn = new FileInputStream(props.getProperty("file"));
            ObjectInputStream in = new ObjectInputStream(fileIn);
            admin = (Administratie) in.readObject();
            in.close();
            fileIn.close();
            System.out.printf("The file \"data.ser\" has been loaded into the program!");
            return admin;
        }
        catch(IOException i)
        {
            i.toString();
            return null;
            
        }
        catch(ClassNotFoundException i)
        {
            System.out.printf("The class \"Administratie\" hasn't been found.");
            i.toString();
            return null;
        }
    }

    @Override
    public void save(Administratie admin) throws IOException {
        if (!isCorrectlyConfigured()) {
            throw new RuntimeException("Serialization mediator isn't initialized correctly.");
        }

        try
        {
            FileOutputStream fileOut = new FileOutputStream(props.getProperty("file"));
            ObjectOutputStream out = new ObjectOutputStream(fileOut);
            out.writeObject(admin);
            out.close();
            fileOut.close();
            System.out.printf("File has been saved to \"data.ser\"");
        }
        catch(IOException i)
        {
            i.toString();
        }
  
    }

    /**
     * Laadt de instellingen, in de vorm van een Properties bestand, en controleert
     * of deze in de juiste vorm is.
     * @param props
     * @return
     */
    @Override
    public boolean configure(Properties props) {
        this.props = props;
        return isCorrectlyConfigured();
    }

    @Override
    public Properties config() {
        return props;
    }

    /**
     * Controleert of er een geldig Key/Value paar bestaat in de Properties.
     * De bedoeling is dat er een Key "file" is, en de Value van die Key 
     * een String representatie van een FilePath is (eg. C:\\Users\Username\test.txt).
     * 
     * @return true if config() contains at least a key "file" and the
     * corresponding value is formatted like a file path
     */
    @Override
    public boolean isCorrectlyConfigured() {
        if (props == null) {
            return false;
        }
        return props.containsKey("file") 
                && props.getProperty("file").contains(File.separator);
    }
}
