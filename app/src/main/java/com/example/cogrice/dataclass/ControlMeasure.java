package com.example.cogrice.dataclass;

import com.example.cogrice.utils.AlertHelper;

import java.io.Serializable;
import java.util.HashMap;
import java.util.List;

public class ControlMeasure implements Serializable {
    private String diseaseFeature;
    private String agriControl;
    private String chemControl;

    public static HashMap<String,ControlMeasure> controlMeasureMap = new HashMap<String, ControlMeasure>();
    public static HashMap<String,String> enToCnNameMap = new HashMap<String, String>();
    private static HashMap<String, String> cnToEnNameMap = new HashMap<String, String>();

    public static String getEnName(String diseaseCnTypeName) {
        return ControlMeasure.cnToEnNameMap.getOrDefault(diseaseCnTypeName,"未指定英文名");
    }

    public static String getCnName(CharSequence diseaseEnTypeName) {
        return ControlMeasure.enToCnNameMap.getOrDefault(diseaseEnTypeName,"未指定中文名");
    }

    public String getDiseaseFeature() {
        return diseaseFeature;
    }

    public void setDiseaseFeature(String diseaseFeature) {
        this.diseaseFeature = diseaseFeature;
    }

    public String getAgriControl() {
        return agriControl;
    }

    public void setAgriControl(String agriControl) {
        this.agriControl = agriControl;
    }

    public String getChemControl() {
        return chemControl;
    }

    public void setChemControl(String chemControl) {
        this.chemControl = chemControl;
    }

    public static HashMap<String, ControlMeasure> getControlMeasureMap() {
        return controlMeasureMap;
    }

    public static void setControlMeasureMap(HashMap<String, ControlMeasure> controlMeasureMap) {
        ControlMeasure.controlMeasureMap = controlMeasureMap;
    }

    public static HashMap<String, String> getEnToCnNameMap() {
        return enToCnNameMap;
    }

    public static void setEnToCnNameMap(HashMap<String, String> enToCnNameMap) {
        ControlMeasure.enToCnNameMap = enToCnNameMap;
    }




    public ControlMeasure(String diseaseFeature, String agriControl, String chemControl) {
        this.diseaseFeature = diseaseFeature;
        this.agriControl = agriControl;
        this.chemControl = chemControl;
    }

    /**
     * @param diseaseCnName
     * @return 防治记录
     */
    public static ControlMeasure getMeasureForCnName(String diseaseCnName) {
       return controlMeasureMap.getOrDefault(diseaseCnName,new ControlMeasure());
    }

    public static void initControlMeasures(){
        List<Wiki> wikis = Wiki.getAllRemoteWikiRecords();
        ControlMeasure.initControlMeasures(wikis);
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
            enToCnNameMap.put(wiki.getEnTypeName(),wiki.getCnTypename());
            cnToEnNameMap.put(wiki.getCnTypename(),wiki.getEnTypeName());
        }
    }

    public static ControlMeasure getMeasureForEnName(String enTypeName) {
        return controlMeasureMap.getOrDefault(enToCnNameMap.getOrDefault(enTypeName,"???"),new ControlMeasure());
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
