package com.dkmk100.arsomega.client.staff;

import com.dkmk100.arsomega.ArsOmega;
import com.dkmk100.arsomega.items.ModularStaff;
import com.dkmk100.arsomega.util.IStaffModel;
import net.minecraft.world.item.ItemStack;
import org.apache.commons.lang3.tuple.Pair;
import software.bernie.ars_nouveau.geckolib3.core.AnimationState;
import software.bernie.ars_nouveau.geckolib3.core.IAnimatableModel;
import software.bernie.ars_nouveau.geckolib3.core.PlayState;
import software.bernie.ars_nouveau.geckolib3.core.builder.Animation;
import software.bernie.ars_nouveau.geckolib3.core.builder.AnimationBuilder;
import software.bernie.ars_nouveau.geckolib3.core.builder.ILoopType;
import software.bernie.ars_nouveau.geckolib3.core.controller.AnimationController;
import software.bernie.ars_nouveau.geckolib3.core.event.predicate.AnimationEvent;
import software.bernie.ars_nouveau.geckolib3.core.keyframe.*;
import software.bernie.ars_nouveau.geckolib3.core.molang.MolangParser;
import software.bernie.ars_nouveau.geckolib3.core.processor.IBone;
import software.bernie.ars_nouveau.geckolib3.core.snapshot.BoneSnapshot;
import software.bernie.ars_nouveau.geckolib3.core.util.Axis;
import software.bernie.ars_nouveau.shadowed.eliotlash.mclib.math.IValue;

import java.util.*;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.stream.Collectors;

public class StaffAnimationController<T extends ModularStaff> extends AnimationController<T> {
    boolean justStopped = false;
    ModularStaff.StaffModelPart myPart;

    public StaffAnimationController(T animatable, String name, float transitionLengthTicks, IAnimationPredicate<T> animationPredicate, ModularStaff.StaffModelPart part) {
        super(animatable,name,transitionLengthTicks,animationPredicate);
        myPart = part;
    }

    public void setAnimation(AnimationBuilder builder, AnimationEvent event){
        List<Object> data = event.getExtraData();
        ItemStack stack = (ItemStack) data.get(0);
        ModularStaff item = (ModularStaff) data.get(1);
        IStaffModel model = (IStaffModel) data.get(2);
        ModularStaff.StaffModelPart part = (ModularStaff.StaffModelPart) data.get(3);
        this.setAnimation(builder,model,part,item,stack);
    }

    public static List<Object> getExtraData(ItemStack stack, ModularStaff item, IStaffModel model, ModularStaff.StaffModelPart part){
        List<Object> data = new ArrayList<>();
        data.add(stack);
        data.add(item);
        data.add(model);
        data.add(part);
        return data;
    }

    public void setAnimation(AnimationBuilder builder, IStaffModel model, ModularStaff.StaffModelPart part, ModularStaff item, ItemStack stack) {
        if (part != myPart) {
            return;
        }
        if (builder != null && builder.getRawAnimationList().size() != 0) {
            if (!builder.getRawAnimationList().equals(this.currentAnimationBuilder.getRawAnimationList()) || this.needsAnimationReload) {
                AtomicBoolean encounteredError = new AtomicBoolean(false);
                LinkedList<Animation> animations = builder.getRawAnimationList().stream().map((rawAnimation) -> {
                    Animation animation = model.getAnimation(rawAnimation.animationName, item, stack, part);

                    if (animation == null) {
                        encounteredError.set(true);
                    }

                    if (animation != null && rawAnimation.loopType != null) {
                        animation.loop = rawAnimation.loopType;


                    }

                    return animation;
                }).collect(Collectors.toCollection(LinkedList::new));
                if (encounteredError.get()) {
                    return;
                }

                this.animationQueue = animations;
                this.currentAnimationBuilder = builder;
                this.shouldResetTick = true;
                this.animationState = AnimationState.Transitioning;
                this.justStartedTransition = true;
                this.needsAnimationReload = false;
            }
        } else {
            this.animationState = AnimationState.Stopped;
        }
    }

    @Override
    public void process(double tick, AnimationEvent<T> event, List<IBone> modelRendererList, Map<String, Pair<IBone, BoneSnapshot>> boneSnapshotCollection, MolangParser parser, boolean crashWhenCantFindBone) {
        var data = event.getExtraData();
        ModularStaff.StaffModelPart part = (ModularStaff.StaffModelPart) data.get(3);
        if(part != myPart){
            return;
        }
        super.process(tick,event,modelRendererList,boneSnapshotCollection,parser,crashWhenCantFindBone);
    }
}
