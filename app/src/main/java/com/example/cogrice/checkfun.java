package com.example.cogrice;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.TextView;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class checkfun extends AppCompatActivity {

//    public static  boolean checktel(String value){
//        String pattern = "^[1]([3-9])[0-9]{9}$";
//        Pattern r = Pattern.compile(pattern);
//        Matcher m = r.matcher(value);
//        return m.matches();
//    }
    /**
     * 判断手机号码是否合理
     *
     */
    public static boolean checktel(String phoneNums) {
        if (isMatchLength(phoneNums, 11)
                && isMobileNO(phoneNums)) {
            return true;
        }
        return false;
    }

    /**
     * 判断一个字符串的位数
     */
    public static boolean isMatchLength(String str, int length) {
        if (str.isEmpty()) {
            return false;
        } else {
            return str.length() == length ? true : false;
        }
    }

    /**
     * 验证手机格式
     */
    public static boolean isMobileNO(String mobileNums) {
        /*
         * 移动：134、135、136、137、138、139、150、151、157(TD)、158、159、187、188
         * 联通：130、131、132、152、155、156、185、186 电信：133、153、180、189、（1349卫通）
         * 总结起来就是第一位必定为1，第二位必定为3或5或8，其他位置的可以为0-9
         */
        String telRegex = "[1][358]\\d{9}";// "[1]"代表第1位为数字1，"[358]"代表第二位可以为3、5、8中的一个，"\\d{9}"代表后面是可以是0～9的数字，有9位。
        if (TextUtils.isEmpty(mobileNums))
            return false;
        else
            return mobileNums.matches(telRegex);
    }


    public static Boolean checkEmail(String str) {
        Boolean isEmail = false;
        String expr = "\\w+[\\w]*@[\\w]+\\.[\\w]+$";

        if (str.matches(expr)) {
            isEmail = true;
        }
        return isEmail;
    }

    private static boolean isNumeric(String str)
    {
        Pattern pattern = Pattern.compile("[0-9]*");
        Matcher isNum = pattern.matcher(str);
        return isNum.matches();
    }

    public static boolean isDate(String strDate)
    {
        Pattern pattern = Pattern.compile("^((\\d{2}(([02468][048])|([13579][26]))[\\-\\/\\s]?((((0?[13578])|(1[02]))[\\-\\/\\s]?" +
                "((0?[1-9])|([1-2][0-9])|(3[01])))|(((0?[469])|(11))[\\-\\/\\s]?((0?[1-9])|([1-2][0-9])|(30)))|(0?2[\\-\\/\\s]?((0?[1-9])|([1-2][0-9])))))" +
                "|(\\d{2}(([02468][1235679])|([13579][01345789]))[\\-\\/\\s]?((((0?[13578])|(1[02]))[\\-\\/\\s]?((0?[1-9])|" +
                "([1-2][0-9])|(3[01])))|(((0?[469])|(11))[\\-\\/\\s]?((0?[1-9])|([1-2][0-9])|(30)))|(0?2[\\-\\/\\s]?((0?[1-9])|" +
                "(1[0-9])|(2[0-8]))))))(\\s(((0?[0-9])|([1-2][0-3]))\\:([0-5]?[0-9])((\\s)|(\\:([0-5]?[0-9])))))?$");
        Matcher m = pattern.matcher(strDate);
        return m.matches();
    }



    public static boolean checkIdcard(String IDStr)
    {
        String[] ValCodeArr = {"1", "0", "X", "9", "8", "7", "6", "5", "4", "3", "2"};
        String[] Wi = {"7", "9", "10", "5", "8", "4", "2", "1", "6", "3", "7", "9", "10", "5", "8", "4", "2"};
        String Ai = "";
        if (IDStr.length() != 18)
        {
            return false;
        }
        if (IDStr.length() == 18)
        {
            Ai = IDStr.substring(0, 17);
        }
        if (isNumeric(Ai) == false)
        {
            return false;
        }
        String strYear = Ai.substring(6, 10);// 年份
        String strMonth = Ai.substring(10, 12);// 月份
        String strDay = Ai.substring(12, 14);// 日


        if (isDate(strYear + "-" + strMonth + "-" + strDay) == false)
        {
//          errorInfo = "身份证生日无效。";
            return false;
        }
        GregorianCalendar gc = new GregorianCalendar();
        SimpleDateFormat s = new SimpleDateFormat("yyyy-MM-dd");
        try
        {
            if ((gc.get(Calendar.YEAR) - Integer.parseInt(strYear)) > 150 || (gc.getTime().getTime() - s.parse(strYear + "-" + strMonth + "-" + strDay).getTime()) < 0)
            {
                //errorInfo = "身份证生日不在有效范围。";
                return false;
            }
        } catch (NumberFormatException e)
        {
            e.printStackTrace();
        } catch (java.text.ParseException e)
        {
            e.printStackTrace();
        }
        if (Integer.parseInt(strMonth) > 12 || Integer.parseInt(strMonth) == 0)
        {
            //errorInfo = "身份证月份无效";
            return false;
        }
        if (Integer.parseInt(strDay) > 31 || Integer.parseInt(strDay) == 0)
        {
            //errorInfo = "身份证日期无效";
            return false;
        }
        // ================ 判断最后一位的值 ================
        int TotalmulAiWi = 0;
        for (int i = 0; i < 17; i++)
        {
            TotalmulAiWi = TotalmulAiWi + Integer.parseInt(String.valueOf(Ai.charAt(i))) * Integer.parseInt(Wi[i]);
        }
        int modValue = TotalmulAiWi % 11;
        String strVerifyCode = ValCodeArr[modValue];
        Ai = Ai + strVerifyCode;

        if (IDStr.length() == 18)
        {
            if (Ai.equals(IDStr) == false)
            {
                //errorInfo = "身份证无效，不是合法的身份证号码";
                return false;
            }
        } else
        {
            return true;
        }
        return true;
    }

    public static boolean check_isnull(String value) {
        if(value.equals(""))
            return true;
        else
            return false;
    }
    public static void nullerrorlistener(final TextView editText){
        editText.setOnFocusChangeListener(new View.OnFocusChangeListener(){
            public void onFocusChange(View arg0, boolean hasFocus) {
                if (hasFocus && editText.getText().toString().trim().length() == 0) {
                    editText.setError("该数据不能为空！");
                }
            }
        });
    }
    public static void iderrorlistener(final TextView editText){
        editText.setOnFocusChangeListener(new View.OnFocusChangeListener(){
            public void onFocusChange(View arg0, boolean hasFocus) {
                if (hasFocus && !checkIdcard(editText.getText().toString().trim())) {
                    editText.setError("身份证格式有误！");
                }
            }
        });
    }
    public static void Emailerrorlistener(final TextView editText){
        editText.setOnFocusChangeListener(new View.OnFocusChangeListener(){
            public void onFocusChange(View arg0, boolean hasFocus) {
                if (hasFocus && !checkEmail(editText.getText().toString().trim())) {
                    editText.setError("邮箱格式有误！");
                }
            }
        });
    }
}