package it.unibo.tutoring.controller.profile;

import it.unibo.tutoring.UserAccount;
import it.unibo.tutoring.model.credit.CreditRecord;
import it.unibo.tutoring.model.credit.CreditService;
import it.unibo.tutoring.model.user.UserRepository;

public final class ProfileController {

    private final UserRepository userRepository;

    public ProfileController(final UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public UserAccount getCurrentUser() {
        return this.userRepository.getCurrentUser();
    }

    public CreditRecord getCreditRecord(final String matricola) {
        return it.unibo.tutoring.AppConfig.getInstance().getCreditService().getCreditRecord(matricola);
    }
}
