package it.unibo.tutoring;

final class CurrentSession {

    private static UserAccount currentUser;

    private CurrentSession() {}

    static void setUser(final UserAccount user) {
        currentUser = user;
    }

    static UserAccount getUser() {
        return currentUser;
    }

    static void clear() {
        currentUser = null;
    }

    static boolean isLoggedIn() {
        return currentUser != null;
    }
}