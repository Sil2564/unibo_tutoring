package it.unibo.tutoring.model.credit;

public class DefaultBadgePolicy implements BadgePolicy {

    private static final int EXPERT_THRESHOLD = 50;
    private static final int INTERMEDIATE_THRESHOLD = 20;

    @Override
    public Badge calculateBadge(final int totalHours) {
        if (totalHours >= EXPERT_THRESHOLD) {
            return Badge.EXPERT;
        }
        if (totalHours >= INTERMEDIATE_THRESHOLD) {
            return Badge.INTERMEDIATE;
        }
        return Badge.BEGINNER;
    }

    @Override
    public int getNextThreshold(final int totalHours) {
        if (totalHours < INTERMEDIATE_THRESHOLD) {
            return INTERMEDIATE_THRESHOLD;
        }
        if (totalHours < EXPERT_THRESHOLD) {
            return EXPERT_THRESHOLD;
        }
        // If they are already expert, the next threshold is technically achieved.
        // We return their current hours so progress shows 100%.
        return totalHours;
    }
}
