import java.time.LocalDate;

public class CheckingAccount extends Account { //inheritance
    private final double dailyLimit; // Günlük limit
    private double dailyWithdrawnAmount; // Günlük çekilen toplam
    private LocalDate lastTransactionDate; // Son işlem tarihi
    private boolean limitAdjusted = false;
    private String adjustmentMessage = "";
//Constructor
public CheckingAccount(String accountNumber, String customerName, double dailyLimit, NotificationService notificationService) {
    super(accountNumber, customerName, notificationService);
    this.dailyWithdrawnAmount = 0.0;
    this.lastTransactionDate = LocalDate.now();

    // BACKEND GÜVENLİK DUVARI
    if (dailyLimit < 0) {
        this.dailyLimit = 5000;
        this.limitAdjusted = true;
        this.adjustmentMessage = "Negative limit (" + dailyLimit + ") rejected. Defaulting to 5000.";
    } else if (dailyLimit > 20000) {
        this.dailyLimit = 20000;
        this.limitAdjusted = true;
        this.adjustmentMessage = "Limit " + dailyLimit + " exceeds max. Capping to 20000.";
    } else {
        this.dailyLimit = dailyLimit;
    }
}
    public boolean isLimitAdjusted() {
        return this.limitAdjusted;
    }

    public String getAdjustmentMessage() { // Bu zaten String, doğru
        return this.adjustmentMessage;
    }

    @Override
    public boolean deposit(double amount) {
        if (super.deposit(amount)) {
            System.out.println("[SYSTEM LOG] Deposit successful. Account: " + this.getAccountNumber() + " | Amount: " + amount + " | New Balance: " + this.getBalance());

            String customerMessage = "Dear customer, an amount of " + amount + " has been successfully deposited into your account.";
            triggerNotification(customerMessage);
            return true;
        } else {
            return false ;
        }
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
        if (this.dailyWithdrawnAmount + amount > dailyLimit) {
            System.out.println("[ERROR]: Transaction denied. Daily withdrawal limit exceeded.");
            return false;
        }
        //Limit uygun hesapta yeteri kadar para var mı?
        if (super.withdraw(amount)) {
            this.dailyWithdrawnAmount += amount;
            System.out.println("[SYSTEM LOG] Withdraw successful. Amount: " + amount + " | Current Daily Total: " + dailyWithdrawnAmount);

            String customerMessage = "Dear customer, a withdrawal of " + amount + " USD/TL has been successfully made from your account.";
            triggerNotification(customerMessage);
            return true;
        } else {
            return false;
        }
    }
}