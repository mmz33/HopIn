package aub.hopin;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.Semaphore;

public class MessagesHandler {
    private static ArrayList<String> messageQueue = new ArrayList<>();
    private static Semaphore semaphore = new Semaphore(1);
    private static Timer checkTimer = null;

    public static void start() {
        checkTimer = new Timer();
        TimerTask updateTask = new TimerTask() {
            public void run() {
                try {
                    UserInfo info = ActiveUser.getInfo();
                    if (info == null) return;
                    String email = info.email;
                    if (email == null) return;

                    HashMap<String, String> response = Server.checkMessages(email);
                    if (response == null) return;

                    for (String sender : response.keySet()) {
                        if (sender == null) continue;
                        String message = response.get(sender);
                        if (message == null) continue;
                        try {
                            semaphore.acquire();
                        } catch (InterruptedException e) {
                        }
                        messageQueue.add(sender + " " + message);
                        semaphore.release();
                    }
                } catch (ConnectionFailureException e) {
                }
            }
        };
        checkTimer.scheduleAtFixedRate(updateTask, 2000, 2500);
    }

    public static void stop() {
        if (checkTimer != null) {
            checkTimer.cancel();
        }
    }

    public static boolean anyMessages() {
        try { semaphore.acquire(); } catch (InterruptedException e) {}
        boolean result = messageQueue.size() > 0;
        semaphore.release();
        return result;
    }

    public static String pollMessage() {
        try { semaphore.acquire(); } catch (InterruptedException e) {}
        String result = "";
        if (messageQueue.size() > 0) {
            result = messageQueue.get(0);
            messageQueue.remove(0);
        }
        semaphore.release();
        return result;
    }
}
