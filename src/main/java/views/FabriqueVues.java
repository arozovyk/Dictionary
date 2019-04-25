package views;

import controleur.Controleur;

public interface FabriqueVues {

    MenuPrincipalInterface buildMenuPrincipalView(Controleur c);

    AccueilInterface buildAccueilView(Controleur c);



}
