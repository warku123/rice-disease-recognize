package com.example.cogrice.dataclass;

public class DiseaseInfo {
    private String agriControl;
    private String diseaseFeature;
    private String chemControl;


    public DiseaseInfo(String agriControl, String diseaseFeature, String chemControl) {
        this.agriControl = agriControl;
        this.diseaseFeature = diseaseFeature;
        this.chemControl = chemControl;
    }

    @Override
    public String toString() {
        return "DiseaseInfo{" +
                "agriControl='" + agriControl + '\'' +
                ", diseaseFeature='" + diseaseFeature + '\'' +
                ", chemControl='" + chemControl + '\'' +
                '}';
    }

    public DiseaseInfo(){
        this("农业防治","疾病特点","化学防治");
    }

    /**
     * TODO 读取数据库
     */
    public void loadFromFile(){

    }

}
