package com.example.cogrice.dataclass;

import com.example.cogrice.HttpClient;
import com.example.cogrice.dataclass.History.HistoryRawPOJO;
import com.example.cogrice.dataclass.Wiki.WikiRawPOJO;
import com.example.cogrice.utils.AlertHelper;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.util.ArrayList;
import java.util.List;

/**
 * TODO 解析Json文件
 */
public class JSONHelper {

    public static String getAllRecordsUrl = "http://40.73.0.45:80/record/get_all_records";
    public static String getAllWikisUrl = "http://40.73.0.45:80/get_all_wiki";

    public static String getRecordsJsonStringAfterPost(String username) {
        String json = HttpClient.doPostOneMsg(getAllRecordsUrl, "username", username);
        return json;
    }

    public static List<HistoryRawPOJO> getHistoryPOJOsFromJson(String username) {
        String recordsJson = null;
        int count = 2;
        recordsJson = getRecordsJsonStringAfterPost(username);
        while (recordsJson == null || recordsJson.toLowerCase().contains("fail")) {
            AlertHelper.warn("服务器繁忙，请稍候");
            AlertHelper.warnNotImplemented("获取JSON第" + count + "次");
            recordsJson = getRecordsJsonStringAfterPost(username);
            count++;
        }
        AlertHelper.warnNotImplemented("获取到历史记录JSON信息" + recordsJson);
        List<HistoryRawPOJO> historyRawPOJOS = null;
        try {
            historyRawPOJOS = new ObjectMapper().readValue(recordsJson, new TypeReference<List<HistoryRawPOJO>>() {
            });
        } catch (JsonProcessingException e) {
            e.printStackTrace();
        }
        return historyRawPOJOS;
    }

    public static List<WikiRawPOJO> getWikiPOJOsFromJson() {
        String wikisJson = null;
        int count = 1;
        while (wikisJson == null) {
            wikisJson = getWikiJsonStringAfterPost();
            AlertHelper.warnNotImplemented("获取JSON第" + count + "次");
            count++;
        }
        AlertHelper.warnNotImplemented("获取到WikiJSON信息" + wikisJson);
        List<WikiRawPOJO> wikiRawPOJOS = null;
        try {
            wikiRawPOJOS = new ObjectMapper().readValue(wikisJson, new TypeReference<List<WikiRawPOJO>>() {
            });
        } catch (JsonProcessingException e) {
            e.printStackTrace();
        }
        return wikiRawPOJOS;
    }

    private static String getWikiJsonStringAfterPost() {
        String json = HttpClient.doGet(getAllWikisUrl);
        return json;
    }

    public static void main(String[] args) throws JsonProcessingException {
        String username = "JLKASD";
        ArrayList<HistoryRawPOJO> histories = (ArrayList<HistoryRawPOJO>) getHistoryPOJOsFromJson(username);
        for (HistoryRawPOJO history : histories) {
            System.out.println(history);
        }
    }

}
