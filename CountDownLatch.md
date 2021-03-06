# Multi process handling [ CountDownLatch ]
![](https://img.shields.io/badge/Java-1.8%20version-brightgreen) ![](https://img.shields.io/badge/ConuntDownLatch-java.util.concurrent-orange)

  Java 에서 사용하는 Multi Process 개발 시 유용하게 사용할 수 있는 방법 ` [Java기준] `
* 해당 Class 는 GNU General Public License version 2 정책에 따라 퍼블리싱 되었습니다.


## Multi thread의 병렬 프로그램 제어

#### 단일 thread의 종료 처리

아래는 단일 thread의 처리를 ``join`` 으로 처리한 예 입니다.

``` java
Thread thread = new Thread() {
	@Override
	public void run() {
		System.out.println("start trhead.");
		try {
			Thread.sleep(1000);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		System.out.println("end trhead.");
	}
};
thread.start();
try {
	// 스레드가 끝날때 까지 대기한다.
	thread.join();
} catch (InterruptedException e) {
	e.printStackTrace();
}
System.out.println("Thread terminated.");

```
단일 thread를 제어하는 것이라면 ``join`` 으로 제어가 가능하지만 많은 thread를 제어해야 하는 경우라면 그다지
효율적이지 않을 듯 합니다.

---

#### 복수 thread 의 효율적 대기 처리

Java Concurrent package에서는 Multi thread를 효율적으로 제어하는 방법을 제공합니다.


다음은 CountDownLatch 을 사용해서 thread를 제어하는 sequence diagram 입니다.
![image](https://user-images.githubusercontent.com/6250760/157005659-d6148347-e96e-4ce6-8b37-5b7b796696a0.png)


> 위의 예제는 thread 5개가 모두 완료된 상태가 되어야 후속 처리를 진행하는 flow 를 설명합니다.
> 각각의 thread에서는 처리가 완료 되었을 경우 count down 를 호출합니다.
> 처음 정의한 thread의 count가 0이 되면 다음 처리로 넘어가게 됩니다.
> 처리를 기다리는 method 는  **countDownLatch.await()** 입니다.


---

#### 복수 thread 의 효율적 대기 처리 (예제)

위의 sequence diagram 을 program으로 구현하면 아래와 같습니다.
``CountDownLatch`` 객체를 각각의 thread에 넘겨줍니다.  
마지막의 ``모든 처리가 완료 되어 표시...`` 는 모든 thread가 완료되면 출력이 됩니다.


```java
    private void runWorkers() throws InterruptedException {
        CountDownLatch countDownLatch = new CountDownLatch(5); // 최대 5개의 쓰레드 동시 실행
        IntStream.range(0, 5)
                .mapToObj(i -> new Worker(i, countDownLatch))
                .map(Thread::new)
                .forEach(Thread::start);

        // 기다리는 처리
        countDownLatch.await();

        System.out.println("모든 처리가 완료 되어 표시...");
    }

```

각 thread는 실행이 완료되면 전달받은 ``CountDownLatch``인스턴스의 ``countDown()`` method를 호출합니다.


```java

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
                System.out.println("시작 thread --- " + index);
                Thread.sleep(3000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            } finally {
                System.out.println("종료 thread --- " + index);
                countDownLatch.countDown();
            }
        }
    }

```

실행 결과는 아래와 같습니다.
```javascript

시작 thread --- 2
시작 thread --- 3
시작 thread --- 1
시작 thread --- 4
시작 thread --- 0
종료 thread --- 2
종료 thread --- 4
종료 thread --- 0
종료 thread --- 1
종료 thread --- 3
모든 처리가 완료 되어 표시...

Process finished with exit code 0

```

thread 실행과 종료가 모두 제 각각이지만 가장 마지막에 실행되어야 하는 처리는 thread가 모두 종료된 이후 실행되는 것을 알수 있습니다.
