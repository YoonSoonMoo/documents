# Schedule Process [ ScheduledExecutorService ]
![](https://img.shields.io/badge/Java-1.8%20version-brightgreen) ![](https://img.shields.io/badge/ConuntDownLatch-java.util.concurrent-orange)

  Java Original 스케줄 프로세스를 쉽게 만드는 방법 ` [Java기준] `

## 스케줄 프로그램

그냥 소스로 설명하는 것이 가장 간단

``` java
    public static void main(String[] args) {

        ScheduledExecutorService executor = Executors.newScheduledThreadPool(2);
        Runnable runnable = () -> {
            System.out.println("++ Repeat task : " + LocalTime.now());
            sleepSec(3);
            System.out.println("-- Repeat task : " + LocalTime.now());
        };
        int initialDelay = 2;
        int delay = 3;

        // schedule the job
        System.out.println("Scheduled task : " + LocalTime.now());
        executor.scheduleWithFixedDelay(
                runnable, initialDelay, delay, TimeUnit.SECONDS);
    }
```

ScheduledExecutorService을 사용하면 간단하게 스케줄 프로그램을 만들 수 있다.  
본격적인 스케줄 프로그램이라면 Quartz 를 사용해야 겠지만 위의 내용만으로도 왠간한  
스케줄 프로그램을 만들 수 있지 않을까?

TimeUit 이라는 클래스도 꽤 괜찮아 보임 ( jdk 1.5 부터 있던 기능인데... )

sleepSec method는 아래
```java
    private static void sleepSec(int sec) {
        try {
            TimeUnit.SECONDS.sleep(sec);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
```


---

