### 웹클라이언트 사용 ( non-blocking )
http://localhost:8080/webclient
결과
```logcatfilter
2022-11-02 10:13:24.516  INFO 26424 --- [nio-8080-exec-5] k.p.y.w.w.s.WebClientCompareService      : ---- webClientConnect start!
2022-11-02 10:13:24.518  INFO 26424 --- [nio-8080-exec-5] k.p.y.w.w.s.WebClientCompareService      : ---- webClientConnect over!! 
2022-11-02 10:13:27.531  INFO 26424 --- [ctor-http-nio-3] k.p.y.w.w.s.WebClientCompareService      : Total result(3Sec): 3013
```
### 내부에서 3초 소요되는 api 를 3번 호출했을때

```logcatfilter
2022-11-02 17:28:21.769  INFO 29328 --- [nio-8080-exec-1] k.p.y.w.w.s.WebClientCompareService      : ---- webClientConnect start!
2022-11-02 17:28:21.774  INFO 29328 --- [nio-8080-exec-1] k.p.y.w.w.s.WebClientCompareService      : ---- webClientConnect over!! 
2022-11-02 17:28:21.774  INFO 29328 --- [nio-8080-exec-1] k.p.y.w.w.s.WebClientCompareService      : ---- webClientConnect start!
2022-11-02 17:28:21.775  INFO 29328 --- [nio-8080-exec-1] k.p.y.w.w.s.WebClientCompareService      : ---- webClientConnect over!! 
2022-11-02 17:28:21.776  INFO 29328 --- [nio-8080-exec-1] k.p.y.w.w.s.WebClientCompareService      : ---- webClientConnect start!
2022-11-02 17:28:21.777  INFO 29328 --- [nio-8080-exec-1] k.p.y.w.w.s.WebClientCompareService      : ---- webClientConnect over!! 
2022-11-02 17:28:24.792  INFO 29328 --- [ctor-http-nio-5] k.p.y.w.w.s.WebClientCompareService      : Total result(3Sec): 3022
2022-11-02 17:28:24.792  INFO 29328 --- [ctor-http-nio-6] k.p.y.w.w.s.WebClientCompareService      : Total result(3Sec): 3018
2022-11-02 17:28:24.806  INFO 29328 --- [ctor-http-nio-7] k.p.y.w.w.s.WebClientCompareService      : Total result(3Sec): 3030
```

### 레스트템플릿 ( blocking)
http://localhost:8080/resttemplate
결과
```logcatfilter
2022-11-02 10:10:04.810  INFO 18112 --- [nio-8080-exec-1] k.p.y.w.w.s.WebClientCompareService      : ---- restTemplateConnect start!
2022-11-02 10:10:07.869  INFO 18112 --- [nio-8080-exec-1] k.p.y.w.w.s.WebClientCompareService      : Total result(3Sec): 3057
2022-11-02 10:10:07.870  INFO 18112 --- [nio-8080-exec-1] k.p.y.w.w.s.WebClientCompareService      : ---- restTemplateConnect over!
```

### 내부에서 3초 소요되는 api 를 3번 호출했을때  
```logcatfilter
2022-11-02 17:26:04.090  INFO 29328 --- [nio-8080-exec-6] k.p.y.w.w.s.WebClientCompareService      : ---- restTemplateConnect start!
2022-11-02 17:26:07.109  INFO 29328 --- [nio-8080-exec-6] k.p.y.w.w.s.WebClientCompareService      : Total result(3Sec): 3017
2022-11-02 17:26:07.109  INFO 29328 --- [nio-8080-exec-6] k.p.y.w.w.s.WebClientCompareService      : ---- restTemplateConnect over!
2022-11-02 17:26:07.109  INFO 29328 --- [nio-8080-exec-6] k.p.y.w.w.s.WebClientCompareService      : ---- restTemplateConnect start!
2022-11-02 17:26:10.128  INFO 29328 --- [nio-8080-exec-6] k.p.y.w.w.s.WebClientCompareService      : Total result(3Sec): 3019
2022-11-02 17:26:10.128  INFO 29328 --- [nio-8080-exec-6] k.p.y.w.w.s.WebClientCompareService      : ---- restTemplateConnect over!
2022-11-02 17:26:10.128  INFO 29328 --- [nio-8080-exec-6] k.p.y.w.w.s.WebClientCompareService      : ---- restTemplateConnect start!
2022-11-02 17:26:13.146  INFO 29328 --- [nio-8080-exec-6] k.p.y.w.w.s.WebClientCompareService      : Total result(3Sec): 3016
2022-11-02 17:26:13.146  INFO 29328 --- [nio-8080-exec-6] k.p.y.w.w.s.WebClientCompareService      : ---- restTemplateConnect over!
```

