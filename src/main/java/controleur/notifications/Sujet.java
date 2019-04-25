package controleur.notifications;

public interface Sujet {

    void enregistrerObservateur(Observateur s);
    void broadCastNotification(Notification n);
}
