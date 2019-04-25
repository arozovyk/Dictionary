package views.jfx;

import controleur.Controleur;
import controleur.notifications.Notification;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.layout.Pane;
import javafx.stage.Stage;
import views.AccueilInterface;

import java.io.IOException;
import java.net.URL;

public class AccueilView implements AccueilInterface {

    private Stage primaryStage;
    Controleur controleur;
    private Scene maScene;

    @FXML
    private Button saisirLeResultat;

    private void setPrimaryStage(Stage primaryStage) {
        this.primaryStage = primaryStage;
    }

    public void setControleur(Controleur controleur) {
        this.controleur = controleur;
    }


    public void setMaScene(Scene maScene) {
        this.maScene = maScene;
    }

    public Scene getMaScene() {
        return maScene;
    }



    public static AccueilView creerInstance(Controleur c, Stage primaryStage) {
        URL location = AccueilView.class.getResource("/views/jfx/Accueil.fxml");
        FXMLLoader fxmlLoader = new FXMLLoader(location);
        Pane root = null;
        try {
            root = (Pane) fxmlLoader.load();
        } catch (IOException e) {
            e.printStackTrace();
        }
        AccueilView vue = fxmlLoader.getController();
        vue.setPrimaryStage(primaryStage);
        vue.setMaScene(new Scene(root, root.getPrefWidth(),root.getPrefHeight()));
        vue.setControleur(c);
        c.enregistrerObservateur(vue);

        return vue;
    }


    public void show(){

            saisirLeResultat.setVisible(false);

        primaryStage.setTitle("Accueil");
        primaryStage.setScene(getMaScene());
        primaryStage.show();
    }

    @Override
    public void informeUtilisateurErreur(String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setContentText(message);
        alert.showAndWait();

    }



    @Override
    public void notifier(Notification n) {
        switch (n.getTypeNotification()) {
            case CONNEXION_ERREUR: {
                this.informeUtilisateurErreur(n.getMessage());
                return;
            }
        }

    }




    public void goToAjouterMatch(ActionEvent actionEvent) {
    }
}
