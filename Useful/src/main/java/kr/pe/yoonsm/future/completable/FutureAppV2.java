package kr.pe.yoonsm.future.completable;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.function.Supplier;

/**
 * Created by yoonsm@daou.co.kr on 2022-06-23
 */
public class FutureAppV2 {

    public static void main(String[] args) throws InterruptedException, ExecutionException {

        FutureAppV2 app = new FutureAppV2();
        app.runProcess();

    }


    private void runProcess() throws ExecutionException, InterruptedException {

        long start = System.currentTimeMillis();

        MyScore myScore = new MyScore();
        myScore.setId("yoon");
        // 스코어 베이스 role을 넘긴다.

        CompletableFuture<MyScore> completableFuture = CompletableFuture.supplyAsync(new SpecialRole(myScore))
                .thenCompose(s -> CompletableFuture.supplyAsync(new DistanceRole(myScore)));
        completableFuture.get();

        long end = System.currentTimeMillis();
        System.out.println("time :" + (end - start));
    }


    class SpecialRole implements Supplier<MyScore> {

        MyScore myScore;

        public SpecialRole(MyScore myScore) {
            this.myScore = myScore;
        }

        @Override
        public MyScore get() {
            myScore.setScore(myScore.getScore() + 10);
            System.out.println("SpecialRole :" + myScore.getScore());

            try {
                Thread.sleep(500);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }

            return myScore;
        }
    }


    class DistanceRole implements Supplier<MyScore> {

        MyScore myScore;

        public DistanceRole(MyScore myScore) {
            this.myScore = myScore;
        }

        @Override
        public MyScore get() {

            myScore.setScore(myScore.getScore() + 1);
            System.out.println("DistanceRole : " + myScore.getScore());

            try {
                Thread.sleep(200);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }


            return myScore;
        }
    }


    static class MyScore {
        String id;
        int score;

        public String getId() {
            return id;
        }

        public void setId(String id) {
            this.id = id;
        }

        public int getScore() {
            return score;
        }

        public void setScore(int score) {
            this.score = score;
        }
    }
}
