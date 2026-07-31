package it.unibo.tutoring.view.components;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

class NavigationHelperTest {

    @Test
    void loggedInUsersGoToDashboardFromBrandLink() {
        assertFalse(NavigationHelper.shouldOpenDashboard(false));
        assertTrue(NavigationHelper.shouldOpenDashboard(true));
    }
}
