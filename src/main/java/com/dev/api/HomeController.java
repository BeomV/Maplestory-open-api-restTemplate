package com.dev.api;

import java.io.IOException;
import com.dev.dao.normalInfo;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.*;

import java.util.stream.Stream;
import com.dev.dao.charstat;

import org.json.JSONArray;
import com.dev.dao.PotentialString;
import org.json.JSONException;
import org.json.JSONObject;
import com.dev.dao.ItemEQ;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.http.*;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.servlet.NoHandlerFoundException;

/**
 * Handles requests for the application home page.
 */
@Controller
public class HomeController {

	private static final Logger logger = LoggerFactory.getLogger(HomeController.class);
	@Value("${api.token}")
	private String apiToken;

	@Bean
	public RestTemplate restTemplate() {
		return new RestTemplate();
	}

	@RequestMapping(value = "/",method = RequestMethod.GET)
	public String home(Locale locale, Model model) {
		logger.info("Welcome home! The client locale is {}.", locale);

		Date date = new Date();
		DateFormat dateFormat = DateFormat.getDateTimeInstance(DateFormat.LONG, DateFormat.LONG, locale);

		String formattedDate = dateFormat.format(date);

		model.addAttribute("serverTime", formattedDate );
		logger.info(apiToken);
		return "home";
	}
	@ExceptionHandler(NoHandlerFoundException.class)
	@ResponseStatus(HttpStatus.NOT_FOUND)
	@RequestMapping(value = "/maplestory", produces = "application/json;charset=UTF-8",method = RequestMethod.GET)
	public String mapleStoryApi(@RequestParam(value = "characterName", required = true) String characterName, Model model) {
		/*String characterName = "미르검준"; // 캐릭터 이름*/
		String apiUrl = "https://open.api.nexon.com/maplestory/v1/id?character_name=" + characterName;
		HttpHeaders headers = new HttpHeaders();
		headers.setContentType(MediaType.APPLICATION_JSON); // Spring 5 이후 버전
		headers.add("charset", "UTF-8"); // UTF-8 인코딩 명시
		headers.set("accept", "application/json");
		headers.set("x-nxopen-api-key", apiToken); // API 토큰 설정

		HttpEntity<String> entity = new HttpEntity<>(headers);
		RestTemplate restTemplate = new RestTemplate();
		ObjectMapper mapper = new ObjectMapper();

		String ocid = "";
		List<ItemEQ> items = new ArrayList<>();
		List<charstat> statList = new ArrayList<>();
		charstat stats = new charstat();
		List<normalInfo> normalList = new ArrayList<>();
		try {
			// 캐릭터 ID 조회
			ResponseEntity<String> response = restTemplate.exchange(apiUrl, HttpMethod.GET, entity, String.class);
			logger.info(response.getBody());
			MapleStoryResponse msResponse = mapper.readValue(response.getBody(), MapleStoryResponse.class);
			ocid = msResponse.getOcid().trim();

			// 날짜 계산
			Calendar cal = Calendar.getInstance();
			Date setTime = cal.getTime();
			if (cal.get(Calendar.HOUR_OF_DAY) >= 1) {
				cal.add(Calendar.DATE, -1);
			} else {
				cal.add(Calendar.DATE, -2);
			}
			setTime = cal.getTime();

			// 캐릭터 기본 정보 조회 및 처리
			normalInfo n = new normalInfo();
			String formattedDate = new SimpleDateFormat("yyyy-MM-dd").format(setTime);
			String normalApiUrl = "https://open.api.nexon.com/maplestory/v1/character/basic?ocid=" + ocid + "&date=" + formattedDate;
			ResponseEntity<String> nResponse = restTemplate.exchange(normalApiUrl, HttpMethod.GET, entity, String.class);
			processNormalInfo(nResponse.getBody(), normalList, n);

			for (normalInfo log : normalList) {
				logger.info("로그 : " + log);
			}

			if (!n.getWorld_name().equals("리부트") && !n.getWorld_name().equals("리부트2")) return "home";

				// 장비 정보 조회
				String equipmentApiUrl = "https://open.api.nexon.com/maplestory/v1/character/item-equipment?ocid=" + ocid + "&date=" + formattedDate;
				ResponseEntity<String> equipmentResponse = restTemplate.exchange(equipmentApiUrl, HttpMethod.GET, entity, String.class);

				// 아이템 장비 정보 처리
				String jsonData = equipmentResponse.getBody();
				JSONObject jsonObject = new JSONObject(jsonData);
				JSONArray itemEquipmentArray = jsonObject.getJSONArray("item_equipment");
				processItemEquipment(itemEquipmentArray, items);


				// 스탯 정보 조회
				String charStatUrl = "https://open.api.nexon.com/maplestory/v1/character/stat?ocid=" + ocid + "&date=" + formattedDate;
				ResponseEntity<String> statResponse = restTemplate.exchange(charStatUrl, HttpMethod.GET, entity, String.class);
				String jsonStat = statResponse.getBody();
				JSONObject jsonStatOb = new JSONObject(jsonStat);
				JSONArray StatArray = jsonStatOb.getJSONArray("final_stat");
				processStatInfo(StatArray, stats);

			} catch(IOException | JSONException e){
				logger.error("[Error] : " + e.toString());
				// 오류 처리
			}
			logger.info("모델: " + stats);

			model.addAttribute("stats", stats);
			model.addAttribute("normalList", normalList);
			model.addAttribute("items", items);

		return "home"; // 장비 정보 JSON 형태의 응답 반환
	}

	private void processNormalInfo(String Noraml_Data, List<normalInfo> normalList,normalInfo n){
		JSONObject jsonNormal = new JSONObject(Noraml_Data);

		/*if(!jsonNormal.getString("world_name").equals("리부트")) return;*/
		n.setWorld_name(jsonNormal.getString("world_name"));
		n.setCharacter_class(jsonNormal.getString("character_class"));
		n.setCharacter_image(jsonNormal.getString("character_image"));
		n.setCharacter_guild_name(jsonNormal.getString("character_guild_name"));
		n.setCharacter_level(jsonNormal.getInt("character_level"));

		normalList.add(n);
	}

	private void processStatInfo(JSONArray StatArray, charstat stats){
		/*List<String> req_statList = Arrays.asList("데미지","보스 몬스터 데미지","최종 데미지","방어율 무시","크리티컬 확률","크리티컬 데미지",
				"아케인포스","어센틱포스","STR","DEX","INT","LUK","아이템 드롭률","메소 획득량","버프 지속시간","전투력");*/
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

	private void processItemEquipment(JSONArray itemEquipmentArray, List<ItemEQ> items) {
		for (int i = 0; i < itemEquipmentArray.length(); i++) {
			JSONObject jsonItem = itemEquipmentArray.getJSONObject(i);
			ItemEQ item = new ItemEQ();
			PotentialString ps = new PotentialString();

			JSONObject itemAddOption = jsonItem.getJSONObject("item_add_option");
			int strValue = itemAddOption.optInt("str", 0);
			int dexValue = itemAddOption.optInt("dex", 0);
			int lukValue = itemAddOption.optInt("luk", 0);
			int intValue = itemAddOption.optInt("int", 0);
			int atp = itemAddOption.optInt("attack_power", 0);
			int mgp = itemAddOption.optInt("magic_power", 0);
			int allValue = itemAddOption.optInt("all_stat", 0) * 10;

			int maxAddOption = Stream.of(strValue, dexValue, lukValue, intValue)
					.max(Integer::compare)
					.orElse(0);
			int maxValue = Stream.of(maxAddOption, atp, mgp)
					.max(Integer::compare)
					.orElse(0);

			int resultAdd = (maxValue == atp || maxValue == mgp) ? maxValue : (maxAddOption + allValue);

			String stforce = jsonItem.optString("starforce", "0");
			if (!"0".equals(stforce)) {
				item.setStarforce(stforce + "성");
			}

			int seedring = Integer.parseInt(jsonItem.optString("special_ring_level"));
			if(seedring != 0){
				item.setSpecial_ring_level(seedring);
				logger.info("시드링 레벨 : "+ seedring);
			}


			String p1 = jsonItem.optString("potential_option_1", "");
			String p2 = jsonItem.optString("potential_option_2", "");
			String p3 = jsonItem.optString("potential_option_3", "");
			if (resultAdd > 0) {
				item.setAdd_Stat(resultAdd + "급");
			}

			item.setItem_name(jsonItem.optString("item_name", ""));
			item.setItem_shape_icon(jsonItem.optString("item_shape_icon", ""));
			item.setPotential_option_grade(jsonItem.optString("potential_option_grade", ""));

			String[] arr = ps.getContainOption();
			for (String option : arr) {
				if (p1.contains(option)) {
					item.setPotential_option_1(processPotentialOption(p1));
				}
				if (p2.contains(option)) {
					item.setPotential_option_2(processPotentialOption(p2));
				}
				if (p3.contains(option)) {
					item.setPotential_option_3(processPotentialOption(p3));
				}
			}

			items.add(item);
		}
	}

	private String processPotentialOption(String potentialOption) {
		potentialOption = potentialOption.replace("보스 몬스터 공격 시 데미지", "보공");
		potentialOption = potentialOption.replace("크리티컬 데미지", "크뎀");
		potentialOption = potentialOption.replace("몬스터 방어율 무시", "방무");
		potentialOption = potentialOption.replace("모든 스킬의 재사용 대기시간", "쿨감");
		potentialOption = potentialOption.replace("아이템 드롭률", "아획");
		potentialOption = potentialOption.replace("메소 획득량", "메획");

		int cutOffIndex = potentialOption.indexOf("초");
		if (cutOffIndex != -1) {
			potentialOption = potentialOption.substring(0, cutOffIndex + 1); // "초" 포함하여 그 부분까지 유지
		}

		return potentialOption;
	}

}
