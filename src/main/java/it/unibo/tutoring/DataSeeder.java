package it.unibo.tutoring;

import java.time.LocalDate;
import java.time.LocalTime;

import it.unibo.tutoring.model.box.BoxRepository;
import it.unibo.tutoring.model.box.BoxTutoraggioImpl;
import it.unibo.tutoring.model.box.BoxType;
import it.unibo.tutoring.model.box.TitoloAnnuncioGenerator;

/**
 * Aggiunto da Niki: 
 * Questa classe si occupa di popolare automaticamente il database al primissimo avvio
 * in modo da non avere mai la dashboard vuota quando un nuovo utente entra.
 * Crea sia gli utenti base (con la password di default) sia i loro relativi annunci!
 */
public class DataSeeder {

    public static void runIfEmpty() {
        // Se ci sono già degli annunci nel sistema, evitiamo di duplicarli
        if (!BoxRepository.getAllBoxes().isEmpty()) {
            return;
        }

        System.out.println("Nessun annuncio presente: avvio seeding automatico della dashboard...");

        // Aggiunto da Niki: Registriamo tutti gli utenti necessari per farli combaciare con gli annunci.
        // La password per tutti sarà 'Password123' di default.
        registerUser("Marco", "Fabbri", "0011223344", "Architettura");
        registerUser("Giulia", "Neri", "0011223355", "Biomedical Engineering");
        registerUser("Alessandro", "Ferretti", "0011223366", "Digital Transformation Management");
        registerUser("Elena", "Santini", "0011223377", "Ingegneria biomedica");
        registerUser("Luca", "Moretti", "0011223388", "Ingegneria e scienze informatiche");
        registerUser("Sara", "Bellini", "0011223399", "Ingegneria elettronica");
        registerUser("Davide", "Costa", "0011224400", "Ingegneria elettronica per l'intelligenza artificiale");
        registerUser("Chiara", "Villani", "0011224411", "Neuroscienze e riabilitazione neuropsicologica");
        registerUser("Francesca", "Serra", "0011224422", "Psicologia clinica");
        registerUser("Martina", "Rinaldi", "0011224433", "Psicologia scolastica e di comunità");
        registerUser("Andrea", "Testa", "0011224444", "Scienze e cultura della gastronomia");
        registerUser("Valentina", "Longo", "0011224455", "Scienze e tecnologie alimentari");
        registerUser("Matteo", "Barbieri", "0011224466", "Tecnologie alimentari");
        registerUser("Giorgia", "Pellegrini", "0011224477", "Tecnologie dei sistemi informatici");
        registerUser("Simone", "Galli", "0011224488", "Viticoltura ed enologia");
        registerUser("Beatrice", "Marchetti", "0011224499", "Work, Organizational and Personnel Psychology");

        // Aggiunto da Niki: Carichiamo finalmente i 16 annunci richiesti nella dashboard
        addBox(BoxType.OFFER, "Architettura", "Progettazione Architettonica", "Offro supporto sui progetti di laboratorio, dal concept alle tavole finali. Ho già sostenuto Progettazione 2 con lode.", "2026-07-03", "0011223344");
        addBox(BoxType.REQUEST, "Biomedical Engineering", "Biomeccanica", "Cerco aiuto per capire i modelli di analisi del movimento e le equazioni della dinamica applicate al corpo umano.", "2026-07-08", "0011223355");
        addBox(BoxType.OFFER, "Digital Transformation Management", "Data Analytics", "Spiego analisi dei dati con Excel e Python base, utile per i case study del corso. Disponibile su prenotazione.", "2026-07-11", "0011223366");
        addBox(BoxType.REQUEST, "Ingegneria biomedica", "Strumentazione Biomedica", "Ho bisogno di un ripasso su sensori e trasduttori per sistemi biomedicali prima dell'orale.", "2026-06-25", "0011223377");
        addBox(BoxType.OFFER, "Ingegneria e scienze informatiche", "Algoritmi e Strutture Dati", "Ripetizioni su alberi, grafi e complessità computazionale, con esercizi pratici in Java.", "2026-07-14", "0011223388");
        addBox(BoxType.REQUEST, "Ingegneria elettronica", "Campi Elettromagnetici", "Cerco supporto sulle equazioni di Maxwell e la propagazione delle onde, argomento ostico dell'esame.", "2026-07-01", "0011223399");
        addBox(BoxType.OFFER, "Ingegneria elettronica per l'intelligenza artificiale", "Reti Neurali", "Offro aiuto su reti neurali, backpropagation e progetti in Python/PyTorch. Ho seguito il corso l'anno scorso con 29.", "2026-07-19", "0011224400");
        addBox(BoxType.REQUEST, "Neuroscienze e riabilitazione neuropsicologica", "Neuropsicologia", "Vorrei ripassare le sindromi neuropsicologiche e i test diagnostici principali per l'esame di gennaio.", "2026-06-29", "0011224411");
        addBox(BoxType.OFFER, "Psicologia clinica", "Psicopatologia", "Aiuto nello studio del DSM-5 e dei principali quadri psicopatologici, con schemi riassuntivi.", "2026-07-06", "0011224422");
        addBox(BoxType.REQUEST, "Psicologia scolastica e di comunità", "Psicologia dell'Educazione", "Cerco un ripasso sui modelli di apprendimento e le teorie dello sviluppo cognitivo a scuola.", "2026-07-16", "0011224433");
        addBox(BoxType.OFFER, "Scienze e cultura della gastronomia", "Chimica degli Alimenti", "Spiego le basi di chimica applicata agli alimenti, utile per l'esame del primo anno.", "2026-06-22", "0011224444");
        addBox(BoxType.REQUEST, "Scienze e tecnologie alimentari", "Microbiologia degli Alimenti", "Ho bisogno di supporto sui processi di fermentazione e i patogeni alimentari prima dell'appello.", "2026-07-09", "0011224455");
        addBox(BoxType.OFFER, "Tecnologie alimentari", "Tecnologie di Conservazione degli Alimenti", "Offro ripetizioni sui metodi di conservazione (termici, chimici, fisici) con esempi industriali.", "2026-07-21", "0011224466");
        addBox(BoxType.REQUEST, "Tecnologie dei sistemi informatici", "Sicurezza Informatica", "Cerco aiuto sui concetti base di crittografia e sicurezza delle reti per l'esame di fine corso.", "2026-07-24", "0011224477");
        addBox(BoxType.OFFER, "Viticoltura ed enologia", "Chimica Enologica", "Aiuto su fermentazione alcolica e analisi chimiche del vino, ho già superato l'esame con 28.", "2026-07-04", "0011224488");
        addBox(BoxType.REQUEST, "Work, Organizational and Personnel Psychology", "Organizational Behavior", "Vorrei ripassare i modelli di comportamento organizzativo e le dinamiche di gruppo in azienda.", "2026-07-27", "0011224499");

        System.out.println("Seeding completato!");
    }

    private static void registerUser(String name, String surname, String matricola, String corso) {
        String email = name.toLowerCase() + "." + surname.toLowerCase() + "@studio.unibo.it";
        // Registrazione vera e propria usando l'infrastruttura dell'app! 
        // Data nascita fittizia per questi utenti generati
        AuthService.getInstance().register(name, surname, matricola, email, "Password123", "01/01/2000", corso);
    }

    private static void addBox(BoxType tipo, String corso, String materia, String note, String dataStr, String matricola) {
        String titolo = TitoloAnnuncioGenerator.generaTitolo(tipo, materia);
        // Costruiamo l'annuncio con i dati passati, durata fissa di 2 ore e data dinamica
        BoxTutoraggioImpl box = new BoxTutoraggioImpl(
            titolo, corso, materia, "", LocalDate.parse(dataStr), LocalTime.of(10, 0), 2, matricola, tipo, note
        );
        BoxRepository.addBox(box);
    }
}
