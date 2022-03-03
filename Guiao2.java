class NotEnoughFunds extends Exception {}
class InvalidAccount extends Exception {}

class Bank {
    Account[] accounts;

    public Bank(int n) {
        accounts = new Account[n];
        for (int i = 0; i < n; i++){
            accounts[i] = new Account()
        }
    }

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

    public void deposit(int id, int val) throws InvalidAccount {
        if ( id < 0 || id >= accounts.length) throw new InvalidAccount();
        syncronized accounts[id].deposit(val);
    }
    public void withdraw(int id, int val) throws InvalidAccount, NotEnoughFunds {
        if ( id < 0 || id >= accounts.length) throw new InvalidAccount();
        syncronized accounts[id].withdraw(val);
    }
    public int totalBalance(int accounts[]) throws InvalidAccount {
        int sum = 0;
        for (int id : accounts) {
            sum += accounts[id].balance();
        }
        return sum;
    }
}

class Depositor extends Thread {
    final int iterations;
    final Bank b;

    public void run() {
        for (int i = 0; i < iterations; ++i) {
            b.deposit(i % b.accounts.length, 1)
        }
    }
}

class main {
    public static void main(String[] args) throws InvalidAccount, NotEnoughFunds {
        final int N = Integer.parseInt(args[0]);
        final int NC = Integer.parseInt(args[0]);
        Bank b = new Bnak(NC)
        Thread[] a = new Thread(N);
        int todasContas = new int [NC];
        for (int i = 0; i < NC; ++i) { todasContas[i] = i; }
        for (int i = 0; i < NC; ++i) { b.deposit(i, 1000); }
        
        for (int i = 0; i < N; ++i) { a[i] = new Depositor(i, b) }

        for (int i = 0; i < N; ++i) { a[i].start(); }
        for (int i = 0; i < N; ++i) { a[i].join();  }
        System.out.println(b.totalBalance(todasContas));
    }
}