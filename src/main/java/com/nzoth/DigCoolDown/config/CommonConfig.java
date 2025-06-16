package com.nzoth.DigCoolDown.config;

import net.minecraftforge.common.ForgeConfigSpec;

public class CommonConfig {
    /**
    *配置文件设置
    */
    public static ForgeConfigSpec.IntValue globalBlockCooldown;

    public static final ForgeConfigSpec SPEC;

    static {
        ForgeConfigSpec.Builder builder = new ForgeConfigSpec.Builder();

        globalBlockCooldown = builder
                .comment("破坏两个方块之间的全局冷却，单位刻（ticks），0 表示无冷却，大于0表示挖掘方块至少为设定值")
                .defineInRange("globalBlockCooldown", 5, 0, 200);

        SPEC = builder.build();
    }
}
