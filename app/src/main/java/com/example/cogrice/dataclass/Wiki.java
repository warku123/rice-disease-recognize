package com.example.cogrice.dataclass;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;

import com.example.cogrice.utils.AlertHelper;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class Wiki implements Serializable {
    public static final int GOT_ALL_WIKIS = 1;
    public static final int NETWORK_ERROR = 2;


    private String enTypeName;
    private String cnTypename;
    private String diseaseFeature;
    private String agriControl;

    public String getEnTypeName() {
        return enTypeName;
    }

    public void setEnTypeName(String enTypeName) {
        this.enTypeName = enTypeName;
    }

    public String getCnTypename() {
        return cnTypename;
    }

    public void setCnTypename(String cnTypename) {
        this.cnTypename = cnTypename;
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

    public Wiki(String enTypeName, String cnTypename, String diseaseFeature, String agriControl, String chemControl, String imgUrl) {
        this.enTypeName = enTypeName;
        this.cnTypename = cnTypename;
        this.diseaseFeature = diseaseFeature;
        this.agriControl = agriControl;
        this.chemControl = chemControl;
        this.imgUrl = imgUrl;
    }

    public Wiki() {
    }

    public String getChemControl() {
        return chemControl;
    }

    public void setChemControl(String chemControl) {
        this.chemControl = chemControl;
    }

    public String getImgUrl() {
        return imgUrl;
    }

    public void setImgUrl(String imgUrl) {
        this.imgUrl = imgUrl;
    }

    private String chemControl;
    private String imgUrl;


    /**
     * TODO 获取所有远程防治信息
     */
    public static List<Wiki> getAllRemoteWikis() {
        AlertHelper.warnNotImplemented("获取远程Wiki");
        List<Wiki> wikis = Wiki.startDownloading();
        return wikis;
    }

    private static List<Wiki> startDownloading() {
        List<WikiRawPOJO> wikiRawPOJOS = JSONHelper.getWikiPOJOsFromJson();
        List<Wiki> wikis = fillPOJOList(wikiRawPOJOS);
        return wikis;
    }

    public boolean containsKeyword(String filterWord) {
        return  this.getCnTypename().contains(filterWord);
    }

    public static class HistoriesDownloadThread extends Thread {
        private final Handler WikiViewHandler;

        public HistoriesDownloadThread(Handler WikiViewHandler) {
            this.WikiViewHandler = WikiViewHandler;
        }

        @Override
        public void run() {
            super.run();
            AlertHelper.warnNotImplemented("下载线程采用Glide加载图片");
            List<Wiki> histories = Wiki.getAllRemoteWikiRecords();
            Message msg = Message.obtain();
            msg.what = GOT_ALL_WIKIS;
            Bundle historiesBundle = new Bundle();
            historiesBundle.putSerializable("WikiList", (Serializable) histories);
            msg.setData(historiesBundle);
            this.WikiViewHandler.sendMessage(msg);
        }
    }


    public static void startDownloadingWikis(Handler wikiViewHandler) {
        new Wiki.WikiDownloadThread(wikiViewHandler).start();
    }


    public static class WikiDownloadThread extends Thread {
        private final Handler wikiViewHandler;

        public WikiDownloadThread(Handler wikiViewHandler) {
            this.wikiViewHandler = wikiViewHandler;
        }

        @Override
        public void run() {
            super.run();
            AlertHelper.warnNotImplemented("下载Wiki");
            List<Wiki> wikis = Wiki.getAllRemoteWikiRecords();
            ControlMeasure.initControlMeasures(wikis);
            Message msg = Message.obtain();
            msg.what = GOT_ALL_WIKIS;
            Bundle wikiBundle = new Bundle();
            wikiBundle.putSerializable("wikiList", (Serializable) wikis);
            msg.setData(wikiBundle);
            this.wikiViewHandler.sendMessage(msg);
        }
    }

    public static List<Wiki> getAllRemoteWikiRecords() {
        ArrayList<WikiRawPOJO> wikiRawPOJOS = (ArrayList<WikiRawPOJO>) getWikiPOJOsFromJson();
        return fillPOJOList(wikiRawPOJOS);
    }

    private static List<Wiki> fillPOJOList(List<Wiki.WikiRawPOJO> wikiRawPOJOS) {
        ArrayList<Wiki> wikis = new ArrayList<>();
        for (int i = 0;i<wikiRawPOJOS.size();i++) {
            wikis.add(wikiRawPOJOS.get(i).toWiki());
        }
        return wikis;
    }

    private static List<WikiRawPOJO> getWikiPOJOsFromJson() {
        return JSONHelper.getWikiPOJOsFromJson();
    }

    /**
     * TODO
     */
    public static class WikiRawPOJO {

        @JsonProperty("en_type_name")
        private String enTypeName;
        @JsonProperty("cn_type_name")
        private String cnTypeName;
        @JsonProperty("disease_feature")
        private String diseaseFeature;
        @JsonProperty("agri_control")
        private String agriControl;
        @JsonProperty("chem_control")
        private String chemControl;
        @JsonProperty("img_url")
        private String imgUrl;

        /**
         * TODO
         *
         * @return
         */
        public Wiki toWiki() {
            Wiki result = new Wiki();
            result.enTypeName = this.enTypeName;
            result.cnTypename = this.cnTypeName;
            result.diseaseFeature = this.diseaseFeature;
            result.agriControl = this.agriControl;
            result.chemControl = this.chemControl;
            result.imgUrl = this.imgUrl;
            AlertHelper.warnNotImplemented("没有编写类");
            return result;
        }
    }
}
