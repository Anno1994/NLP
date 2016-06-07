

import bank1.Bank;
import org.junit.Assert;

/**
 * Created by Max on 21.04.2016.
 */
public class BankTest {
    private Bank bank;

    @org.junit.Before
    public void setUp() throws Exception {
        bank = new bank1.Bank();
        bank.createAccount(); // KNr: 0
        bank.createAccount(); // KNr: 1
    }

    @org.junit.After
    public void tearDown() throws Exception {
        bank = null;
    }

    @org.junit.Test
    public void deposit() throws Exception {
        bank.deposit(0, 100);
        Assert.assertEquals(bank.getAccountBalance(0), 100);
    }

    @org.junit.Test
    public void withdraw() throws Exception {
        bank.withdraw(1, 50);
        Assert.assertEquals(bank.getAccountBalance(1), -50);
    }

    @org.junit.Test
    public void transfer() throws Exception {
        bank.transfer(0, 1, 100);
        Assert.assertEquals(bank.getAccountBalance(0), -100);
        Assert.assertEquals(bank.getAccountBalance(1), 100);
    }

    @org.junit.Test
    public void createAccount() throws Exception {
        Assert.assertEquals(bank.createAccount(), 2);
    }
}