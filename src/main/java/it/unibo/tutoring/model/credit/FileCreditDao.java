package it.unibo.tutoring.model.credit;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Properties;

public class FileCreditDao implements CreditDao {

    private static final String DATA_DIR = "data";
    private static final String FILE_PATH = DATA_DIR + "/credits.properties";
    private final Properties properties;

    public FileCreditDao() {
        this.properties = new Properties();
        ensureFileExists();
        loadProperties();
    }

    private void ensureFileExists() {
        final File dir = new File(DATA_DIR);
        if (!dir.exists()) {
            dir.mkdirs();
        }
        final File file = new File(FILE_PATH);
        if (!file.exists()) {
            try {
                file.createNewFile();
            } catch (final IOException e) {
                System.err.println("Errore nella creazione del file salvataggio crediti: " + e.getMessage());
            }
        }
    }

    private void loadProperties() {
        try (FileInputStream in = new FileInputStream(FILE_PATH)) {
            properties.load(in);
        } catch (final IOException e) {
            System.err.println("Errore nel caricamento dei crediti: " + e.getMessage());
        }
    }

    private void saveProperties() {
        try (FileOutputStream out = new FileOutputStream(FILE_PATH)) {
            properties.store(out, "Salvataggio Ore Tutoraggio");
        } catch (final IOException e) {
            System.err.println("Errore nel salvataggio dei crediti: " + e.getMessage());
        }
    }

    @Override
    public int getUserHours(final String matricola) {
        final String value = properties.getProperty(matricola);
        if (value != null) {
            try {
                return Integer.parseInt(value);
            } catch (final NumberFormatException e) {
                return 0;
            }
        }
        return 0;
    }

    @Override
    public void saveUserHours(final String matricola, final int totalHours) {
        properties.setProperty(matricola, String.valueOf(totalHours));
        saveProperties();
    }
}
