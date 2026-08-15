package it.unibo.tutoring;

public final class UniBoTutoringHomeLauncher {

    private UniBoTutoringHomeLauncher() {
    }

    public static void main(final String[] args) {
        // Aggiunto da Niki: Popolamento del database in automatico (cosi' i prof e i nuovi utenti vedono gli annunci di test!)
        DataSeeder.runIfEmpty();
        
        UniBoTutoringHomeApp.run(args);
    }
}
