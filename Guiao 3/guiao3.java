
import java.util.concurrent.locks.*;

import java.util.HashMap;
import java.util.stream.IntStream;
import java.util.Random;

class InvalidAccount extends Exception {}
class NotEnoughFunds extends Exception {}

class Bank {

    private HashMap<Integer, Account> todasContas = new HashMap<>();
    private int id = 0;
    private Lock l = new ReentrantLock();

    private static class Account {
        private int balance;
        Lock lock = new ReentrantLock();

        public Account(int saldIni) {
            this.balance = saldIni;
        }

        public int getBal() { return this.balance; }
        public void deposit(int val) { this.balance += val; }
        public void withdraw(int val) throws NotEnoughFunds { 
            if (this.balance - val < 0) throw new NotEnoughFunds();
            this.balance -= val;
        }
    }


    public int createAccount(int saldIni) {
        Account c = new Account(saldIni);
        this.l.lock();
        try{
            todasContas.put(id, c);
            id++;
            return id - 1;
        }
        finally{ l.unlock(); }
    }

    public int closeAccount(int id) throws InvalidAccount {
        this.l.lock();
        try{
            Account c = getAcc(id);
            int saldo = c.getBal();
            todasContas.remove(id);
            return saldo;
        }
        finally{ l.unlock(); }
    }
    
    private Account getAcc(int id) throws InvalidAccount {
        if (!todasContas.keySet().contains(id) ) throw new InvalidAccount();
        return  todasContas.get(id);
    }

    public void deposit(int id, int val) throws InvalidAccount {
        Account c;
        l.lock();
        try{
            c = getAcc(id);
            c.lock.lock();
        }
        finally{ l.unlock(); }
        try {
            c.deposit(val);
        } finally{ c.lock.unlock(); }
    }
    public void withdraw(int id, int val) throws InvalidAccount, NotEnoughFunds {
        Account c;
        l.lock();
        try{
            c = getAcc(id);
            c.lock.lock();
        }
        finally{ l.unlock(); }
        try {
            c.withdraw(val);
        } finally{ c.lock.unlock(); }
    }

    void transfer(int from, int to, int val) throws InvalidAccount, NotEnoughFunds {
        Account accfrom = getAcc(from);
        Account accto = getAcc(to);
        accfrom.withdraw(val);
        accto.deposit(val);
    }

    public int totalBalance(int contas[]) throws InvalidAccount {
        int sum = 0;
        for (int id : contas) {
            sum += getAcc(id).getBal();
        }
        return sum;
    }
}

class depositor extends Thread{
    private Bank b;
    private int I;
    private int id;

    public depositor(Bank b, int id, int I) {
        this.b = b;
        this.id = id;
        this.I = I;
    }
    public void run() {
        try {
            for (int i = 0; i < I; i++) { b.deposit(id, 1); }
        } catch( InvalidAccount e) {}
    }
}

class transferer extends Thread{
    private Bank b;
    private int I;
    private int idF;
    private int idT;

    public transferer(Bank b, int idF, int idT, int I) {
        this.b = b;
        this.idF = idF;
        this.idT = idT;
        this.I = I;
    }
    public void run() {
        try {
            for (int i = 0; i < I; i++) { b.transfer(idF, idT, 1); }
        } catch( Exception e) {}
    }
}

class main1 {
    public static void main(String args[]) throws InterruptedException, InvalidAccount{
        final int N = Integer.parseInt(args[0]);
        final int NC = Integer.parseInt(args[1]);
        final int I = Integer.parseInt(args[2]);
        final Bank b = new Bank();
        Thread[] threads = new Thread[N];
        Random rand = new Random();

        // Open Accs
        for (int i = 0; i < NC; i++) {
            b.createAccount(1000);
        }
        // Depositors
        for (int i = 0; i < N; i++) {
            int id = rand.nextInt() % 5;
            threads[i] = new depositor(b, id, I);
        }
        // Transferers
        /* for (int i = N/2; i < N; i++) {
            int idF = rand.nextInt() % 5;
            int idT = rand.nextInt() % 5;
            threads[i] = new transferer(b, idF, idT, I);
        } */
        // Start Threads
        int[] contas = IntStream.range(0, NC).toArray();
        System.out.println(b.totalBalance(contas));
        for (int i = 0; i < N; i++) { threads[i].start(); }
        for (int i = 0; i < N; i++) { threads[i].join(); }
        System.out.println(b.totalBalance(contas));
        // Close Accs
        for (int i = 0; i < NC; i++) {
            int saldo = b.closeAccount(i);
            System.out.println("Acc" +  i + ": " + saldo);
        }

    }
}