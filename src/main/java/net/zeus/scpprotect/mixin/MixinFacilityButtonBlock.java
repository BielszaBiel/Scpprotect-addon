package net.zeus.scpprotect.mixin;

import java.util.Map;
import java.util.HashMap;

import net.minecraft.core.BlockPos;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;

import net.zeus.scpprotect.level.block.blocks.FacilityButtonBlock;
import net.zeus.scpprotect.level.block.entity.FacilityButtonBlockEntity;
import net.zeus.scpprotect.level.item.SCPItems;
import net.zeus.scpprotect.level.sound.SCPSounds;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(FacilityButtonBlock.class)
public class MixinFacilityButtonBlock {

    private static final Map<Item, Double> THRESHOLDS = new HashMap<>();
    static {
        THRESHOLDS.put(SCPItems.GUARDKEYCARD.get(), 3.5);
        THRESHOLDS.put(SCPItems.SC_PKEYCARD.get(), 4.5);
        THRESHOLDS.put(SCPItems.C_IKEYCARD.get(), 4.5);
        THRESHOLDS.put(SCPItems.MTF_KEYCARD.get(), 6.0);
        THRESHOLDS.put(SCPItems.MTF_CAPKEYCARD.get(), 6.5);
    }

    @Inject(
      method = "m_6227_",                          
      at = @At("HEAD"),
      cancellable = true
    )
    private void onUseHandleCustom(
        BlockState state,
        Level world,
        BlockPos pos,
        Player player,
        InteractionHand hand,
        BlockHitResult hit,
        CallbackInfoReturnable<InteractionResult> cir
    ) {
        ItemStack stack = player.getItemInHand(hand);
        Item item = stack.getItem();
        Double threshold = THRESHOLDS.get(item);
        if (threshold != null) {
            FacilityButtonBlockEntity be = (FacilityButtonBlockEntity) world.getBlockEntity(pos);
            // jeśli brak be lub zamek, działamy jak domyślnie
            if (be == null) {
                return;
            }
    
            world.playSound(
                null,
                pos,
                SCPSounds.KEYCARD_READER_USE.get(),
                player.getSoundSource(),
                1.0F,
                1.0F
            );
            // sprawdź poziom
            if (threshold >= be.keycardLevel || stack.is(SCPItems.LEVEL_OMNI_KEYCARD.get())) {
                // otwórz drzwi
                cir.setReturnValue(((FacilityButtonBlock)(Object)this).press(state, world, pos));
            } else {
                player.displayClientMessage(
                    net.minecraft.network.chat.Component.literal(
                        "You need a level " + be.keycardLevel + " keycard!"
                    ),
                    true
                );
                cir.setReturnValue(InteractionResult.SUCCESS);
            }
        }
    }
}

