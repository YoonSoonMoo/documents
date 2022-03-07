# Multi process handling [ ConuntDownLatch ]
![](https://img.shields.io/badge/Java-1.8%20version-brightgreen) ![](https://img.shields.io/badge/ConuntDownLatch-java.util.concurrent-orange)

  Java 에서 사용하는 Multi Process 개발 시 유용하게 사용할 수 있는 방법 ` [Java기준] `
* 해당 Class 는 GNU General Public License version 2 정책에 따라 퍼블리싱 되었습니다.


## Multi thread의 병렬 프로그램 제어

#### 단일 thread의 종료 처리

아래는 단일 thread의 처리를 ``join`` 으로 처리한 예 입니다.

``` java
Thread thread = new Thread() {
	@Override
	public void run() {
		System.out.println("start trhead.");
		try {
			Thread.sleep(1000);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		System.out.println("end trhead.");
	}
};
thread.start();
try {
	// 스레드가 끝날때 까지 대기한다.
	thread.join();
} catch (InterruptedException e) {
	e.printStackTrace();
}
System.out.println("Thread terminated.");

```
