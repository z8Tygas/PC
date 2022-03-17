class counter {
    private int counter;

    public counter() {this.counter = 0;}

    public void increment() { this.counter += 1; }
    
    public int get_count() { return this.counter; }
}

class incrementer extends Thread {
    private int I;
    private counter c;
    
    public incrementer(counter c, int I) { this.I = I; this.c = c;}

    public void run() {
        for (int i = 1; i <= I; i++) {
            c.increment();
        }
    }
}

class main {
    public static void main(String[] args) throws InterruptedException{
        final int N = Integer.parseInt(args[0]);
        final int I = Integer.parseInt(args[1]);
        counter c = new counter();
        Thread[] threads = new Thread[N];

        for (int i = 0; i < N; i++)
            threads[i] = new incrementer(c, I);
        for (int i = 0; i < N; i++) { threads[i].start(); }
        for (int i = 0; i < N; i++) { threads[i].join(); }

        System.out.println(c.get_count());
    }
}