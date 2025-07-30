package com.lyngarr.cookiesforparrots.mixin;

import net.minecraft.entity.passive.ParrotEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.registry.tag.ItemTags;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.world.World;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(ParrotEntity.class)
public abstract class ParrotEntityMixin {

    @Inject(method = "interactMob", at = @At("HEAD"), cancellable = true)
    private void onInteract(PlayerEntity player, Hand hand, CallbackInfoReturnable<ActionResult> cir) {
        ParrotEntity parrot = (ParrotEntity)(Object)this;
        ItemStack stack = player.getStackInHand(hand);
        World world = parrot.getWorld();

        if (stack.isIn(ItemTags.PARROT_FOOD)) {
            cir.setReturnValue(ActionResult.FAIL);
            return;
        }

        if (stack.isOf(Items.COOKIE)) {
            if (parrot.isTamed()) {
                cir.setReturnValue(ActionResult.FAIL);
                return;
            }
            if (!world.isClient) {
                if (player.getRandom().nextInt(10) == 0) {
                    parrot.setOwner(player);
                    parrot.setTamed(true, true);
                    parrot.getWorld().sendEntityStatus(parrot, (byte)7); // heart particles
                } else {
                    parrot.getWorld().sendEntityStatus(parrot, (byte)6); // smoke particles
                }
                stack.decrement(1);
            }
            cir.setReturnValue(ActionResult.SUCCESS);
        }
    }
}
