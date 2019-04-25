package controleur.notifications;

import controleur.notifications.update.NotificationUpdateParierInterface;
import controleur.notifications.update.NotificationUpdateRecapAnnulerInterface;
import controleur.notifications.update.Visiteur;

import java.util.ArrayList;
import java.util.Collection;

public interface Notification extends Visiteur {

    static Notification creerMajEquipe(Collection<String> liste) {
        return new NotificationUpdateParierInterface(liste);
    }




    enum TypeNotification {RESULTAT_SUCCES,CONNEXION_ERREUR, ANNULER_PARI_ERREUR, PARIER_ERREUR, ANNULER_MAJ_RECAP, PARIS_MAJ_EQUIPES,RESULTAT_ERREUR}


  static Notification creer(String message, TypeNotification typeNotification) {
      return new NotificationImpl(message, typeNotification);
  }

  static Notification creer(TypeNotification typeNotification) {
        return new NotificationImpl(typeNotification);
  }

    String getMessage();
    TypeNotification getTypeNotification();


}
