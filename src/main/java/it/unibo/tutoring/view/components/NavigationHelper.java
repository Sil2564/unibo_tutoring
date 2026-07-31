package it.unibo.tutoring.view.components;

import it.unibo.tutoring.CurrentSession;
import it.unibo.tutoring.UniBoTutoringDashboardApp;
import it.unibo.tutoring.UniBoTutoringHomeApp;
import it.unibo.tutoring.UniBoTutoringLoginApp;
import it.unibo.tutoring.UniBoTutoringRegistrationApp;
import javafx.stage.Stage;

public final class NavigationHelper {

    private NavigationHelper() {
    }

    public static boolean shouldOpenDashboard(final boolean loggedIn) {
        return loggedIn;
    }

    public static void goToHomeOrDashboard(final Stage stage) {
        if (shouldOpenDashboard(CurrentSession.isLoggedIn())) {
            goToDashboard(stage);
            return;
        }
        goToHome(stage);
    }

    public static void goToDashboard(final Stage stage) {
        stage.setScene(UniBoTutoringDashboardApp.createScene());
        stage.setTitle("UniBo Tutoring - Dashboard");
        WindowUtil.maximize(stage);
    }

    public static void goToHome(final Stage stage) {
        stage.setScene(UniBoTutoringHomeApp.createScene());
        stage.setTitle("UniBo Tutoring - Home");
        WindowUtil.maximize(stage);
    }

    public static void goToLogin(final Stage stage) {
        if (shouldOpenDashboard(CurrentSession.isLoggedIn())) {
            goToDashboard(stage);
            return;
        }
        stage.setScene(UniBoTutoringLoginApp.createScene(stage));
        stage.setTitle("UniBo Tutoring - Login");
        WindowUtil.maximize(stage);
    }

    public static void goToRegistration(final Stage stage) {
        if (shouldOpenDashboard(CurrentSession.isLoggedIn())) {
            goToDashboard(stage);
            return;
        }
        stage.setScene(UniBoTutoringRegistrationApp.createScene(stage));
        stage.setTitle("UniBo Tutoring - Registrazione");
        WindowUtil.maximize(stage);
    }
}
