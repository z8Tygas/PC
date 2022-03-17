import java.util.HashMap;
import java.util.concurrent.locks.*;

class InvalidAccount extends Exception{}
class NotEnoughFunds extends Exception{}

class Bank {
    private static class Account {
        private int balance;

        public int balance() { return balance; }
        public void deposit(int val) { balance += val; }
        public void withdraw(int val) throws NotEnoughFunds {
            if (balance - val < 0) {
                throw new NotEnoughFunds();
            }
            balance -= val;
        }
    }

    private HashMap<Integer, Account> accounts = new HashMap<>();
    Lock l = new ReentrantLock();
    int lastId = 0;

    public int createAccount(int initialBalance) {
        
        Account c = new Account();
        c.deposit(initialBalance);
        l.lock();
        try {
            lastId += 1;
            int id = lastId;
            accounts.put(id, c);
            return lastId;
        }
        finally{
            l.unlock();
        }
    }

    public int closeAccount(int id) throws InvalidAccount {
        int balance = accounts.get(id).balance();
        accounts.remove(id);
        return balance;
    }

    public void deposit(int id, int val) throws InvalidAccount{
        l.lock();
        try{
            Account c = accounts.get(id);
            if (c == null) { throw new InvalidAccount();}
            c.deposit(val);
        }
        finally{
            l.unlock();
        }
    }

    public void withdraw(int id, int val) throws InvalidAccount, NotEnoughFunds {
        l.lock();
        try{
            Account c = accounts.get(id);
            if (c == null) { throw new InvalidAccount();}
            c.withdraw(val);
        }
        finally{
            l.unlock();
        }
    }
}

class main {
    public static void main(String[] args) throws InvalidAccount, NotEnoughFunds {
        final int N = Integer.parseInt(args[0]);
        final int NC = Integer.parseInt(args[1]);
        final int I = Integer.parseInt(args[2]);
        Bank b = new Bank();
        Thread[] a = new Thread[N];



        System.out.println("tiago");
    }
}
