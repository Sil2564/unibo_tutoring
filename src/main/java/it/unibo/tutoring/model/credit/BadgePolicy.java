package it.unibo.tutoring.model.credit;

/**
 * Strategy pattern interface for calculating badges based on total tutoring hours.
 */
public interface BadgePolicy {
    
    /**
     * Calculates the badge to assign.
     * @param totalHours the total completed hours.
     * @return the calculated Badge.
     */
    Badge calculateBadge(int totalHours);

    /**
     * Calculates the hours needed for the next badge threshold.
     * @param totalHours the total completed hours.
     * @return the hours of the next threshold, or the current hours if max badge reached.
     */
    int getNextThreshold(int totalHours);
}
