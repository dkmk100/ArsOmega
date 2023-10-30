package com.dkmk100.arsomega.util;

import com.hollingsworth.arsnouveau.common.items.ExperienceGem;
import com.hollingsworth.arsnouveau.setup.ItemsRegistry;
import com.mojang.datafixers.util.Pair;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.util.Mth;
import net.minecraft.world.item.ItemStack;

public class ExperienceUtil {

    public static int getExperienceValue(ItemStack stack){
        if(stack.getItem() instanceof ExperienceGem gem){
            return gem.getValue();
        }
        return 0;
    }

    public static void spendExperience(ServerPlayer player, int points){
        player.giveExperiencePoints(-1 * points);
    }

    public static int getExperiencePoints(ServerPlayer player) {
        return Mth.floor(getExperienceForLevel(player.experienceLevel) + player.experienceProgress * player.getXpNeededForNextLevel());
    }

    //taken from ars's scribes table
    //not that I can think of another way to do this, it's just the simplified closed form solution anyway
    public static int getLevelsFromExp(int exp) {
        if (exp <= 352) {
            return (int) (Math.sqrt(exp + 9) - 3);
        } else if (exp <= 1507) {
            return (int) (8.1 + Math.sqrt(0.4 * (exp - 195.975)));
        }
        return (int) (18.056 + Math.sqrt(0.222 * (exp - 752.986)));
    }

    //taken from ars's scribes table
    //not that I can think of another way to do this, it's just the simplified closed form solution anyway
    public static int getExperienceForLevel(int level) {
        if (level == 0)
            return 0;
        if (level > 0 && level < 17)
            return (int) (Math.pow(level, 2) + 6 * level);
        else if (level > 16 && level < 32)
            return (int) (2.5 * Math.pow(level, 2) - 40.5 * level + 360);
        else
            return (int) (4.5 * Math.pow(level, 2) - 162.5 * level + 2220);
    }


}
