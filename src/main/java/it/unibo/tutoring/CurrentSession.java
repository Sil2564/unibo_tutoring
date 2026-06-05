package it.unibo.tutoring;

public final class CurrentSession {

    private static UserAccount currentUser;

    private CurrentSession() {}

    public static void setUser(final UserAccount user) {
        currentUser = user;
    }

    public static UserAccount getUser() {
        return currentUser;
    }

    public static void clear() {
        currentUser = null;
    }

    public static boolean isLoggedIn() {
        return currentUser != null;
    }
}