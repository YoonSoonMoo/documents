# EhCache
![](https://img.shields.io/badge/spring%20boot-2.7.x.RELEASE-brightgreen) ![](https://img.shields.io/badge/jpa-2.7.5-orange)  ![](https://img.shields.io/badge/H2-1.4-yellow) ![](https://img.shields.io/badge/Gradle-7.X-Green)  ![](https://img.shields.io/badge/lombok-1.18.8-blue)  
powered by [Java]  platform development team present ⓒ2022 DAOU Tech., INC. All rights reserved.

**기대 효과**
- Cache를 사용하면 자주 사용되는 리소스가 존재 할시 리소스를 얻은 후 캐시 저장소에 만료시간과 함께 저장하고 사용자가 조회를 요청할 때 마다 만료시간 이전까지는 캐시 저장소에 있는 리소스를 사용함으로써 조회 성능을 대폭 향상 시킬 수 있습니다.

## 프로젝트 구성

필요한 라이브러리는 아래와 같습니다.  
`build.gradle`
```css
	// SpringBoot
	implementation 'org.springframework.boot:spring-boot-starter-web'
	implementation 'org.springframework.boot:spring-boot-starter-aop'

	// JPA
	implementation 'org.springframework.boot:spring-boot-starter-data-jpa'
	runtimeOnly 'com.h2database:h2'

	// ehcache
	implementation 'org.springframework.boot:spring-boot-starter-cache'
	implementation 'org.ehcache:ehcache:3.10.8'
	implementation 'javax.cache:cache-api:1.1.1'

	compileOnly 'org.projectlombok:lombok'
	annotationProcessor 'org.projectlombok:lombok'
	testImplementation 'org.springframework.boot:spring-boot-starter-test'
```

ehcache는 최신버전 3.10 버전으로 구성을 했습니다.  
- offheap 저장공간을 제공 ( 본 예제에서도 offheap을 저장공간으로 지정했습니다. )
- javax.cache API(JSR-107)와 호환성을 제공
  
> 2.x 버전은 GC에 의해 데이타가 소멸되는 경우가 있습니다.

gradle 설정을 추가하면 필요한 라이브러리가 로딩됩니다.  
다음은 ehcache 설정입니다. 크게 2개의 파일이 대상이 됩니다.
`application.yml` ehcache 설정파일을 지정해 줍니다.

```yml
spring:
  cache:
    jcache:
      config: classpath:ehcache.xml
```
`ehcache.xml` 위에서 지정한 ehcache의 설정파일 입니다.    
파일 위치는 resource 입니다. 

```xml
<?xml version="1.0" encoding="UTF-8"?>
<config xmlns='http://www.ehcache.org/v3'>
    <cache alias="findUserHistoryCache">
        <key-type>org.springframework.cache.interceptor.SimpleKey</key-type>
        <value-type>java.util.List</value-type>
        <expiry>
            <ttl unit="seconds">60</ttl>
        </expiry>
        <resources>
            <offheap unit="MB">10</offheap>
        </resources>
    </cache>
</config>
```

샘플 코드이므로 주요한 항목만 설정이 되어 있습니다.  
여기서 확인할 내용은 `alias` 캐시의 이름 그리고 `ttl` 해당캐시의 유효시간 그리고 캐싱에 사용할 `offheap` 메모리 용량 입니다.  
이 부분은 실제 프로젝트에 적용 시 적절하게 변경을 해야 합니다.  

다음은 메인 프로그램에 캐시를 사용하겠다는 설정입니다.  
`@EnableCaching` 을 추가해 주면 됩니다.  
```java
@SpringBootApplication
@EnableCaching
public class AopApplication {
    public static void main(String[] args) {
        SpringApplication.run(AopApplication.class, args);
    }

}
```

다음은 캐싱할 데이타를 지정하면 됩니다.  
예제에서는 유저 변경 history를 지정했습니다.  
사용방법은 매우 간단합니다. 캐싱할 데이타에 `@Cacheable` 어노테이션을 추가하고 사용할 캐싱저장소 이름을 지정하면 됩니다.  
또한 ttl로 캐싱이 삭제가 되기전에 임이로 캐싱을 삭제하고 싶을때는 `@CacheEvict` 를 추가하고 삭제할 캐싱저장소 이름을 지정하면 됩니다.

```java
    /**
     * 유저 변경 이력을 모두 조회한다.
     * 조회된 내용은 ehCache에 저장된다.
     * @return
     */
    @Cacheable(cacheNames = "findUserHistoryCache" )
    public List<HistoryEntity> getAllHistory() {
        return historyDataJpaRepository.findAll();
    }

    /**
     * 캐시에 저장되어 있는 내용을 삭제한다.
     */
    @CacheEvict(cacheNames ="findUserHistoryCache",allEntries = true)
    public void clearCache(){
        log.info("cache clear!!");
    }

```

예제는 아래의 순서로 캐싱되는 내용을 검증해 볼수 있습니다.
아래의 테스트를 진행해 보면 데이타가 어떤 타이밍에 저장되고 어떤 타이밍에 사용되는지 데이타를 캐싱했을 경우 어떤 문제가 발생하는지 
이를 해결하기 위해 캐싱 데이타를 어떻게 삭제하는지 체험해 볼수 있습니다.

1. 신규 유저를 추가한다. -> 유저변경 이력에 내용이 저장됩니다.
2. 유저 변경 이력을 조회한다. -> 최초 호출이므로 데이타베이스에서 내용을 읽어오고 캐싱 저장소에 내용을 저장합니다.
3. 유저를 변경한다. -> 유저변경 이력이 추가 됩니다.
4. 유저 변경 이력을 조회한다. ->  캐싱된 데이타가 리턴됩니다.  3번에서 유저를 변경한 이력은 볼수 없습니다.
5. 캐싱 저장소 내용을 삭제한다.  ( 또는 1분을 기다리면 캐싱 데이타가 삭제됩니다. )
6. 유저 변경 이력을 조회한다. -> 캐싱 저장소가 삭제 되었으므로 3번 유저변경 이력을 데이타베이스에서 읽어와서 표현합니다.

프로젝트 내에 있는 `UserTest.http` 파일을 열고 위의 순서대로 실행을 합니다.

```json

### [DBRepository]신규 유저를 추가한다.
POST http://localhost:8080/api/v2/adduser
Content-Type: application/json

{
"userId" : "yoonsm",
"userName" : "윤순무",
"age" : 30 ,
"address1": "죽전로 43",
"address2" : "율곡아파트"
}

### [DBRepository]유저를 수정한다. ( 주소1과 주소2 수정 )
POST http://localhost:8080/api/v2/edituser
Content-Type: application/json

{
"userId" : "yoonsm",
"userName" : "윤순무",
"age" : 31 ,
"address1": "죽전로 513",
"address2" : "파랑 아파트 22동"
}

### [DBRepository]이력을 조회한다.
GET http://localhost:8080/api/v2/history/all

### [DBRepository]캐시를 삭제한다.
GET http://localhost:8080/api/v2/clear/cache

```

로그로 확인해 보면
신규 유저가 추가되어 ys_history 테이블에 이력이 추가되었습니다.
```css
2022-12-15 14:28:46.737  INFO 45728 --- [nio-8080-exec-1] k.p.y.h.aop.Aspect.HistoryDBAspect       : UserDao Parameter values UserDao(userId=yoonsm, userName=윤순무, age=30, address1=죽전로 43, address2=율곡아파트)
Hibernate: 
    call next value for hibernate_sequence
Hibernate: 
    insert 
    into
        ys_history
        (change_data, local_date_time, seq) 
    values
        (?, ?, ?)
2022-12-15 14:28:46.747  INFO 45728 --- [nio-8080-exec-1] k.p.y.h.aop.Aspect.HistoryDBAspect       : History Annotation Changed data : yoonsm 신규추가
```
유저 이력을 조회할 경우 데이타베이스에서 이력을 호출합니다. ( 여기서 호출된 내용이 캐싱 됩니다. )  
한번더 호출하면 아래의 로그가 출력되지 않고 캐싱된 데이타를 리턴합니다.
```css
Hibernate: 
    select
        historyent0_.seq as seq1_0_,
        historyent0_.change_data as change_d2_0_,
        historyent0_.local_date_time as local_da3_0_ 
    from
        ys_history historyent0_
2022-12-15 14:31:05.970  INFO 45728 --- [nio-8080-exec-3] k.p.y.h.aop.Aspect.HistoryDBAspect       : >> process time : 13 ms
```
유저를 수정해 봅니다.  
유저 이력에도 내용이 추가된 것을 알수 있습니다.  
다만 이력 조회를 해도 추가된 내용이 표시되지 않습니다.
```css
Hibernate: 
    insert 
    into
        ys_history
        (change_data, local_date_time, seq) 
    values
        (?, ?, ?)
Hibernate: 
    update
        ys_user 
    set
        address1=?,
        address2=?,
        age=?,
        user_id=?,
        user_name=? 
    where
        id=?
2022-12-15 14:35:43.682  INFO 45728 --- [nio-8080-exec-4] k.p.y.h.aop.Aspect.HistoryDBAspect       : History Annotation Changed data : address1 changed 죽전로 513->죽전로 43 address2 changed 파랑 아파트 22동->율곡아파트 age changed 31->30 
2022-12-15 14:35:43.682  INFO 45728 --- [nio-8080-exec-4] k.p.y.history.aop.Aspect.HistoryAspect   : >> process time : 15 ms
```
캐싱을 삭제해 보겠습니다.
```css
2022-12-15 14:38:05.149  INFO 45728 --- [nio-8080-exec-6] k.p.y.h.aop.services.UserDBService       : cache clear!!
```
다시 이력을 조회해 보면 다시 이력을 조회해 오는 것을 알수 있습니다.
```css
Hibernate: 
    select
        historyent0_.seq as seq1_0_,
        historyent0_.change_data as change_d2_0_,
        historyent0_.local_date_time as local_da3_0_ 
    from
        ys_history historyent0_
2022-12-15 14:38:38.080  INFO 45728 --- [io-8080-exec-10] k.p.y.h.aop.Aspect.HistoryDBAspect       : >> process time : 2 ms

```