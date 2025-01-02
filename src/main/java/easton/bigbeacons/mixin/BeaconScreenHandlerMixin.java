package easton.bigbeacons.mixin;

import easton.bigbeacons.PlayerModdedDuck;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.Inventory;
import net.minecraft.screen.BeaconScreenHandler;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.screen.ScreenHandlerType;
import net.minecraft.screen.slot.Slot;
import net.minecraft.server.network.ServerPlayerEntity;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.injection.*;
import org.spongepowered.asm.mixin.injection.invoke.arg.Args;

@Mixin(BeaconScreenHandler.class)
public abstract class BeaconScreenHandlerMixin extends ScreenHandler {

    protected BeaconScreenHandlerMixin(@Nullable ScreenHandlerType<?> type, int syncId) {
        super(type, syncId);
    }

    private static boolean userHasMod(Args args) {
        PlayerEntity player = ((PlayerInventory)args.get(0)).player;
        return !(player instanceof ServerPlayerEntity) || ((PlayerModdedDuck) player).hasMod();
    }

    // kinda hacky, but the only way to make it so that the vanilla slots aren't off by one from the modded slots afaik
    @ModifyConstant(
            method = "<init>(ILnet/minecraft/inventory/Inventory;Lnet/minecraft/screen/PropertyDelegate;Lnet/minecraft/screen/ScreenHandlerContext;)V",
            constant = @Constant(intValue = 136)
    )
    private int movePaymentSlotOffScreen(int curr) {
        return 1000000;
    }

    @ModifyArgs(
            method = "<init>(ILnet/minecraft/inventory/Inventory;Lnet/minecraft/screen/PropertyDelegate;Lnet/minecraft/screen/ScreenHandlerContext;)V",
            at = @At(value = "INVOKE", target = "Lnet/minecraft/screen/BeaconScreenHandler;addPlayerSlots(Lnet/minecraft/inventory/Inventory;II)V")
    )
    private void moveInventorySlots(Args args) {
        if (userHasMod(args)) {
            args.set(1, (int) args.get(1) + 26);
            args.set(2, (int) args.get(2) + 7);
        }
    }

    @Redirect(method = "quickMove", at = @At(value = "INVOKE", target = "net/minecraft/screen/BeaconScreenHandler$PaymentSlot.hasStack()Z"))
    private boolean preventShiftClickingIntoPaymentSlot(BeaconScreenHandler.PaymentSlot slot) {
        return true;
    }


    @Redirect(method = "setEffects", at = @At(value = "INVOKE", target = "Lnet/minecraft/screen/BeaconScreenHandler$PaymentSlot;hasStack()Z"))
    private boolean yesItHasStack(BeaconScreenHandler.PaymentSlot slot) {
        return true;
    }

    /**
     * @author ColdLavaLamp
     * @reason BigBeacons removes the payment slot altogether
     */
    @Overwrite
    public boolean hasPayment() {
        return true;
    }
}
