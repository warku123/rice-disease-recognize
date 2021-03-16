package com.example.cogrice.dataclass;

import com.example.cogrice.HttpClient;
import com.fasterxml.jackson.core.JsonFactory;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.util.ArrayList;
import java.util.List;

/**
 * TODO 解析Json文件
 */
public class JSONHelper {

    public static String getAllRecordsUrl = "http://40.73.0.45:80/record/get_all_records_base64";

    public static String getRecordsJsonStringAfterPost(String httpUrl, String username) {
        String json = HttpClient.doPostString(httpUrl, "username=" + username);
        return json;
    }

    public static String getRecordsJsonStringAfterPost() {
        return getRecordsJsonStringAfterPost(getAllRecordsUrl, "没有设置用户名！");
    }


    public static List<History> getHistoryRecords() throws JsonProcessingException {
        String recordsJson = getRecordsJsonStringAfterPost();
        List<History.HistoryRawPOJO> historyRawPOJOS = new ObjectMapper().readValue(recordsJson, new TypeReference<List<History.HistoryRawPOJO>>() {
        });
        ArrayList<History> result = new ArrayList<History>();
        for(History.HistoryRawPOJO pojo : historyRawPOJOS){
            result.add(pojo.toHistory());
        }
        return result;
    }

    public static void main(String[] args) throws JsonProcessingException {
        ArrayList<History> histories = (ArrayList<History>) getHistoryRecords();
        for(History history:histories){
            System.out.println(history);
        }
    }
}
