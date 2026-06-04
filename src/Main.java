import java.util.Scanner;

public class Main {
    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);

        System.out.println("=== BANKACILIK YÖNETİM SİSTEMİ DİNAMİK TEST PANELİ ===");

        // 1. ADIM: BİLDİRİM SERVİSİ SEÇİMİ (Loose Coupling Testi)
        NotificationService secilenServis = null;

        while (true) {
            System.out.println("\n[1] Lütfen hesap için bildirim servisi seçiniz:");
            System.out.println("1 - SMS Bildirim Servisi");
            System.out.println("2 - E-posta Bildirim Servisi");
            System.out.print("Seçiminiz (1 veya 2): ");

            if (scanner.hasNextInt()) {
                int servisSecim = scanner.nextInt();
                scanner.nextLine(); // Satır sonu karakterini temizle

                if (servisSecim == 1) {
                    secilenServis = new SmsNotification();
                    System.out.println("[SİSTEM]: SMS servisi başarıyla enjekte edildi.");
                    break;
                } else if (servisSecim == 2) {
                    secilenServis = new EmailNotification();
                    System.out.println("[SİSTEM]: E-posta servisi başarıyla enjekte edildi.");
                    break;
                } else {
                    System.out.println("[UYARI]: Geçersiz seçim! Lütfen sadece 1 veya 2 giriniz.");
                }
            } else {
                System.out.println("[HATA]: Lütfen sadece sayısal bir değer giriniz!");
                scanner.next(); // Hatalı girdiyi temizle
            }
        }

        // 2. ADIM: HESAP TÜRÜ SEÇİMİ
        int hesapSecim = 0;
        while (true) {
            System.out.println("\n[2] Hesap Türü Seçiniz:");
            System.out.println("1 - Vadesiz Hesap (CheckingAccount - Günlük Limitli)");
            System.out.println("2 - Vadeli Hesap (SavingsAccount - Vade Süreli)");
            System.out.print("Seçiminiz (1 veya 2): ");

            if (scanner.hasNextInt()) {
                hesapSecim = scanner.nextInt();
                scanner.nextLine(); // Satır sonu temizliği

                if (hesapSecim == 1 || hesapSecim == 2) {
                    break;
                } else {
                    System.out.println("[UYARI]: Geçersiz hesap türü! Lütfen sadece 1 veya 2 giriniz.");
                }
            } else {
                System.out.println("[HATA]: Lütfen sadece sayısal bir değer giriniz!");
                scanner.next();
            }
        }

        // --- HESAP DETAYLARI ALMA (KUTUCUK VE GEÇERSİZ FORMAT KONTROLLERİ) ---
        String accountNum = "";
        while (true) {
            System.out.print("Hesap Numarası Giriniz (Sadece 4-6 haneli rakam): ");
            accountNum = scanner.nextLine().trim();

            if (accountNum.isEmpty()) {
                System.out.println("[UYARI]: Hesap numarası boş bırakılamaz!");
            } else if (!accountNum.matches("^[0-9]{4,6}$")) {
                System.out.println("[UYARI]: Geçersiz format! Hesap numarası sadece rakamlardan oluşmalı ve 4-6 hane arasında olmalıdır.");
            } else {
                break;
            }
        }

        String customerName = "";
        while (true) {
            System.out.print("Müşteri Adı Giriniz (Sadece harf ve boşluk, en az 2 karakter): ");
            customerName = scanner.nextLine().trim();

            if (customerName.isEmpty()) {
                System.out.println("[UYARI]: Müşteri adı boş bırakılamaz!");
            } else if (!customerName.matches("^[a-zA-ZçÇğĞıİöÖşŞüÜ\\s]{2,50}$")) {
                System.out.println("[UYARI]: Geçersiz format! Müşteri adı sadece harflerden ve boşluklardan oluşmalı, en az 2 karakter olmalıdır.");
            } else {
                break;
            }
        }

        Account testHesabi = null;

        // 3. ADIM: SEÇİLEN HESAP TÜRÜNE GÖRE PARAMETRE ALIMI VE OLUŞTURMA
        if (hesapSecim == 1) {
            double limit = 0;
            while (true) {
                System.out.print("Günlük Para Çekme Limiti Giriniz (0 - 20000 TL arası): ");
                String limitInput = scanner.next();
                limitInput = limitInput.replace(",", ".");

                try {
                    limit = Double.parseDouble(limitInput);
                    if (limit < 0) {
                        System.out.println("[UYARI]: Limit negatif olamaz! Lütfen tekrar deneyiniz.");
                    } else if (limit > 20000) {
                        System.out.println("[UYARI]: Banka kuralları gereği günlük limit 20.000 TL'yi aşamaz! Lütfen tekrar deneyiniz.");
                    } else {
                        scanner.nextLine(); // Enter temizliği
                        break;
                    }
                } catch (NumberFormatException e) {
                    System.out.println("[HATA]: Lütfen sadece sayısal bir değer giriniz!");
                }
            }

            testHesabi = new CheckingAccount(accountNum, customerName, limit, secilenServis);
            System.out.println("[SİSTEM]: Vadesiz Hesap (CheckingAccount) " + limit + " TL limitiyle başarıyla oluşturuldu.");

        } else {
            int duration = 0;
            while (true) {
                System.out.print("Vade Süresi Giriniz (En az 1 gün - Örn: 32, 92, 180): ");
                if (scanner.hasNextInt()) {
                    duration = scanner.nextInt();
                    scanner.nextLine(); // Enter temizliği

                    if (duration <= 0) {
                        System.out.println("[UYARI]: Vade süresi en az 1 gün olmalıdır!");
                    } else {
                        break;
                    }
                } else {
                    System.out.println("[HATA]: Lütfen sadece tam sayı bir gün değeri giriniz!");
                    scanner.next();
                }
            }

            double rate;
            if (duration <= 32) {
                rate = 0.40;
            } else if (duration <= 92) {
                rate = 0.45;
            } else {
                rate = 0.48;
            }

            System.out.println("[SİSTEM]: Girilen vadeye göre faiz oranınız otomatik olarak %" + (rate * 100) + " olarak belirlendi.");
            testHesabi = new SavingsAccount(accountNum, customerName, rate, duration, secilenServis);
            System.out.println("[SİSTEM]: Vadeli Hesap (SavingsAccount) başarıyla oluşturuldu.");
        }

        // 4. ADIM: CANLI İŞLEM MENÜSÜ
        boolean devami = true;
        while (devami) {
            System.out.println("\n--- İŞLEM MENÜSÜ ---");
            System.out.println("1 - Para Yatır (deposit)");
            System.out.println("2 - Para Çek (withdraw)");
            System.out.println("3 - Bakiye ve Hesap Durumu Görüntüle");
            System.out.println("4 - Güvenli Çıkış");
            System.out.print("Yapmak istediğiniz işlem: ");

            if (scanner.hasNextInt()) {
                int islem = scanner.nextInt();
                scanner.nextLine(); // Enter temizliği

                switch (islem) {
                    case 1:
                        System.out.print("Yatırmak istediğiniz tutar: ");
                        String inputDep = scanner.next();
                        inputDep = inputDep.replace(",", ".");
                        try {
                            double depAmount = Double.parseDouble(inputDep);
                            System.out.println("\n[İŞLEM BAŞLADI]: deposit(" + depAmount + ") çağrılıyor...");
                            boolean depResult = testHesabi.deposit(depAmount);
                            System.out.println("[İŞLEM SONUCU]: Metottan dönen değer -> " + depResult);
                        } catch (NumberFormatException e) {
                            System.out.println("[HATA]: Geçersiz tutar formatı!");
                        }
                        break;

                    case 2:
                        System.out.print("Çekmek istediğiniz tutar: ");
                        String inputWith = scanner.next();
                        inputWith = inputWith.replace(",", ".");
                        try {
                            double withAmount = Double.parseDouble(inputWith);
                            System.out.println("\n[İŞLEM BAŞLADI]: withdraw(" + withAmount + ") çağrılıyor...");
                            boolean withResult = testHesabi.withdraw(withAmount);
                            System.out.println("[İŞLEM SONUCU]: Metottan dönen değer -> " + withResult);
                        } catch (NumberFormatException e) {
                            System.out.println("[HATA]: Geçersiz tutar formatı!");
                        }
                        break;

                    case 3:
                        System.out.println("\n=== HESAP DETAYLARI ===");
                        System.out.println("Hesap No: " + testHesabi.getAccountNumber());
                        System.out.println("Müşteri: " + testHesabi.getCustomerName());
                        System.out.println("Mevcut Bakiye: " + testHesabi.getBalance() + " TL");
                        break;

                    case 4:
                        devami = false;
                        System.out.println("Test panelinden çıkılıyor. İyi çalışmalar!");
                        break;

                    default:
                        System.out.println("Geçersiz seçim! Lütfen 1-4 arasında bir değer giriniz.");
                }
            } else {
                System.out.println("[HATA]: Lütfen menü seçimi için sayısal bir değer giriniz!");
                scanner.next(); // Hatalı girdiyi temizle
            }
        }
        scanner.close();
    }
}