package com.nzoth.DigCoolDown;

import com.nzoth.DigCoolDown.config.CommonConfig;
import net.minecraftforge.common.ForgeHooks;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.eventbus.api.EventPriority;
import net.minecraftforge.eventbus.api.SubscribeEvent;

public class ForgeEvents {
    /**
     * 限制挖掘速度，使每个方块至少需要 globalBlockCooldown tick
     */
    @SubscribeEvent(priority = EventPriority.HIGHEST)
    public void breakSpeed(PlayerEvent.BreakSpeed event) {
        // 全局冷却（tick/块），配置中设为 >= 0
        int globalCooldown = CommonConfig.globalBlockCooldown.get();
        if (globalCooldown < 0) {
            // 如果没配置或配置不合理，则不限制
            return;
        }

        // 计算最大允许速度 (方块/秒)，保证每 globalCooldown tick 最多挖 1 格
        // Minecraft 每秒 20 tick
        float maxAllowedSpeed = 20f / globalCooldown;

        // 如果当前速度超过最大允许速度，就强制使用 maxAllowedSpeed
        if (event.getNewSpeed() > maxAllowedSpeed) {
            event.setNewSpeed(maxAllowedSpeed);
        }
    }
}


