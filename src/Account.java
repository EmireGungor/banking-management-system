public abstract class Account {
    private String accountNumber;
    private String customerName;
    private double balance;
    private NotificationService notificationService;
//Construct
    public Account(String accountNumber, String customerName, NotificationService notificationService) {
        this.accountNumber = accountNumber;
        this.customerName = customerName;
        this.notificationService = notificationService;
        this.balance = 0.0; //varsayılan
    }
    protected void triggerNotification(String message){
        if(notificationService != null){
            notificationService.sendNotification(message);
        }
    }
//Getter
    public String getAccountNumber() {
        return this.accountNumber;
    }
    public String getCustomerName() {
        return this.customerName;
    }
    public double getBalance() {
        return this.balance;
    }
//Setter
    public void setCustomerName(String customerName) {
        this.customerName = customerName;
    }
//Deposit and withdraw methods
    public boolean deposit(double amount) {
        if(amount > 0) {
            this.balance += amount;
            return true;
        } else {
            System.out.println("[ERROR]: Deposit amount must be greater than zero.");
            return false;
        }
    }
    public boolean withdraw(double amount) {
        if (amount <= 0) {
            System.out.println("[ERROR]: Withdrawal amount must be greater than zero.");
            return false;
        } else if(amount > balance) {
            System.out.println("[ERROR]: Transaction failed. Insufficient funds.");
            return false;
        } else {
            this.balance -= amount;
            return true;
        }
    }
}
