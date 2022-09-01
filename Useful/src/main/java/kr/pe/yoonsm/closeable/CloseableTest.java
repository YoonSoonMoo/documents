package kr.pe.yoonsm.closeable;

/**
 * Created by yoonsm@daou.co.kr on 2022-09-01
 */
public class CloseableTest {

    public static void main(String[] args) {
        System.out.println("-- Try 새로운 기능 auto close 테스트 --");
        try (
                // 자원을 효율적으로 관리하거나 반드시 후속처리 ( 클로징 ) 가 필요한 경우
                WorkAutoCloseImpl workAutoClose = new WorkAutoCloseImpl();
        ) {
            // 처리가 끝나면 close가 호출된다.
            workAutoClose.run();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    static class WorkAutoCloseImpl implements AutoCloseable {

        public void run() {
            System.out.println("Work Work!!");
        }
        @Override
        public void close() throws Exception {
            System.out.println("자동으로 클로즈");
        }
    }

}
