import java.util.stream.IntStream;
import java.util.Random;

class InvalidAccount extends Exception {}
class NotEnoughFunds extends Exception {}

class Bank {

    private static class Account {
        private int balance;

        public Account(int saldIni) {
            this.balance = saldIni;
        }

        public int getBal() { return this.balance; }
        public void withdraw(int val) throws NotEnoughFunds { 
            if (this.balance - val < 0) throw new NotEnoughFunds();
            this.balance -= val;
        }
        public void deposit(int val) { this.balance += val; }
    }

    private Account[] todasContas;
    private int NC;

    public Bank(int NC, int saldIni) {
        this.todasContas = new Account[NC];
        this.NC = NC;
        for (int i = 0; i < NC; i++) {
            todasContas[i] = new Account(saldIni);
        }
    }

    private Account getAcc(int id) throws InvalidAccount {
        if (id < 0 || id >= NC) throw new InvalidAccount();
        return  todasContas[id];
    }

    public void deposit(int id, int val) throws InvalidAccount {
        Account c = getAcc(id);
        synchronized(c) {c.deposit(val);};
    }
    public void withdraw(int id, int val) throws InvalidAccount, NotEnoughFunds {
        Account c = getAcc(id);
        synchronized(c) {c.withdraw(val);}
    }

    void transfer(int from, int to, int val) throws InvalidAccount, NotEnoughFunds {
        Account accfrom = getAcc(from);
        Account accto = getAcc(to);
        synchronized (accfrom) {accfrom.withdraw(val);}
        synchronized (accto) {accto.deposit(val);}
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

class main2 {
    public static void main(String args[]) throws InterruptedException, InvalidAccount{
        final int N = Integer.parseInt(args[0]);
        final int NC = Integer.parseInt(args[1]);
        final int I = Integer.parseInt(args[2]);
        final Bank b = new Bank(NC, 10000);
        Thread[] threads = new Thread[N];
        
        Random rand = new Random();
        
        for (int i = 0; i < N/2; i++) {
            int id = rand.nextInt() % 5;
            threads[i] = new depositor(b, id, I);
        }
        for (int i = N/2; i < N; i++) {
            int idF = rand.nextInt() % 5;
            int idT = rand.nextInt() % 5;
            threads[i] = new transferer(b, idF, idT, I);
        }
        for (int i = 0; i < N; i++) { threads[i].start(); }
        for (int i = 0; i < N; i++) { threads[i].join(); }
        int[] contas = IntStream.range(0, NC).toArray();
        System.out.println(b.totalBalance(contas));
    }
}