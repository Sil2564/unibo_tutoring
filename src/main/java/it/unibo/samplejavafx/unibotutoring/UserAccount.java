package it.unibo.samplejavafx.unibotutoring;

final class UserAccount {

    private final String name;
    private final String surname;
    private final String matricola;
    private final String email;
    private final String passwordHash;

    UserAccount(
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

    String getName() {
        return this.name;
    }

    String getSurname() {
        return this.surname;
    }

    String getMatricola() {
        return this.matricola;
    }

    String getEmail() {
        return this.email;
    }

    String getPasswordHash() {
        return this.passwordHash;
    }
}
