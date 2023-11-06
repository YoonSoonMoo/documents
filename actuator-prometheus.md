# actuator - prometheus
![](https://img.shields.io/badge/spring%20boot-3.1.4.RELEASE-brightgreen) ![](https://img.shields.io/badge/Gradle-8.3-red)  ![](https://img.shields.io/badge/actuator-3.1.4-blue)  
powered by [Java]  platform development team present ⓒ2023 DAOU Tech., INC. All rights reserved.

### actuator 도입 이유

`전투에서 실패한 지휘관은 용서할 수 있지만 경계에서 실패하는 지휘관은 용서할 수 없다`  라는 말이 있다.
이 말을 서비스를 운영하는 개발자에게 맞추어 보면 장애는 언제든지 발생할 수 있다. 하지만 모니터링(경계)은 잘 대응하는 것이 중요하다는 의미이다.
장애가 발생했을 경우 가장 중요시 여기는 것이 무엇인가?  
사업팀 입장이라면 매출에 대한 영향 일것이고 개발팀 입장에서는 얼마나 빠르게 인지했느냐 일 것이다.

개발팀의 입장에서 고려해 본다면  우리가 지켜봐야 하는 내용에는 어떤 것들이 있을까?  
개인적으로는 지표(metric) , 추적(trace) 라고 생각한다.  
이 부분에 대해서는 신규 서비스를 개발할 때 크게 비중을 두고 계획을 한다.  
서비스 오픈 이후에도 개선 작업을 지속하는 이유이다.  

`actuator`는 이전부터 어느 정도 인지하고 있는 기술이었으나 실제 운영 환경에 적용하는 것을 고려하지 않았다.  
그 이유는 서비스 api 에 함께 실려지는 부분으로 보안상의 리스크와 ( 실제 actuator에는 서비스를 원격으로 내리는 기능도 있음 )  
지표 , 추적기능을 `스카우터`가 대체 할 수 있었기 때문이었다.  
하지만 스카우터에서 지원되지 않는 기능이 있었으니 그것은 바로 비지니스 매트릭이다.  
비즈니스 매트릭이란 이전 우리가 개발/운영 했던 배달대행 서비스에서 예를 들면 활성화 된 지점의 액티브 
 라이더의 수, 온라인 매장 수를 예로 들수 있겠다.
시스템 매트릭 (CPU , 메모리) , 애플리케이션 매트릭( 톰켓 쓰레드풀 , 커넥션풀 수 등 ) 에서 문제가   
확인되지 않으나 특정 지점의 라이더와 온라인 매장이 0 이라면 무언가 문제가 있는것 아닐까?  
이런 비지니스 매트릭을 확인하기 위해 나는 프로메테우스와 그라파나를 활용했다.  
비즈니스 매트릭의 장점으로는 개발자 뿐만이 아닌 사업팀에서도 유의미한 정보가 된다는 것이다.

#### 프로젝트 구성

필요한 라이브러리는 아래와 같습니다.  
`build.gradle`
```css
plugins {
    id 'java'
    id 'org.springframework.boot' version '3.1.4'
    id 'io.spring.dependency-management' version '1.1.3'
}
dependencies {
    implementation 'org.springframework.boot:spring-boot-starter-actuator'
    implementation 'org.springframework.boot:spring-boot-starter-web'
    testImplementation 'org.springframework.boot:spring-boot-starter-test'
}
```

기본적으로 `actuator` 가 설치되어 있다면 아래의 url 로 아래와 같은 결과를 확인할 수 있다.  
http://localhost:8080/actuator

```json
{
  _links: {
    self: {
      href: "http://localhost:8080/actuator",
      templated: false
    },
    health-path: {
      href: "http://localhost:8080/actuator/health/{*path}",
      templated: true
    },
    health: {
      href: "http://localhost:8080/actuator/health",
      templated: false
    }
  }
}
```

appliaction.yml 에 아래와 같이 정의하면 actuator 기능으로 어플리케이션를 리모트 셧다운 할수 있다.
```yaml
management:
  endpoints:
    web:
      exposure:
        include: "*"
  endpoint:
    shutdown:
      enabled: true
```
단 post 로 호출을 해야한다.
프로젝트에 정의되어 있는 UserTest.http 에서 아래의 url을 실행해 보자.

```html
POST http://localhost:8080/actuator/shutdown
```

시스템의 정보를 얻는 메트릭은 추가적인 설정이 필요하다.
```yaml
management:
  info:
    java:
      enabled: true
    os:
      enabled: true
  endpoints:
    web:
      exposure:
        include: "*"
  endpoint:
    shutdown:
      enabled: true
```
시스템의 메트릭 종류를 알고자 한다면 아래의 URL에서 확인이 가능하다.
```
http://localhost:8080/actuator/metrics
```

#### 본격적인 어플리케이션 관련 메트릭
- 스프링 MVC 메트릭
>스프링 MVC 컨트롤러가 처리하는 모든 요청을 다룬다.
메트릭 이름: http.server.requests

- 데이터소스 메트릭
> DataSource , 커넥션 풀에 관한 메트릭을 확인할 수 있다.
jdbc.connections. 으로 시작한다.
최대 커넥션, 최소 커넥션, 활성 커넥션, 대기 커넥션 수 등을 확인할 수 있다.
- 로그 메트릭
> logback.events : logback 로그에 대한 메트릭을 확인할 수 있다
- 톰켓 메트릭
> 톰켓의 최대 쓰레드 ,사용 쓰레드 수를 포함한 다양한 메트릭을 확인할 수 있다.
```yaml
server:
 tomcat:
  mbeanregistry:
    enabled: true
```

#### 마이크로미터 프로메테우스 구현체 실현
프로메테우스를 실행하고 구현되어 기존 프로젝트에 프로메테우스용 매트릭 구현체를 추가한다.  
아래의 내용을 `gradle` 에 추가한다.
```yaml
implementation 'io.micrometer:micrometer-registry-prometheus'
```
> 실행 : http://localhost:8080/actuator/prometheus

이제 프로메테우스가 본 프로젝트에서 value를 가져갈수 있도록 설정한다.
```yaml
    #추가 prometheus.yml
 -  job_name: "spring-actuator"
    metrics_path: '/actuator/prometheus'
    scrape_interval: 1s
    static_configs:
        - targets: ['localhost:8080']
```
연동이 잘 되었다면 아래의 URL로 프로메테우스 설정을 확인 할 수 있다.
> http://localhost:9090/config  
> http://localhost:9090/targets


레디스 (DataRedis CrudRepository 사용시) Redis의 동작

```javascript
D:\tools\Redis-x64-5.0.14.1>redis-cli.exe monitor
OK
1698658337.356791 [0 127.0.0.1:55597] "PING"
1698658337.368867 [0 127.0.0.1:55597] "INFO"

-- 1개의 상품을 등록할때 (product:a00001)
1698658350.671327 [0 127.0.0.1:55597] "DEL" "product:a00001"
1698658350.673215 [0 127.0.0.1:55597] "HMSET" "product:a00001" "_class" "kr.pe.yoonsm.actuator.repository.entity.Product" "id" "a00001" "price" "23000" 
"productName" "\xec\x88\x9c\xeb\xac\xb4\xec\x8b\xa0\xeb\xb0\x9c" "quantity" "10"
1698658350.675562 [0 127.0.0.1:55597] "SMEMBERS" "product:a00001:idx"
1698658350.676839 [0 127.0.0.1:55597] "TYPE" "product:productName:\xec\x88\x9c\xeb\xac\xb4\xec\x8b\xa0\xeb\xb0\x9c"
1698658350.677944 [0 127.0.0.1:55597] "SREM" "product:productName:\xec\x88\x9c\xeb\xac\xb4\xec\x8b\xa0\xeb\xb0\x9c" "a00001"
1698658350.678453 [0 127.0.0.1:55597] "DEL" "product:a00001:idx"
1698658350.679299 [0 127.0.0.1:55597] "SADD" "product:productName:\xec\x88\x9c\xeb\xac\xb4\xec\x8b\xa0\xeb\xb0\x9c" "a00001"
1698658350.679902 [0 127.0.0.1:55597] "SADD" "product:a00001:idx" "product:productName:\xec\x88\x9c\xeb\xac\xb4\xec\x8b\xa0\xeb\xb0\x9c"

-- 1개의 상품을 갱신할때 (product:a00001)
1698828560.396978 [0 127.0.0.1:58942] "HGETALL" "product:a00001"
 product:a00001 키로 지정된 value를 취득한다.
1698828560.416124 [0 127.0.0.1:58942] "DEL" "product:a00001"
 찾은 키의 정보를 삭제한다.
1698828560.418374 [0 127.0.0.1:58942] "HMSET" "product:a00001" "_class" "kr.pe.yoonsm.actuator.repository.entity.Product" "id" "a00001" "price" "23000" "productName" "\xec\x88\x9c\xeb\xac\xb4\xec\x8b\xa0\xeb\xb0\x9c2" "quantity" "5"
 삭제한 키에 새로운 멀티 필드값을 적재한다.
1698828560.420651 [0 127.0.0.1:58942] "SMEMBERS" "product:a00001:idx"
 해당 키를 smembers에 등록한다.
1698828560.421785 [0 127.0.0.1:58942] "TYPE" "product:productName:\xec\x88\x9c\xeb\xac\xb4\xec\x8b\xa0\xeb\xb0\x9c"
 상품이름으로 인덱싱을 했으므로 타입을 검색한다.
1698828560.423299 [0 127.0.0.1:58942] "SREM" "product:productName:\xec\x88\x9c\xeb\xac\xb4\xec\x8b\xa0\xeb\xb0\x9c" "a00001"
 인덱싱 (smembers)에서 해당 값을 삭제한다. a00001
1698828560.423859 [0 127.0.0.1:58942] "DEL" "product:a00001:idx"
 키 인덱싱 값을 삭제한다.
1698828560.424804 [0 127.0.0.1:58942] "SADD" "product:productName:\xec\x88\x9c\xeb\xac\xb4\xec\x8b\xa0\xeb\xb0\x9c2" "a00001"
 인덱싱 (smembers)에 a00001를 추가 (키는 상품이름)
1698828560.425527 [0 127.0.0.1:58942] "SADD" "product:a00001:idx" "product:productName:\xec\x88\x9c\xeb\xac\xb4\xec\x8b\xa0\xeb\xb0\x9c2"
 인덱싱 (smembers)에 상품이름을 추가한다. (키는 상품코드)

-- 상품 이름으로 검색할때
1698827995.470290 [0 127.0.0.1:58742] "SINTER" "product:productName:\xec\x88\x9c\xeb\xac\xb4\xec\x8b\xa0\xeb\xb0\x9c"
 입력값을 가지는 중복된 값을 취득한다.
1698827995.471589 [0 127.0.0.1:58742] "HGETALL" "product:a00002"
 추출된 키정보로 값을 취득한다.
1698827995.472317 [0 127.0.0.1:58742] "HGETALL" "product:a00001"

```


비지네스 용도의 metrics를 class method 단위로 등록해서 사용한다.  
AOP 방식을 지원하므로 간단하게 등록해서 사용해 보자
metrics를 관리자에 Counted (카운트용 Aspect ) 를 아래와 같이 등록한다.
```java
    @Bean
    public CountedAspect countedAspect(MeterRegistry meterRegistry) {
        return new CountedAspect(meterRegistry);
    }
```

적용 대상 class에 `counted` 를 적용해 보자
```java
    @Counted("my.product")
    public CommonResponse<String> addProductProcess(ProductRequest productRequest) {
        CommonResponse commonResponse = new CommonResponse();
        Product product = Product.builder()
                .id(productRequest.getId())
                .productName(productRequest.getProductName())
                .price(productRequest.getPrice())
                .quantity(productRequest.getQuantity()).build();
        productRepository.save(product);

        commonResponse.setResult("200");
        commonResponse.setData(product.getProductName());
        return commonResponse;
    }
```
my.product -> my_product_total 의 키가 생성되며 위의 method를 호출할 경우  
아래와 같은 metric을 확인할 수 있다.




출처 :
https://hyperconnect.github.io/2022/12/12/fix-increasing-memory-usage.html