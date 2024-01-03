<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ page session="false" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<%@ page isELIgnored="false" %>
<!DOCTYPE html>
<html lang="UTF-8">
<head>
	<meta charset="UTF-8" />
	<meta name="viewport" content="width=device-width, initial-scale=1.0" />
	<link rel="stylesheet" href="/resources/css/style.css" />
	<script src="https://kit.fontawesome.com/1652357a48.js" crossorigin="anonymous"></script>

	<title>RPG LOG!</title>
</head>
<body>
<div class="main">
	<div class="header">
		<div class="gnb">
			<ul>
				<div>
					<li><a href="/">HOME</a></li>
				</div>

				<div>
					<li><a>메이플스토리</a></li>
					<%--<li><a>로스트아크</a></li>--%>
				</div>
			</ul>
		</div>
	</div>

	<div class="main_warp">
		<div class="wrap01_detail">
                    <span>
                        <h1>RPG LOG!</h1>
                        <p>메이플스토리 로스트아크 데이터를 제공합니다.</p>
                    </span>
		</div>
	</div>

	<div class="search_section">
		<form action="/maplestory" method="get" id="mapleStoryForm">
			<div class="input-container">
				<input type="text" placeholder="닉네임을 입력해주세요" id="SearchNick" name="characterName"/>
				<i class="fa-solid fa-magnifying-glass" onclick="submitForm()"></i>
			</div>
		</form>
	</div>
	<div class="info-container">
		<div class="character">

			<div class="profile">
				<c:forEach items="${normalList}" var="n">
					<img src=${n.character_image}>
				</c:forEach>
			</div>
			<div class="char_info">

				<div class="normal_info">
					<c:forEach items="${normalList}" var="n">
						<li>
							<strong>서버:</strong>
							<p>${n.world_name}</p>
						</li>
						<li>
							<strong>직업:</strong>
							<p>${n.character_class}</p>
						</li>
						<li>
							<strong>레벨:</strong>
							<p>${n.character_level}</p>
						</li>
						<li>
							<strong>길드:</strong>
							<p>${n.character_guild_name}</p>
						</li>
					</c:forEach>

				</div>


				<div class="stat_info">
				<c:if test="${stats.attack_point != null}">
					<li>
						<strong>전투력:</strong>
						<p><fmt:formatNumber value="${stats.attack_point}" groupingUsed="true" /></p>
					</li>
					<li>
						<strong>데미지:</strong>
						<p><fmt:formatNumber value="${stats.damage}" groupingUsed="true" />%</p>
					</li>
					<li>
						<strong>보스 몬스터 데미지:</strong>
						<p><fmt:formatNumber value="${stats.boss_Damage}" groupingUsed="true" />%</p>
					</li>
					<li>
						<strong>최종 데미지:</strong>
						<p><fmt:formatNumber value="${stats.final_Data}" groupingUsed="true" />%</p>
					</li>
					<li>
						<strong>방어율 무시:</strong>
						<p><fmt:formatNumber value="${stats.def_Ignore}" groupingUsed="true" />%</p>
					</li>
					<li>
						<strong>크리티컬 확률:</strong>
						<p><fmt:formatNumber value="${stats.critical_val}" groupingUsed="true" />%</p>
					</li>
					<li>
						<strong>크리티컬 데미지:</strong>
						<p><fmt:formatNumber value="${stats.critical_Damage}" groupingUsed="true" />%</p>
					</li>
					<li>
						<strong>버프 지속시간:</strong>
						<p><fmt:formatNumber value="${stats.buff}" groupingUsed="true" />%</p>
					</li>
					<br>
					<li>
						<strong>아케인 포스:</strong>
						<p><fmt:formatNumber value="${stats.AC_Force}" groupingUsed="true" /></p>
					</li>
					<li>
						<strong>어센틱 포스:</strong>
						<p><fmt:formatNumber value="${stats.AS_Force}" groupingUsed="true" /></p>
					</li>
					<br>
					<li>
						<strong>STR:</strong>
						<p><fmt:formatNumber value="${stats.str}" groupingUsed="true" /></p>
					</li>
					<li>
						<strong>DEX:</strong>
						<p><fmt:formatNumber value="${stats.dex}" groupingUsed="true" /></p>
					</li>
					<li>
						<strong>INT:</strong>
						<p><fmt:formatNumber value="${stats.c_Int}" groupingUsed="true" /></p>
					</li>
					<li>
						<strong>LUK:</strong>
						<p><fmt:formatNumber value="${stats.luk}" groupingUsed="true" /></p>
					</li>
					<br>
					<li>
						<strong>아이템 드롭률:</strong>
						<p><fmt:formatNumber value="${stats.item}" groupingUsed="true" />%</p>
					</li>
					<li>
						<strong>메소 획득량:</strong>
						<p><fmt:formatNumber value="${stats.meso}" groupingUsed="true" />%</p>
					</li>
				</c:if>
				</div>

			</div>


		</div>
		<div class="item">
			<c:if test="${items == null}">
			<h2>조회된 결과가 없습니다.</h2>
			</c:if>
			<ul>

				<c:forEach items="${items}" var="item">
					<li>
						<div id="itemimg">
							<img src=${item.item_shape_icon}>
						</div>
						<c:if test="${item.special_ring_level != 0}">
							<div id="itemlist">${item.item_name} ${item.special_ring_level}레벨</div>
						</c:if>
						<c:if test="${item.special_ring_level == 0}">
							<div id="itemlist">${item.item_name}</div>
						</c:if>
						<div id="itemconfig">
							<div class="star_force">${item.starforce}</div>
							<div class="po_grade">${item.potential_option_grade}</div>
							<div>${item.add_Stat}</div>
							<div>${item.potential_option_1} ${item.potential_option_2} ${item.potential_option_3}</div>
						</div>
					</li>
				</c:forEach>
			</ul>
		</div>
	</div>
</div>
</div>
</div>
</body>
<script src="https://code.jquery.com/jquery-3.6.0.min.js"></script>
<script src="/resources/js/main.js"></script>
<footer>
	<p>Copyright © 2023 BeomV
		<br>
		Contact: beom5110@gmail.com
	</p>

</footer>
</html>
