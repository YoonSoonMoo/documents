# LCS [ Longest Common Subsequence ]
![](https://img.shields.io/badge/Java-1.8%20version-brightgreen) ![](https://img.shields.io/badge/Longest_Common_Subsequence-Algorithm-orange)

## 문자열 검색 알고리즘

### LCS
LCS는 주로 최장 공통 부분수열(Longest Common Subsequence)을 말합니다만, 최장 공통 문자열(Longest Common Substring)을 말하기도 합니다.
여기에서는 최장 공통 부분수열에 관련해서 설명을 합니다.

최장 공통 부분수열의 점화식을 코드로 작성해보았습니다.  
위와 마찬가지로 LCS라는 2차원 배열에 매칭하고 마진값을 설정한 후 검사합니다.


``` java
package kr.pe.yoonsm.lcs;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

/**
 * Created by yoonsm@daou.co.kr on 2022-07-11
 */
public class LcsAlgorithm {

    static char[] A, B;
    static int[][] dp;
    static int max = 0;

    // LCS란 Longest Common Subsequence의 약자로 최장 공통 부분 문자열
    public static void main(String[] args) throws IOException {
        BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
        // 각 문자열을 입력 받는다.
        String a = br.readLine();
        String b = br.readLine();
        // 각 문자열의 길이가 다르므로 따로 저장해둔다.
        int alength = a.length();
        int blength = b.length();
        // 각 문자열을 나눠서 저장할 char 배열.
        A = new char[alength + 1];
        B = new char[blength + 1];
        // 각 문자열을 char 배열에 문자 하나씩 옮겨 담는다.
        for(int i = 1; i <= alength; i++) {
            A[i] = a.charAt(i - 1);
        }
        for(int i = 1; i <= blength; i++) {
            B[i] = b.charAt(i - 1);
        }

        // 각 문자의 비교가 끝났을 때, 해당 위치에서 가질 수 있는 LCS의 값을 저장할 2차원 dp테이블을 정의한다.
        // 첫 행에서도 이전 문자를 참고할 수 있도록 패딩을 준다.
        dp = new int[blength + 1][alength + 1];

        // B의 모든 문자열을 A문자열과 비교
        for(int i = 1; i <= blength; i++) {
            for(int j = 1; j <= alength; j++) {
                // 만일 두 문자가 같은 경우
                if(B[i] == A[j]) {
                    // 대각선의 값을 참고하여 LCS의 값을 + 1한다.
                    dp[i][j] = dp[i - 1][j - 1] + 1;
                }
                // 두 문자가 다른 경우
                else {
                    // 각 문자열의 이전 문자 중 최대 LCS값을 선택.
                    dp[i][j] = Math.max(dp[i - 1][j], dp[i][j - 1]);
                }
            }
        }
        // 최종으로 탐색한 위치에 LCS의 최대 값이 저장되어 있을 것이다.
        System.out.println(dp[blength][alength]);
    }
}

```

---

#### 실행 결과

``` java
array = {
    기흥구 구갈동  380 한성1차아파트 103동101호,
    기흥구 구갈동  396 한양아파트 101동103호,
    기흥구 구갈동  396 한양아파트 104동 1401호,
    기흥구 구갈동  396 한양아파트 109동1205호,
    기흥구 구갈동  401-3 세명약국 기흥 CU 골드점,
    기흥구 구갈동  403-7 금강빌라 2동 201호,
    기흥구 구갈동  411-5 402,
    기흥구 구갈동  46-11 기흥역 파크 푸르지오 303-2001,
    기흥구 구갈동  46-11 기흥역 파크 푸르지오 306동706호,
    기흥구 구갈동  546-2 나등빌 302,
    기흥구 구갈동  546-2 나등빌 302호,
    기흥구 구갈동  553-4 기흥구 구갈동 553-4 202호,
    기흥구 구갈동  555-1 소운빌 303호,
    기흥구 구갈동  574 강남마을3단지주공아파트 303동403호,
    기흥구 구갈동  594-2 스카이프라자 리빙텔 531호,
    기흥구 구갈동  597 강남마을코오롱하늘채아파트 502동 1905호,
    기흥구 구갈동  597 강남마을코오롱하늘채아파트 503동 1703호,
    기흥구 구갈동  600 강남마을 자연앤아파트 404동 1906호,
    기흥구 구갈동  603 강남마을6단지자연&아이원아파트 605동1404호,
    기흥구 구갈동 234-3 힐스테이트 기흥 204동 905호,
    기흥구 구갈동 234-5 기흥역 더샵 104동 4502호,
    기흥구 구갈동 377-27 101호,
    기흥구 구갈동 396 한양아파트 110동 901호,
    기흥구 구갈동 397-2 두원아파트 103동 403호,
    기흥구 구갈동 561-2 우리빌 206호,
    기흥구 구갈동 567-7 한진빌 201호,
    기흥구 동백동  584 백현마을주공9단지아파트 2904동 902호,
    기흥구 동백동  584 백현마을주공9단지아파트 2910동702호
}

위의 주소를 대상으로 LCS 알고리즘을 적용할 경우
 
input : 기흥구 구갈동 46-11 기흥역파크푸르지오 303-2001
최고점 : 경기도 용인시 기흥구 구갈동  46-11 기흥역 파크 푸르지오 303-2001 , score : 32

input : 구갈동  600 강남마을 자연앤아파트 404동 1906
최고점 : 경기도 용인시 기흥구 구갈동  600 강남마을 자연앤아파트 404동 1906호 , score : 30

```
텍스트에 공백문자나 특수문자가 포함되어 있더라도 알고리즘 네이밍처리 입력된 값과 가장 길게 매칭된 카운트를  
돌려주기 때문에 사람이 DB의 like 나 java의 문자열 검색 method로 처리할 수 없는 결과를 취할수 있습니다.  