package bank1;

import java.util.HashMap;

/**
 * Created by Max on 21.04.2016.
 */

public class Bank {
    private HashMap<Long, Account> accounts = new HashMap<>();
    private NumberGenerator accNoGenerator = new NumberGenerator();

    public void deposit(long accNo, int amount) {
        getAccount(accNo).deposit(amount, "Es wurden " + amount + " € eingezahlt");
    }

    public void withdraw(long accNo, int amount) {
        getAccount(accNo).withdraw(amount, "Es wurden " + amount + " € abgehoben");
    }

    public void transfer(long from, long to, int amount) {
        getAccount(to).deposit(amount, "Es wurden " + amount + " € von der Kontonummer " + from + " auf Ihr Konto überwiesen.");
        getAccount(from).withdraw(amount, "Es wurden " + amount + " € auf die Kontonummer " + to + " von Ihrem Konto überwiesen.");
    }

    public void getAccountEntries(long accNo) {
        System.out.println(getAccount(accNo).toString());
    }

    private Account getAccount(long accNo) {
        return accounts.get(accNo);
    }

    public int getAccountBalance(long accNo) {
        return getAccount(accNo).getBalance();
    }

    public long createAccount() {
        long nextNum = accNoGenerator.genNext();
        accounts.put(nextNum, new Account(nextNum));
        return nextNum;
    }


//    public static void main(String args[]) throws InterruptedException {
//        final Bank bank = new Bank();
//        Thread[] threads = new Thread[4];
//        for (int i=0; i<40; i++) {
//            bank.createAccount();
//        }
//        for (int i=0; i<4; i++) {
//            Thread thread = new Thread(new Runnable() {
//                @Override
//                public void run() {
//                    for (int k=0; k<20000; k++) {
//                        if (k%2==0) {
//                            bank.deposit(k%40, 1);
//                        } else {
//                            bank.withdraw(k%40, 1);
//                        }
//                    }
//                }
//            });
//            threads[i] = thread;
//            thread.start();
//        }
//        for (Thread thread : threads) {
//            thread.join();
//        }
//        for (int i=0; i<40; i++) {
//            System.out.println(bank.getAccountBalance(i));
//        }
//    }
}
