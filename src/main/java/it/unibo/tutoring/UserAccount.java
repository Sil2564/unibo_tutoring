package it.unibo.tutoring;

public final class UserAccount {

    final String name;
    final String surname;
    final String matricola;
    final String email;
    final String passwordHash;
    final String birthDate;
    private String presentazione;

    public UserAccount(
        final String name,
        final String surname,
        final String matricola,
        final String email,
        final String passwordHash,
        final String birthDate
    ) {
        this(name, surname, matricola, email, passwordHash, birthDate, "");
    }

    public UserAccount(
        final String name,
        final String surname,
        final String matricola,
        final String email,
        final String passwordHash,
        final String birthDate,
        final String presentazione
    ) {
        this.name = name;
        this.surname = surname;
        this.matricola = matricola;
        this.email = email;
        this.passwordHash = passwordHash;
        this.birthDate = birthDate == null ? "" : birthDate;
        this.presentazione = presentazione == null ? "" : presentazione;
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

    public String getBirthDate() {
        return this.birthDate;
    }

    /**
     * Breve presentazione libera che l'utente puo' scrivere e modificare in
     * qualsiasi momento dal proprio profilo (es. "Sono uno studente di 24
     * anni di Cesena, mi sto laureando in ..."). Vuota di default.
     */
    public String getPresentazione() {
        return this.presentazione;
    }

    public void setPresentazione(final String presentazione) {
        this.presentazione = presentazione == null ? "" : presentazione;
    }
}