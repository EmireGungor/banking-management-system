import java.time.LocalDate;

public class Main {
    public static void main(String[] args) {
        System.out.println("====== STARTING PHASE 1-2-3 ARCHITECTURE TEST ======\n");

        // -----------------------------------------------------------------
        // TEST 1: Vadesiz Hesap Tanımlama ve Para Yatırma (Checking Account & SMS)
        // -----------------------------------------------------------------
        System.out.println("--- TEST 1: Checking Account Creation & Deposit ---");
        // Günlük limiti 5000 TL olan bir vadesiz hesap açıyoruz
        CheckingAccount checking = new CheckingAccount("TR1001", "Kaan", 5000.0);

        // Hesaba 5000 TL yatırıyoruz (Dokümandaki Akış 2 - SMS tetiklenmeli)
        checking.deposit(5000.0);
        System.out.println();

        // -----------------------------------------------------------------
        // TEST 2: Vadesiz Hesap Hata Yönetimi (Yetersiz Bakiye & Günlük Limit)
        // -----------------------------------------------------------------
        System.out.println("--- TEST 2: Checking Account Withdrawal Limits ---");

        // Senaryo A: Bakiyeden fazla para çekme denemesi (7000 TL)
        System.out.println("[Attempt 1] Trying to withdraw 7000 TL (Insufficent Funds expected):");
        checking.withdraw(7000.0);

        // Bakiyeyi test için biraz artıralım (deposit bildirimini tekrar görmemek için super kullandım)
        ((Account)checking).addInterest(5000.0); // bakiye artık 10000 TL

        // Senaryo B: Günlük limiti aşma denemesi (Limit 5000 TL, biz 6000 TL çekiyoruz)
        System.out.println("\n[Attempt 2] Trying to withdraw 6000 TL (Limit Exceeded expected):");
        checking.withdraw(6000.0);

        // Senaryo C: Kümülatif Limit Kontrolü (Arka arkaya işlem)
        System.out.println("\n[Attempt 3] Withdrawing 3000 TL (Should be successful):");
        checking.withdraw(3000.0);

        System.out.println("\n[Attempt 4] Withdrawing another 3000 TL (Cumulative limit should block this):");
        checking.withdraw(3000.0); // 3000 + 3000 = 6000 > 5000 (Engellenmeli!)
        System.out.println();

        // -----------------------------------------------------------------
        // TEST 3: Vadeli Hesap ve Faiz İptal Uyarıları (Savings Account & Email)
        // -----------------------------------------------------------------
        System.out.println("--- TEST 3: Savings Account & Premature Withdrawal ---");
        // %45 faizli, 30 gün vadeli bir hesap açıyoruz
        SavingsAccount savings = new SavingsAccount("TR2002", "Ahmet", 0.45, 30);

        // Hesaba başlangıç parası yatırıyoruz (Email tetiklenmeli)
        savings.deposit(4000.0);
        System.out.println();

        // Vade dolmadan (Bugün, açılış günüdür yani vadeden öncedir) para çekmeyi deniyoruz
        System.out.println("[Attempt 1] Withdrawing 1000 TL before maturity date (Warning & Email expected):");
        savings.withdraw(1000.0);

        System.out.println("\n====== END OF ARCHITECTURE TEST ======");
    }
}