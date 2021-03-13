package com.example.cogrice.dataclass;

public class DiseasePreventionInfo {
    private String agriControl;
    private String diseaseFeature;
    private String chemControl;


    public DiseasePreventionInfo(String agriControl, String diseaseFeature, String chemControl) {
        this.agriControl = agriControl;
        this.diseaseFeature = diseaseFeature;
        this.chemControl = chemControl;
    }

    public DiseasePreventionInfo(){
        this("农业防治","疾病特点","化学防治");
    }

    /**
     * TODO 读取数据库
     */
    public void loadFromFile(){

    }

}
