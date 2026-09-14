# HelpDesk

직원이 문의를 티켓으로 등록하고 상태를 관리하는 사내 헬프데스크 웹 서비스

## 기술 스택
- Spring Boot 2.7.3
- Thymeleaf
- MyBatis
- MySQL
- Spring Security (세션 기반 Form Login)

## 실행 준비

### 1. Gradle Wrapper 생성 (최초 1회)
이 프로젝트는 gradle-wrapper.jar를 포함하고 있지 않습니다. 로컬에 Gradle이 설치되어 있다면
프로젝트 루트에서 아래 명령어를 한 번 실행해 wrapper를 생성하세요.

```bash
gradle wrapper --gradle-version 7.5
```

Gradle이 설치되어 있지 않다면 VS Code의 `Extension Pack for Java` + `Gradle for Java` 확장을 설치한 뒤
폴더를 열면 자동으로 Gradle 프로젝트로 인식되어 별도 wrapper 없이도 빌드/실행이 가능합니다.

### 2. MySQL 데이터베이스 생성
```sql
CREATE DATABASE helpdesk CHARACTER SET utf8mb4;
```
그 다음 `src/main/resources/schema.sql`의 내용을 실행해 테이블을 생성합니다.

### 3. application.yml 수정
`src/main/resources/application.yml`에서 본인 환경에 맞게 MySQL 계정 정보를 수정하세요.

```yaml
spring:
  datasource:
    username: root
    password: 여기_본인_비밀번호
```

### 4. 실행
- VS Code: `HelpdeskApplication.java` 열고 `Run` 버튼 클릭
- 또는 터미널: `./gradlew bootRun` (wrapper 생성 후) / `gradle bootRun`

### 5. HikariCP 커넥션풀 테스트
DB 접속 정보(`application.yml`)를 맞춘 뒤 아래 중 하나로 확인할 수 있습니다.

**A) 애플리케이션 기동 로그로 확인**
`./gradlew bootRun`으로 앱을 켜면 콘솔에 아래와 같은 로그가 순서대로 찍힙니다.
```
com.zaxxer.hikari.HikariDataSource : HelpdeskHikariPool - Starting...
com.zaxxer.hikari.pool.HikariPool  : HelpdeskHikariPool - Added connection ...
========== HikariCP 풀 상태 확인 ==========
Pool Name        : HelpdeskHikariPool
Max Pool Size     : 10
...
```
이게 보이면 HikariCP 풀이 정상적으로 뜬 겁니다.

**B) 테스트 코드로 확인 (권장)**
```bash
./gradlew test --tests "com.helpdesk.config.HikariConnectionPoolTest"
```
`HikariConnectionPoolTest`가 3가지를 검증합니다.
1. DataSource가 실제로 `HikariDataSource` 타입인지
2. 커넥션을 정상적으로 얻고 반납할 수 있는지
3. `maximum-pool-size`(10)를 다 쓴 상태에서 하나 더 요청하면 `connection-timeout`(3초) 뒤 예외가 나는지 — **이게 통과하면 풀이 실제로 커넥션 개수를 제한하고 있다는 확실한 증거**입니다

### 6. 동작 확인
1. http://localhost:8080/user/signup 에서 회원가입
2. http://localhost:8080/user/login 에서 로그인
3. 로그인 성공 시 홈(`/`)으로 이동, 세션 유지 확인

## 진행 상황
- [x] 프로젝트 생성 및 기본 세팅
- [x] 회원가입
- [x] 로그인 (Spring Security + 세션)
- [ ] 문의 등록
- [ ] 문의 목록 조회 (페이징 · 상태 필터)
- [ ] 문의 상세 조회
- [ ] 문의 상태 변경
- [ ] 문의 삭제
- [ ] 댓글 작성 · 조회
- [ ] 키워드 검색
- [ ] 우선순위 필드
- [ ] 담당자 배정 필드
- [ ] 마이페이지
- [ ] 첨부파일 업로드
- [ ] 테스트코드 작성
