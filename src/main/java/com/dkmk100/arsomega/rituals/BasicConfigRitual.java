package com.dkmk100.arsomega.rituals;

import com.dkmk100.arsomega.ArsOmega;
import com.hollingsworth.arsnouveau.api.ArsNouveauAPI;
import com.hollingsworth.arsnouveau.api.ritual.AbstractRitual;
import com.hollingsworth.arsnouveau.common.util.PortUtil;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.phys.AABB;
import net.minecraftforge.common.ForgeConfigSpec;

import java.util.List;

public abstract class BasicConfigRitual extends AbstractRitual implements ConfigurableRitual{

    public BasicConfigRitual(){
        ArsOmega.LOGGER.info("basic config ritual constructor called! ");
        AbstractRitual ritual = ArsNouveauAPI.getInstance().getRitualMap().get(this.getID());
        if(ritual!=null && ritual instanceof BasicConfigRitual){
            ArsOmega.LOGGER.info("copying config!");
            BasicConfigRitual config = (BasicConfigRitual) ritual;
            this.ENABLED = config.ENABLED;
            this.DURATION = config.DURATION;
            this.RANGE = config.RANGE;
        }
        else{
            ArsOmega.LOGGER.info("no config to copy, hopefully this is registration");
        }
    }

    protected ForgeConfigSpec.BooleanValue ENABLED;
    protected ForgeConfigSpec.IntValue DURATION;

    protected ForgeConfigSpec.DoubleValue RANGE;

    @Override
    public void tryTick() {
        if (this.tile != null && this.getContext().isStarted && !this.getContext().isDone) {
            if(this.ConfirmEnabled()) {
                this.tick();
            }
            else{
                this.tile.ritual = null;

                this.setFinished();
            }
        }
    }

    @Override
    public void buildConfig(ForgeConfigSpec.Builder builder) {
        ArsOmega.LOGGER.info("building config for ritual: "+getID());
        builder.comment("Can be used to disable the ritual globally.");
        ENABLED = builder.define("enabled",true);
        int dur = getDefaultDuration();
        double ran = getDefaultRange();
        if(dur > 0) {
            builder.comment("Can be used to change the duration of the ritual, higher values take longer.");
            builder.comment("Default value: "+dur);
            DURATION = builder.defineInRange("duration", dur, 0, 100);
        }
        else{
            ArsOmega.LOGGER.info("ritual "+getID()+" does not define duration.");
        }
        if(ran > 0){
            builder.comment("Can be used to change the range of the ritual, in blocks away from the brazier");
            builder.comment("Default range: "+ran);
            RANGE = builder.defineInRange("range", ran, 0, 100);
        }
        else{
            ArsOmega.LOGGER.info("ritual "+getID()+" does not define range.");
        }
        buildExtraConfig(builder);
    }

    private boolean ConfirmEnabled(){
        ArsOmega.LOGGER.info("ritual: "+getID());
        ArsOmega.LOGGER.info("ritual enabled? "+ENABLED.get());
        if(!ENABLED.get()){
            double range = getDefaultRange() > 0 ? RANGE.get() : 5.0;
            List<Player> players = this.getWorld().getEntitiesOfClass(Player.class, (new AABB(this.getPos())).inflate(range*2));
            for(Player player : players){
                PortUtil.sendMessage(player,"this ritual is disabled in the config");
            }
            this.setFinished();
            return false;
        }
        return true;
    }

    protected void buildExtraConfig(ForgeConfigSpec.Builder builder){
        ArsOmega.LOGGER.info("ritual "+getID()+" does not define extra config. ");
    }

    protected int getDefaultDuration(){
        return -1;
    }
    protected double getDefaultRange(){
        return -1;
    }
}
