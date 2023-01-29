package io.githuhb.usfese;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import net.mamoe.mirai.event.events.MessageEvent;
import net.mamoe.mirai.console.plugin.jvm.JavaPlugin;
import net.mamoe.mirai.console.plugin.jvm.JvmPluginDescriptionBuilder;
import net.mamoe.mirai.event.GlobalEventChannel;
import net.mamoe.mirai.event.Listener;
import net.mamoe.mirai.event.events.GroupMessageEvent;
import net.mamoe.mirai.event.events.MessageEvent;
import net.mamoe.mirai.message.data.MessageChain;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

public class AnimeGetter {
    public static String doGet(String httpUrl){
        HttpURLConnection connection = null;
        InputStream is = null;
        BufferedReader br = null;
        StringBuffer result = new StringBuffer();
        try {
            //创建连接
            URL url = new URL(httpUrl);
            connection = (HttpURLConnection) url.openConnection();
            //设置请求方式
            connection.setRequestMethod("GET");
            //设置连接超时时间
            connection.setReadTimeout(15000);
            //开始连接
            connection.connect();
            //获取响应数据
            if (connection.getResponseCode() == 200) {
                //获取返回的数据
                is = connection.getInputStream();
                if (null != is) {
                    br = new BufferedReader(new InputStreamReader(is, "UTF-8"));
                    String temp = null;
                    while (null != (temp = br.readLine())) {
                        result.append(temp);
                    }
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (null != br) {
                try {
                    br.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            if (null != is) {
                try {
                    is.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            //关闭远程连接
            connection.disconnect();
        }
        return result.toString();
    }
    public static class Bangumi{
        public class Weekday{ String en;String cn;String ja;int id;}
        public class Item { int id;String url;int type;String name;String name_cn;String summary;String air_date;int air_weekday;}
        public Weekday weekday;
        public Item[] items;
    }

    public static void sendBgm(MessageEvent event){
        Gson gson = new Gson();
        ArrayList<Bangumi> bgms = gson.fromJson(doGet("https://api.bgm.tv/calendar"), new TypeToken<List<Bangumi>>(){}.getType());
        Calendar now = Calendar.getInstance();
        int wkd = now.get(Calendar.DAY_OF_WEEK); //获取周几
        if(now.getFirstDayOfWeek() == Calendar.SUNDAY){ //一周第一天是否为星期天
            wkd = wkd - 1; //若一周第一天为星期天，则-1
            if(wkd == 0){
                wkd = 7;
            }
        }
        for(int i=0;i<bgms.size();i++){
            if(bgms.get(i).weekday.id == wkd){
                event.getSubject().sendMessage(bgms.get(i).weekday.cn+"\n");
                for(int j=0;j<bgms.get(i).items.length;j++){
                    Bangumi.Item item = bgms.get(i).items[j];
                    event.getSubject().sendMessage(String.valueOf(j+1) + ".\n" + item.name_cn + "\n" + item.name + "\n" + "放送时间:" + item.air_date);
                }

            }
        }
    }

}
