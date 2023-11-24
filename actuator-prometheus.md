# actuator - prometheus
![](https://img.shields.io/badge/spring%20boot-3.1.4.RELEASE-brightgreen) ![](https://img.shields.io/badge/Gradle-8.3-red)  ![](https://img.shields.io/badge/actuator-3.1.4-blue) ![](https://img.shields.io/badge/redis-5.0.14-orange)  
powered by [Java]  platform development YSM present ⓒ2023 DAOU Tech., INC. All rights reserved.

### Actuator 도입 이유

`전투에서 실패한 지휘관은 용서할 수 있지만 경계에서 실패하는 지휘관은 용서할 수 없다`  라는 말이 있다.  
이 말을 개발적인 관점에서 해석한다면  문제(장애)를 사전에 발견하고 대응하는 것에 대한 중요성을 강조한 내용이라 생각한다.

그러면 우리가 모니터링(경계)해야 하는 내용에는 어떤 것들이 있을까?  
개인적으로는 지표(metric) , 추적(trace) 라고 생각한다.

지표 또는 추적을 위한 기술로 `actuator`를 설명 하고자 한다.  
`actuator`는 지표와 추적에 적합한 기술이나 실제 운영 환경에 적용하는 것을 고려하지 않았다.  
그 이유는 서비스에 적용할 경우 보안상의 리스크와 ( 실제 actuator에는 서비스를 원격으로 내리는 기능도 있음 )  
지표 , 추적 기능을 별도의 APM( `스카우터`)이 대체 할 수 있었기 때문 이었다.    
하지만 스카우터에서 지원되지 않는 기능이 있었으니 그것은 바로 비지니스 메트릭이다.  
비즈니스 메트릭이란 프로세스의 최종 결과를 시간의 흐름에 따라 표시한 값을 의미한다.   
이전 우리가 개발/운영 했던 배달대행 서비스에서 예를 들어보자.  
활성화 된 특정 지점 주문수 또는 활동중인 라이더 수 등이 있겠다.  
시스템 메트릭 (CPU , 메모리) , 애플리케이션 메트릭( 톰켓 쓰레드 , DB 커넥션 풀 수 ) 에서 문제가   
확인되지 않으나 특정 지점의 매출이 0원 또는 매출이 있으나 활동하고 있는 라이더 수가 0명이라면 뭔가 문제가 있는것 아닐까?    
이런 문제를 인지하기 위해 배달대행 서비스에서는 로그 기반으로 메트릭을 생성하고 해당 데이타를 프로메테우스에 저장  
시각화는 그라파나를 활용했다.  
![](https://lh3.googleusercontent.com/u/0/drive-viewer/AK7aPaCegUhXOBrKeAHPqHDtnlYTmYOydfvpWjMrP8ywZOmmFJFWR4AvataWudWy6z-0A2cyBsl6TjiFkRgxtqMwdOUhgftkqQ=w1594-h1019)

추가적으로 비즈니스 메트릭의 내용에 따라 개발팀 뿐만이 아닌 사업팀에서도 유의미한 정보가 될수도 있다.
이러한 비즈니스 메트릭을 만들기 위한 가장 효율적인 기술이 `actuator` 라고 생각했다.

### 무엇을 검증 해볼까?

`actuator` 를 이용한 비즈니스 메트릭 검증을 검토해 보자
상품을 등록,수정,검색하는 API 대상으로 호출된 카운트 (상품 등록수 , 검색 검수 등)를   
비즈니스 메트릭으로 정의하고 그 결과를 확인해 보자.  
추가적으로 Redis 사용에 있어 DataRedis 그리고 RedisTemplate 사용에 대한 성능 검증 (프로메테우스 사용)도 병행해 보자.

- gradle 구성 및 필요 라이브러리 소개
- `actuator`의 확인
- `actuator`의 설정
- 어플리케이션 관련 메트릭에 대한 소개
- 프로메테우스 설치 및 설명
- AOP를 통한 마이크로미터 설정 설명
- actuator - prometheus 연동을 통한 성능 비교  
  ㄴ DataRedis 사용 BL 작성 (V1) : Redis 발생 커멘트 확인  
  ㄴ RedisTemplate 사용 BL 작성 (V2) : Redis 발생 커멘트 확인

### 프로젝트 구성

필요한 라이브러리는 아래와 같다.  
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
프로메테우스를 실행하고 구현되어 기존 프로젝트에 프로메테우스용 메트릭 구현체를 추가한다.  
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

비지니스 용도의 metrics를 class method 단위로 등록해서 사용한다.  
AOP 방식을 지원하므로 간단하게 등록해서 사용해 보자
metrics를 관리자에 Counted (카운트용 Aspect) 를 아래와 같이 등록한다.
```java
    @Bean
    public CountedAspect countedAspect(MeterRegistry meterRegistry) {
        return new CountedAspect(meterRegistry);
    }
```

적용 대상 class에 `counted` 를 적용해 보자
```java
    @Counted("my.redisTemp.product")
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
my.redisTemp.product -> my_redisTemp_product_total 의 키가 생성되며 위의 method를 호출할 경우  
아래와 같은 metric을 확인할 수 있다. (count metric)
```javascript
# HELP my_redisTemp_product_total  
# TYPE my_redisTemp_product_total counter
my_redisTemp_product_total{class="kr.pe.yoonsm.actuator.service.ProductRedisTempService",exception="none",method="addProductProcess",result="success",} 1.0
my_redisTemp_product_total{class="kr.pe.yoonsm.actuator.service.ProductRedisTempService",exception="none",method="findProductByProductName",result="success",} 2.0
my_redisTemp_product_total{class="kr.pe.yoonsm.actuator.service.ProductRedisTempService",exception="none",method="updateProductProcess",result="success",} 1.0
my_redisTemp_product_total{class="kr.pe.yoonsm.actuator.service.ProductRedisTempService",exception="none",method="findProductById",result="success",} 7.0
```
![](https://lh3.googleusercontent.com/u/0/drive-viewer/AK7aPaDApdqcSFovFWaDLtWvKGb_YFu2YnHCtOqxCkd7zxS7QH4M9lp4W5vVc2UovLXU4fEmww57ClRgou9UasPvSPclggPfsw=w1594-h1019)

### actuator - prometheus 연동을 통한 성능 비교
시스템 및 어플리케이션 메트릭이 적용되었으니 Redis 데이타 핸들링 방식 2가지를 서로 비교 하도록 해 보겠다.  

Redis (DataRedis CrudRepository 사용시) Redis의 동작
DataRedis의 구현체는 매우 간단하다.
상품명을 찾는 method만 추가 했으며 JPA 와 동일하게 검색조건을 정의한다.

```java
@Repository
public interface ProductRepository extends CrudRepository<Product, String> {
    Optional<List<Product>> findByProductName(String productName);
}
```

아래는 ProductRepository를 사용하여 상품을 등록 또는 검색하는 코드 이다.  
상품의 갱신 또한 등록과 비슷하다.
```java
    // 상품 Entity를 생성
    Product product = Product.builder()
        .id(productRequest.getId())
        .productName(productRequest.getProductName())
        .price(productRequest.getPrice())
        .quantity(productRequest.getQuantity()).build();
    // 상품을 Redis로 등록
    productRepository.save(product);

    // 상품명으로 검색
    Optional<List<Product>> productList = productRepository.findByProductName(productName);
```
상품의 등록 / 수정 / 검색을 ProductRepository를 사용하여 구현했을 경우 Redis에 발행된 명령어는 아래와 같다.

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

Redis (RedisTemplate 사용시)

DataRedis와 달리 redisTemplate 를 사용할 경우 모든 내용을 구현해 줘야 한다.  
주요한 내용으로는 상품명을 검색조건으로 지정하기 때문에 smember(index) 를 사용하며  
key : 상품명 , 상품코드 : value로 값을 저장한다.  
물론 값이 추가 되거나 갱신될 경우 smembers도 함께 처리를 해줘야 한다.

아래는 상품정보가 갱신될 때 smember의 내용을 갱신하는 내용  
삭제 후 다시 추가
```java
      // 기존의 인덱스는 삭제한다.
      redisIndexTemplate.opsForSet().remove("PRODUCT_NAME:" + result.getProductName(), productRequest.getId());
      redisIndexTemplate.opsForSet().remove("PRODUCT_ID:" + productRequest.getId(), productRequest.getProductName());

      // 상품명으로 ID를 찾는 인덱스 추가
      redisIndexTemplate.opsForSet().add("PRODUCT_NAME:" + productRequest.getProductName(), productRequest.getId());
      // ID로 를 찾는 인덱스 추가
      redisIndexTemplate.opsForSet().add("PRODUCT_ID:" + productRequest.getId(), productRequest.getProductName());

```

아래는 smember를 기준으로 상품값을 가져오는 케이스
```java
        // smember 에서 인덱싱된 상품명을 검색한다.
        Set<String> resultList = redisIndexTemplate.opsForSet().members("PRODUCT_NAME:" + productName);

        // 추출된 키로 실 데이타를 검색한다.
        for (String key : resultList.stream().toList()) {
            log.debug("인덱스 검색된 키 : {}", key);
            returnList.add(redisTemplate.opsForValue().get(key));
        }

```

같은 요건을 redisTemplate로 구현했을 경우 Redis에 발행되는 명령어를 확인해 보면 아래와 같다.
상품을 등록하고 , 수정하고 , 검색하는 api를 호출한 경우 Redis에 발행된 명령어

```javascript
-- [상품을 등록]
1699434149.692686 [0 127.0.0.1:63249] "SET" "b00002" "{\"id\":\"b00002\",\"product_name\":\"\xec\x88\x9c\xeb\xac\xb4\xec\x9e\xa5\xea\xb0\x91\",\"quantity\":3,\"price\":1000}"
-- 상품명을 인덱스(smember)에 등록
1699434149.694739 [0 127.0.0.1:63249] "SADD" "PRODUCT_NAME:\xec\x88\x9c\xeb\xac\xb4\xec\x9e\xa5\xea\xb0\x91" "b00002"
1699434149.695520 [0 127.0.0.1:63249] "SADD" "PRODUCT_ID:b00002" "\xec\x88\x9c\xeb\xac\xb4\xec\x9e\xa5\xea\xb0\x91"

-- [상품 갱신]
-- 키정보로 Redis에서 값을 취한다.
1699434660.458428 [0 127.0.0.1:63249] "GET" "b00002"
-- 수정한 값을 저장한다.
1699434660.461310 [0 127.0.0.1:63249] "SET" "b00002" "{\"id\":\"b00002\",\"product_name\":\"\xeb\xb9\xb5\xea\xb0\x95\xec\x88\x9c\xeb\xac\xb4\xec\x9e\xa5\xea\xb0\x91\",\"quantity\":3,\"price\":1000}"
-- 상품명 인덱스와 상품아이디 인덱스를 삭제한다.
1699434660.462470 [0 127.0.0.1:63249] "SREM" "PRODUCT_NAME:\xec\x88\x9c\xeb\xac\xb4\xec\x9e\xa5\xea\xb0\x91" "b00002"
1699434660.463148 [0 127.0.0.1:63249] "SREM" "PRODUCT_ID:b00002" "\xeb\xb9\xb5\xea\xb0\x95\xec\x88\x9c\xeb\xac\xb4\xec\x9e\xa5\xea\xb0\x91"
-- 상품명 인덱스와 상품아이디 인덱스를 추가한다.
1699434660.463759 [0 127.0.0.1:63249] "SADD" "PRODUCT_NAME:\xeb\xb9\xb5\xea\xb0\x95\xec\x88\x9c\xeb\xac\xb4\xec\x9e\xa5\xea\xb0\x91" "b00002"
1699434660.464312 [0 127.0.0.1:63249] "SADD" "PRODUCT_ID:b00002" "\xeb\xb9\xb5\xea\xb0\x95\xec\x88\x9c\xeb\xac\xb4\xec\x9e\xa5\xea\xb0\x91"

-- [상품 검색]
-- 인덱스에서 상품명을 취득한다 (set)
1699434817.290931 [0 127.0.0.1:63249] "SMEMBERS" "PRODUCT_NAME:\xec\x88\x9c\xeb\xac\xb4\xec\x9e\xa5\xea\xb0\x91"
-- 취득한 키만큼 값을 취한다.
1699434817.293564 [0 127.0.0.1:63249] "GET" "b00001"
```

Redis의 2가지 구현 방법을 아래와 같은 테스트 케이스를 만들어 비교해 보았다.
```java
    String[] inputData = {"순무양말", "순무덧신", "순무신발", "순무마스카라", "순무립스틱", "순무잠바", "순무스카프"};
    RestTemplate restTemplate = new RestTemplate();

    @Test
    @DisplayName("RedisDataJPA를 사용하여 상품등록")
    public void productSaveV1_test() {
        // 10000등록 2분36초 소요
        int LOOP_COUNT = 10000;
        String url = "http://localhost:8080/v1/orders/addProduct";
        Random random = new Random();

        // 100개의 데이터 생성
        for (int i = 0; i < LOOP_COUNT; i++) {
            // 랜덤으로 한 개의 데이터 선택
            int index = random.nextInt(inputData.length);

            ProductRequest productRequest = new ProductRequest();
            productRequest.setId("a" + String.format("%04d", i));
            productRequest.setProductName(inputData[index]);
            productRequest.setPrice(1200);
            productRequest.setQuantity(3);
            HttpEntity<ProductRequest> httpEntity = new HttpEntity<>(productRequest);
            ResponseEntity<String> response = restTemplate.exchange(
                    url,
                    HttpMethod.POST,
                    httpEntity,
                    String.class
            );
            // 만들어진 데이타 표시
            System.out.println(response);
        }
    }

```
테트스 케이스 수행 시간

- 데이타의 등록

| 수량        | DataRedis (V1) | RedisTemplate(V2) | 
|-----------|----------------|-------------------|
| 수행시간 | 2분 36초       | 2분 38초          |

- 등록된 1만건의 데이타 기준 검색 시간

| Request수 | DataRedis (V1) | RedisTemplate(V2) |
|-----------|----------------|-------------------|
| 100       | 1031ms         | 1029ms            |
| 1000      | 11670ms        | 11390ms           |
| 10000     | 122000ms       | 124000ms          |

결과는 테스트 케이스 실행 시간 및 `actuator` - `prometheus` 마이크로 미터로 확인 이 가능하다.  
![](https://lh3.googleusercontent.com/u/0/drive-viewer/AK7aPaAGhon3VHHKlHxENclI9EKcoGRX0b80LWINX6RebSFqJat9nhdtDJKdNsSOeQmXGxysJIXxph5u0FAWEJfvrViA6pIXKg=w2149-h1019)

비지니스 메트릭은 성능 테스트와는 상관 없음  
서비스에 추가한 `@Counted` 애노테이션 영향으로 호출된 수 만큼 우상향 그래프가 그려졌다.  
![](https://lh3.googleusercontent.com/u/0/drive-viewer/AK7aPaCBUuh1Pn_ebVuFZ3o9iIxFkzehBcK0M5UX3Lvu1lu3e9vva3wGJ5-uI4bXIE0nGa8FJy149UP4otCQTmvdH1HLBVDiwQ=w1709-h1019)

지금에 와서 생각해 보니 `@Counted` 메트릭 대신 `@Timed` 메트릭을 적용했다면 V1,V2의 성능 검증을 prometheus에서 조금 더
수월하게 진행할 수 있었을 듯 하다.  
cpu 사용률과 jvm의 상태등을 확인해 보았으나 만건의 데이타로는 수치가 미미하여 더 확대해서 테스트를 진행해 보아야 할듯 하다.
  
결론적으로 `DataRedis`와 `RedisTemplate` 의 사용에 있어 큰 차이가 없었다.  
DataRedis에서 발행되는 Redis 명령어를 보면 일반적으로 RedisTemplate에서 구현하는 것보다는 범용화된  
(최적화된?) 명령어를 사용하는 듯 하다.  
DataRedis를 사용할 경우 본인의 의지와 상관없는 불필요한 데이타가 쌓이며 일반적인 JPA 키워드 사용에 제한이 있다.  
Redis는 RDB가 아니기에 당연한 내용이지만 코딩시 실수의 여지가 있다.  
아래는 DataRedis에서 사용할 수 없는 타입을 정의한 것이다. ( 대다수 사용불가 )  
```java
	public static enum Type {
		BETWEEN(2, "IsBetween", "Between"), IS_NOT_NULL(0, "IsNotNull", "NotNull"), 
                IS_NULL(0, "IsNull", "Null"), LESS_THAN("IsLessThan", "LessThan"), 
                LESS_THAN_EQUAL("IsLessThanEqual", "LessThanEqual"), 
                GREATER_THAN("IsGreaterThan","GreaterThan"), 
                GREATER_THAN_EQUAL("IsGreaterThanEqual", "GreaterThanEqual"), 
                BEFORE("IsBefore","Before"), AFTER("IsAfter", "After"), NOT_LIKE("IsNotLike", "NotLike"), 
                LIKE("IsLike","Like"), STARTING_WITH("IsStartingWith", "StartingWith", "StartsWith"), 
                ENDING_WITH("IsEndingWith", "EndingWith", "EndsWith"), 
                IS_NOT_EMPTY(0, "IsNotEmpty", "NotEmpty"), 
                IS_EMPTY(0, "IsEmpty","Empty"), 
                NOT_CONTAINING("IsNotContaining", "NotContaining", "NotContains"), 
                CONTAINING("IsContaining", "Containing", "Contains"), NOT_IN("IsNotIn", "NotIn"), 
                IN("IsIn","In"), NEAR("IsNear", "Near"), WITHIN("IsWithin", "Within"), 
                REGEX("MatchesRegex","Matches", "Regex"), EXISTS(0, "Exists"), TRUE(0, "IsTrue", "True"), 
                FALSE(0,"IsFalse", "False"), 
                NEGATING_SIMPLE_PROPERTY("IsNot","Not"), SIMPLE_PROPERTY("Is", "Equals");
```
다만  smembers(index)를 사용하는 경우 개발자가 실수할 수 있는 부분을 커버해 주는 부분은 마음에 든다.
그래서 나는 사용에 제한이 있는 DataRedis보다는 개발에 유연함이 있는 redisTemplate를 사용할 것 같다.  
이 부분은 개인취향 일수도 있겠지만 ...

#### 마치며
비즈니스 메트릭 구성에 있어 actuator 활용은 꽤 메리트가 있다고 생각한다.  
prometheus에 쌓여있는 데이타를 어떻게 추출하며 (PromQL) 이를 그라파나등에 어떻게 시각화 시키느냐는 또 하나의 과제 이다.  
actuator 를 활용 한다면 부하 검증 등에도 상당히 도움이 될수 있다는 부분을 Redis 검증을 통해 알아보았다.  
