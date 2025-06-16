package com.nzoth.DigCoolDown.command;

import java.nio.file.Path;
import com.electronwill.nightconfig.core.io.WritingMode;
import com.electronwill.nightconfig.core.file.CommentedFileConfig;
import com.nzoth.DigCoolDown.config.CommonConfig;
import com.nzoth.DigCoolDown.DigCoolDown;
import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import net.minecraft.commands.Commands;
import net.minecraft.commands.CommandSourceStack;
import net.minecraftforge.event.RegisterCommandsEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

import static com.mojang.brigadier.arguments.IntegerArgumentType.integer;

@Mod.EventBusSubscriber(modid = DigCoolDown.MOD_ID, bus = Mod.EventBusSubscriber.Bus.FORGE)
public class DigCoolDownCommands {
    /**
    *在游戏中实现通过命令修改冷却值
    */
    private static final String PATH = (DigCoolDown.MOD_ID + "-common.toml");

    @SubscribeEvent
    public static void onRegisterCommands(RegisterCommandsEvent evt) {
        evt.getDispatcher().register(
                Commands.literal("digcd")
                        .then(createSetCommand())
                        .then(createChangeCommand("increase", true))
                        .then(createChangeCommand("decrease", false))
        );
    }

    private static LiteralArgumentBuilder<CommandSourceStack> createSetCommand() {
        return Commands.literal("set")
                .then(Commands.argument("value", integer(0))
                        .executes(ctx -> {
                            int v = IntegerArgumentType.getInteger(ctx, "value");
                            return applyAndPersist(v, ctx.getSource(), "已设置冷却为 %d 刻".formatted(v));
                        }));
    }

    private static LiteralArgumentBuilder<CommandSourceStack> createChangeCommand(String name, boolean inc) {
        return Commands.literal(name)
                .then(Commands.argument("delta", integer(0))
                        .executes(ctx -> {
                            int delta = IntegerArgumentType.getInteger(ctx, "delta");
                            // 读取当前值
                            int current = CommonConfig.globalBlockCooldown.get();
                            int updated = inc ? current + delta : current - delta;
                            if (updated < 0) updated = 0;
                            return applyAndPersist(updated, ctx.getSource(),
                                    "已%s冷却 %d 刻，当前值 %d 刻".formatted(
                                            inc ? "增加" : "减少", delta, updated));
                        }));
    }

    /**
     * 将新的冷却值写入到配置文件并刷新 SPEC，最后反馈消息
     */
    private static int applyAndPersist(int newValue, CommandSourceStack src, String successMsg) {
        try {
            Path configPath = DigCoolDown.CONFIG_DIR.resolve(PATH);
            // 加载或创建 TOML 配置
            CommentedFileConfig config = CommentedFileConfig.builder(configPath)
                    .sync()
                    .autosave()
                    .writingMode(WritingMode.REPLACE)
                    .build();
            config.load();

            // 写入新值到 “mining.globalBlockCooldown”
            config.set("mining.globalBlockCooldown", newValue);
            config.save();

            // 让 SPEC 重新消费配置，刷新内存中的值
            CommonConfig.globalBlockCooldown.set(newValue);

            src.sendSuccess(() -> net.minecraft.network.chat.Component.literal(
                         String.format(successMsg)), false);
        } catch (Exception e) {
            src.sendFailure(net.minecraft.network.chat.Component.literal(
                    "操作失败: " + e.getMessage()));
            return 0;
        }
        return 1;
    }
}
