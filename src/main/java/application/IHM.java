package application;

import controleur.Controleur;
import javafx.application.Application;
import javafx.stage.Stage;
import views.FabriqueVues;
import views.jfx.FabriqueVuesJFX;

public class IHM extends Application {

    @Override
    public void start(Stage primaryStage)  {
        FabriqueVues fabriqueVues = new FabriqueVuesJFX(primaryStage);
        new Controleur(fabriqueVues);
    }
    void go(String[] args){
        launch(args);
    }

}
