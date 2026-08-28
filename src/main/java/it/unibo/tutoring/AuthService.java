package it.unibo.tutoring;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * pubblic AuthService per renderla visibile anche ad altri package
 */
public final class AuthService {

    //definisco una classe statica interna per rappresentare il risultato della registrazione
    static final class RegistrationResult {
        private final boolean success; //memmorizza true per registrazione riuscita
        private final String message;  //memorizza un messaggio di errore

        RegistrationResult(final boolean success, final String message) {
            this.success = success;
            this.message = message;
        }

        boolean isSuccess() {
            return this.success;
        }

        String getMessage() {
            return this.message;
        }
    }

    //definisce il percorso del file CSV che memorizza gli utenti registrati e il separatore di campo utilizzato nel file CSV
    private static final Path STORAGE_PATH = Path.of("data", "users.csv");
    private static final String FIELD_SEPARATOR = ";";
    private static final AuthService INSTANCE = new AuthService(); //crea un'istanza singleton di AuthService

    //memorizza gli utenti registrati in due mappe: una mappa per matricola e una mappa per email
    private final Map<String, UserAccount> usersByMatricola = new HashMap<>();
    private final Map<String, UserAccount> usersByEmail = new HashMap<>();

    //costruttore privato per caricare gli utenti registrati dal file CSV
    private AuthService() {
        this.loadUsers(); //chiama il metodo loadUsers() per caricare gli utenti registrati dal file CSV
    }

    //restituisce l'istanza singleton di AuthService perchè 
    // il costruttore è privato e non può essere istanziato direttamente
    public static AuthService getInstance() {
        return INSTANCE;
    }

    /* metodo di registrazione che accetta i dati dell'utente e restituisce un oggetto RegistrationResult 
        che indica se la registrazione è riuscita o meno, insieme a un messaggio di errore in caso di fallimento. 
        Il metodo esegue le seguenti operazioni:
            1. Pulisce i dati dell'utente rimuovendo eventuali spazi bianchi all'inizio e alla fine dei campi.
            2. Verifica che la matricola sia composta da 10 cifre.
            3. Verifica che la password soddisfi i requisiti di sicurezza (almeno 6 caratteri, una maiuscola,
    */
    synchronized RegistrationResult register(
        final String name,
        final String surname,
        final String matricola,
        final String email,
        final String password,
        final String birthDate,
        final String corso
    ) {
        //pulizia dagli spazi bianchi 
        final String cleanName = name.trim();
        final String cleanSurname = surname.trim();
        final String cleanMatricola = matricola.trim();
        final String cleanEmail = email.trim().toLowerCase();

        if (!cleanMatricola.matches("\\d{10}")) {
            return new RegistrationResult(false, "La matricola deve contenere 10 cifre.");
        }
        if (!isPasswordValid(password)) {
            return new RegistrationResult(false, "La password deve avere almeno 6 caratteri, una maiuscola, una minuscola, un numero e un carattere speciale.");
        }
        if (this.usersByMatricola.containsKey(cleanMatricola)) {
            return new RegistrationResult(false, "La matricola è già registrata.");
        }
        if (this.usersByEmail.containsKey(cleanEmail)) {
            return new RegistrationResult(false, "L'email è già registrata.");
        }

        // Qui creiamo il nuovo utente passandogli tutti i dati inclusa la data di nascita e il corso di studi!
        final UserAccount user = new UserAccount(
            cleanName,
            cleanSurname,
            cleanMatricola,
            cleanEmail,
            hashPassword(password),
            birthDate,
            corso
        );
        /*struttura dati per memorizzare l'utente appena registrato in entrambe le mappe (per matricola e per email)
            o user e permette d individuare 
            subito la matricola corretta invece di scorrerle tutte */
        this.usersByMatricola.put(cleanMatricola, user);
        this.usersByEmail.put(cleanEmail, user);
        try {
            this.persistUsers();    //chiama il metodo persistUsers() per salvare i dati dell'utente appena registrato nel file CSV
            return new RegistrationResult(true, "Registrazione completata.");
        } catch (final IOException ex) {    //intercetta eventuali eccezioni di I/O durante il salvataggio dei dati e rimuove l'utente appena registrato dalle mappe
            this.usersByMatricola.remove(cleanMatricola);
            this.usersByEmail.remove(cleanEmail);
            return new RegistrationResult(false, "Errore nel salvataggio dei dati.");
        }
    }

    //synchronized permette di evitare problemi di concorrenza quando più thread accedono contemporaneamente al metodo authenticate
    synchronized boolean authenticate(final String matricola, final String password) {
        final String cleanMatricola = matricola.trim(); //rimuove spazi vuoti 
        if (!isPasswordValid(password)) {   //se la pwd non rispetta i requisiti di sicurezza, restituisce false
            return false;
        }
        //verifica se l'utente esiste nella mappa usersByMatricola e se la password fornita corrisponde all'hash della password memorizzata per quell'utente. 
        // Se entrambe le condizioni sono vere, restituisce true, altrimenti false.
        final UserAccount user = this.usersByMatricola.get(cleanMatricola);
        if (user == null) {
            return false;
        }//restituisce true se la password fornita corrisponde all'hash della password memorizzata per quell'utente
        return user.getPasswordHash().equals(hashPassword(password));
    }

    /**
     * Verifica se una password è valida.
     *
     * @param password la password da verificare
     * @return {@code true} se la password è valida, {@code false} altrimenti
     */
    public static boolean isPasswordValid(final String password) {
        if (password == null) {
            return false;
        }
        // Rimuove eventuali spazi bianchi all'inizio e alla fine della password
        final String cleanPassword = password.trim();
        // La password deve avere almeno 6 caratteri
        if (cleanPassword.length() < 6) {
            return false;
        }
        final boolean hasLowercase = password.matches(".*[a-z].*");
        final boolean hasUppercase = password.matches(".*[A-Z].*");        
        final boolean hasDigit = password.matches(".*[0-9].*");
        final boolean hasSpecialCharacter = password.matches(".*[!@#$%^&*()_+\\-=\\[\\]{}|;':\",./<>?].*");
        return hasLowercase && hasUppercase && hasDigit && hasSpecialCharacter;
    }

    //metodo privato per caricare profili utente 
    private void loadUsers() {
        try {
            if (!Files.exists(STORAGE_PATH)) {
                return;
            }
            //legge tutte le righe del file e restituisce una lista di stringhe, una per ogni riga del file
            final List<String> lines = Files.readAllLines(STORAGE_PATH, StandardCharsets.UTF_8);
            for (final String line : lines) { //ciclo per elaborare le righe del file 
                if (line.isBlank()) {
                    continue;
                }
                //divide la riga in sergmenti tramite il separatore definito e restituisce un array di stringhe, una per ogni campo della riga
                final String[] fields = line.split(FIELD_SEPARATOR, -1);
                // La colonna "presentazione" e' stata aggiunta in un secondo
                // momento: righe salvate prima di allora hanno solo 5 campi.
                if (fields.length < 5) {
                    continue;
                }
                final String presentazione = fields.length >= 6 ? unescapeFromCsv(fields[5]) : "";
                final String birthDate = fields.length >= 7 ? fields[6] : "";
                // Leggiamo l'ottavo campo (indice 7) per il corso. Se gli account vecchi non ce l'hanno, usiamo un fallback così non esplode niente
                final String corso = fields.length >= 8 ? fields[7] : "Non specificato";
                
                // Aggiunto da Niki: avatar path
                final String avatarPath = fields.length >= 9 ? fields[8] : "";
                //inizializza un oggetto user con i 5 campi obbligatori e gli opsìzionali
                final UserAccount user = new UserAccount(fields[0], fields[1], fields[2], fields[3], fields[4], birthDate, corso, presentazione, avatarPath);
                this.usersByMatricola.put(user.getMatricola(), user); //registra utente 
                this.usersByEmail.put(user.getEmail(), user); //registra utente usando email come chiave
            } 
        } catch (final IOException ex) { //Intercetta eventuali errori di lettura del file
            
            //in caso di errore svuota le mappe matricola e mail per non lasciare lo stato parzialmente caricato
            this.usersByMatricola.clear();  
            this.usersByEmail.clear();
        }
    }

    private void persistUsers() throws IOException {
        Files.createDirectories(STORAGE_PATH.getParent());
        final List<String> rows = this.usersByMatricola.values().stream()
            .map(user -> String.join(
                FIELD_SEPARATOR,
                user.getName(),
                user.getSurname(),
                user.getMatricola(),
                user.getEmail(),
                user.getPasswordHash(),
                sanitizeForCsv(user.getPresentazione()),
                user.getBirthDate() == null ? "" : user.getBirthDate(),
                // Salviamo anche il corso in coda al CSV!
                user.getCorso() == null ? "" : user.getCorso(),
                // Aggiunto da Niki: Salviamo l'avatar path!
                user.getAvatarPath() == null ? "" : user.getAvatarPath()
            ))
            .sorted()
            .toList();
        final String content = rows.isEmpty()
            ? ""
            : String.join(System.lineSeparator(), rows) + System.lineSeparator();
        Files.writeString(STORAGE_PATH, content, StandardCharsets.UTF_8);
    }

    private static String sanitizeForCsv(final String value) {
        if (value == null) {
            return "";
        }
        // Non eliminiamo gli a-capo: li "escapiamo" cosi' una presentazione
        // su piu' righe si legge per intero anche dopo essere stata salvata
        // su un'unica riga di data/users.csv.
        return value.replace(FIELD_SEPARATOR, ",")
            .replace("\r\n", "\n")
            .replace("\n", "\\n")
            .trim();
    }

    private static String unescapeFromCsv(final String value) {
        return value == null ? "" : value.replace("\\n", "\n");
    }

    /**
     * Aggiorna la presentazione di un utente e la persiste subito su
     * data/users.csv, cosi' resta modificabile in qualunque momento dal
     * proprio profilo e sopravvive al riavvio dell'applicazione.
     */
    public synchronized boolean updatePresentazione(final String matricola, final String presentazione) {
        if (matricola == null) {
            return false;
        }
        final UserAccount user = this.usersByMatricola.get(matricola.trim());
        if (user == null) {
            return false;
        }
        user.setPresentazione(presentazione);
        try {
            this.persistUsers();
            return true;
        } catch (final IOException ex) {
            return false;
        }
    }

    public synchronized boolean updatePassword(final String matricola, final String newPassword) {
        if (matricola == null || !isPasswordValid(newPassword)) {
            return false;
        }
        final UserAccount user = this.usersByMatricola.get(matricola.trim());
        if (user == null) {
            return false;
        }
        final UserAccount updatedUser = new UserAccount(
            user.getName(),
            user.getSurname(),
            user.getMatricola(),
            user.getEmail(),
            hashPassword(newPassword),
            user.getBirthDate(),
            user.getCorso(),
            user.getPresentazione(),
            user.getAvatarPath()
        );
        this.usersByMatricola.put(updatedUser.getMatricola(), updatedUser);
        this.usersByEmail.put(updatedUser.getEmail(), updatedUser);
        try {
            this.persistUsers();
            return true;
        } catch (final IOException ex) {
            this.usersByMatricola.put(user.getMatricola(), user);
            this.usersByEmail.put(user.getEmail(), user);
            return false;
        }
    }
    
    // Aggiunto da Niki: salva permanentemente le modifiche apportate direttamente al profilo (es. corso, avatar, data di nascita)
    public void saveChanges() {
        try {
            persistUsers();
        } catch (final IOException ex) {
            // Ignoriamo temporaneamente l'errore se non si può scrivere, come per le altre operazioni
        }
    }

    // Metodo privato per calcolare l'hash della password utilizzando SHA-256
    private static String hashPassword(final String password) {
        try {
            //inizializza un oggetto MessageDigest con l'algoritmo SHA-256 e calcola l'hash della password fornita come input.
            final MessageDigest digest = MessageDigest.getInstance("SHA-256");
            //converte la password in un array di byte utilizzando la codifica UTF-8 e calcola l'hash della password.
            final byte[] hash = digest.digest(password.getBytes(StandardCharsets.UTF_8));
            //crea un string builder per costruire la rappresentazione esadecimale dell'hash e restituisce la stringa risultante
            final StringBuilder builder = new StringBuilder(hash.length * 2);
            for (final byte b : hash) { //itera su tutti i byte
                builder.append(String.format("%02x", b)); //converte un byte in stringa esadecimale a 2 cifre
            }
            return builder.toString(); //restituisce la rappresentazione esadecimale dell'hash della password
        } catch (final NoSuchAlgorithmException ex) {
            //se l'algoritmo SHA-256 non è disponibile, viene generata un'eccezione IllegalStateException con un messaggio di errore
            throw new IllegalStateException("SHA-256 non disponibile", ex);
        }
    }
    public UserAccount getUser(final String matricola){ 
    return this.usersByMatricola.get(matricola.trim());
    }
    // metodo per effettuare il login di un utente utilizzando l'identificatore (matricola o email) e la password forniti come input.
    public UserAccount login(final String identifier, final String password) {
    if (identifier == null || password == null) { //contralla che nessun campo sia nullo
        return null; //altrimenti interrompi operazione
    }

    final String cleanIdentifier = identifier.trim(); //rimuove eventuali spazi bianchi all'inizio e alla fine dell'identificatore
    final UserAccount user = cleanIdentifier.contains("@") //se stringa ha la @, sarà interpretata come email
        ? this.usersByEmail.get(cleanIdentifier.toLowerCase())
        : this.usersByMatricola.get(cleanIdentifier);

        //L'hash salvato nel profilo utente deve coincidere esattamente con l'hash calcolato al volo sulla password appena fornita
    return user != null && user.getPasswordHash().equals(hashPassword(password))
        ? user
        : null;
}
}