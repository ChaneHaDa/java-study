import java.util.LinkedList;
import java.util.Queue;
import java.util.Random;

public class ProducerConsumerSolve1 {

    static class Buffer {
        private Queue<Integer> queue = new LinkedList<>();
        private int capacity;

        public Buffer(int capacity) {
            this.capacity = capacity;
        }

        public synchronized void produce(int item) throws InterruptedException {
            while (queue.size() == capacity) {
                wait();
            }

            queue.add(item);
            System.out.println("생산: " + item + " | 버퍼 크기: " + queue.size());
            notifyAll();
        }

        public synchronized int consume() throws InterruptedException {
            while (queue.isEmpty()) {
               wait();
            }

            Integer item = queue.poll();
            System.out.println("소비: " + item + " | 버퍼 크기: " + queue.size());
            notifyAll();
            return item;
        }
    }

    static class Producer implements Runnable {
        private Buffer buffer;
        private String name;
        private Random random = new Random();

        public Producer(Buffer buffer, String name) {
            this.buffer = buffer;
            this.name = name;
        }

        @Override
        public void run() {
            try {
                for (int i = 0; i < 5; i++) {
                    int item = random.nextInt(100);
                    System.out.println("[" + name + "] 생산 시도: " + item);
                    buffer.produce(item);
                    Thread.sleep(random.nextInt(200));
                }
            } catch (InterruptedException e) {
                System.out.println(name + " 인터럽트!");
            }
        }
    }

    static class Consumer implements Runnable {
        private Buffer buffer;
        private String name;
        private Random random = new Random();

        public Consumer(Buffer buffer, String name) {
            this.buffer = buffer;
            this.name = name;
        }

        @Override
        public void run() {
            try {
                for (int i = 0; i < 3; i++) {
                    System.out.println("[" + name + "] 소비 시도");
                    int item = buffer.consume();
                    if (item == -1) {
                        System.out.println("[" + name + "] 경쟁 조건으로 실패!");
                    }
                    Thread.sleep(random.nextInt(300));
                }
            } catch (InterruptedException e) {
                System.out.println(name + " 인터럽트!");
            }
        }
    }

    public static void main(String[] args) throws InterruptedException {
        System.out.println("=== 생산자-소비자 문제 (wait/notifyAll 적용) ===");

        Buffer buffer = new Buffer(3);

        Thread producer1 = new Thread(new Producer(buffer, "생산자1"));
        Thread producer2 = new Thread(new Producer(buffer, "생산자2"));
        Thread consumer1 = new Thread(new Consumer(buffer, "소비자1"));
        Thread consumer2 = new Thread(new Consumer(buffer, "소비자2"));
        Thread consumer3 = new Thread(new Consumer(buffer, "소비자3"));

        producer1.start();
        producer2.start();
        consumer1.start();
        consumer2.start();
        consumer3.start();

        producer1.join();
        producer2.join();
        consumer1.join();
        consumer2.join();
        consumer3.join();

        System.out.println("=== 실행 완료 ===");
    }
}