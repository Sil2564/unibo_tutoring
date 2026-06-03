package it.unibo.tutoring;

import javafx.stage.Stage;

public final class UserSession {

    private UserSession() {}

    public static String getDisplayName() {
        final UserAccount user = CurrentSession.getUser();
        return user != null ? user.getName() + " " + user.getSurname() : "Utente";
    }

    public static String getMatricola() {
        final UserAccount user = CurrentSession.getUser();
        return user != null ? user.getMatricola() : null;
    }

    public static Runnable createLogoutAction(final Stage stage) {
        return () -> {
            CurrentSession.clear();
            stage.setScene(UniBoTutoringLoginApp.createScene(stage));
            stage.setTitle("UniBo Tutoring - Login");
        };
    }
}
