import java.time.LocalDate;

public class SavingsAccount extends Account { //inheritance
    private LocalDate creationDate; //Vadeli hesap açılış tarihi
    private double interestRate;
    private int maturityDuration; //Vade süresi
    private boolean isInterestLost;//Vade hakkı kayboldu mu?

    public SavingsAccount(String accountNumber, String customerName, int maturityDuration, NotificationService notificationService) {
        super(accountNumber, customerName, notificationService); //makes connection with Account
        if (maturityDuration <= 0) {
            throw new IllegalArgumentException("Maturity duration must be greater than 0.");
        }
        this.maturityDuration = maturityDuration;
        if (this.maturityDuration <= 32) {
            this.interestRate = 0.40; // %40 faiz
        } else if (this.maturityDuration <= 92) {
            this.interestRate = 0.45; // %45 faiz
        } else {
            this.interestRate = 0.48; // %48 faiz
        }
        this.isInterestLost = false;
        this.creationDate = LocalDate.now();
    }

    @Override
    public boolean deposit(double amount) {
        if (super.deposit(amount)) {
            System.out.println("[SYSTEM LOG] Deposit successful. Account: " + this.getAccountNumber() + " | Amount: " + amount + " | New Balance: " + this.getBalance());

            String customerMessage = "Dear customer, an amount of " + amount + " has been successfully deposited into your account.";
            triggerNotification(customerMessage);
            return true;
        } else {
            return false;
        }
    }

    @Override
    public boolean withdraw(double amount) {
        //vade dolum günü hesaplama
        LocalDate maturityDate = creationDate.plusDays(maturityDuration);

        //vade dolmadan önce
        if (LocalDate.now().isBefore(maturityDate)) {
            //faiz uygulanmadıysa
            if (!isInterestLost) {
                System.out.println("[WARNING]: Premature withdrawal executed prior to the maturity date. Accrued interest yield has been forfeited.");
                this.isInterestLost = true; //faiz getirisi kalıcı olarak iptal edildi!
            }
        } else {
            //vade dolduğunda
            if (!isInterestLost) {
                applyMaturityInterest();
            }
        }

        boolean isSuccess = super.withdraw(amount);

        if (isSuccess) {
            String emailMessage = "A withdrawal transaction has been executed from your account numbered " + getAccountNumber() + ".";
            triggerNotification(emailMessage);
        }
        return isSuccess;
    }

    private void applyMaturityInterest() {
        double currentBalance = getBalance();
        double interestAmount = currentBalance * this.interestRate;

        super.deposit(interestAmount);
        this.isInterestLost = true;

        System.out.println("[SUCCESS]: Maturity period expired. Accrued interest has been successfully added. Amount: " + interestAmount);
    }
}
