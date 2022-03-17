import java.util.Random;

class NotEnoughFunds extends Exception {}
class InvalidAccount extends Exception {}

class Bank {
    private Account[] accounts;

    public Bank(int n) {
        accounts = new Account[n];
        for (int i = 0; i < n; i++){
            accounts[i] = new Account();
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

    public Account getAccount(int id) throws InvalidAccount {
        if ( id < 0 || id >= accounts.length) throw new InvalidAccount();
        return this.accounts[id];
    }

    public int getAccountsLength() {
        return this.accounts.length;
    }

    public void deposit(int id, int val) throws InvalidAccount {
        Account c = getAccount(id);
        synchronized (c) {c.deposit(val);}
    }
    public void withdraw(int id, int val) throws InvalidAccount, NotEnoughFunds {
        Account c = getAccount(id);
        synchronized (c) {c.withdraw(val);}
    }
    public synchronized int totalBalance(int accounts[]) throws InvalidAccount {
        int sum = 0;
        for (int id : accounts) {
            sum += getAccount(id).balance();
        }
        return sum;
    }
    public void transfer(int from, int to, int val) throws InvalidAccount, NotEnoughFunds {
        if (from == to) return;
        Account cfrom = getAccount(from);
        Account cto = getAccount(to);
        Account o1, o2;
        if (from < to) {
            o1 = cfrom;
            o2 = cto;
        }
        else {
            o1 = cto;
            o2 = cfrom;
        }
        synchronized(o1) {
            synchronized(o2) {
                cfrom.withdraw(val);
                cto.deposit(val);
            }
        }
        /*synchronized(cfrom) {     DeadLock -> se cfrom estive ra espera do lock da cto e se a cto tiver a espera do lock da cfrom
            synchronized(cto) {
                withdraw(from, val);
                deposit(to, val);
            }
        }*/
    }
}

class Transferer extends Thread {
    final int iterations;
    final Bank b;

    public Transferer(int iterations, Bank b) {this.iterations = iterations; this.b = b;}

    public void run() {
        Random r = new Random();
        for (int i = 0; i < iterations; i++){
            int from = r.nextInt(b.getAccountsLength());
            int to = r.nextInt(b.getAccountsLength());
            try{
                b.transfer(from, to, 1);
            } catch(Exception e) {}
        }
    }
}

class Depositor extends Thread {
    final int iterations;
    final Bank b;

    public Depositor(int iterations, Bank b) {this.iterations = iterations; this.b = b;}

    public void run() {
        for (int i = 0; i < iterations; ++i) {
            try {
                b.deposit(i % b.getAccountsLength(), 1);
            } catch(Exception e) {}
        }
    }
}

class Observer extends Thread {
    final int iterations;
    final Bank b;

    public Observer(int iterations, Bank b) { this.iterations = iterations; this.b = b;}

    public void run() {
        try{
            for (int i = 0; i < iterations; i++) {
                int NC = 10;
                int[] todasContas = new int[NC];
                int balance = b.totalBalance(todasContas);
                if (balance != 10 * 1000000){
                    System.out.println("saldo errado " + balance);
                }
            }
        } catch(Exception e) {}
    }
}

class main {
    public static void main(String[] args) throws InvalidAccount, NotEnoughFunds, InterruptedException {
        final int N = Integer.parseInt(args[0]);
        final int NC = Integer.parseInt(args[1]);
        final int I = Integer.parseInt(args[2]);
        Bank b = new Bank(NC);
        Thread[] a = new Thread[N];
        int[] todasContas = new int[NC];
        for (int i = 0; i < NC; ++i) { todasContas[i] = i; }
        for (int i = 0; i < NC; ++i) { b.deposit(i, 1000000); }
        
        //for (int i = 0; i < N; ++i) { a[i] = new Depositor(I, b); }
        for (int i = 0; i < N; ++i) { a[i] = new Transferer(I, b); }
        new Observer(I, b).start();

        for (int i = 0; i < N; ++i) { a[i].start(); }
        for (int i = 0; i < N; ++i) { a[i].join();  }
        System.out.println(b.totalBalance(todasContas));
    }
}
