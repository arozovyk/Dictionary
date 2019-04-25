package controleur.notifications.update;

import controleur.notifications.NotificationImpl;

import java.util.Collection;

public class NotificationUpdateParierInterface extends NotificationImpl {



    Collection<String> equipes;
    public NotificationUpdateParierInterface(Collection<String> collection) {
        super(TypeNotification.PARIS_MAJ_EQUIPES);
        equipes = collection;
    }


}
