import java.time.LocalDate;

public class CheckingAccount extends Account { //inheritance
    private final double dailyLimit; // Günlük limit
    private double dailyWithdrawnAmount; // Günlük çekilen toplam
    private LocalDate lastTransactionDate; // Son işlem tarihi
//Constructor
    public CheckingAccount(String accountNumber, String customerName, double dailyLimit) {
        super(accountNumber, customerName, new SmsNotification());
        this.dailyLimit = dailyLimit;
        this.dailyWithdrawnAmount = 0.0; //varsayılan
        this.lastTransactionDate = LocalDate.now(); //ilk tarih bugün
    }

    @Override
    public void deposit(double amount) {
        super.deposit(amount);
        String message = "An amount of " + amount + " TL has been deposited into your account numbered " + getAccountNumber() + ". Current balance: " + getBalance() + " TL";
        triggerNotification(message);
    }

    @Override
    public boolean withdraw(double amount) {
        //Gün değişikliği kontrolü (Tarih farklıysa sıfırla)
        if(!LocalDate.now().equals(lastTransactionDate)) {
            this.dailyWithdrawnAmount = 0.0;
            this.lastTransactionDate = LocalDate.now();
        }
        //Limit ve Bakiye
        //Limit aşımı olduğunda
        if (amount > dailyLimit || (this.dailyWithdrawnAmount + amount > dailyLimit)) {
            System.out.println("[ERROR]: Transaction denied. Daily withdrawal limit exceeded.");
            return false;
        }
        //Limit uygun hesapta yeteri kadar para var mı?
        if (super.withdraw(amount)) {
            this.dailyWithdrawnAmount += amount;
            System.out.println("[SUCCESS]: Transaction completed. Current daily total withdrawal: " + dailyWithdrawnAmount);
            return true;
        } else {
            return false;
        }
    }
}