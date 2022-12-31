package com.dkmk100.arsomega.rituals;

import com.dkmk100.arsomega.blocks.PortalBlock;
import com.dkmk100.arsomega.blocks.PortalBlockEntity;
import com.dkmk100.arsomega.potions.ModPotions;
import com.dkmk100.arsomega.util.RegistryHandler;
import com.hollingsworth.arsnouveau.api.entity.IDispellable;
import com.hollingsworth.arsnouveau.api.event.DispelEvent;
import com.hollingsworth.arsnouveau.api.ritual.AbstractRitual;
import com.hollingsworth.arsnouveau.api.spell.Spell;
import com.hollingsworth.arsnouveau.api.spell.SpellContext;
import com.hollingsworth.arsnouveau.client.particle.ParticleLineData;
import com.hollingsworth.arsnouveau.client.particle.ParticleUtil;
import net.minecraft.core.BlockPos;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.common.MinecraftForge;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class RitualDispel extends AbstractRitual {

    @Override
    protected void tick() {
        Level world = this.getWorld();
        if (world.isClientSide) {
            BlockPos pos = this.getPos();

            for (int i = 0; i < 100; ++i) {
                Vec3 particlePos = (new Vec3((double) pos.getX(), (double) pos.getY(), (double) pos.getZ())).add(0.5D, 0.0D, 0.5D);
                particlePos = particlePos.add(ParticleUtil.pointInSphere().multiply(5.0D, 5.0D, 5.0D));
                world.addParticle(ParticleLineData.createData(this.getCenterColor()), particlePos.x(), particlePos.y(), particlePos.z(), (double) pos.getX() + 0.5D, (double) (pos.getY() + 1), (double) pos.getZ() + 0.5D);
            }
        }

        if (!world.isClientSide && world.getGameTime() % 20L == 0L) {
            if(this.needsSourceNow()){
                return;
            }
            else{
                this.setNeedsSource(true);
            }
            this.incrementProgress();
            if (this.getProgress() > 5) {

                int sideRange = 5;
                int upRange = 7;
                int downRange = 2;
                for(int x = -1 * sideRange;x<sideRange;x++) {
                    for (int z = -1 * sideRange; z < sideRange; z++) {
                        for (int y = -1 * downRange; y < upRange; y++) {
                            BlockPos pos = this.getPos().offset(x, y, z);
                            if (world.isInWorldBounds(pos)) {
                                if (world.getBlockState(pos).getBlock() instanceof PortalBlock) {
                                    world.setBlockAndUpdate(pos, Blocks.AIR.defaultBlockState());
                                }
                            }
                        }
                    }
                }

                List<LivingEntity> entities = this.getWorld().getEntitiesOfClass(LivingEntity.class, (new AABB(this.getPos().above(upRange-downRange))).inflate(sideRange*2 + 1, downRange + upRange,sideRange*2 + 1));
                SpellContext context = new SpellContext(new Spell(),null);
                for (LivingEntity entity : entities) {
                    if(entity.hasEffect(ModPotions.DISPELLANT)){
                        continue;
                    }
                    //dispel code
                    Collection<MobEffectInstance> effects = entity.getActiveEffects();
                    MobEffectInstance[] array = effects.toArray(new MobEffectInstance[0]);
                    if (MinecraftForge.EVENT_BUS.post(new DispelEvent(new EntityHitResult(entity), world, null, new ArrayList<>(), context))) {
                        continue;
                    }

                    MobEffectInstance[] var9 = array;
                    int var10 = array.length;

                    for(int var11 = 0; var11 < var10; ++var11) {
                        MobEffectInstance e = var9[var11];
                        if (e.isCurativeItem(new ItemStack(Items.MILK_BUCKET))) {
                            entity.removeEffect(e.getEffect());
                        }
                    }

                    if (entity instanceof IDispellable && entity.isAlive() && entity.getHealth() > 0.0F && !entity.isRemoved()) {
                        ((IDispellable)entity).onDispel(null);
                    }
                }

                this.setFinished();
            }
        }
    }

    @Override
    public int getSourceCost() {
        return 10;
    }

    @Override
    public String getID() {
        return "dispel";
    }
}
