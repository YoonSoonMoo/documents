# 트랜잭션 처리의 고찰 [ Transaction with External Api Call ]
![](https://img.shields.io/badge/Java-1.8%20version-brightgreen) ![](https://img.shields.io/badge/Section-Transation-orange)

## 외부 API 호출을 포함한 트랜잭션 처리
아래의 sequence diagram을 보면 외부API 호출 이후 문제가 발생했을 경우 [출금처리,포인트처리] 까지 rollback 되게 됩니다.  
하지만 API로 이미 출금 신청이 진행되었기 때문에 실제 출금은 이루어 졌으나 DB 상으로는 차감이 되지 않는 문제가 발생하게 됩니다. 

[![](https://mermaid.ink/img/pako:eNplksFOwkAQhl9lslfhBXowkeDBm9F462VpB22k27psD4aQkEiM0RgPxqgJmHrAeMAEAZGDT9Qu7-As1IJw29n_m_lndrbBnMBFZrE6nkUoHCx7_Fhy3xYAPFKBiPwKynnkqEDCUX0RhVwqz_FCLhTs7O-tX5VL6zf6eZpOWhlqqhS3t7coBAtmd33dnc6up6AnnWR6Bfom1sMPA5ZLUCQQNsDhU_rWh_R2oLvj2UUL9Pd7et-FzRzdiXVMevyQjsYZZihSCSLWylwXJQu5xyJeovkAy4y8zaW26pwN0_5MRl-gH_uEwAGqSAqTU5HITyGUQaWGfmYDJnulJ6Dp0rhH3bf0S89AKNxV9p-TCJSXi6QW5-9MejIcJKMfqtLWl6-swHyUPvdc2nrD8DZTJ-ijzSw6uljlUU3ZzBZNQqPQ5Qp3XY-Wz6wqr9WxwMzHODwXDrOUjPAPyn5ORjV_Ab7nAiU)](https://mermaid.live/edit#pako:eNplksFOwkAQhl9lslfhBXowkeDBm9F462VpB22k27psD4aQkEiM0RgPxqgJmHrAeMAEAZGDT9Qu7-As1IJw29n_m_lndrbBnMBFZrE6nkUoHCx7_Fhy3xYAPFKBiPwKynnkqEDCUX0RhVwqz_FCLhTs7O-tX5VL6zf6eZpOWhlqqhS3t7coBAtmd33dnc6up6AnnWR6Bfom1sMPA5ZLUCQQNsDhU_rWh_R2oLvj2UUL9Pd7et-FzRzdiXVMevyQjsYZZihSCSLWylwXJQu5xyJeovkAy4y8zaW26pwN0_5MRl-gH_uEwAGqSAqTU5HITyGUQaWGfmYDJnulJ6Dp0rhH3bf0S89AKNxV9p-TCJSXi6QW5-9MejIcJKMfqtLWl6-swHyUPvdc2nrD8DZTJ-ijzSw6uljlUU3ZzBZNQqPQ5Qp3XY-Wz6wqr9WxwMzHODwXDrOUjPAPyn5ORjV_Ab7nAiU)

API를 통한 출금 성공 여부를 이력에 저장해야 하기 때문에 그렇다고 출금 이력 저장을 출금신청 API 호출전으로 이동할 수도 없습니다.  
위의 상황을 해결하기 위한 최선의 방법은 무엇인지 고민을 해봐야 합니다.  

1. 출금 신청을 취소하는 별도의 API를 제공하는지 확인
```html
만일 외부 API에서 출금취소에 관련한 API 를 지원한다면 롤백시 출금 취소API를 호출한다.
다만 이 경우라도 출금 시도를 한 이력은 유효하므로 이력 저장은 별도로 진행을 해주는 것이 좋을 듯 하다.
```

2. 피해 최소화 선택
```html
출금에 관련한 취소 API를 제공하지 않는다면 이미 배는 떠나 버렸기 때문에 서비스 피해 최소화를 
위한 작업을 진행하는 것이 차선책이다.
출금 이력 저장에 문제가 발생했다면 해당 처리는 본 transaction에서 제외하고 처리하고 후속 처리를
진행하도록 한다.
이 경우 출금 이력은 DB와 sync가 맞지 않는 장애가 발생한다.
하지만 모든 처리가 rollback 되었다면 어떤 문제가 발생할지 생각해 보자.
출금요청을 했으므로 기존의 계좌에서는 출금 요청한 금액만큼 차감해서 DB에 저장을 했어야 하는데
rollback되면 출금 요청을 한적이 없게 된다. 
외부 출금 API는 처리가 되었으니 서비스 상 무한출금이 가능한 형태가 된다.
서비스 운영상 어떤것이 더 큰 문제인가?
```

코드상으로 살펴보면 transaction이 출금처리에 묶여 있어서 어떤 처리에서든 Exception이 발생하면 rollback이 발생한다.
```java
@Transactional(rollbackFor=Exception.class)
public void 출금처리(출금신청Vo){
    
    Object pointData = db.getPointData(출금신청Vo.getUserId());
    Object userData = db.getUserData(출금신청Vo.getUserId());
    String result = "fail"; 
    
    if(pointData == "no problem" && userData == "no problem"){
        // 여기서 포인트 차감과 출금처리가 DB에 저장된다.
        if(db.포인트차감_출금처리(pointData,userData)){
            출금신청APIVo = makeData(pointData,userData);
            result = http.send(출금신청APIVo);
        }
    }
    
    // 출금 결과가 성공이 아니면 출금실패 Exception throw -> rollback처리
    if(result = fail) throw Exception
    
    db.saveHistory(result);
    noti.send(result);
}
```
위의 로직에서라면 noti.send(result) 항목에서 Exception이 발생한다고 해도 모든 처리가 rollback 처리된다.
