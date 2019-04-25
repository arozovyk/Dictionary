package views;

import controleur.notifications.Observateur;

public interface AccueilInterface extends Observateur {

    void show();

    void informeUtilisateurErreur(String message);
}
