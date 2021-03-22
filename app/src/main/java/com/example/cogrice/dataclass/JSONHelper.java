package com.example.cogrice.dataclass;

import android.os.Bundle;
import android.os.Message;

import com.example.cogrice.HttpClient;
import com.example.cogrice.adapters.HistoryRecordsAdapter;
import com.example.cogrice.adapters.WikiRecordsAdapter;
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
        List<HistoryRawPOJO> historyRawPOJOS = new ArrayList<HistoryRawPOJO>();
        String recordsJson = null;
        int count = 1;
        recordsJson = getRecordsJsonStringAfterPost(username);
        while (recordsJson.equals("connection failed")) {
            if (count > 5) {
                Message msg = new Message();
                msg.what = History.NETWORK_ERROR;
                HistoryRecordsAdapter.getHandler().sendMessage(new Message());
                return historyRawPOJOS;
            }
            AlertHelper.warnNotImplemented("获取JSON第" + count + "次");
            recordsJson = getRecordsJsonStringAfterPost(username);
            count++;
        }
        while (recordsJson.contains("no")) {
            if (count > 5) {
                Message msg = new Message();
                msg.what = History.NO_HISTORY;
                HistoryRecordsAdapter.getHandler().sendMessage(new Message());
                return historyRawPOJOS;
            }
            AlertHelper.warnNotImplemented("获取JSON第" + count + "次");
            recordsJson = getRecordsJsonStringAfterPost(username);
            count++;
        }
        if (recordsJson == null) {
            return historyRawPOJOS;
        }
        AlertHelper.warnNotImplemented("获取到历史记录JSON信息" + recordsJson);
        try {
            historyRawPOJOS = new ObjectMapper().readValue(recordsJson, new TypeReference<List<HistoryRawPOJO>>() {
            });
        } catch (JsonProcessingException e) {
            e.printStackTrace();
        }
        return historyRawPOJOS;
    }

    public static List<WikiRawPOJO> getWikiPOJOsFromJson() {
        List<WikiRawPOJO> wikiRawPOJOS = new ArrayList<WikiRawPOJO>();
        String wikisJson = null;
        int count = 1;
        wikisJson = getWikiJsonStringAfterPost();
        while (wikisJson == null || wikisJson.toLowerCase().contains("fail")) {
            AlertHelper.warnNotImplemented("获取Wiki JSON第" + count + "次");
            if (count > 5) {
                // Message msg = new Message();
                // msg.what = Wiki.NETWORK_ERROR;
                // WikiRecordsAdapter.getHandler().sendMessage(new Message());
                return wikiRawPOJOS;
            }
            count++;
            wikisJson = getWikiJsonStringAfterPost();
        }
        AlertHelper.warnNotImplemented("获取到WikiJSON信息" + wikisJson);
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
