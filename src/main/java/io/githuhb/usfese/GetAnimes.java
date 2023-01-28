package io.githuhb.usfese;

import net.mamoe.mirai.console.plugin.jvm.JavaPlugin;
import net.mamoe.mirai.console.plugin.jvm.JvmPluginDescriptionBuilder;

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
    }
}