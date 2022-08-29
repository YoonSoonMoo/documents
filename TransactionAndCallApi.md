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
만일 외부 API에서 출금취소에 관련한 API 를 지원한다면 롤백시 출금 취소API를 호출합니다.
다만 이 경우라도 출금 시도를 한 이력은 유효하므로 이력 저장은 별도로 진행을 해주는 것이 좋을 듯 합니다.
```

2. 피해 최소화 선택
```html
출금에 관련한 취소 API를 제공하지 않는다면 API 를 통한 후속처리는 불가능 하므로 문제 발생 시 서비스 피해 최소화를 
위한 방법을 고민해 봐야 합니다.  
위의 Flow에서는 7번 출금 이력 저장에 문제가 발생했을 경우 하나의 transaction에 묶여 있는 4번 출금처리와 
포인트처리 까지 모두 rollback 처리가 됩니다.  
외부 출금 API는 처리가 되었으니 서비스 상 무한출금이 가능한 형태가 됩니다.  

API 호출 이후의 처리를 transaction에서 분리하는 방법을 생각해 봅니다.
이 경우 출금 이력은 DB와 sync가 맞지 않는 장애가 발생하게 됩니다.
단 출금처리,포인트 처리는 API 호출 이전으로 DB에 반영이 되어 있어 다중 출금은 막을 수 있습니다.

서비스 운영 상 어떤것이 더 큰 문제일까요?
```

코드상으로 살펴보면 transaction이 출금처리에 묶여 있어서 어떤 처리에서든 Exception 이 발생하면 rollback이 발생합니다.  
물론 API 호출 전에 발생한 Exception 으로 인한 rollback은 문제가 없습니다.
```java
@Transactional(rollbackFor=Exception.class)
public void 출금처리(출금신청Vo){
    
    Object pointData = db.getPointData(출금신청Vo.getUserId());
    Object userData = db.getUserData(출금신청Vo.getUserId());
    String result = "fail";
    출금신청APIVo apiVo = null;

    if(pointData == RESULT.NO_PROBLEM && userData == RESULT.NO_PROBLEM){
        // 여기서 포인트 차감과 출금처리가 DB에 저장된다.
        if(db.포인트차감_출금처리(pointData,userData)){
            apiVo = makeData(pointData,userData);
            result = http.send(출금신청APIVo);
        }
    }
    
    // 출금 결과가 성공이 아니면 출금실패 Exception throw -> rollback처리
    if(StringUtil.equal(result,RESULT.FAIL.getValue()) throw Exception
    
    db.saveHistory(apiVo);
    noti.send(apiVo);
}
```
그러면 피해 최소화를 위한 차선택 방법을 고민해 봅니다.  
transaction 에서 분리를 해야 하는 부분은 아래의 처리 입니다.  
>db.saveHistory(result);  
>noti.send(result);  

소소 코드에 반영을 해보면 아래와 같은 형태가 될 듯 합니다.

```java
@Transactional(rollbackFor=Exception.class, noRollbackFor = NoRollBackException.class)
public void 출금처리(출금신청Vo){
    
    Object pointData = db.getPointData(출금신청Vo.getUserId());
    Object userData = db.getUserData(출금신청Vo.getUserId());
    String result = "fail";
    출금신청APIVo apiVo = null;

        if(pointData == RESULT.NO_PROBLEM && userData == RESULT.NO_PROBLEM){
        // 여기서 포인트 차감과 출금처리가 DB에 저장된다.
        if(db.포인트차감_출금처리(pointData,userData)){
            apiVo = makeData(pointData,userData);
            result = http.send(출금신청APIVo);
        }
    }
    
    // 출금 결과가 성공이 아니면 출금실패 Exception throw -> rollback처리
    if(StringUtil.equal(result,RESULT.FAIL.getValue()) throw Exception
        
    try{
        db.saveHistory(apiVo);
        noti.send(apiVo);
    } catch (Exception ex){
        log.error("출금신청 이력/노티 처리 중 에러", ex.getMessage());
        // transaction에서 예외 처리할 별도의 Exception 으로 대체하고 
        // @Transactional 에 noRollback 으로 정의 해준다.
        throw new NoRollBackException(ex.getMessage());
    }
}
```

NoRollBackException가 발생 했다는 것은 출금 이력이 DB에 반영이 되지 않았거나 노티가 고객에게 발송되지 않았다는  
것을 의미합니다. 결국 완벽한 대응이라고는 말할 수는 없습니다.  
발생된 Exception 의 내용에 따라 recovery 가능 한 케이스도 또는 그렇지 못한 케이스도 존재 합니다.  
그래서 비슷한 내용으로 website 를 검색해 보면 명확한 답을 주지 않습니다.  
"적절한 대응이 필요하다" 라는 말로 끝을 내는 경우가 많습니다.  
그래서 저는 "서비스 피해 최소화" 라는 전제로 내용을 정리해 보았습니다. 
