package it.unibo.tutoring;

public final class UserAccount {

    private final String name;
    private final String surname;
    private final String matricola;
    private final String email;
    private final String passwordHash;

    public UserAccount(
        final String name,
        final String surname,
        final String matricola,
        final String email,
        final String passwordHash
    ) {
        this.name = name;
        this.surname = surname;
        this.matricola = matricola;
        this.email = email;
        this.passwordHash = passwordHash;
    }

    public String getName() {
        return this.name;
    }

    public String getSurname() {
        return this.surname;
    }

    public String getMatricola() {
        return this.matricola;
    }

    public String getEmail() {
        return this.email;
    }

    public String getPasswordHash() {
        return this.passwordHash;
    }
}
