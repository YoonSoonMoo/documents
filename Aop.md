# Aspect Oriented Programming
![](https://img.shields.io/badge/Java-1.8%20version-brightgreen) ![](https://img.shields.io/badge/Spring-AOP-orange)

#### 이력 관리
서비스의 주요 업무의 경우 이력 관리가 중요합니다.  
예를 들어 유저 등록/수정의 변경사항 , 주문변경 사항 , 처리내역(로그성) 저장 등 대다수의 서비스에 `HISTORY` key-word 의 테이블이 데이타베이스에 존재하는 이유 입니다.  
얼마전 담당하고 있는 서비스에서 외부 연동 직후 그 이력을 저장하다 에러가 발생하여 모든 처리가 Rollback 되는 장애가 있었습니다.    
연동처에서는 정상 처리 - 내부 DB는 원복된 상태가 되어 데이타 동기화에 문제가 발생된 것입니다.       
이번 일을 계기로 서비스 이력 관리에 대해 개인적으로 Role을 만들어 보았습니다.

- 이력관리가 메인 처리보다 중요할 수는 없습니다. ( 중요하지 않다는 의미가 아닌 메인 처리에 영향이 없어야 한다는 의미 )
- 외부 연동 결과에 대한 이력 저장은 가급적 Transaction 을 분리하는 것이 좋습니다.
- 어떠한 값들이 어떻게 바뀌었는지 상세를 확인할 수 있어야 합니다.
- 변경이 잦은 처리는 이력 관리를 공통화 하여 부가적인 공수를 줄여야 합니다.
- 이력을 추가하거나 제외하는 처리는 유연하게 처리할 수 있어야 합니다.

위의 조건을 만족하는 적절한 기술을 검토한 결과 AOP 기술이 적합하다는 생각을 하게 되었습니다.

#### AOP 기술의 주요 키워드
AOP 개념 이해는 소스를 통해서 진행해 볼까 합니다만 최소한 키워드 정도는 이해하고 있어야 하기때문에  
간단히 설명을 하고 진행 합니다.

- 타겟(Target)  
>부가기능을 부여할 대상을 의미합니다.
보통은 Service 클래스가 또는 Controller 이 대상이 된다.

- 애스펙트(Aspect)  
>OOP의 클래스와 마찬가지로 애스펙트는 AOP의 기본 모듈입니다.
애스펙트는 부가기능을 정의한 어드바이스와 어드바이스를 어디에 적용할지 결정하는 포인트컷의 조합으로 구성됩니다.
보통 싱글톤 형태의 오브젝트로 존재합니다.

- 어드바이스(Advice)  
>실질적으로 부가기능을 담은 구현체를 의미합니다.
어드바이스의 경우 타겟 오브젝트에 종속되지 않기 때문에 순수하게 부가기능에만 집중할 수 있습니다.

- 조인 포인트(Join Point)  
>어드바이스가 적용될 수 있는 위치를 의미합니다.
스프링 AOP에서는 메소드 조인포인트만 제공하고 있습니다.
따라서, 스프링 프레임워크 내에서의 조인포인트는 메소드를 가리킨다고 생각하셔도 무방합니다.

- 포인트컷(PointCut)  
>부가기능이 적용될 대상(메소드)를 선정하는 방법을 의미합니다.
스프링 AOP의 조인 포인트는 메소드의 실행이므로 스프링의 포인트컷은 메소드를 선정하는 기능을 갖고 있습니다.
따라서 포인트컷의 표현식은 메소드의 실행이라는 의미인 execution으로 시작하고, 메소드의 시그니처를 비교하는 방법을 주로 이용합니다.  
@Before  
타겟 메소드가 실행하기 이전 어드바이스 기능을 수행  
@After  
타겟 메도스의 결과에 상관없이 실행 후 어드바이스 기능을 수행  
@AfterReturning  
타겟 메소드가 정상적으로 결과값을 반환 후 어드바이스 기능을 수행  
@AfterThrowing  
타겟 메소드가 수행 중 예외를 발생하면 어드바이스 기능을 수행  
@Around  
어드바이스가 타겟 메소드를 감싸서 타겟 메소드 호출전, 후 어드바이스 기능을 수행  
Around는 타겟을 실행할 지 혹은 바로 반환할지도 정할 수 있음  

- 프록시(Proxy)  
>클라이언트와 타겟 사이에 투명하게 존재하여 부가기능을 제공하는 오브젝트입니다.
DI를 통해 타겟 대신 클라이언트에게 주입되며, 클라이언트의 메소드 호출을 대신 받아 타겟에 위임해주며 부가기능을 부여합니다.

#### 무엇을 만들것인지 
유저를 생성하고 수정할 경우 일반적으로 이력을 남기게 됩니다.  
유저를 생성/수정하는 것이 메인 태스크로 이력 처리 트랜잭션을 별도로 분리합니다.    
최초 유저 생성 타이밍에도 이력을 ( 신규등록 )  생성된 시간 포함해서 기록 합니다.  
유저 정보를 수정했을 경우 해당 내용 before , after 값을 기록 합니다.  
Target 은 Service Class 로 하며 JoinPoint는 유저 생성과 유저 수정을 대상으로 지정 합니다.    
PointCut은 Annotaion 기반으로 작성하여 대상 Method에 추가/삭제 편의성을 고려 했습니다.  

- Sequence Diagram

[![](https://mermaid.ink/img/pako:eNqFk0Fr2zAUx7-K0CkF10R2Yic-lKR0h-2y0u00cnEtJRHEUibLY1kI5L7LoBQ2aEp26Nihh64NdId9otj7DpMcu3WcwA4G673fk_7_p6cpDDgm0IMReR8TFpAT6g-EH_YYAH4sOYvDcyKyVSC5AOvVPPlxm15_0aGxLyQN6NhnEgScScFHow1czkREfKABqYa7r0-roVihZ2TMI6qOmlSzQxrp8DbwpAccHh2VRAAPpFfLdDlPLn4l3xcaLSU1m8vS4Ofl-vEq58FzgSCBBGJw7tcs5BhWo25YtmUA0z3QWaW_so-W38UY1DpdwWOGt7Btb4o-0w2PpKkTL7FGK8ihLtPlHjg5LtSly8vkYQXSx5_JRSaScUmAoIOhBLyf451j0uci6zhhuGLFtg0L_c_KWPCAEEzZ4BWn7FR90sxjtQOgawp2r7etVqb3X9UV7Rezp6_ljTd21vd364c_4O-3y3Txu6x3dyT02dlxeY0UMQEGSBerZHmjmjdPr292u7ajv9Pty80g77bmLQ3VNTO1gy8pZ6CTdSeIZclh2cPWTBYjspG3Zyqf57lwkrPQgOrc0KdYPdapruxBOSQh6UFP_WLS9-OR7MEemyk0HmNfkhdYG4Je3x9FxID6Pb-ZsAB6ui0FlD_4J0q9tXechwWkltCbwo_QayHTtdpuy2k7VqveVFcGJ9Cz7JaJmnWEkIPqDkL2zICfsvq66bYbtuUgy2m6zYY7-wc8qKmt?type=png)](https://mermaid.live/edit#pako:eNqFk0Fr2zAUx7-K0CkF10R2Yic-lKR0h-2y0u00cnEtJRHEUibLY1kI5L7LoBQ2aEp26Nihh64NdId9otj7DpMcu3WcwA4G673fk_7_p6cpDDgm0IMReR8TFpAT6g-EH_YYAH4sOYvDcyKyVSC5AOvVPPlxm15_0aGxLyQN6NhnEgScScFHow1czkREfKABqYa7r0-roVihZ2TMI6qOmlSzQxrp8DbwpAccHh2VRAAPpFfLdDlPLn4l3xcaLSU1m8vS4Ofl-vEq58FzgSCBBGJw7tcs5BhWo25YtmUA0z3QWaW_so-W38UY1DpdwWOGt7Btb4o-0w2PpKkTL7FGK8ihLtPlHjg5LtSly8vkYQXSx5_JRSaScUmAoIOhBLyf451j0uci6zhhuGLFtg0L_c_KWPCAEEzZ4BWn7FR90sxjtQOgawp2r7etVqb3X9UV7Rezp6_ljTd21vd364c_4O-3y3Txu6x3dyT02dlxeY0UMQEGSBerZHmjmjdPr292u7ajv9Pty80g77bmLQ3VNTO1gy8pZ6CTdSeIZclh2cPWTBYjspG3Zyqf57lwkrPQgOrc0KdYPdapruxBOSQh6UFP_WLS9-OR7MEemyk0HmNfkhdYG4Je3x9FxID6Pb-ZsAB6ui0FlD_4J0q9tXechwWkltCbwo_QayHTtdpuy2k7VqveVFcGJ9Cz7JaJmnWEkIPqDkL2zICfsvq66bYbtuUgy2m6zYY7-wc8qKmt)

#### 이렇게 구현을 해 봅니다.
코드는 급조한 내용으로 억지스러운 부분이 있습니다만 이력관리에 대해 AOP를 어떤 방향으로 구현했는지를 확인해 주세요. 

- 주요 로직
> Controller : 유저 정보를 추가 한다.

```java
    @ResponseBody
    @RequestMapping(value = "/adduser", method = {RequestMethod.POST})
    public String addUser(@RequestBody UserDao userDao) {

        if (userService.getUserInfoByUserId(userDao.getUserId()) != null) {
            return "이미 있는 유저 입니다.";
        }
        userService.addUser(userDao);
        return "유저가 추가되었습니다 " + userDao;
    }
```
> Service : 신규 유저를 메모리 DB에 저장한다.
```java
    final HistoryRepository historyRepository;
    final UserRepository userRepository;

    @UserHistoryAnnotation
    //  @TimerAnnotation -> point cut으로 설정
    public boolean addUser(UserDao userDao) {
        userRepository.insertData(userDao);
        log.info("유저를 추가했습니다. : {}", userDao);
        return true;
    }
```
> Repository : 유저정보를 관리 ( Memory DB )
```java

   private List<UserDao> userDb = new LinkedList<>();

   public List<UserDao> findHistoryByUserName(String userName) {
           return userDb.stream().filter(data -> (data.getUserName().startsWith(userName))).collect(Collectors.toList());
   }
   public List<UserDao> getAlldata() {
           return userDb;
   }
   public void insertData(UserDao userDao) {
           userDb.add(userDao);
   }
```
> Aspect : AOP 로직
```java
    @Around("@annotation(kr.pe.yoonsm.history.aop.Aspect.UserHistoryAnnotation)") // --> 포인트컷(PointCut) 애노테이션 지정
    public Object addHistory(ProceedingJoinPoint proceedingJoinPoint) throws Throwable { // --> 어드바이스(Advice)

        // Request 에서 전달받은 원본값
        UserDao userDao = (UserDao) Arrays.stream(proceedingJoinPoint.getArgs())
                .sequential()
                .filter(data -> (data instanceof UserDao)).findFirst().orElse(null);
        UserDao userDaoDb = null;

        // 커밋되기 전의 값을 미리 세팅 해야 한다. ( deep copy ) Memory repository 이기 때문에...
        //UserDao userDaoDb = userRepository.findByUserId(userDao.getUserId());
        if (userDao != null) {
            userDaoDb = objectMapper.treeToValue(objectMapper.valueToTree(userRepository.findByUserId(userDao.getUserId())), UserDao.class);
            log.info("Parameter first Db values {}", userDaoDb);
        }

        // Main 처리  JoinPoint 기준 위 / 아래 양쪽에 구현되어 있다.
        Object ret = proceedingJoinPoint.proceed();

        log.info("main process complete!!");

        // insert / update 가 성공일 경우 history를 저장한다.
        if (ret instanceof Boolean && userDao != null) {
            if (userDao != null && ((Boolean) ret).booleanValue()) {
                HistoryDao historyDao = new HistoryDao();
                historyDao.setSeq(historyRepository.getAllData().size());
                historyDao.setLocalDateTime(LocalDateTime.now());
                log.info("Parameter values {}", userDao);
                // DB에 데이타가 존재하므로 update 처리
                if (userDaoDb != null) {
                    String changedString = commonService.diff(userDaoDb, userDao, UserDao.class);
                    if (changedString.length() > 0) {
                        historyDao.setChangeData(changedString);
                    }
                } else {
                    historyDao.setChangeData(userDao.getUserId() + " 신규추가");
                }
                log.info("History Annotation Changed data : {}", historyDao.getChangeData());
                historyRepository.addHistory(historyDao);
            }
        }
        return ret;
    }

```

> Common : 두 객체간의 변경된 값 비교
```java
    public <T> String diff(T target1, T target2, Class<T> targetClass) {
        StringBuilder sb = new StringBuilder();
        try {
            for (PropertyDescriptor pd : Introspector.getBeanInfo(targetClass, Object.class).getPropertyDescriptors()) {
                Object value1 = pd.getReadMethod().invoke(target1);
                Object value2 = pd.getReadMethod().invoke(target2);

                boolean isEqualValue = (value1 == value2) || (value1 != null && value1.equals(value2));
                // 같지 않은 값이 있다면
                if (!isEqualValue) {
                    sb.append(pd.getName()).append(" changed ").append(value1)
                            .append("->").append(value2)
                            .append(" ");
                }
            }
        } catch (IntrospectionException | InvocationTargetException | IllegalAccessException e) {
            throw new RuntimeException(e);
        }
        return sb.toString();
    }
```
#### 로그출력
신규유저를 등록하고 이후 이름을 변경한 로그 입니다.

```css
2022-10-22 16:46:13.370  UserRepository : UserDB count : 0
2022-10-22 16:46:13.419  HistoryAspect  : Parameter first Db values null
2022-10-22 16:46:13.433  UserService    : 유저를 추가했습니다. | UserService.addUser : UserDao(userId=yoonsm, userName=윤순무, age=48, address1=강동구, address2=둔촌2동 98)
2022-10-22 16:46:13.439  HistoryAspect  : >> time  annotation : 20
2022-10-22 16:46:13.439  HistoryAspect  : main process complete!!
2022-10-22 16:46:13.443  HistoryAspect  : Parameter values UserDao(userId=yoonsm, userName=윤순무, age=48, address1=강동구, address2=둔촌2동 98)
2022-10-22 16:46:13.443  HistoryAspect  : History Annotation Changed data : yoonsm 신규추가
						 
2022-10-22 16:46:13.444  UserRepository : UserDB count : 1
2022-10-22 16:46:13.464  HistoryAspect  : Parameter first Db values UserDao(userId=yoonsm, userName=윤순무, age=48, address1=강동구, address2=둔촌2동 98)
2022-10-22 16:46:13.465  UserRepository : UserDB count : 1
2022-10-22 16:46:13.465  UserService    : 유저를 수정했습니다. | UserService.editUser : UserDao(userId=yoonsm, userName=윤유림, age=48, address1=강동구, address2=둔촌2동 98)
2022-10-22 16:46:13.466  HistoryAspect  : >> time  annotation : 1
2022-10-22 16:46:13.466  HistoryAspect  : main process complete!!
2022-10-22 16:46:13.466  HistoryAspect  : Parameter values UserDao(userId=yoonsm, userName=윤유림, age=48, address1=강동구, address2=둔촌2동 98)
2022-10-22 16:46:13.467  HistoryAspect  : History Annotation Changed data : userName changed 윤순무->윤유림 
```