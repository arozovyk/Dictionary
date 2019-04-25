package views.jfx;

import controleur.Controleur;
import javafx.stage.Stage;
import views.*;

public class FabriqueVuesJFX implements FabriqueVues {

    Stage primaryStage;

    public FabriqueVuesJFX(Stage primaryStage) {
        this.primaryStage = primaryStage;
    }

    @Override
    public MenuPrincipalInterface buildMenuPrincipalView(Controleur c) {
        return MenuPrincipal.creerInstance(c,primaryStage);

    }

    @Override
    public AccueilInterface buildAccueilView(Controleur c) {
        return AccueilView.creerInstance(c,primaryStage);
    }



}
