package com.example.cogrice.dataclass;

public class ControlMeasure {
    private String agriControl;
    private String diseaseFeature;
    private String chemControl;


    public ControlMeasure(String agriControl, String diseaseFeature, String chemControl) {
        this.agriControl = agriControl;
        this.diseaseFeature = diseaseFeature;
        this.chemControl = chemControl;
    }

    @Override
    public String toString() {
        return "ControlMeasure{" +
                "agriControl='" + agriControl + '\'' +
                ", diseaseFeature='" + diseaseFeature + '\'' +
                ", chemControl='" + chemControl + '\'' +
                '}';
    }

    public ControlMeasure(){
        this("农业防治","疾病特点","化学防治");
    }

    /**
     * TODO 读取数据库
     */
    public void loadFromFile(){

    }

}
