package com.example.cogrice.dataclass;

import android.graphics.Bitmap;

import com.example.cogrice.HttpClient;

import java.util.ArrayList;

public class Wiki {
    private String diseaseType;
    private ControlMeasure controlMeasure;
    private String briefIntro;
    private Bitmap instancePhoto;

    public Bitmap getInstancePhoto() {
        return instancePhoto;
    }

    public String getBriefIntro() {
        return briefIntro;
    }

    public void setBriefIntro(String briefIntro) {
        this.briefIntro = briefIntro;
    }

    public void setInstancePhoto(Bitmap instancePhoto) {
        this.instancePhoto = instancePhoto;
    }

    public Wiki(String diseaseType, ControlMeasure controlMeasure, Bitmap instancePhoto,String briefIntro) {
        this.diseaseType = diseaseType;
        this.controlMeasure = controlMeasure;
        this.instancePhoto = instancePhoto;
        this.briefIntro = briefIntro;
    }

    public Wiki(){
        this("【Wiki示例】",new ControlMeasure(),null,"Wiki防治措施简介");
    }
    /**
     * TODO 获取所有远程防治信息
     */
    public static ArrayList<Wiki> getAllRemoteWikis() {
        ArrayList<Wiki> result = new ArrayList<Wiki>();
        // HttpClient.doGet("http://40.73.0.45:80/get_all_wikis");
        // 获取远程历史记录
        for(int i = 0;i<10;i++){
            result.add(new Wiki());
        }
        System.out.println("生成10个缺省Wiki记录");
        return result;
    }


    public ControlMeasure getControlMeasure() {
        return controlMeasure;
    }

    public void setControlMeasure(ControlMeasure controlMeasure) {
        this.controlMeasure = controlMeasure;
    }

    public String getDiseaseType() {
        return diseaseType;
    }

    public void setDiseaseType(String diseaseType) {
        this.diseaseType = diseaseType;
    }
}
