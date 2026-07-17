package it.unibo.tutoring.model.user;

import it.unibo.tutoring.CurrentSession;
import it.unibo.tutoring.UserAccount;

public final class UserRepository {

    public UserRepository() {
    }

    public UserAccount getCurrentUser() {
        // Al momento recupera dalla sessione, in futuro potrebbe leggere dal DB
        return CurrentSession.getUser();
    }
}
