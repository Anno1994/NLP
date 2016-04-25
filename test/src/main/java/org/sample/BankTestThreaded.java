package sample;

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.util.HashSet;
import java.util.Set;

/**
 * Created by Max on 25.04.2016.
 */
public class BankTestThreaded {
    private Thread[] threads;
    private Bank bank;

    @Before
    public void setUp() throws Exception {
        bank = new Bank();
        bank.createAccount();                                   // num: 0
        bank.createAccount();                                   // num: 1
        threads = new Thread[4];
    }

    @After
    public void tearDown() throws Exception {
        bank = null;
        threads = null;
    }

    @Test
    public void deposit() throws Exception {
        for (int i = 0; i < 4; i++) {
            threads[i] = new Thread(new Runnable() {
                @Override
                public void run() {
                    for (int j=0; j<1000; j++) {
                        bank.deposit(1, 1);
                    }
                }
            });
        threads[i].start();}
        for (Thread t : threads) {
            t.join();
        }
        Assert.assertEquals(4000, bank.getAccountBalance(1));
    }

    @Test
    public void withdraw() throws Exception {
        for (int i = 0; i < 4; i++) {
            threads[i] = new Thread(new Runnable() {
                @Override
                public void run() {
                    for (int j=0; j<1000; j++) {
                        bank.withdraw(0, 1);
                    }
                }
            });
            threads[i].start();}
        for (Thread t : threads) {
            t.join();
        }
        Assert.assertEquals(-4000, bank.getAccountBalance(0));
    }

    @Test
    public void transfer() throws Exception {
        for (int i = 0; i < 4; i++) {
            threads[i] = new Thread(new Runnable() {
                @Override
                public void run() {
                    for (int j=0; j<1000; j++) {
                        bank.transfer(0, 1, 1);
                    }
                }
            });
            threads[i].start();}
        for (Thread t : threads) {
            t.join();
        }
        Assert.assertEquals(4000, bank.getAccountBalance(1));
        Assert.assertEquals(-4000, bank.getAccountBalance(0));
    }

    @Test
    public void createAccount() throws Exception {
        Set<Long> accNos = new HashSet<Long>();
        for (int i = 0; i < 4; i++) {
            threads[i] = new Thread(new Runnable() {
                @Override
                public void run() {
                    for (int j=0; j<100; j++) {
                        accNos.add(bank.createAccount());
                    }
                }
            });
            threads[i].start();}
        for (Thread t : threads) {
            t.join();
        }
        Assert.assertEquals(accNos.size(), 400);
    }
}