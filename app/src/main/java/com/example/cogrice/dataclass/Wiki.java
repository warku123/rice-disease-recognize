package com.example.cogrice.dataclass;

import android.graphics.Bitmap;
import android.net.wifi.aware.DiscoverySession;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;

import com.example.cogrice.HttpClient;
import com.example.cogrice.utils.AlertHelper;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class Wiki implements Serializable{
    public static final int GOT_ALL_WIKIS = 1;


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
        HttpClient.doGet("http://40.73.0.45:80/get_all_wikis");
        AlertHelper.warnNotImplemented("获取远程Wiki");
        Wiki.startDownloading();
        ControlMeasure.initControlMearsures();
        return result;
    }

    private static void startDownloading() {
        
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

    public static class WikiDownloadThread extends Thread {
        private final Handler historyViewHandler;
        private Handler wikiViewHandler;

        public WikiDownloadThread(Handler historyViewHandler) {
            this.historyViewHandler = historyViewHandler;
        }

        @Override
        public void run() {
            super.run();
            AlertHelper.warnNotImplemented("下载Wiki");
            AlertHelper.warnNotImplemented("默认用户zpg");
            List<Wiki> histories = Wiki.getAllRemoteWikiRecords("zpg");
            Message msg = Message.obtain();
            msg.what = GOT_ALL_WIKIS;
            Bundle wikiBundle = new Bundle();
            wikiBundle.putSerializable("wikiList", (Serializable) histories);
            msg.setData(wikiBundle);
            this.wikiViewHandler.sendMessage(msg);
        }
    }

    /**
     * 通过JSON获取POJO，得到WIKI
     * @param username
     * @return
     */
    private static List<Wiki> getAllRemoteWikiRecords(String username) {
        ArrayList<WikiRawPOJO> historyRawPOJOS = (ArrayList<WikiRawPOJO>) getWikiPOJOsFromJson(username);
        return fillPOJOList(historyRawPOJOS);
    }

    private static List<Wiki> fillPOJOList(ArrayList<Wiki.WikiRawPOJO> wikiRawPOJOS) {
        ArrayList<Wiki> histories = new ArrayList<>();
        for (Wiki.WikiRawPOJO wikiRawPOJO : wikiRawPOJOS) {
            histories.add(wikiRawPOJO.toWiki());
        }
        return histories;
    }
    private static List<WikiRawPOJO> getWikiPOJOsFromJson(String username) {
        return JSONHelper.getWikiPOJOsFromJson(username);
    }

    /**
     * TODO
     */
    public class WikiRawPOJO {

        /**
         * TODO
         * @return
         */
        public Wiki toWiki() {
            Wiki result = null;
            AlertHelper.warnNotImplemented("没有编写类");
            return result;
        }
    }
}
