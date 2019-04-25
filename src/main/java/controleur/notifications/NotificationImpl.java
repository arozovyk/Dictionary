package controleur.notifications;

import views.*;

public class NotificationImpl implements Notification {

    String message;
    TypeNotification typeNotification;

    protected NotificationImpl(String message, TypeNotification typeNotification) {
        this.message = message;
        this.typeNotification = typeNotification;
    }

    protected NotificationImpl(TypeNotification typeNotification) {
        this("",typeNotification);
    }

    @Override
    public String getMessage() {
        return message;
    }

    @Override
    public TypeNotification getTypeNotification() {
        return typeNotification;
    }


}
