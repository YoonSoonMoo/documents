# Auto Close [ AutoCloseable ]
![](https://img.shields.io/badge/Java-1.7%20version-brightgreen) ![](https://img.shields.io/badge/AutoCloseable-java.util.closeable-orange)

Java7부터 Try-with-resources 구문을 지원하고 이것을 사용하면 자원을 쉽게 해제할 수 있습니다.  
다음 코드는 Try-with-resources를 사용하여 InputStream으로 파일의 문자열을 모두 출력하는 코드입니다. 

```java

public static void main(String args[]) {
    try (
        FileInputStream is = new FileInputStream("file.txt");
        BufferedInputStream bis = new BufferedInputStream(is)
    ) {
        int data = -1;
        while ((data = bis.read()) != -1) {
            System.out.print((char) data);
        }
    } catch (IOException e) {
        e.printStackTrace();
    }
}

```


## AutoCloseAble 계승한 자동 클로징 클래스 만들기 

#### 뭐 소스로 이해하자

``` java

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

           
```

실행 결과는 아래

>
> -- Try 새로운 기능 auto close 테스트 --  
Work Work!!  
자동으로 클로즈  


WorkAutoClose가 실행된 이후 자동으로 클로즈 된것을 알수 있다.  
유용할 듯한 느낌!!  
테스트코드는 Useful 안에  
