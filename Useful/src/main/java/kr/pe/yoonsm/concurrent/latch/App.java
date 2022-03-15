package kr.pe.yoonsm.concurrent.latch;

import java.util.concurrent.CountDownLatch;
import java.util.stream.IntStream;

public class App {

    public static void main(String[] args) throws InterruptedException {
        App app = new App();
        app.runWorkers();
    }

    private void runWorkers() throws InterruptedException {
        // 최대 5개의 쓰레드 동시 실행
        CountDownLatch countDownLatch = new CountDownLatch(5);
        IntStream.range(0, 5)
                .mapToObj(i -> new Worker(i, countDownLatch))
                .map(Thread::new)
                .forEach(Thread::start);

        // 기다리는 처리
        countDownLatch.await();
        System.out.println("모든 처리가 완료되었습니다...");

    }

    public class Worker implements Runnable { //쓰레드
        private CountDownLatch countDownLatch;
        private int index;

        public Worker(final int index, final CountDownLatch countDownLatch) {
            this.index = index;
            this.countDownLatch = countDownLatch;
        }

        @Override
        public void run() {
            try {
                System.out.println("시작 thread --- "+ index);
                Thread.sleep(3000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            } finally {
                System.out.println("종료 thread --- " + index);
                countDownLatch.countDown();
            }
        }
    }
}