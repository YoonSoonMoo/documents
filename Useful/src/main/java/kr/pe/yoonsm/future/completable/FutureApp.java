package kr.pe.yoonsm.future.completable;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.function.Supplier;

/**
 * Created by yoonsm@daou.co.kr on 2022-05-30
 */
public class FutureApp {

    public static void main(String[] args) throws InterruptedException {
        FutureApp app = new FutureApp();
        try {
            app.runWorkers();
        } catch (ExecutionException e) {
            throw new RuntimeException(e);
        }
    }

    private void runWorkers() throws InterruptedException, ExecutionException {

        // return value가 있는 경우
        CompletableFuture<String> returnValueFuture = CompletableFuture.supplyAsync(new ReturnWorker(5000));
        // return value가 없는 경우
        CompletableFuture<Void> future = CompletableFuture.runAsync(new NoReturnWorker());
        // Exception 핸들링
        CompletableFuture<String> errorFuture = CompletableFuture.supplyAsync(new ErrorWorker())
                .handle((s, t) -> s != null ? s : "Oh my god! Error");
        // 이어지는 작업 ( 값이 있는 작업들의 연속 ) Async 가 붙어있는 것은 별도 쓰레드로 작한다.
        CompletableFuture<String> continueFuture = CompletableFuture.supplyAsync(new ReturnWorker(1500))
                .thenApply(s -> {
                    return s + " twice run";
                })
                .thenApplyAsync(s -> {
                    return s + " all process done!";
                });
        // 이어지는 작업 ( 값이 없는 작업들의 연속 )
        CompletableFuture<Void> continueFutureNoReturn = CompletableFuture.runAsync(new NoReturnWorker()).thenAccept(s ->
                {
                    System.out.println("No process");
                }
        );

        // 가장 빠른 처리 한개만
        CompletableFuture<String> anyofFuture = CompletableFuture.anyOf(returnValueFuture, continueFuture)
                .thenApply(s -> {
                    return s + " best !!!";
                }
        );

        // Future 들의 연속성
        CompletableFuture<String> continueFutures = CompletableFuture.supplyAsync(new ReturnWorker(2000))
                .thenCompose(s -> CompletableFuture.supplyAsync(new ReturnWorker(3000)));

        System.out.println("return get(): thread id : " + returnValueFuture.get());
        System.out.println("noReturn get(): thread id : " + future.get());
        System.out.println("error get(): thread id : " + errorFuture.get());
        System.out.println("continue Future apply = " + continueFuture.get());
        System.out.println("continueFuture No return apply = " + continueFutureNoReturn.get());
        System.out.println("continue Futures = " + continueFutures.get());
        System.out.println("best Futures = " + anyofFuture.get());

    }


    class ErrorWorker implements Supplier {
        @Override
        public String get() {
            String value = "no way";
            if (!value.equals("")) {
                throw new RuntimeException("anyway error!");
            }
            return value;
        }
    }

    class NoReturnWorker implements Runnable {

        @Override
        public void run() {
            try {
                System.out.println("no return value thread :" + Thread.currentThread().getId());
                Thread.sleep(1000);
                System.out.println(Thread.currentThread().getId() + " end");
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        }
    }

    class ReturnWorker implements Supplier {

        int processTime = 1000;

        public ReturnWorker(int processTime) {
            this.processTime = processTime;
        }

        @Override
        public String get() {
            try {
                System.out.println(processTime + "ms runtime thread :" + Thread.currentThread().getId());
                Thread.sleep(processTime);
                System.out.println(Thread.currentThread().getId() + " end");
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
            return String.valueOf(Thread.currentThread().getId());
        }
    }

}
