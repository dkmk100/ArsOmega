package com.dkmk100.arsomega.rituals;

import com.dkmk100.arsomega.ArsRegistry;
import com.dkmk100.arsomega.util.RegistryHandler;
import com.hollingsworth.arsnouveau.api.ANFakePlayer;
import com.hollingsworth.arsnouveau.api.ritual.AbstractRitual;
import com.hollingsworth.arsnouveau.api.spell.EntitySpellResolver;
import com.hollingsworth.arsnouveau.api.spell.Spell;
import com.hollingsworth.arsnouveau.api.spell.SpellContext;
import com.hollingsworth.arsnouveau.api.spell.SpellResolver;
import com.hollingsworth.arsnouveau.api.util.ManaUtil;
import com.hollingsworth.arsnouveau.api.util.SourceUtil;
import com.hollingsworth.arsnouveau.client.particle.ParticleColor;
import com.hollingsworth.arsnouveau.client.particle.ParticleLineData;
import com.hollingsworth.arsnouveau.client.particle.ParticleUtil;
import com.hollingsworth.arsnouveau.common.block.tile.RuneTile;
import com.hollingsworth.arsnouveau.setup.BlockRegistry;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.core.Direction;
import net.minecraft.world.phys.AABB;
import net.minecraft.core.BlockPos;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.level.Level;
import net.minecraft.server.level.ServerLevel;
import net.minecraftforge.common.util.FakePlayer;

import java.util.ArrayList;
import java.util.List;

public class RitualAura extends AbstractRitual {
    @Override
    public String getID() {
        return "aura";
    }
    @Override
    protected void tick() {
        Level world = this.getWorld();
        if (world.isClientSide) {
            BlockPos pos = this.getPos();

            for (int i = 0; i < 100; ++i) {
                Vec3 particlePos = (new Vec3((double) pos.getX(), (double) pos.getY(), (double) pos.getZ())).add(0.5D, 0.0D, 0.5D);
                particlePos = particlePos.add(ParticleUtil.pointInSphere().multiply(3.0D, 3.0D, 3.0D));
                world.addParticle(ParticleLineData.createData(this.getCenterColor()), particlePos.x(), particlePos.y(), particlePos.z(), (double) pos.getX() + 0.5D, (double) (pos.getY() + 1), (double) pos.getZ() + 0.5D);
            }
        }

        if (!world.isClientSide && world.getGameTime() % 20L == 0L) {
            this.incrementProgress();
            if (this.getProgress() % 8 == 0) {
                boolean sensitive = false;
                boolean extract = false;
                int discount = 0;
                int cap = 10;
                List<ItemStack> items = this.getConsumedItems();
                int aoe = 0;
                //if lag becomes an issue I can always make a static ritual manager to save this data like flight does, but that seems difficult
                for(ItemStack stack : items){
                    if(stack.getItem() == ArsRegistry.GLYPH_AOE) {
                        if (stack.getCount() <= 0) {
                            aoe += 1;
                        } else {
                            aoe += stack.getCount();
                        }
                    }
                    else if(stack.getItem() == ArsRegistry.GLYPH_SENSITIVE){
                        sensitive = true;
                    }
                    else if(stack.getItem() == ArsRegistry.GLYPH_EXTRACT){
                        extract = true;
                    }
                }

                BlockPos pos = getPos();
                FakePlayer fakePlayer = ANFakePlayer.getPlayer((ServerLevel)world);
                fakePlayer.setPos((double)pos.getX(), (double)pos.getY(), (double)pos.getZ());
                List<Spell> spells = new ArrayList<Spell>();
                for(Direction dir : Direction.values()){
                    if(dir==Direction.DOWN){
                        BlockPos pos2 = pos.relative(dir, 1);
                        Block block = world.getBlockState(pos2).getBlock();
                        if(block == Blocks.DIAMOND_BLOCK){
                            cap+=2;
                            aoe +=2;
                            discount +=15;
                        }
                        else if(block == Blocks.IRON_BLOCK){
                            cap+=2;
                        }
                        else if(block == Blocks.GOLD_BLOCK){
                            cap+=2;
                            aoe+=1;
                            discount+=7;
                        }
                        else if(block == Blocks.NETHERITE_BLOCK){
                            cap+=5;
                            aoe+=2;
                            discount+=30;
                        }
                        else if(block == RegistryHandler.GORGON_STONE.get()){
                            cap+=12;
                            aoe-=2;
                            discount-=30;
                        }

                    }
                    else if(dir==Direction.UP){
                        BlockPos pos2 = pos.relative(dir, 1);
                        Block block = world.getBlockState(pos2).getBlock();
                        if(block == Blocks.BEACON){
                            cap+=5;
                            aoe +=15;
                        }
                    }
                    else {
                        BlockPos pos2 = pos.relative(dir, 1);
                        if(world.getBlockState(pos2).getBlock() == BlockRegistry.RUNE_BLOCK){
                            RuneTile tile = (RuneTile) world.getBlockEntity(pos2);
                            if(tile.spell!=null){
                                spells.add(tile.spell);
                            }
                        }
                    }
                }
                aoe = Math.min(aoe,cap);//cap AOE

                //by having total cost, we only search for source jars once and thus everythign works much better.
                //initial value is extra/min cost
                int totalCost = 3;
                for(Spell spell : spells){
                    totalCost+= Math.max(spell.getCastingCost() - discount, 0);//discount is per-spell, but can never take it below 0
                }
                if(SourceUtil.takeSourceNearbyWithParticles(pos, world, 6, totalCost) != null) {
                    List<LivingEntity> entities = this.getWorld().getEntitiesOfClass(LivingEntity.class, (new AABB(this.getPos())).inflate(10.0D + aoe * 2).inflate(7, 0, 7));
                    for (LivingEntity entity : entities) {
                        boolean player = entity instanceof Player;
                        if ((!player && extract) || (!extract && (player || !sensitive))) {
                            for (Spell spell : spells) {
                                EntitySpellResolver resolver = new EntitySpellResolver((new SpellContext(spell, fakePlayer)).withCastingTile(world.getBlockEntity(pos)).withType(SpellContext.CasterType.TURRET).withColors(new ParticleColor.IntWrapper(255, 255, 255)));
                                resolver.onCastOnEntity(entity);
                            }
                        }
                    }
                }
            }
        }
    }

    @Override
    public ParticleColor getCenterColor() {
        return new ParticleColor(255,255,230);
    }
    @Override
    public boolean canConsumeItem(ItemStack stack) {
        return stack.getItem() == ArsRegistry.GLYPH_AOE || stack.getItem() == ArsRegistry.GLYPH_SENSITIVE || stack.getItem() == ArsRegistry.GLYPH_EXTRACT;
    }
}
