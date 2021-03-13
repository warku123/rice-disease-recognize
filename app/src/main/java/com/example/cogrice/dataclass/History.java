package com.example.cogrice.dataclass;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.widget.ImageView;

import java.util.ArrayList;
import java.util.Date;

public class History {
    private String diseaseType;
    private Date date;
    private DiseaseInfo diseaseInfo;
    private Bitmap photo;

    public Bitmap getPhoto() {
        return photo;
    }

    public void setPhoto(Bitmap photo) {
        this.photo = photo;
    }


    /**
     * TODO 获取所有的历史记录
     */
    public static ArrayList<History> getHistoryRecords() {
        ArrayList<History> result = new ArrayList<History>();
        for (int i = 0; i < 10; i++) {
            // 生成10个历史记录
            result.add(new History());
        }
        return result;
    }

    public String getDiseaseType() {
        return diseaseType;
    }

    public void setDiseaseType(String diseaseType) {
        this.diseaseType = diseaseType;
    }

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    public DiseaseInfo getDiseaseInfo() {
        return diseaseInfo;
    }

    public void setDiseaseInfo(DiseaseInfo diseaseInfo) {
        this.diseaseInfo = diseaseInfo;
    }

    public History(String diseaseType, Date date, DiseaseInfo diseaseInfo, Bitmap photo) {
        this.diseaseType = diseaseType;
        this.date = date;
        this.diseaseInfo = diseaseInfo;
        this.photo = photo;
    }

    /**
     * 默认构造函数
     */
    public History() {
        this("考虑采用枚举类",
                new Date(121, 1, 1, 12, 0, 0),
                new DiseaseInfo(),
                // FIXME 绝对路径！
                BitmapFactory.decodeFile("D:\\Research\\GraduationInternship\\rice-disease-recognize\\app\\src\\main\\res\\drawable\\camera.jpg")
        );
    }

    /**
     * 存储一条历史记录，利用文件存储
     */
    public void dump() {
        // TODO
    }

    /**
     * @return 本地历史记录对象
     */
    public History load_local() {
        // TODO
        return null;
    }

    @Override
    public String toString() {
        return "History{" +
                "diseaseType='" + diseaseType + '\'' +
                ", date=" + date +
                ", diseaseInfo=" + diseaseInfo +
                '}';
    }

    public static void main(String[] args) {
        History history = new History("考虑采用枚举类", new Date(121, 1, 1, 12, 0, 0), new DiseaseInfo(), BitmapFactory.decodeFile("drawable\\camera.jpg"));
        System.out.println(history.toString());
    }

}
