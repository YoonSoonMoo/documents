# History object compare each other from aop
![](https://img.shields.io/badge/Java-1.8%20version-brightgreen) ![](https://img.shields.io/badge/Spring-AOP-orange)

#### Spring의 주요 개념 AOP 를 이해해 봅니다.

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

- 프록시(Proxy)  
>클라이언트와 타겟 사이에 투명하게 존재하여 부가기능을 제공하는 오브젝트입니다.
DI를 통해 타겟 대신 클라이언트에게 주입되며, 클라이언트의 메소드 호출을 대신 받아 타겟에 위임해주며 부가기능을 부여합니다.

#### 일단 무엇을 만들지 생각해 봅니다.
유저를 생성하고 수정할 경우 일반적으로 이력을 남기게 됩니다.  
유저를 생성/수정하는 것이 main task로 이력 처리는 트랜잭션을 별도로 분리합니다.    
최초 유저 생성시에도 생성된 시간을 포함해서 기록하고자 합니다.  
유저 정보를 수정했을 경우 수정한 내용이 어떻게 변경되었는지 기록을 하고자 합니다.

- Sequence diagram

[![](https://mermaid.ink/img/pako:eNqFk0Fr2zAUx7-K0CkF10R2Yic-lKR0h-2y0u00cnEtJRHEUibLY1kI5L7LoBQ2aEp26Nihh64NdId9otj7DpMcu3WcwA4G673fk_7_p6cpDDgm0IMReR8TFpAT6g-EH_YYAH4sOYvDcyKyVSC5AOvVPPlxm15_0aGxLyQN6NhnEgScScFHow1czkREfKABqYa7r0-roVihZ2TMI6qOmlSzQxrp8DbwpAccHh2VRAAPpFfLdDlPLn4l3xcaLSU1m8vS4Ofl-vEq58FzgSCBBGJw7tcs5BhWo25YtmUA0z3QWaW_so-W38UY1DpdwWOGt7Btb4o-0w2PpKkTL7FGK8ihLtPlHjg5LtSly8vkYQXSx5_JRSaScUmAoIOhBLyf451j0uci6zhhuGLFtg0L_c_KWPCAEEzZ4BWn7FR90sxjtQOgawp2r7etVqb3X9UV7Rezp6_ljTd21vd364c_4O-3y3Txu6x3dyT02dlxeY0UMQEGSBerZHmjmjdPr292u7ajv9Pty80g77bmLQ3VNTO1gy8pZ6CTdSeIZclh2cPWTBYjspG3Zyqf57lwkrPQgOrc0KdYPdapruxBOSQh6UFP_WLS9-OR7MEemyk0HmNfkhdYG4Je3x9FxID6Pb-ZsAB6ui0FlD_4J0q9tXechwWkltCbwo_QayHTtdpuy2k7VqveVFcGJ9Cz7JaJmnWEkIPqDkL2zICfsvq66bYbtuUgy2m6zYY7-wc8qKmt?type=png)](https://mermaid.live/edit#pako:eNqFk0Fr2zAUx7-K0CkF10R2Yic-lKR0h-2y0u00cnEtJRHEUibLY1kI5L7LoBQ2aEp26Nihh64NdId9otj7DpMcu3WcwA4G673fk_7_p6cpDDgm0IMReR8TFpAT6g-EH_YYAH4sOYvDcyKyVSC5AOvVPPlxm15_0aGxLyQN6NhnEgScScFHow1czkREfKABqYa7r0-roVihZ2TMI6qOmlSzQxrp8DbwpAccHh2VRAAPpFfLdDlPLn4l3xcaLSU1m8vS4Ofl-vEq58FzgSCBBGJw7tcs5BhWo25YtmUA0z3QWaW_so-W38UY1DpdwWOGt7Btb4o-0w2PpKkTL7FGK8ihLtPlHjg5LtSly8vkYQXSx5_JRSaScUmAoIOhBLyf451j0uci6zhhuGLFtg0L_c_KWPCAEEzZ4BWn7FR90sxjtQOgawp2r7etVqb3X9UV7Rezp6_ljTd21vd364c_4O-3y3Txu6x3dyT02dlxeY0UMQEGSBerZHmjmjdPr292u7ajv9Pty80g77bmLQ3VNTO1gy8pZ6CTdSeIZclh2cPWTBYjspG3Zyqf57lwkrPQgOrc0KdYPdapruxBOSQh6UFP_WLS9-OR7MEemyk0HmNfkhdYG4Je3x9FxID6Pb-ZsAB6ui0FlD_4J0q9tXechwWkltCbwo_QayHTtdpuy2k7VqveVFcGJ9Cz7JaJmnWEkIPqDkL2zICfsvq66bYbtuUgy2m6zYY7-wc8qKmt)

#### 이렇게 구현을 해 봅니다.

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
    @Around("@annotation(kr.pe.yoonsm.history.aop.Aspect.UserHistoryAnnotation)")
    public Object addHistory(ProceedingJoinPoint proceedingJoinPoint) throws Throwable {

        // Request 에서 전달받은 원본값
        UserDao userDao = (UserDao) Arrays.stream(proceedingJoinPoint.getArgs())
                .sequential()
                .filter(data -> (data instanceof UserDao)).findFirst().orElse(null);
        UserDao userDaoDb = null;

        // 커밋되기 전의 값을 미리 세팅 해야 한다. ( deep copy ) Memory repository 이기 때문에...
        //UserDao userDaoDb = userRepository.findByUserId(userDao.getUserId());
        if(userDao != null) {
            userDaoDb = objectMapper.treeToValue(
                    objectMapper.valueToTree(
                            userRepository.findByUserId(userDao.getUserId())), UserDao.class);
            log.info("Parameter first Db values {}", userDaoDb);
        }

        // Main 처리
        Object ret = proceedingJoinPoint.proceed();

        log.info("main process complete!!");

        // insert / update 가 성공일 경우 history를 저장한다.
        if (ret instanceof Boolean && userDao != null) {
            if (userDao != null && ((Boolean) ret).booleanValue()) {
                HistoryDao historyDao = new HistoryDao();
                log.info("Parameter values {}", userDao);
                if (userDaoDb != null) {
                    String changedString = commonService.diff(userDaoDb, userDao, UserDao.class);
                    if (changedString.length() > 0) {
                        historyDao.setSeq(historyRepository.getAllData().size());
                        historyDao.setChangeData(changedString);
                        historyDao.setLocalDateTime(LocalDateTime.now());
                        historyRepository.addHistory(historyDao);
                        log.info("History Annotation : {}", changedString);
                    }
                } else {
                    historyDao.setSeq(historyRepository.getAllData().size());
                    historyDao.setChangeData(userDao.getUserId() + " 신규추가");
                    historyDao.setLocalDateTime(LocalDateTime.now());
                    historyRepository.addHistory(historyDao);
                }
            }
        }

        return ret;
    }

```

> Common : 두 객체간의 비교
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