package it.unibo.tutoring;

public final class UserAccount {

     final String name;
     final String surname;
     final String matricola;
     final String email;
     final String passwordHash;

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
