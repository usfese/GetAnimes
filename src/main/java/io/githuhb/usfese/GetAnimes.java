package io.githuhb.usfese;

import net.mamoe.mirai.console.plugin.jvm.JavaPlugin;
import net.mamoe.mirai.console.plugin.jvm.JvmPluginDescriptionBuilder;
import net.mamoe.mirai.event.GlobalEventChannel;
import net.mamoe.mirai.event.Listener;
import net.mamoe.mirai.event.events.MessageEvent;

import java.util.regex.Pattern;

public final class GetAnimes extends JavaPlugin {
    public static final GetAnimes INSTANCE = new GetAnimes();

    private GetAnimes() {
        super(new JvmPluginDescriptionBuilder("io.githuhb.usfese.getanimes", "0.1.0")
                .name("Get Animes")
                .info("从Bangumi获取今日新番")
                .author("usfese")
                .build());
    }

    @Override
    public void onEnable() {
        getLogger().info("Plugin loaded!");
        Listener listener= GlobalEventChannel.INSTANCE.subscribeAlways(MessageEvent.class, event->{
            if (event.getMessage().contentToString().equals("今日新番")) {
                AnimeGetter.sendBgm(event);
            }
            if (Pattern.matches("^查询 \\d{1,}$", event.getMessage().contentToString())) AnimeGetter.sendBgm(event);
        });
    }
}