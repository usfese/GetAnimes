package io.githuhb.usfese;

import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;
import com.google.gson.reflect.TypeToken;
import io.githuhb.usfese.Subject_Parser.Bangumi;
import io.githuhb.usfese.Subject_Parser.Subject;
import net.mamoe.mirai.event.events.MessageEvent;
import net.mamoe.mirai.message.data.PlainText;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import static io.githuhb.usfese.HttpGetter.doGet;

public class AnimeGetter {

    //查询今日新番
    public static void sendBgm(MessageEvent event) {
        Gson gson = new Gson();
        ArrayList<Bangumi> bgms = null;
        try {
            bgms = gson.fromJson(doGet("https://api.bgm.tv/calendar"), new TypeToken<List<Bangumi>>() {
            }.getType());
        } catch (IOException e) {
            event.getSubject().sendMessage("I/O失败!");
        }
        Calendar c = Calendar.getInstance();
        c.setTime(new Date());
        int weekday = c.get(Calendar.DAY_OF_WEEK);
        if (weekday == 1) {
            weekday = 7;
        } else {
            weekday--;
        }
        for (int i = 0; i < bgms.size(); i++) {
            if (bgms.get(i).weekday.id == weekday) {
                event.getSubject().sendMessage(bgms.get(i).weekday.cn + "\n");
                for (int j = 0; j < bgms.get(i).items.length; j++) {
                    Bangumi.Item item = bgms.get(i).items[j];
                    event.getSubject().sendMessage("id:" + item.id + "\n" + item.name_cn + (item.name_cn.equals("") ? "" : "\n") + item.name + "\n" + "放送时间:" + item.air_date);
                }

            }
        }
        event.getSubject().sendMessage("输入\"查询 [id]\"获取详细信息");
    }

    //查询详细信息
    public static void sendInfo(MessageEvent event) {
        try {
            String id = event.getMessage().contentToString().split(" ")[1];
            Gson gson = new Gson();
            Subject sub = gson.fromJson(doGet("https://api.bgm.tv/v0/subjects/" + id), Subject.class);
//            URL url = new URL(sub.images.common);
//            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
//            //设置请求方式为"GET"
//            conn.setRequestMethod("GET");
//            //超时响应时间为5秒
//            conn.setConnectTimeout(5 * 1000);
//            //通过输入流获取图片数据
//            InputStream inStream = conn.getInputStream();
//            //得到图片的二进制数据，以二进制封装得到数据，具有通用性
//            byte[] bytes = readInputStream(inStream);
//            Image cover = event.getSubject().uploadImage(ExternalResource.create(bytes));
            sub.id = Integer.parseInt(id);
            //发送消息链
            event.getSubject().sendMessage(new PlainText("id:" + id + "\n名称：" + sub.name + "\n" + sub.name_cn + (sub.name_cn.equals("") ? "" : "\n")).plus("简介：" + sub.summary));
            StringBuilder sb = new StringBuilder("标签：");
            for (int i = 0; i < sub.tags.length; i++) {
                sb.append(sub.tags[i].name);
                sb.append(" ");
            }
            sb.deleteCharAt(sb.length() - 1);
            event.getSubject().sendMessage(sb.toString());
        } catch (JsonSyntaxException e) {
            event.getSubject().sendMessage("解析失败!");
//        } catch (MalformedURLException e) {
//            event.getSubject().sendMessage("获取失败!");
        } catch (IOException e) {
            event.getSubject().sendMessage("I/O失败!");
        } catch (Exception e) {
            event.getSubject().sendMessage("失败!");
        }
    }
}
