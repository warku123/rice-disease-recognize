package com.example.cogrice.dataclass;

import android.graphics.Bitmap;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;

import com.example.cogrice.HttpClient;
import com.example.cogrice.Userinfo;
import com.example.cogrice.adapters.HistoryRecordsAdapter;
import com.example.cogrice.utils.AlertHelper;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.io.Serializable;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class History implements Serializable {
    public static final int GOT_ALL_HISTORIES = 1;
    public static final int NETWORK_ERROR = 2;
    public static final int NO_HISTORY = 3;


    private String diseaseEnTypeName;
    private Date time;
    private ControlMeasure controlMeasure;
    private Bitmap photo;
    private String photoUrl;

    public String getPhotoUrl() {
        return photoUrl;
    }

    public void setPhotoUrl(String photoUrl) {
        this.photoUrl = photoUrl;
    }

    @Override
    public String toString() {
        return "History{" +
                "diseaseType='" + diseaseEnTypeName + '\'' +
                ", time=" + time +
                ", controlMeasure=" + controlMeasure +
                ", photo=" + photo +
                ", photoUrl='" + photoUrl + '\'' +
                '}';
    }

    public static class HistoryRawPOJO {
        /**
         * record_id : 4
         * record_time : 2021-03-17 15:13:21
         * record_result : Tomato___Early_blight
         * record_image_path : http://40.73.0.45/download/relative/record_image/2021-3/2021_3_17-15_13_21-Tomato___Early_blight.bmp
         */

        @JsonProperty("record_id")
        private String recordId;
        @JsonProperty("record_time")
        private String recordTime;
        @JsonProperty("record_result")
        private String recordResult;
        @JsonProperty("record_image_path")
        private String recordImagePath;

        @Override
        public String toString() {
            return "HistoryRawPOJO{" +
                    "recordId='" + recordId + '\'' +
                    ", recordTime='" + recordTime + '\'' +
                    ", recordResult='" + recordResult + '\'' +
                    ", recordImagePath='" + recordImagePath + '\'' +
                    '}';
        }

        public String getRecordId() {
            return recordId;
        }

        public void setRecordId(String recordId) {
            this.recordId = recordId;
        }

        public String getRecordTime() {
            return recordTime;
        }

        public void setRecordTime(String recordTime) {
            this.recordTime = recordTime;
        }

        public String getRecordResult() {
            return recordResult;
        }

        public void setRecordResult(String recordResult) {
            this.recordResult = recordResult;
        }

        public String getRecordImagePath() {
            return recordImagePath;
        }

        public void setRecordImagePath(String recordImagePath) {
            this.recordImagePath = recordImagePath;
        }

        public History toHistory() {
            AlertHelper.warnNotImplemented("正在填充" + this.toString());
            History result = new History();
            SimpleDateFormat ft = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            Date time = null;
            try {
                time = ft.parse(recordTime);
                System.out.println(time);
            } catch (ParseException e) {
                System.out.println("Unparseable using " + ft);
            }
            result.setControlMeasure(ControlMeasure.getMeasureForEnName(recordResult));
            result.setTime(time);
            // TODO! IMPORTANT! 转化为BitMap
            result.setDiseaseEnTypeName(recordResult);
            result.setPhotoUrl(recordImagePath);
            // result.setPhoto(ImageHelper.downloadImageAndLoadAsBitmap(recordImagePath));
            return result;
        }
    }

    public Bitmap getPhoto() {
        return photo;
    }

    public void setPhoto(Bitmap photo) {
        this.photo = photo;
    }


    /**
     * TODO 获取所有的历史记录
     *
     * @param username
     */
    public static List<History> getAllRemoteHistoryRecords(String username) {
        ArrayList<HistoryRawPOJO> historyRawPOJOS = (ArrayList<HistoryRawPOJO>) getHistoryPOJOsFromJson(username);
        return fillPOJOList(historyRawPOJOS);
    }

    private static List<History> fillPOJOList(ArrayList<HistoryRawPOJO> historyRawPOJOS) {
        ArrayList<History> histories = new ArrayList<>();
        for (int i = 0;i<historyRawPOJOS.size();i++) {
            histories.add(historyRawPOJOS.get(i).toHistory());
        }
        return histories;
    }

    private static List<HistoryRawPOJO> getHistoryPOJOsFromJson(String username) {
        return JSONHelper.getHistoryPOJOsFromJson(username);
    }

    public String getDiseaseEnTypeName() {
        return diseaseEnTypeName;
    }

    public void setDiseaseEnTypeName(String diseaseEnTypeName) {
        this.diseaseEnTypeName = diseaseEnTypeName;
    }

    public Date getTime() {
        return this.time;
    }

    public String getFormattedDate() {
        SimpleDateFormat ft = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        return ft.format(this.time);
    }

    public void setTime(Date time) {
        this.time = time;
    }

    public ControlMeasure getControlMeasure() {
        return controlMeasure;
    }

    public void setControlMeasure(ControlMeasure controlMeasure) {
        this.controlMeasure = controlMeasure;
    }


    public static void startDownloadingHistories(Handler historyViewHandler) {
        new HistoriesDownloadThread(historyViewHandler).start();
    }


    public static class HistoriesDownloadThread extends Thread {
        private final Handler historyViewHandler;

        public HistoriesDownloadThread(Handler historyViewHandler) {
            this.historyViewHandler = historyViewHandler;
            AlertHelper.warnNotImplemented("绑定Handler");
            HistoryRecordsAdapter.setHandler(historyViewHandler);
        }

        @Override
        public void run() {
            super.run();
            // 初始化防治信息，如果用户没有再次拉取防治信息，则以开启应用时的为准
            ControlMeasure.initControlMeasures();

            AlertHelper.warnNotImplemented("下载线程采用Glide加载图片");
            // 阻塞，等待历史记录获取
            AlertHelper.warnNotImplemented("请设置默认用户名");
            List<History> histories = History.getAllRemoteHistoryRecords(Userinfo.username);
            if(histories.size() == 0){
                Message message = Message.obtain();
                message.what = History.NO_HISTORY;
                HistoryRecordsAdapter.getHandler().sendMessage(message);
                return;
            }
            Message msg = Message.obtain();
            msg.what = GOT_ALL_HISTORIES;
            Bundle historiesBundle = new Bundle();
            historiesBundle.putSerializable("historyList", (Serializable) histories);
            msg.setData(historiesBundle);
            this.historyViewHandler.sendMessage(msg);
        }
    }

    public static void main(String[] args) {
//        String bitmapString = HttpClient.doGet("http://40.73.0.45/download/relative/record_image/2021-3/2021_3_17-15_11_58-Tomato___Early_blight.bmp");
        String bitmapString = HttpClient.doGet("http://40.73.0.45/download/relative/record_image/2021-3/lADPD2eDPMraS4bNBoLNCcQ_2500_1666.jpg");
        System.out.println(bitmapString.length());
    }
}
