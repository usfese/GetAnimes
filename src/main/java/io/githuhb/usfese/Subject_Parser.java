package io.githuhb.usfese;

public class Subject_Parser {
    //用于解析条目JSON的条目类
    public class Subject {
        String date;
        String platform;

        class Images {
            String common;
        }

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

    public class Bangumi {
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

}
