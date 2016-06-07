import threading
import datetime
from enum import Enum


class Bank(object):
    """Bank class which holds accounts"""
    accounts = []

    def __init__(self):
        """Constructor"""
        self.accounts = []


    def deposit(self, accNo, amount):
        """deposit function which adds the amount(param) to the given account number in the accounts list"""
        self.accounts[accNo].deposit(amount, "deposit")


    def withdraw(self, accNo, amount):
        """just decrease the account with the given parameter"""
        self.accounts[accNo].withdraw(amount, "withdraw")


    def createAccount(self):
        """create a new account and add it to the accounts list, afterwards return the acc number"""
        nextNum = NumberGenerator.generateNumber(NumberGenerator)
        account = Account(nextNum)
        self.accounts.append(account)
        return nextNum


    def getAccountBalance(self, accNo):
        """get the balance of the account with the given accNo"""
        return self.accounts[accNo].getBalance()


#    def getAccount(self, accNo):
#        """just for testing the print/entries in accounts -> delete!!!"""
#        return self.accounts[accNo]



class NumberGenerator(object):
    """generates numbers for new accounts by adding 1 to num in each functin call (generateNumber)"""
    num = 0

    def __init__(self):
        """Constructor"""
        self.num = 0


    def generateNumber(self):
        """add 1 to the number and returns it"""
        self.num += 1
        return self.num



class Account(object):
    """Account class which holds the ID, balance and entries"""

    def __init__(self, id):
        """Constructer, which sets the given ID and lastModified date"""
        self.id = id
        self.lastModified = datetime.datetime
        self.balance = 0
        self.entries = []


    def deposit(self, amount, text):
        """deposit method which highers the balance by amount"""
        self.balance += amount
        self.entries.append(Entry(amount, text, 0))
        self.lastModified = datetime.datetime


    def withdraw(self, amount, text):
        """withdraw function which lowers the balance by amount"""
        self.balance -= amount
        self.entries.append(Entry(-amount, text, 1))
        self.lastModified = datetime.datetime


    def getBalance(self):
        """get the balance"""
        return self.balance


#    def printEntries(self):
#        """just printing for testing entries(list) in account..."""
#        for e in self.entries:
#            e.print()



class Entry(object):
    """Entry Class"""

    def __init__(self, amount, text, entryType):
        """Constructor"""
        self.amount = amount
        self.text = text
        self.EntryType = entryType


#    def print(self):
#        """just printing for testing entries..."""
#        print(self.amount, self.text, self.EntryType)



class EntryType(Enum):
    """Enum for the EntryTypes"""
    DEPOSIT = 0
    WITHDRAW = 1



class Thread(threading.Thread):
    """Thread class which will deposit/withdraw on several accounts"""

    def __init__(self):
        """Constructor"""
        threading.Thread.__init__(self)


    def run(self, bank):
        """run function of the thread which just does whatever is needed... -> lock"""
        lock = threading.Lock()

        for i in range(0, 1000):
            lock.acquire()                              #lock, would be better if you use one lock per accNo
            bank.deposit(bank, 0, 1)                    #here is just every deposit()/withdraw() in bank locked.
            bank.withdraw(bank, 1, 2)
            lock.release()



def main():
    """Main function for initialization and testing"""
    bank = Bank
    bank.createAccount(bank)
    bank.createAccount(bank)

    threads = []
    for i in range(0, 5):
        thread = Thread()
        threads.append(thread)

    for t in threads:
        t.run(bank)

    print(bank.getAccountBalance(bank, 0))
    print(bank.getAccountBalance(bank, 1))

    #testAccount = bank.getAccount(bank, 0)                         #Entries & printing them works
    #testAccount.printEntries()

if __name__ == "__main__":
    main()
