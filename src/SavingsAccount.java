import java.time.LocalDate;

public class SavingsAccount extends Account { //inheritance
    private double interestRate;
    private LocalDate creationDate; //Vadeli hesap açılış tarihi
    private int maturityDuration; //Vade süresi
    private boolean isInterestLost; //Vade hakkı kayboldu mu?

    public SavingsAccount(String accountNumber, String customerName, double interestRate, int maturityDuration, NotificationService notificationService) {
        super(accountNumber, customerName, notificationService); //makes connection with Account
        if (maturityDuration < 0) {
            throw new IllegalArgumentException("Maturity duration cannot be negative.");
        }
        this.interestRate = interestRate;
        this.maturityDuration = maturityDuration;
        this.isInterestLost = false;
        this.creationDate = LocalDate.now();
    }

    @Override
    public boolean deposit(double amount) {
       if (super.deposit(amount)) {
           String message = "An amount of " + amount + " TL has been deposited into your account numbered " + getAccountNumber() + ". Current balance: " + getBalance() + " TL";
           triggerNotification(message);
           return true;
       } else {
           return false ;
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
                // Faiz hesaplaması
                double interestAmount = getBalance() * interestRate;
                // Hesaplanan faiz Account sınıfındaki metoda gider
                this.isInterestLost = true; //faiz getirisi bir kere eklendi!
                System.out.println("[SUCCESS]: Maturity period expired. Accrued interest has been added.");
            }
        }

        boolean isSuccess = super.withdraw(amount);

        if (isSuccess) {
            String emailMessage = "A withdrawal transaction has been executed from your account numbered " + getAccountNumber() + ".";
            triggerNotification(emailMessage);
        }
        return isSuccess;
    }
}
