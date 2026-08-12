package it.unibo.tutoring.view.components;

import javafx.stage.Stage;

/** Pulsante standard per tornare alla Dashboard dalla vista corrente. */
public final class DashboardButton extends AppButton {

    public DashboardButton() {
        this("← Dashboard");
    }

    public DashboardButton(final String text) {
        super(text);
        getStyleClass().add("text-link");
        asSecondary();
        setOnAction(event -> NavigationHelper.goToDashboard(
                (Stage) getScene().getWindow()));
    }
}
