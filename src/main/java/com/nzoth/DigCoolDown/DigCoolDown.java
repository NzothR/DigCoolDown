
package com.nzoth.DigCoolDown;

import com.nzoth.DigCoolDown.config.CommonConfig;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.ModLoadingContext;
import net.minecraftforge.fml.config.ModConfig;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.eventbus.api.IEventBus;

@Mod(DigCoolDown.MOD_ID)
public class DigCoolDown {
    public static final String MOD_ID = "digcooldown";
    public static final java.nio.file.Path CONFIG_DIR =
            net.minecraftforge.fml.loading.FMLPaths.CONFIGDIR.get();

    public DigCoolDown() {
        // 注册通用配置
        ModLoadingContext.get().registerConfig(
                ModConfig.Type.COMMON,
                CommonConfig.SPEC
        );

        // 注册事件与命令
        IEventBus forgeBus = MinecraftForge.EVENT_BUS;
        forgeBus.register(new ForgeEvents());
    }
}
