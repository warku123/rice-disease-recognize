package com.example.cogrice.dataclass;

import com.example.cogrice.HttpClient;
import com.example.cogrice.dataclass.History.HistoryRawPOJO;
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

    public static String getRecordsJsonStringAfterPost(String username) {
        String json = HttpClient.doPostString(getAllRecordsUrl, "username=" + username);
        return json;
    }

    public static List<HistoryRawPOJO> getHistoryPOJOsFromJson(String username) {
        String recordsJson = getRecordsJsonStringAfterPost(username);
        AlertHelper.log("获取到历史记录JSON信息" + recordsJson);
        List<HistoryRawPOJO> historyRawPOJOS = null;
        try {
            historyRawPOJOS = new ObjectMapper().readValue(recordsJson, new TypeReference<List<HistoryRawPOJO>>() {
            });
        } catch (JsonProcessingException e) {
            e.printStackTrace();
        }
        return historyRawPOJOS;
    }

    public static void main(String[] args) throws JsonProcessingException {
        String username = "JLKASD";
        ArrayList<HistoryRawPOJO> histories = (ArrayList<HistoryRawPOJO>) getHistoryPOJOsFromJson(username);
        for(HistoryRawPOJO history:histories){
            System.out.println(history);
        }
    }
}
