package kr.pe.yoonsm.lambda;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Created by yoonsm@daou.co.kr on 2023-03-06
 */
public class LambdaTest {

    public static void main(String[] args) throws InterruptedException {
        System.out.println("test start!");

        //given
        List<RequestInput> requestList = List.of(new RequestInput("yoon","윤순무",23),new RequestInput("kim","김철호",21));

        //when Lambda의 map 에서 복사가 되는지 확인
        List<DBEntity> resultList = requestList.stream().map(re -> new DBEntity(re.getId(),re.getName())).collect(Collectors.toList());

        //then
        resultList.stream().forEach(re-> System.out.println(re.getName()));
    }


    static class RequestInput{

        public RequestInput(String id, String name, int age) {
            this.id = id;
            this.name = name;
            this.age = age;
        }

        String id;
        String name;
        int age;

        public String getId() {
            return id;
        }

        public void setId(String id) {
            this.id = id;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public int getAge() {
            return age;
        }

        public void setAge(int age) {
            this.age = age;
        }
    }

    static class DBEntity{

        String id;
        String name;

        public DBEntity(String id, String name) {
            this.id = id;
            this.name = name;
        }

        public String getId() {
            return id;
        }

        public void setId(String id) {
            this.id = id;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }
    }

}
