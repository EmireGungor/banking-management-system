public class SmsNotification implements NotificationService { //interface'i kullanmak için implemets kullanıldı.
    @Override
    public void sendNotification(String message) {
        System.out.println("[SMS NOTIFICATION]: " + message);
    }
}