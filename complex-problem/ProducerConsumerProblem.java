import java.util.LinkedList;
import java.util.Queue;
import java.util.Random;

public class ProducerConsumerProblem {

    static class UnsafeBuffer {
        private Queue<Integer> queue = new LinkedList<>();
        private int capacity;

        public UnsafeBuffer(int capacity) {
            this.capacity = capacity;
        }

        public void produce(int item) throws InterruptedException {
            while (queue.size() == capacity) {
                System.out.println("[문제 발생!] 버퍼 가득 - 생산자가 계속 확인 중 (바쁜 대기)");
                Thread.sleep(50);
            }

            queue.add(item);
            System.out.println("생산: " + item + " | 버퍼 크기: " + queue.size());
        }

        public int consume() throws InterruptedException {
            while (queue.isEmpty()) {
                System.out.println("[문제 발생!] 버퍼 비어있음 - 소비자가 계속 확인 중 (바쁜 대기)");
                Thread.sleep(50);
            }

            Integer item = queue.poll();
            if (item == null) {
                System.out.println("[경쟁 조건!] null 반환 - 동시에 여러 소비자가 접근!");
                return -1;
            }
            System.out.println("소비: " + item + " | 버퍼 크기: " + queue.size());
            return item;
        }
    }

    static class Producer implements Runnable {
        private UnsafeBuffer buffer;
        private String name;
        private Random random = new Random();

        public Producer(UnsafeBuffer buffer, String name) {
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
        private UnsafeBuffer buffer;
        private String name;
        private Random random = new Random();

        public Consumer(UnsafeBuffer buffer, String name) {
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
        System.out.println("=== 생산자-소비자 문제 시연 ===");
        System.out.println("동기화 없이 실행 - 경쟁 조건과 바쁜 대기 문제 발생!");
        System.out.println("버퍼 크기: 3");
        System.out.println("생산자 2개 (각 5개 생산), 소비자 3개 (각 3개 소비)\n");

        UnsafeBuffer buffer = new UnsafeBuffer(3);

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

        System.out.println("\n=== 실행 완료 ===");
        System.out.println("문제점:");
        System.out.println("1. 바쁜 대기(Busy Waiting) - CPU 자원 낭비");
        System.out.println("2. 경쟁 조건(Race Condition) - 여러 스레드가 동시에 접근");
        System.out.println("3. 데이터 불일치 - 동기화 없이 공유 자원 접근");
    }
}