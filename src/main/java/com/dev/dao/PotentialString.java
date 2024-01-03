package com.dev.dao;

import lombok.Data;

@Data
public class PotentialString {
    private String[] containOption;

    public PotentialString() {
        this.containOption = new String[] {"STR", "DEX","INT","LUK","보스 몬스터 공격 시 데미지","크리티컬 데미지","몬스터 방어율 무시",
        "모든 스킬의 재사용 대기시간","올스탯","마력","공격력","아이템 드롭률","메소 획득량"};
    }
}
