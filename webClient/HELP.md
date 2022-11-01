### 웹클라이언트 사용 ( non-blocking )
http://localhost:8080/webclient
결과
```logcatfilter
2022-11-01 14:41:48.243  INFO 12548 --- [nio-8080-exec-2] k.p.y.w.w.s.WebClientCompareService      : ---- webClientConnect start!
2022-11-01 14:41:51.828  INFO 12548 --- [nio-8080-exec-2] k.p.y.w.w.s.WebClientCompareService      : ---- webClientConnect over!!
2022-11-01 14:41:54.961  INFO 12548 --- [ctor-http-nio-2] k.p.y.w.w.s.WebClientCompareService      : resultFor3Sec: Process done!! : 3003 ms
2022-11-01 14:41:54.961  INFO 12548 --- [ctor-http-nio-2] k.p.y.w.w.s.WebClientCompareService      : Total result(3Sec): 6717
```


### 레스트템플릿 ( blocking)
http://localhost:8080/resttemplate
결과
```logcatfilter
2022-11-01 14:43:56.979  INFO 12548 --- [io-8080-exec-10] k.p.y.w.w.s.WebClientCompareService      : ---- restTemplateConnect start!
2022-11-01 14:43:59.995  INFO 12548 --- [io-8080-exec-10] k.p.y.w.w.s.WebClientCompareService      : Total result(3Sec): 3016
2022-11-01 14:43:59.996  INFO 12548 --- [io-8080-exec-10] k.p.y.w.w.s.WebClientCompareService      : ---- restTemplateConnect over!
```
