import java.util.Scanner;

public class Main {
    public static void main(String[] args) {
        Bank bank = new Bank();
        Scanner scanner = new Scanner(System.in);

        System.out.println("--- WELCOME TO THE BANKING MANAGEMENT SYSTEM ---");

        while (true) {
            // Complete English terminal menu framework
            System.out.println("\n--- BANKING MANAGEMENT SYSTEM ---");
            System.out.println("1- Define Checking Account");
            System.out.println("2- Define Savings Account");
            System.out.println("3- Deposit Money");
            System.out.println("4- Withdraw Money");
            System.out.println("5- Display Account Details & Balances");
            System.out.println("6- Secure Exit");
            System.out.print("Your Selection: ");

            int choice = getIntInput(scanner);
            if (choice == -1) continue;

            switch (choice) {
                case 1: // Define Checking Account
                    String checkingName = getValidName(scanner);

                    //FAIL-FAST ACCOUNT NUMBER VALIDATION LOOP
                    String checkingNo;
                    while (true) {
                        checkingNo = getValidAccountNo(scanner); // Format check

                        if (bank.isAccountExists(checkingNo)) {
                            System.out.println("\n[ERROR]: Account creation failed! Account number " + checkingNo + " already exists in the system.");
                        } else {
                            break;
                        }
                    }

                    NotificationService checkingNotification = getNotificationChoice(scanner);

                    CheckingAccount checkingAccount = new CheckingAccount(checkingNo, checkingName, 5000.0, checkingNotification);
                    bank.addAccount(checkingAccount);

                    System.out.println("\n[SUCCESS]: Account successfully created for " + checkingAccount.getCustomerName() + ".");
                    checkingAccount.triggerNotification("Welcome to our bank! Your checking account " + checkingAccount.getAccountNumber() + " has been successfully activated.");
                    break;

                case 2: // Define Savings Account
                    String savingsName = getValidName(scanner);

                    //FAIL-FAST ACCOUNT NUMBER VALIDATION LOOP
                    String savingsNo;
                    while (true) {
                        savingsNo = getValidAccountNo(scanner); // Format check

                        // Quietly checking with the bank layer
                        if (bank.isAccountExists(savingsNo)) {
                            System.out.println("\n[ERROR]: Account creation failed! Account number " + savingsNo + " already exists in the system.");
                        } else {
                            break;
                        }
                    }


                    //MATURITY DURATION VALIDATION LOOP
                    System.out.print("Enter Maturity Duration (in days): ");
                    int duration = getIntInput(scanner);
                    while (duration <= 0) {
                        System.out.println("\n[ERROR]: Maturity duration must be greater than 0 days. Try again.");
                        System.out.print("Enter Maturity Duration (in days): ");
                        duration = getIntInput(scanner);
                    }

                    NotificationService savingsNotification = getNotificationChoice(scanner);

                    SavingsAccount savingsAccount = new SavingsAccount(savingsNo, savingsName, duration, savingsNotification);
                    bank.addAccount(savingsAccount);

                    System.out.println("\n[SUCCESS]: Account successfully created for " + savingsAccount.getCustomerName() + ".");
                    savingsAccount.triggerNotification("Welcome to our bank! Your savings account " + savingsAccount.getAccountNumber() + " has been successfully activated.");
                    break;

                case 3: // Deposit Money
                    System.out.print("Enter Account Number for Deposit: ");
                    String depositNo = scanner.nextLine().trim();
                    Account depAccount = bank.findAccount(depositNo); // Map point-of-contact lookup

                    if (depAccount != null) {
                        System.out.print("Enter Amount to Deposit: ");
                        double amount = getDoubleInput(scanner);
                        if (amount > 0) {
                            depAccount.deposit(amount); // Triggers overridden deposit inside SavingsAccount
                        } else {
                            System.out.println("[ERROR]: Amount must be positive.");
                        }
                    } else {
                        System.out.println("[ERROR]: Account not found!");
                    }
                    break;

                case 4: // Withdraw Money
                    System.out.print("Enter Account Number for Withdrawal: ");
                    String withdrawNo = scanner.nextLine().trim();
                    Account withAccount = bank.findAccount(withdrawNo); // Map point-of-contact lookup

                    if (withAccount != null) {
                        System.out.print("Enter Amount to Withdraw: ");
                        double amount = getDoubleInput(scanner);
                        if (amount > 0) {
                            withAccount.withdraw(amount); // Triggers business logic & safety checks
                        } else {
                            System.out.println("[ERROR]: Amount must be positive.");
                        }
                    } else {
                        System.out.println("[ERROR]: Account not found!");
                    }
                    break;

                case 5: // Display Account Details & Balances
                    bank.displayAllAccounts(); // Lists each account sequentially

                    System.out.printf("Total Bank Vault: %,.2f USD/TL\n", bank.getTotalBankBalance()); // Dynamic collection summary
                    break;

                case 6: // Secure Exit
                    System.out.println("[SYSTEM LOG]: Exiting system securely. Goodbye!");
                    scanner.close(); // Prevent resource leaks
                    System.exit(0); // Gracefully terminate execution
                    break;

                default:
                    System.out.println("[ERROR]: Invalid selection! Please enter a number between 1 and 6.");
            }
        }
    }

    // --- REUSABLE VALIDATION & HELPER METHODS (DRY PRINCIPLE) ---

    // 1. Centralized Name Validation
    private static String getValidName(Scanner scanner) {
        while (true) {
            System.out.print("Enter Customer Name: ");
            String name = scanner.nextLine().trim();
            if (name.matches("^[a-zA-ZÇŞĞÜÖİçşğüöı\\s]+$")) {
                return name;
            }
            System.out.println("[ERROR]: Invalid name format! Letters and spaces only. Try again.");
        }
    }

    // 2. Centralized Account Number Validation (Format: TR followed by 4 digits)
    private static String getValidAccountNo(Scanner scanner) {
        while (true) {
            System.out.print("Enter Account Number (e.g., TR1001): ");
            String accountNo = scanner.nextLine().trim();
            if (accountNo.matches("^TR\\d{4}$")) {
                return accountNo;
            }
            System.out.println("[ERROR]: Invalid account number format! Must start with 'TR' followed by 4 digits. Try again.");
        }
    }

    // 3. User-Friendly Notification Preference Selection
    private static NotificationService getNotificationChoice(Scanner scanner) {
        while (true) {
            System.out.println("Select Notification Channel:");
            System.out.println("  1- SMS Notification");
            System.out.println("  2- Email Notification");
            System.out.print("Your Selection: ");

            int choice = getIntInput(scanner);
            if (choice == 1) return new SmsNotification();
            if (choice == 2) return new EmailNotification();

            System.out.println("[ERROR]: Invalid choice! Please select 1 or 2.");
        }
    }

    // 4. Safe Numerical Input Processing to Prevent Runtime Crashes
    private static int getIntInput(Scanner scanner) {
        if (scanner.hasNextInt()) {
            int val = scanner.nextInt();
            scanner.nextLine(); // Clear newline buffer
            return val;
        }
        System.out.println("[ERROR]: Invalid input! Please enter a valid integer.");
        scanner.nextLine(); // Clear faulty token
        return -1;
    }

    private static double getDoubleInput(Scanner scanner) {
        if (scanner.hasNextDouble()) {
            double val = scanner.nextDouble();
            scanner.nextLine(); // Clear newline buffer
            return val;
        }
        System.out.println("[ERROR]: Invalid input! Please enter a valid number.");
        scanner.nextLine(); // Clear faulty token
        return -1.0;
    }
}