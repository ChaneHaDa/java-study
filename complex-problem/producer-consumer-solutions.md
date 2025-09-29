# 생산자-소비자 문제 해결 방법

## 문제점 요약
현재 코드의 주요 문제점:
- **바쁜 대기(Busy Waiting)**: CPU 자원 낭비
- **경쟁 조건(Race Condition)**: 데이터 불일치
- **동기화 부재**: 여러 스레드의 동시 접근으로 인한 오류

## 해결 방법

### 1. synchronized + wait() / notify()
가장 전통적인 Java 동기화 방법

#### 구현 방법
```java
public synchronized void produce(int item) throws InterruptedException {
    while (queue.size() == capacity) {
        wait();  // 버퍼가 가득 차면 대기
    }
    queue.add(item);
    notifyAll();  // 대기 중인 소비자들에게 알림
}

public synchronized int consume() throws InterruptedException {
    while (queue.isEmpty()) {
        wait();  // 버퍼가 비면 대기
    }
    int item = queue.poll();
    notifyAll();  // 대기 중인 생산자들에게 알림
    return item;
}
```

#### 장점
- 간단하고 직관적인 구현
- Java 기본 기능 사용
- 모든 Java 버전에서 지원

#### 단점
- 모든 대기 스레드를 깨우므로 비효율적일 수 있음
- 세밀한 제어가 어려움

---

### 2. BlockingQueue 사용
Java 5부터 제공되는 concurrent 패키지 활용

#### 구현 방법
```java
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

public class SafeBuffer {
    private BlockingQueue<Integer> queue;

    public SafeBuffer(int capacity) {
        this.queue = new LinkedBlockingQueue<>(capacity);
    }

    public void produce(int item) throws InterruptedException {
        queue.put(item);  // 자동으로 대기 처리
    }

    public int consume() throws InterruptedException {
        return queue.take();  // 자동으로 대기 처리
    }
}
```

#### 장점
- 가장 간단한 구현
- 내부적으로 최적화된 동기화
- 스레드 안전성 보장
- 다양한 구현체 선택 가능 (`ArrayBlockingQueue`, `LinkedBlockingQueue`, `PriorityBlockingQueue` 등)

#### 단점
- 커스텀 로직 추가가 제한적
- 내부 구현을 제어할 수 없음

---

### 3. ReentrantLock + Condition
더 세밀한 제어가 필요한 경우

#### 구현 방법
```java
import java.util.concurrent.locks.ReentrantLock;
import java.util.concurrent.locks.Condition;

public class SafeBuffer {
    private Queue<Integer> queue = new LinkedList<>();
    private int capacity;
    private ReentrantLock lock = new ReentrantLock();
    private Condition notFull = lock.newCondition();
    private Condition notEmpty = lock.newCondition();

    public void produce(int item) throws InterruptedException {
        lock.lock();
        try {
            while (queue.size() == capacity) {
                notFull.await();  // 버퍼가 가득 차면 대기
            }
            queue.add(item);
            notEmpty.signal();  // 소비자에게만 알림
        } finally {
            lock.unlock();
        }
    }

    public int consume() throws InterruptedException {
        lock.lock();
        try {
            while (queue.isEmpty()) {
                notEmpty.await();  // 버퍼가 비면 대기
            }
            int item = queue.poll();
            notFull.signal();  // 생산자에게만 알림
        } finally {
            lock.unlock();
        }
    }
}
```

#### 장점
- 생산자와 소비자를 구분하여 알림 (효율적)
- 공정성(fairness) 옵션 제공
- 타임아웃 설정 가능
- tryLock() 등 다양한 락 획득 방법

#### 단점
- 구현이 복잡함
- finally 블록에서 unlock 필수

---

### 4. Semaphore 사용
리소스 카운팅 기반 동기화

#### 구현 방법
```java
import java.util.concurrent.Semaphore;

public class SafeBuffer {
    private Queue<Integer> queue = new LinkedList<>();
    private Semaphore mutex = new Semaphore(1);  // 상호 배제
    private Semaphore empty;  // 빈 슬롯 개수
    private Semaphore full = new Semaphore(0);  // 채워진 슬롯 개수

    public SafeBuffer(int capacity) {
        empty = new Semaphore(capacity);
    }

    public void produce(int item) throws InterruptedException {
        empty.acquire();  // 빈 슬롯 대기
        mutex.acquire();  // 임계 영역 진입
        try {
            queue.add(item);
        } finally {
            mutex.release();  // 임계 영역 탈출
        }
        full.release();  // 채워진 슬롯 증가
    }

    public int consume() throws InterruptedException {
        full.acquire();  // 채워진 슬롯 대기
        mutex.acquire();  // 임계 영역 진입
        try {
            return queue.poll();
        } finally {
            mutex.release();  // 임계 영역 탈출
        }
        empty.release();  // 빈 슬롯 증가
    }
}
```

#### 장점
- 명시적인 리소스 카운팅
- 다중 생산자/소비자에 효과적
- 이론적으로 명확한 모델

#### 단점
- 구현이 복잡함
- 세마포어 순서를 잘못 사용하면 데드락 발생 가능

---

## 성능 및 사용 권장사항

### 간단한 경우
- **BlockingQueue** 사용 추천
- 구현이 가장 간단하고 안전

### 교육/학습 목적
- **synchronized + wait/notify** 사용
- 동기화 개념 이해에 적합

### 고성능이 필요한 경우
- **ReentrantLock + Condition** 사용
- 세밀한 제어와 최적화 가능

### 특수한 요구사항
- **Semaphore**: 리소스 카운팅이 중요한 경우
- **Exchanger**: 1:1 교환이 필요한 경우
- **Phaser**: 단계별 동기화가 필요한 경우

## 추가 고려사항

### 공정성(Fairness)
- FIFO 순서 보장이 필요한지 확인
- `ReentrantLock(true)`, `Semaphore(permits, true)` 등으로 설정

### 타임아웃
- 무한 대기 방지를 위한 타임아웃 설정
- `offer(item, timeout, unit)`, `poll(timeout, unit)` 등 활용

### 인터럽트 처리
- `InterruptedException` 적절히 처리
- 스레드 종료 시그널 구현

### 모니터링
- 버퍼 상태 확인 메서드 추가
- 통계 정보 수집 (생산/소비 횟수, 대기 시간 등)