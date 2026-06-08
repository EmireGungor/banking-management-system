import java.util.HashMap;
import java.util.Map;

public class Bank {

    private Map<String, Account> accounts = new HashMap<>();

    public boolean addAccount(Account account) {
        String accountNumber = account.getAccountNumber();
        if (accounts.containsKey(accountNumber)) {
            return false;
        }
        accounts.put(accountNumber, account);
        return true;
    }

    public boolean isAccountExists(String accountNumber) {
        return accounts.containsKey(accountNumber);
    }

    public Account findAccount(String accountNo) {
        return accounts.get(accountNo);
    }

    public double getTotalBankBalance() {
        double total = 0.0;
        for (Account account : accounts.values()) { //for-each
            total += account.getBalance();
        }
        return total;
    }

    public void displayAllAccounts() {
        // Kayıtlı hesap yoksa çalıştırma!
        if (accounts.isEmpty()) {
            System.out.println("[SYSTEM LOG]: No accounts registered in the system yet.");
            return;
        }

        System.out.println("--- ALL REGISTERED ACCOUNTS ---");

        for (Account account : accounts.values()) {
            System.out.println("Account No: " + account.getAccountNumber() +
                    " | Owner: " + account.getCustomerName() +
                    " | Balance: " + account.getBalance());
        }

        System.out.println("----------------------------------------");
    }
}