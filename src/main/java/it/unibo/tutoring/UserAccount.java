package it.unibo.tutoring;

public final class UserAccount {

    final String name;
    final String surname;
    final String matricola;
    final String email;
    final String passwordHash;
    // Aggiunto per tracciare la data di nascita (GG/MM/AAAA) richiesta durante la registrazione
    final String birthDate;
    // Aggiunto per salvare il corso di studi selezionato dal menu a tendina
    final String corso;
    
    private String presentazione;

    public UserAccount(
        final String name,
        final String surname,
        final String matricola,
        final String email,
        final String passwordHash,
        final String birthDate,
        final String corso
    ) {
        this(name, surname, matricola, email, passwordHash, birthDate, corso, "");
    }

    public UserAccount(
        final String name,
        final String surname,
        final String matricola,
        final String email,
        final String passwordHash,
        final String birthDate,
        final String corso,
        final String presentazione
    ) {
        this.name = name;
        this.surname = surname;
        this.matricola = matricola;
        this.email = email;
        this.passwordHash = passwordHash;
        this.birthDate = birthDate == null ? "" : birthDate;
        this.corso = corso == null ? "Non specificato" : corso;
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

    // Metodo per recuperare il corso salvato nel profilo
    public String getCorso() {
        return this.corso;
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