package com.dkmk100.arsomega.items;

import com.dkmk100.arsomega.client.staff.StaffAnimationController;
import com.dkmk100.arsomega.entities.EntityDemonRay;
import net.minecraft.world.item.Item;
import software.bernie.ars_nouveau.geckolib3.core.IAnimatable;
import software.bernie.ars_nouveau.geckolib3.core.PlayState;
import software.bernie.ars_nouveau.geckolib3.core.builder.AnimationBuilder;
import software.bernie.ars_nouveau.geckolib3.core.controller.AnimationController;
import software.bernie.ars_nouveau.geckolib3.core.event.predicate.AnimationEvent;
import software.bernie.ars_nouveau.geckolib3.core.manager.AnimationData;
import software.bernie.ars_nouveau.geckolib3.core.manager.AnimationFactory;

public class Cloak extends BasicItem implements IAnimatable {
    public AnimationFactory factory = new AnimationFactory(this);

    public Cloak(Properties p) {
        super(p);
    }
    public Cloak(Properties p, boolean ench) {
        super(p, ench);
    }

    private <P extends Item & IAnimatable> PlayState predicate(AnimationEvent<P> event) {
        //event.getController().setAnimation(new AnimationBuilder().addAnimation("idle", true));
        return PlayState.CONTINUE;
    }

    @Override
    public void registerControllers(AnimationData data) {
        data.addAnimationController(new AnimationController<Cloak>(this, "controller", 0, this::predicate));

    }

    @Override
    public AnimationFactory getFactory() {
        return this.factory;
    }
}
