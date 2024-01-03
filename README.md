# 프로젝트 소개
메이플스토리에서 공식으로 API를 공개함에 따라 기존 웹 크롤링,스크래핑으로만 제공 되었던 웹 서비스를 구현해보았다. 
<br>프론트는 기본적으로 간단하게 구현되어 있다.

## API Token

메이플스토리 API 토큰 [발급하기](https://openapi.nexon.com/)

### Project Version
```xml
<properties>
    <java-version>17</java-version>
    <org.springframework-version>5.3.20</org.springframework-version>
    <org.aspectj-version>1.6.10</org.aspectj-version>
    <org.slf4j-version>1.6.6</org.slf4j-version>
</properties>
```



### #application.properties
```properties
api.token=Your Token
```
### servlet-context.xml
```xml
<context:property-placeholder location="classpath:application.properties"/>
```

### Token 가져오기
```java
@Value("${api.token}")
	private String apiToken;
```

### 예외처리
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

### 캐릭터 능력치 상세 조회
```java
private void processStatInfo(JSONArray StatArray, charstat stats){
		Map<String, java.util.function.Consumer<String>> setters = new HashMap<>();
		setters.put("데미지", stats::setDamage);
		setters.put("보스 몬스터 데미지", stats::setBoss_Damage);
		setters.put("최종 데미지", stats::setFinal_Data);
		setters.put("방어율 무시", stats::setDef_Ignore);
		setters.put("크리티컬 확률", stats::setCritical_val);
		setters.put("크리티컬 데미지", stats::setCritical_Damage);
		setters.put("아케인포스", stats::setAC_Force);
		setters.put("어센틱포스", stats::setAS_Force);
		setters.put("STR", stats::setStr);
		setters.put("DEX", stats::setDex);
		setters.put("INT", stats::setC_Int);
		setters.put("LUK", stats::setLuk);
		setters.put("아이템 드롭률", stats::setItem);
		setters.put("메소 획득량", stats::setMeso);
		setters.put("버프 지속시간", stats::setBuff);
		setters.put("전투력", stats::setAttack_point);

		for (int i = 0; i < StatArray.length(); i++) {
			JSONObject stat = StatArray.getJSONObject(i);
			String statName = stat.getString("stat_name");
			String statValue = stat.getString("stat_value");

			// 해당하는 setter 호출
			if (setters.containsKey(statName)) {
				setters.get(statName).accept(statValue);
			}
		}
}
```

