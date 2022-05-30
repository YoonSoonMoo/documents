# Multi process handling [ CompletableFuture ]
![](https://img.shields.io/badge/Java-1.8%20version-brightgreen) ![](https://img.shields.io/badge/ConuntDownLatch-java.util.concurrent-orange)

Java 에서 사용하는 Multi Process 개발 시 유용하게 사용할 수 있는 방법 그 두번째 ` [Java기준] `

## Multi thread의 병렬 프로그램 제어

#### 1) 결과값이 있는 future

``` java
        CompletableFuture<String> returnValueFuture = CompletableFuture
            .supplyAsync(new ReturnWorker(5000));
            
            ...
            
        // Supplier를 implements 해야 한다.    
        class ReturnWorker implements Supplier {
        
            ...
        
        returnValueFuture.get() // 값이 취해질 때 까지 대기   
           
```
별도의 Future Thread가 생성되어 실행됩니다.  
메인 Thread는 결과가 리턴 될때까지 대기를 하게 됩니다.

---

#### 2) 결과값이 없는 future

``` java
        CompletableFuture<Void> future = CompletableFuture
        .runAsync(new NoReturnWorker());
            
            ...
            
        // Runable 을 implements 해야 한다.    
        class NoReturnWorker implements Runnable {

        
            ...
        
        future.get() // null 값이 리턴됨   
           
```
메인 Thread는 결과가 리턴 될때까지 대기를 하게 됩니다.
결과값은 null

---

#### 3) 연속작업 ( 값이 있는 작업들의 연속 )

``` java
        CompletableFuture<String> continueFuture = CompletableFuture.supplyAsync(new ReturnWorker(1500))
                .thenApply(s -> {
                    return s + " twice run";
                })
                .thenApplyAsync(s -> {
                    return s + " all process done!";
        });

        
        ...
        
        continueFuture.get() // null 값이 리턴됨   
           
```


첫번째 작업이 완료되면 다음 작업이 시작됩니다.  
thenApply은 동일 thread thenApplyAsync는 별도의 thread를 생성해서  
동작하는 것이 틀립니다.

그외 가장 빠른 처리 한개만 선택하는 anyof 와 연속된 CompletableFuture를 
정의 할수 있는 thenCompose , 동시실행을 지원하는 combine 등을 고려해 볼 수 있다.

상세한 내용은 샘플코드를 확인해 보세요.
