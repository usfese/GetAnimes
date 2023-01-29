package io.githuhb.usfese;

import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;
import com.google.gson.reflect.TypeToken;
import net.mamoe.mirai.event.events.MessageEvent;
import net.mamoe.mirai.message.data.Image;
import net.mamoe.mirai.message.data.MessageChain;
import net.mamoe.mirai.message.data.PlainText;
import net.mamoe.mirai.utils.ExternalResource;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
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
            connection.setRequestProperty("User-agent", "usfese/GetAnimes (https://github.com/usfese/GetAnimes)");
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

    public static class Bangumi {
        public class Weekday {
            String en;
            String cn;
            String ja;
            int id;
        }

        public class Item {
            int id;
            String url;
            int type;
            String name;
            String name_cn;
            String summary;
            String air_date;
            int air_weekday;
        }

        public Weekday weekday;
        public Item[] items;
    }

    public static byte[] readInputStream(InputStream inStream) throws Exception {
        ByteArrayOutputStream outStream = new ByteArrayOutputStream();
        //创建一个Buffer字符串
        byte[] buffer = new byte[1024];
        //每次读取的字符串长度，如果为-1，代表全部读取完毕
        int len = 0;
        //使用一个输入流从buffer里把数据读取出来
        while ((len = inStream.read(buffer)) != -1) {
            //用输出流往buffer里写入数据，中间参数代表从哪个位置开始读，len代表读取的长度
            outStream.write(buffer, 0, len);
        }
        //关闭输入流
        inStream.close();
        //把outStream里的数据写入内存
        return outStream.toByteArray();
    }

    public static class Subject {
        String date;
        String platform;

        class Images {
            String common;
        }

        Images images;
        String summary;
        String name;
        String name_cn;

        class Tag {
            String name;
            int count;
        }

        Tag[] tags;

        class Rating {
            float score;
        }

        Rating rating;
        int total_episodes;
        int id;
        String url;
    }

    public static void sendBgm(MessageEvent event) {
        Gson gson = new Gson();
        ArrayList<Bangumi> bgms = gson.fromJson(doGet("https://api.bgm.tv/calendar"), new TypeToken<List<Bangumi>>() {
        }.getType());
        Calendar now = Calendar.getInstance();
        int wkd = now.get(Calendar.DAY_OF_WEEK); //获取周几
        if (now.getFirstDayOfWeek() == Calendar.SUNDAY) { //一周第一天是否为星期天
            wkd = wkd - 1; //若一周第一天为星期天，则-1
            if (wkd == 0) {
                wkd = 7;
            }
        }
        for (int i = 0; i < bgms.size(); i++) {
            if (bgms.get(i).weekday.id == wkd) {
                event.getSubject().sendMessage(bgms.get(i).weekday.cn+"\n");
                for (int j = 0; j < bgms.get(i).items.length; j++) {
                    Bangumi.Item item = bgms.get(i).items[j];
                    event.getSubject().sendMessage("id:" + item.id + "\n" + item.name_cn + "\n" + item.name + "\n" + "放送时间:" + item.air_date);
                }

            }
        }
        event.getSubject().sendMessage("输入\"查询 [id]\"获取详细信息");
    }

    public static void sendInfo(MessageEvent event) {
        try {
            String id = event.getMessage().contentToString().split(" ")[1];
            Gson gson = new Gson();
            Subject sub = gson.fromJson(doGet("https://api.bgm.tv/v0/subjects/" + id), Subject.class);
            URL url = new URL(sub.images.common);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            //设置请求方式为"GET"
            conn.setRequestMethod("GET");
            //超时响应时间为5秒
            conn.setConnectTimeout(5 * 1000);
            //通过输入流获取图片数据
            InputStream inStream = conn.getInputStream();
            //得到图片的二进制数据，以二进制封装得到数据，具有通用性
            byte[] bytes = readInputStream(inStream);
            Image cover = event.getSubject().uploadImage(ExternalResource.create(bytes));
            sub.id = Integer.parseInt(id);
            MessageChain msc = new PlainText("id:" + id + "\n名称：" + sub.name + "\n" + sub.name_cn).plus(cover);
            event.getSubject().sendMessage(msc);
        } catch (JsonSyntaxException e) {
            event.getSubject().sendMessage("解析失败!");
        } catch (MalformedURLException e) {
            event.getSubject().sendMessage("获取失败!");
        } catch (IOException e) {
            throw new RuntimeException(e);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
