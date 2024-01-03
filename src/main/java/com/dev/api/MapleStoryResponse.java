package com.dev.api;

import lombok.Data;

@Data
public class MapleStoryResponse {
    private String ocid;
    private String name;

    // getter와 setter
    public String getOcid() {
        return ocid;
    }

    public void setOcid(String ocid) {
        this.ocid = ocid;
    }
}