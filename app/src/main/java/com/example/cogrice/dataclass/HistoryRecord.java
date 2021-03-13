package com.example.cogrice.dataclass;

import android.content.SharedPreferences;

import java.util.ArrayList;
import java.util.Date;

public class HistoryRecord {
    private String diseaseType;
    private Date date;
    private DiseasePreventionInfo diseasePreventionInfo;

    /**
     * TODO 获取所有的历史记录
     */
    public static ArrayList<HistoryRecord> getHistoryRecords() {
        ArrayList<HistoryRecord> result = new ArrayList<HistoryRecord>();
        for(int i = 0;i<10;i++){
            // 生成10个历史记录
            result.add(new HistoryRecord());
        }
        return result;
    }

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

    public HistoryRecord(String diseaseType, Date date, DiseasePreventionInfo diseasePreventionInfo) {
        this.diseaseType = diseaseType;
        this.date = date;
        this.diseasePreventionInfo = diseasePreventionInfo;
    }

    /**
     * 默认构造函数
     */
    public HistoryRecord(){
        this("考虑采用枚举类",new Date(121,1,1,12,0,0),new DiseasePreventionInfo());
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

    @Override
    public String toString() {
        return "HistoryRecord{" +
                "diseaseType='" + diseaseType + '\'' +
                ", date=" + date +
                ", diseasePreventionInfo=" + diseasePreventionInfo +
                '}';
    }

    public static void main(String[] args) {
        HistoryRecord historyRecord = new HistoryRecord();
        System.out.println(historyRecord.toString());
    }

}
