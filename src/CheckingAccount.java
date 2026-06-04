import java.time.LocalDate;

public class CheckingAccount extends Account { //inheritance
    private final double dailyLimit; // Günlük limit
    private double dailyWithdrawnAmount; // Günlük çekilen toplam
    private LocalDate lastTransactionDate; // Son işlem tarihi
//Constructor
public CheckingAccount(String accountNumber, String customerName, double dailyLimit, NotificationService notificationService) {
    super(accountNumber, customerName, notificationService);
    this.dailyWithdrawnAmount = 0.0;
    this.lastTransactionDate = LocalDate.now();

    // BACKEND GÜVENLİK DUVARI
    if (dailyLimit < 0) {
        this.dailyLimit = 5000; // Negatif sızarsa varsayılan limit
    } else if (dailyLimit > 20000) {
        this.dailyLimit = 20000; // Üst sınır aşılırsa maksimum tavan limit
    } else {
        this.dailyLimit = dailyLimit; // Normalse kabul et
    }
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
            String message = "[SUCCESS]: Transaction completed. Current daily total withdrawal: " + dailyWithdrawnAmount;
            triggerNotification(message);
            return true;
        } else {
            return false;
        }
    }
}