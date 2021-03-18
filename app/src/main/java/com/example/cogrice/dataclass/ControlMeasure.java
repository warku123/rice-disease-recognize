package com.example.cogrice.dataclass;

import com.example.cogrice.utils.AlertHelper;

import java.util.HashMap;
import java.util.List;

public class ControlMeasure {
    private String diseaseFeature;
    private String agriControl;
    private String chemControl;

    public static HashMap<String,ControlMeasure> controlMeasureMap = new HashMap<String, ControlMeasure>();

    public ControlMeasure(String diseaseFeature, String agriControl, String chemControl) {
        this.diseaseFeature = diseaseFeature;
        this.agriControl = agriControl;
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
    public static void initControlMeasures(List<Wiki> wikis){
        AlertHelper.warnNotImplemented("防治信息未初始化");
        // 数量问题
        for(Wiki wiki:wikis){
            controlMeasureMap.put(wiki.getCnTypename(),new ControlMeasure(wiki.getDiseaseFeature(),wiki.getAgriControl(),wiki.getChemControl()));
        }
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
