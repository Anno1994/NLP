package sample;

import org.junit.Assert;

/**
 * Created by Max on 21.04.2016.
 */
public class BankTest {
    private Bank bank;

    @org.junit.Before
    public void setUp() throws Exception {
        bank = new Bank();
        bank.createAccount();
        bank.createAccount();
    }

    @org.junit.After
    public void tearDown() throws Exception {
        bank = null;
    }

    @org.junit.Test
    public void deposit() throws Exception {
        bank.deposit(1, 100);
        Assert.assertEquals(bank.getAccountBalance(1), 100);
    }

    @org.junit.Test
    public void withdraw() throws Exception {
        bank.withdraw(2, 50);
        Assert.assertEquals(bank.getAccountBalance(2), -50);
    }

    @org.junit.Test
    public void transfer() throws Exception {
        bank.transfer(1, 2, 100);
        Assert.assertEquals(bank.getAccountBalance(1), -100);
        Assert.assertEquals(bank.getAccountBalance(2), 100);
    }

    @org.junit.Test
    public void createAccount() throws Exception {
        Assert.assertEquals(bank.createAccount(), 3);
    }
}