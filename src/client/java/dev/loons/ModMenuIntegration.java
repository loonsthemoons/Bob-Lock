package dev.loons;

import com.terraformersmc.modmenu.api.ConfigScreenFactory;
import com.terraformersmc.modmenu.api.ModMenuApi;
import me.shedaniel.clothconfig2.api.ConfigBuilder;
import me.shedaniel.clothconfig2.api.ConfigCategory;
import me.shedaniel.clothconfig2.api.ConfigEntryBuilder;
import net.minecraft.network.chat.Component;

public class ModMenuIntegration implements ModMenuApi {
        @Override
        public ConfigScreenFactory<?> getModConfigScreenFactory() {
                return parent -> {
                        ConfigBuilder builder = ConfigBuilder.create()
                                        .setParentScreen(parent)
                                        .setTitle(Component.literal("Bob Lock Configuration"));

                        BobLockConfig config = BobLockConfig.getInstance();
                        ConfigEntryBuilder entryBuilder = builder.entryBuilder();

                        ConfigCategory general = builder.getOrCreateCategory(Component.literal("General"));

                        general.addEntry(entryBuilder.startBooleanToggle(Component.literal("Enabled"), config.enabled)
                                        .setDefaultValue(true)
                                        .setSaveConsumer(newValue -> config.enabled = newValue)
                                        .build());

                        general.addEntry(
                                        entryBuilder.startIntSlider(Component.literal("Bobbing Depth (%)"),
                                                        config.depthPercent, 0, 200)
                                                        .setDefaultValue(100)
                                                        .setTooltip(Component.literal("100% is the recommended depth."))
                                                        .setSaveConsumer(newValue -> config.depthPercent = newValue)
                                                        .build());

                        general.addEntry(entryBuilder
                                        .startIntSlider(Component.literal("Smoothness"), config.smoothness, 1, 100)
                                        .setDefaultValue(30)
                                        .setTooltip(Component.literal("Lower values = smoother/slower transition."))
                                        .setSaveConsumer(newValue -> config.smoothness = newValue)
                                        .build());

                        general.addEntry(
                                        entryBuilder.startIntSlider(Component.literal("Return Delay (ms)"),
                                                        config.delayMs, 0, 1000)
                                                        .setDefaultValue(90)
                                                        .setTooltip(Component.literal(
                                                                        "How long to wait before the hand returns."))
                                                        .setSaveConsumer(newValue -> config.delayMs = newValue)
                                                        .build());

                        builder.setSavingRunnable(config::save);
                        return builder.build();
                };
        }
}
