package com.example.cogrice.dataclass;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

public class History {
    private String diseaseType;
    private Date date;
    private ControlMeasure controlMeasure;
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
    public static ArrayList<History> getRemoteHistoryRecords() {
        ArrayList<History> result = new ArrayList<History>();
        // 获取远程历史记录
        for(int i = 0;i<10;i++){
            result.add(new History());
        }
        System.out.println("生成10个缺省历史记录");
        return result;
    }

    public String getDiseaseType() {
        return diseaseType;
    }

    public void setDiseaseType(String diseaseType) {
        this.diseaseType = diseaseType;
    }

    public Date getDate(){return this.date;}

    public String getFormattedDate() {
        SimpleDateFormat ft = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
        return ft.format(this.date);
    }

    public void setDate(Date date) {
        this.date = date;
    }

    public ControlMeasure getControlMeasure() {
        return controlMeasure;
    }

    public void setControlMeasure(ControlMeasure controlMeasure) {
        this.controlMeasure = controlMeasure;
    }

    public History(String diseaseType, Date date, ControlMeasure controlMeasure, Bitmap photo) {
        this.diseaseType = diseaseType;
        this.date = date;
        this.controlMeasure = controlMeasure;
        this.photo = photo;
    }

    /**
     * 默认构造函数
     */
    public History() {
        this("水稻稻瘟病",
                new Date(121, 1, 1, 15, 26, 10),
                new ControlMeasure(),
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
                ", controlMeasure=" + controlMeasure +
                '}';
    }

    public static void main(String[] args) {
        History history = new History("考虑采用枚举类", new Date(121, 1, 1, 12, 0, 0), new ControlMeasure(), BitmapFactory.decodeFile("drawable\\camera.jpg"));
        System.out.println(history.toString());
    }

}
