package pl.pwr.bikeplanner.accounts.application;

public class EmailAlreadyUsedException extends RuntimeException {

    public EmailAlreadyUsedException(String email) {
        super("Account with email " + email + " already exists");
    }
}
