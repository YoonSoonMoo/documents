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
유저를 생성/수정하는 것이 main task로 이력 처리는 트랜잭션을 별도로 분리하는 것이 좋을 듯 합니다.
최초 유저 생성시에도 생성된 시간을 포함해서 기록하고자 합니다.  
유저 정보를 수정했을 경우 수정한 내용이 어떻게 변경되었는지 기록을 하고자 합니다.

- Sequence diagram

[![](https://mermaid.ink/img/pako:eNqFk89r2zAUx_8VoVMKrrGdxE58KEnpDttlpdtp5KLaL4kgljJZHstCIPddBqXQQVOyQ8cOO2xtoDvsL4q9_2GSY7f5NXYwWO993tNXXz2NccBDwD6O4W0CLIATSnqCRB2GEEkkZ0l0DiJfBZILtFxM06_fs5tPOjQkQtKADgmTKOBMCj4YrOD1TAziHQ1gO9x-ebodShR6BkMeU7XVaDvbp7EObwKPetDh0dGaCOSj7Hqezafpxc_0y0yja0nNFrI0-HG-fLguePRUICCQSPTOScWxXcOpWYZTdQxkegc6q_Rv9dHy22GIKq224AkLN7DNsyn6TBseS1Mnnoca3UIOdZku99HJcakum1-m9wuUPXxLL3KRjEtAgvb6EvFugbeOocsF7Fc5FDwACCnrveCUnapPmkWscoB0DbBcTlmyV_2GWdndlbqE_1u23nGldHn3Y3n_G_35fJnNfq3r3b1tvWm-T1EjRQLIQNlskc5vlS_T7OZ215B9bVrtrlyN6a47r2mkLpGpJkRSzlArNyhI5D-M2Zi4cgBWCvfM3NO0locpWGxgtW9EaKie4lhXdrDsQwQd7KvfELokGcgO7rCJQpNhSCQ8C_WBsN8lgxgMrF_rqxELsK-dKaHiOT9S6iW94TwqIbXE_hi_x37DNj2n6TXcpus0rLq6NTzCvlNtmHbdsm3btS3XtqsTA3_I6y3Ta9aqjms7bt2r17zJX8acoS8?type=png)](https://mermaid.live/edit#pako:eNqFk89r2zAUx_8VoVMKrrGdxE58KEnpDttlpdtp5KLaL4kgljJZHstCIPddBqXQQVOyQ8cOO2xtoDvsL4q9_2GSY7f5NXYwWO993tNXXz2NccBDwD6O4W0CLIATSnqCRB2GEEkkZ0l0DiJfBZILtFxM06_fs5tPOjQkQtKADgmTKOBMCj4YrOD1TAziHQ1gO9x-ebodShR6BkMeU7XVaDvbp7EObwKPetDh0dGaCOSj7Hqezafpxc_0y0yja0nNFrI0-HG-fLguePRUICCQSPTOScWxXcOpWYZTdQxkegc6q_Rv9dHy22GIKq224AkLN7DNsyn6TBseS1Mnnoca3UIOdZku99HJcakum1-m9wuUPXxLL3KRjEtAgvb6EvFugbeOocsF7Fc5FDwACCnrveCUnapPmkWscoB0DbBcTlmyV_2GWdndlbqE_1u23nGldHn3Y3n_G_35fJnNfq3r3b1tvWm-T1EjRQLIQNlskc5vlS_T7OZ215B9bVrtrlyN6a47r2mkLpGpJkRSzlArNyhI5D-M2Zi4cgBWCvfM3NO0locpWGxgtW9EaKie4lhXdrDsQwQd7KvfELokGcgO7rCJQpNhSCQ8C_WBsN8lgxgMrF_rqxELsK-dKaHiOT9S6iW94TwqIbXE_hi_x37DNj2n6TXcpus0rLq6NTzCvlNtmHbdsm3btS3XtqsTA3_I6y3Ta9aqjms7bt2r17zJX8acoS8)

#### 이렇게 구현을 해 봅니다.
