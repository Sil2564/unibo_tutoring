package it.unibo.tutoring.event;

public class SessionCompletedEvent implements DomainEvent {

    private final String tutorMatricola;
    private final int completedHours;

    public SessionCompletedEvent(final String tutorMatricola, final int completedHours) {
        this.tutorMatricola = tutorMatricola;
        this.completedHours = completedHours;
    }

    public String getTutorMatricola() {
        return tutorMatricola;
    }

    public int getCompletedHours() {
        return completedHours;
    }

    @Override
    public String getEventName() {
        return "SessionCompleted";
    }
}
