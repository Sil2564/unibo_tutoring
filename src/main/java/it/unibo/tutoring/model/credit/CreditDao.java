package it.unibo.tutoring.model.credit;

public interface CreditDao {
    /**
     * Gets the total hours completed by a user.
     * @param matricola the user identifier.
     * @return the total hours, or 0 if no record exists.
     */
    int getUserHours(String matricola);

    /**
     * Saves or updates the total hours completed by a user.
     * @param matricola the user identifier.
     * @param totalHours the total hours to save.
     */
    void saveUserHours(String matricola, int totalHours);
}
