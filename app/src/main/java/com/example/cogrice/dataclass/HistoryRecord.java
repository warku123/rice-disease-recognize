package com.example.cogrice.dataclass;

import android.content.SharedPreferences;

import java.util.Date;

public class HistoryRecord {
    private String diseaseType;
    private Date date;
    private DiseasePreventionInfo diseasePreventionInfo;

    public String getDiseaseType() {
        return diseaseType;
    }

    public void setDiseaseType(String diseaseType) {
        this.diseaseType = diseaseType;
    }

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    public DiseasePreventionInfo getDiseasePreventionInfo() {
        return diseasePreventionInfo;
    }

    public void setDiseasePreventionInfo(DiseasePreventionInfo diseasePreventionInfo) {
        this.diseasePreventionInfo = diseasePreventionInfo;
    }

    /**
     * 存储一条历史记录，利用文件存储
     */
    public void dump(){
        // TODO
    }

    /**
     *
     * @return 本地历史记录对象
     */
    public HistoryRecord load_local(){
        // TODO
        return  null;
    }
}
