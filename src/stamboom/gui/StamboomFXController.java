/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package stamboom.gui;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.Calendar;
import java.util.Properties;
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.Event;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.Pane;
import javafx.stage.Stage;
import stamboom.controller.StamboomController;
import stamboom.domain.Administratie;
import stamboom.domain.Geslacht;
import stamboom.domain.Gezin;
import stamboom.domain.Persoon;
import stamboom.storage.DatabaseMediator;
import stamboom.storage.IStorageMediator;
import stamboom.storage.SerializationMediator;
import stamboom.util.StringUtilities;

/**
 *
 * @author frankpeeters
 */
public class StamboomFXController extends StamboomController implements Initializable {

    //MENUs en TABs
    @FXML MenuBar menuBar;
    @FXML MenuItem miNew;
    @FXML MenuItem miOpen;
    @FXML MenuItem miSave;
    @FXML CheckMenuItem cmDatabase;
    @FXML MenuItem miClose;
    @FXML Tab tabPersoon;
    @FXML Tab tabGezin;
    @FXML Tab tabPersoonInvoer;
    @FXML Tab tabGezinInvoer;

    //PERSOON
    @FXML ComboBox cbPersonen;
    @FXML TextField tfPersoonNr;
    @FXML TextField tfVoornamen;
    @FXML TextField tfTussenvoegsel;
    @FXML TextField tfAchternaam;
    @FXML TextField tfGeslacht;
    @FXML TextField tfGebDatum;
    @FXML TextField tfGebPlaats;
    @FXML ComboBox cbOuderlijkGezin;
    @FXML ListView lvAlsOuderBetrokkenBij;
    @FXML Button btStamboom;

    //INVOER GEZIN
    @FXML ComboBox cbOuder1Invoer;
    @FXML ComboBox cbOuder2Invoer;
    @FXML TextField tfHuwelijkInvoer;
    @FXML TextField tfScheidingInvoer;
    @FXML Button btOKGezinInvoer;
    @FXML Button btCancelGezinInvoer;
    
    //INVOER PERSOON
    @FXML TextField tfInvoerVoornamen;
    @FXML TextField tfInvoerTussenvoegsel;
    @FXML TextField tfInvoerAchternaam;
    @FXML TextField tfInvoerGeboortePlaats;
    @FXML ComboBox cbInvoerGeslacht;
    @FXML ComboBox cbInvoerOuderlijkGezin;
    @FXML TextField tfInvoerGeboorteDatum;
    @FXML Button btOKPersoonInvoer;
    @FXML Button btCancelPersoonInvoer;
    
    //GEZIN
    @FXML TextField tfGezinNummer;
    @FXML TextField tfOuder1;
    @FXML TextField tfOuder2;
    @FXML TextField tfGetrouwdOp;
    @FXML Button btGetrouwdOpOpslaan;
    @FXML TextField tfGescheidenOp;
    @FXML Button btGescheidenOpOpslaan;
    @FXML ListView lvKinderen; 
    @FXML ComboBox cbGezin;

    //opgave 4
    private boolean withDatabase;
    private IStorageMediator storageMediator;
    private DatabaseMediator dbMediator;

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        initComboboxes();
        withDatabase = false;
        
        storageMediator = new SerializationMediator();
        dbMediator = new DatabaseMediator();
    }

    private void initComboboxes() {
//        cbInvoerGeslacht.setItems(FXCollections.observableArrayList(Geslacht.values()));
//        cbOuder1Invoer.setItems((ObservableList) getAdministratie().getPersonen());
//        cbOuder2Invoer.setItems((ObservableList) getAdministratie().getPersonen());
//        cbPersonen.setItems((ObservableList) getAdministratie().getPersonen());
//        cbGezin.setItems((ObservableList) getAdministratie().getGezinnen());
//        cbOuderlijkGezin.setItems((ObservableList) getAdministratie().getGezinnen());
        cbInvoerOuderlijkGezin.setItems((ObservableList) getAdministratie().getGezinnen());
        Administratie temp = this.getAdministratie();
        this.cbPersonen.setItems((ObservableList) temp.getPersonen());
        this.cbOuder1Invoer.setItems((ObservableList) temp.getPersonen());
        this.cbOuder2Invoer.setItems((ObservableList) temp.getPersonen());
        
        this.cbOuderlijkGezin.setItems(temp.getGezinnen());
        this.cbGezin.setItems(temp.getGezinnen());
        
        //PERSOON INVOER
        this.cbInvoerGeslacht.setItems(FXCollections.observableArrayList(Geslacht.values()));
        this.cbOuderlijkGezin.setItems(temp.getGezinnen());
    }

    public void selectPersoon(Event evt) {
        Persoon persoon = (Persoon) cbPersonen.getSelectionModel().getSelectedItem();
        showPersoon(persoon);
    }

    private void showPersoon(Persoon persoon) {
        if (persoon == null) {
            clearTabPersoon();
        } else {
            tfPersoonNr.setText(persoon.getNr() + "");
            tfVoornamen.setText(persoon.getVoornamen());
            tfTussenvoegsel.setText(persoon.getTussenvoegsel());
            tfAchternaam.setText(persoon.getAchternaam());
            tfGeslacht.setText(persoon.getGeslacht().toString());
            tfGebDatum.setText(StringUtilities.datumString(persoon.getGebDat()));
            tfGebPlaats.setText(persoon.getGebPlaats());
            if (persoon.getOuderlijkGezin() != null) {
                cbOuderlijkGezin.getSelectionModel().select(persoon.getOuderlijkGezin());
            } else {
                cbOuderlijkGezin.getSelectionModel().clearSelection();
            }

            //todo opgave 3
            lvAlsOuderBetrokkenBij.setItems(persoon.getAlsOuderBetrokkenIn());
        }
    }

    public void setOuders(Event evt) {
        if (tfPersoonNr.getText().isEmpty()) {
            return;
        }
        Gezin ouderlijkGezin = (Gezin) cbOuderlijkGezin.getSelectionModel().getSelectedItem();
        if (ouderlijkGezin == null) {
            return;
        }

        int nr = Integer.parseInt(tfPersoonNr.getText());
        Persoon p = getAdministratie().getPersoon(nr);
        if(getAdministratie().setOuders(p, ouderlijkGezin)){
            showDialog("Success", ouderlijkGezin.toString()
                + " is nu het ouderlijk gezin van " + p.getNaam());
        }
        
    }

    public void selectGezin(Event evt) {
        Gezin gezin = (Gezin) cbGezin.getSelectionModel().getSelectedItem();
        showGezin(gezin);
        
    }

    private void showGezin(Gezin gezin) {
        
        if (gezin == null)
        {
            clearTabGezin();
        }
        else
        {
            tfGezinNummer.setText(String.valueOf(gezin.getNr()));
            if(gezin.getOuder1() != null) {
                tfOuder1.setText(gezin.getOuder1().getNaam());
            }
            if(gezin.getOuder2() != null) {
                tfOuder2.setText(gezin.getOuder2().getNaam());
            }
            
            Calendar huwelijksDatum = gezin.getHuwelijksdatum();
            Calendar scheidingsDatum = gezin.getScheidingsdatum();
            
            if(huwelijksDatum != null) {
                tfGetrouwdOp.setText(StringUtilities.datumString(huwelijksDatum));
            }
            if(scheidingsDatum != null) {
                tfGescheidenOp.setText(StringUtilities.datumString(scheidingsDatum));
            }
            
            lvKinderen.setItems(FXCollections.observableArrayList(gezin.getKinderen()));
        }

    }

    public void setHuwdatum(Event evt) {
        Calendar cal;
        Gezin gezin;
        
        try {
            gezin = getAdministratie().getGezin(Integer.parseInt(tfGezinNummer.getText()));
            cal = StringUtilities.datum(tfGetrouwdOp.getText());
        }
        catch (IllegalArgumentException IAE) {
            showDialog("Incorrecte Invoer", "Ongeldige datum, controleer de datum");
            return;
        }
        
        if(gezin != null && cal != null) {
            if(getAdministratie().setHuwelijk(gezin, cal)) {
                showGezin(gezin);
                showDialog("Nieuwe datum toegevoed", "Nieuwe datum succesvol toegevoegd");
            }
            else {
                showDialog("Er is iets fout gegaan", "De nieuwe datum is niet toegevoegd");
            }
        }
        else {
            showDialog("Er is iets fout gegaan", "Gezin of datum was fout");
        }
    }

    public void setScheidingsdatum(Event evt) {
        Calendar cal;
        Gezin gezin;
        
        try {
            gezin = getAdministratie().getGezin(Integer.parseInt(tfGezinNummer.getText()));
            cal = StringUtilities.datum(tfGescheidenOp.getText());
        }
        catch (IllegalArgumentException IAE) {
            showDialog("Incorrecte Invoer", "Ongeldige datum, controleer de datum");
            return;
        }
        
        if(gezin != null && cal != null) {
            if(getAdministratie().setScheiding(gezin, cal)) {
                showGezin(gezin);
                showDialog("Nieuwe datum toegevoed", "Nieuwe datum succesvol toegevoegd");
            }
            else {
                showDialog("Er is iets fout gegaan", "De nieuwe datum is niet toegevoegd");
            }
        }
        else {
            showDialog("Er is iets fout gegaan", "Gezin of datum was fout");
        }

    }

    public void cancelPersoonInvoer(Event evt) {
        clearTabPersoonInvoer();
    }

    public void okPersoonInvoer(Event evt) {
        String voornamen = tfInvoerVoornamen.getText();
        String tussenvoegsel = tfInvoerTussenvoegsel.getText();
        String achternaam = tfInvoerAchternaam.getText();
        String geboortePlaats = tfInvoerGeboortePlaats.getText();
        Geslacht geslacht = (Geslacht)cbInvoerGeslacht.getSelectionModel().getSelectedItem();
        Gezin gezin = (Gezin) this.cbInvoerOuderlijkGezin.getSelectionModel().getSelectedItem();
        Calendar geboorteDatum = null;
        
        try {
            geboorteDatum = StringUtilities.datum(tfInvoerGeboorteDatum.getText());
        }
        catch (IllegalArgumentException IAE) {
            showDialog("Ongeldige datum", "Datum incorrect");
        }
        
        if(voornamen.isEmpty() || achternaam.isEmpty() || geboortePlaats.isEmpty() || geboorteDatum == null) {
            showDialog("Ongeldige gegevens", "Een van de velden is niet (goed) ingevuld");
        }
        
        String[] vnamen = voornamen.split("\\s+");
        
        Persoon persoon = getAdministratie().addPersoon(geslacht, vnamen, achternaam, tussenvoegsel, geboorteDatum, geboortePlaats, gezin);
        
        if(persoon != null) {
            showDialog("Succes", "De persoon is toegevoegd");
        }
        else {
            showDialog("Fout", "Er is iets fout gegaan");
        }
        
        clearTabPersoonInvoer();
        initComboboxes();
    }

    public void okGezinInvoer(Event evt) {
        Persoon ouder1 = (Persoon) cbOuder1Invoer.getSelectionModel().getSelectedItem();
        if (ouder1 == null) {
            showDialog("Warning", "eerste ouder is niet ingevoerd");
            return;
        }
        Persoon ouder2 = (Persoon) cbOuder2Invoer.getSelectionModel().getSelectedItem();
        
        Calendar huwdatum;
        try {
            huwdatum = StringUtilities.datum(tfHuwelijkInvoer.getText());
        } catch (IllegalArgumentException exc) {
            showDialog("Warning", "huwelijksdatum :" + exc.getMessage());
            return;
        }
        Gezin g;
        if (huwdatum != null) {
            g = getAdministratie().addHuwelijk(ouder1, ouder2, huwdatum);
            boolean setouder1 = ouder1.setOuders(g);
            boolean setouder2 = ouder2.setOuders(g);
            if (g == null) {
                showDialog("Warning", "Invoer huwelijk is niet geaccepteerd");
            } else {
                Calendar scheidingsdatum;
                try {
                    scheidingsdatum = StringUtilities.datum(tfScheidingInvoer.getText());
                    if(scheidingsdatum != null){
                        getAdministratie().setScheiding(g, scheidingsdatum);
                    }
                } catch (IllegalArgumentException exc) {
                    showDialog("Warning", "scheidingsdatum :" + exc.getMessage());
                }
            }
        } else {
            g = getAdministratie().addOngehuwdGezin(ouder1, ouder2);
            boolean setouder1 = ouder1.setOuders(g);
            boolean setouder2 = ouder2.setOuders(g);
            if (g == null) {
                showDialog("Warning", "Invoer ongehuwd gezin is niet geaccepteerd");
            }
        }

        clearTabGezinInvoer();
        initComboboxes();
    }

    public void cancelGezinInvoer(Event evt) {
        clearTabGezinInvoer();
    }

    
    public void showStamboom(Event evt) {
        if(!tfPersoonNr.getText().isEmpty()) {
            Persoon persoon = getAdministratie().getPersoon(Integer.parseInt(tfPersoonNr.getText()));
            
            TreeItem<String> tree = CreateTree(persoon, null);
            
            TreeView treeView = new TreeView(tree);

            Scene scene = new Scene(treeView, 500, 500);
            Stage stage = new Stage();
            stage.setTitle("Stamboom");
            stage.setScene(scene);

            stage.show();
        }
    }
    
    public TreeItem<String> CreateTree(Persoon persoon, TreeItem parentBranch) {
        TreeItem<String> branch = new TreeItem<>(persoon.standaardgegevens());
        
        if(parentBranch != null) {
            parentBranch.getChildren().add(branch);
        }
        
        if(persoon.getOuderlijkGezin() == null)
        {
            return branch;
        }
        
        Persoon ouder1 = persoon.getOuderlijkGezin().getOuder1();
        Persoon ouder2 = persoon.getOuderlijkGezin().getOuder2();
        
        if(ouder1 != null) {
            CreateTree(ouder1, branch); 
        }
        
        if(ouder2 != null) {
            CreateTree(ouder2, branch);
        }
        
        return branch;
    }

    public void createEmptyStamboom(Event evt) {
        this.clearAdministratie();
        clearTabs();
        initComboboxes();
    }

    
    public void openStamboom(Event evt) throws IOException {
//        File file = new File("file");
//        this.deserialize(file);
//        getAdministratie().setObservable();
//        initComboboxes();
        
          try
          {
              super.loadFromDatabase();
              Administratie a = this.getAdministratie();
              this.initComboboxes();
              a.getPersonen();
          }
          catch(IOException x)
          {
              Logger.getLogger(StamboomFXController.class.getName()).log(Level.SEVERE, null, x);
          }
                  
    }

    
    public void saveStamboom(Event evt) {
        try
        {
            super.initDatabaseMedium();
            File bestand = new File("file");
            Properties props = new Properties(); //Init nieuwe properties
            props.setProperty("file", bestand.getAbsolutePath()); //Zet het path correct
            storageMediator.configure(props); //Configureer de mediator
            
            storageMediator.save(getAdministratie()); //Sla het object op
            
//            //save to database
//            if (dbMediator == null)
//            {
//                dbMediator = new DatabaseMediator();
//            }
            
            super.saveToDatabase();
        }
        catch(Exception ex)
        {
            ex.printStackTrace();
        }
       
    }

    
    public void closeApplication(Event evt) {
        saveStamboom(evt);
        getStage().close();
    }

   
    public void configureStorage(Event evt) {
        withDatabase = cmDatabase.isSelected();
    }

 
    public void selectTab(Event evt) {
        Object source = evt.getSource();
        if (source == tabPersoon) {
            clearTabPersoon();
        } else if (source == tabGezin) {
            clearTabGezin();
        } else if (source == tabPersoonInvoer) {
            clearTabPersoonInvoer();
        } else if (source == tabGezinInvoer) {
            clearTabGezinInvoer();
        }
    }

    private void clearTabs() {
        clearTabPersoon();
        clearTabPersoonInvoer();
        clearTabGezin();
        clearTabGezinInvoer();
    }

    
    private void clearTabPersoonInvoer() {
        tfInvoerVoornamen.clear();
        tfInvoerTussenvoegsel.clear();
        tfInvoerAchternaam.clear();
        cbInvoerGeslacht.getSelectionModel().clearSelection();
        tfInvoerGeboorteDatum.clear();
        tfInvoerGeboortePlaats.clear();
        cbInvoerOuderlijkGezin.getSelectionModel().clearSelection();
    }

    
    private void clearTabGezinInvoer() {
        cbOuder1Invoer.getSelectionModel().clearSelection();
        cbOuder2Invoer.getSelectionModel().clearSelection();
        tfHuwelijkInvoer.clear();
        tfScheidingInvoer.clear();
    }

    private void clearTabPersoon() {
        cbPersonen.getSelectionModel().clearSelection();
        tfPersoonNr.clear();
        tfVoornamen.clear();
        tfTussenvoegsel.clear();
        tfAchternaam.clear();
        tfGeslacht.clear();
        tfGebDatum.clear();
        tfGebPlaats.clear();
        cbOuderlijkGezin.getSelectionModel().clearSelection();
        lvAlsOuderBetrokkenBij.setItems(FXCollections.emptyObservableList());
    }

    
    private void clearTabGezin() {
        tfGezinNummer.clear();
        tfOuder1.clear();
        tfOuder2.clear();
        tfGetrouwdOp.clear();
        tfGescheidenOp.clear();
        lvKinderen.setItems(FXCollections.emptyObservableList());
       
    }

    private void showDialog(String type, String message) {
        Stage myDialog = new Dialog(getStage(), type, message);
        myDialog.show();
    }

    private Stage getStage() {
        return (Stage) menuBar.getScene().getWindow();
    }

}
