# Maplestory-open-api-restTemplate

메이플스토리 공식 Open API를 활용하여 캐릭터 및 아이템 정보를 조회하는 웹 서비스입니다.
기존 웹 크롤링/스크래핑 방식 대신 공식 API를 사용하여 안정적인 데이터 조회를 구현하였습니다.

## 기술 스택

| 구분 | 기술 | 버전 |
|------|------|------|
| **Language** | Java | 17 |
| **Framework** | Spring MVC | 5.3.20 |
| **Build** | Maven | - |
| **View** | JSP + JSTL | - |
| **API 통신** | RestTemplate | - |
| **JSON 파싱** | Jackson, org.json, json-simple | - |
| **기타** | Lombok | 1.18.28 |

## 주요 기능

### 캐릭터 정보 조회
- 넥슨 Open API 토큰 기반 인증
- 캐릭터 기본 정보 조회
- 캐릭터 능력치 상세 조회
  - 데미지, 보스 데미지, 최종 데미지
  - 방어율 무시, 크리티컬 확률/데미지
  - 아케인포스, 어센틱포스
  - STR, DEX, INT, LUK
  - 아이템 드롭률, 메소 획득량
  - 전투력

### 아이템 정보 조회
- 장비 아이템 상세 정보

## 프로젝트 구조

```
src/main/java/com/dev/api/
├── controller/       # API 컨트롤러 (RestTemplate 호출)
├── model/            # VO 클래스 (charstat 등)
└── exception/        # 전역 예외 처리 (@ControllerAdvice)
```

## 기술적 구현

### API 토큰 관리

`application.properties`에서 토큰을 관리하고 `@Value`로 주입합니다.

```properties
api.token=Your Token
```

```java
@Value("${api.token}")
private String apiToken;
```

### 능력치 파싱 - Map 기반 동적 Setter 패턴

JSON 배열의 능력치 데이터를 Map과 함수형 인터페이스로 동적 매핑하여,
조건 분기 없이 깔끔하게 처리합니다.

```java
Map<String, Consumer<String>> setters = new HashMap<>();
setters.put("데미지", stats::setDamage);
setters.put("보스 몬스터 데미지", stats::setBoss_Damage);
setters.put("크리티컬 확률", stats::setCritical_val);
// ...

for (int i = 0; i < StatArray.length(); i++) {
    JSONObject stat = StatArray.getJSONObject(i);
    String statName = stat.getString("stat_name");
    String statValue = stat.getString("stat_value");
    if (setters.containsKey(statName)) {
        setters.get(statName).accept(statValue);
    }
}
```

### 전역 예외 처리

```java
@ControllerAdvice
public class CommonExceptionAdvice {
    @ExceptionHandler(Exception.class)
    public String except(Exception e, Model model) {
        model.addAttribute("exception", e);
        return "home";
    }
}
```

## 시작하기

### 사전 요구사항
- JDK 17
- Maven
- 메이플스토리 API 토큰 ([발급하기](https://openapi.nexon.com/))

### 1. API 토큰 설정

`src/main/resources/application.properties`에 발급받은 토큰을 입력합니다.

```properties
api.token=발급받은_API_토큰
```

### 2. 빌드 및 실행

```bash
mvn clean package
```

Tomcat 서버에 WAR 파일을 배포하여 실행합니다.
