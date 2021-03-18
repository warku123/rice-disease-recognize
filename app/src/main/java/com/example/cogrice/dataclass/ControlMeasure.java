package com.example.cogrice.dataclass;

import com.example.cogrice.utils.AlertHelper;

import java.util.HashMap;
import java.util.List;

public class ControlMeasure {
    private String agriControl;
    private String diseaseFeature;
    private String chemControl;

    public static HashMap<String,ControlMeasure> controlMeasureMap = new HashMap<String, ControlMeasure>();

    public ControlMeasure(String agriControl, String diseaseFeature, String chemControl) {
        this.agriControl = agriControl;
        this.diseaseFeature = diseaseFeature;
        this.chemControl = chemControl;
    }

    /**
     * @param diseaseName
     * @return 防治记录
     */
    public static ControlMeasure getMeasureFor(String diseaseName) {
       return controlMeasureMap.getOrDefault(diseaseName,new ControlMeasure());
    }

    /**
     * TODO 初始化防治信息，不必多次访问
     * @param wikis
     */
    public static void initControlMearsures(List<Wiki> wikis){
        AlertHelper.warnNotImplemented("防治信息未初始化");
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
        this("农业防治更新中","疾病特点更新中","化学防治更新中");
    }

    /**
     * TODO 读取数据库
     */
    public void loadFromFile(){

    }

}
