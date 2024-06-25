package easton.bigbeacons.mixin;

import easton.bigbeacons.BigBeacons;
import easton.bigbeacons.PlayerModdedDuck;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.network.packet.s2c.play.PlayerListS2CPacket;
import net.minecraft.server.network.ServerPlayerEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ServerPlayerEntity.class)
public class ServerPlayerEntityMixin implements PlayerModdedDuck {

    boolean hasMod = false;

    @Override
    public boolean hasMod() {
        return this.hasMod;
    }

    @Override
    public void setHasMod(boolean modded) {
        this.hasMod = modded;
    }

    @Inject(method = "onStatusEffectRemoved", at = @At("TAIL"))
    private void doRemoveFlight(StatusEffectInstance effect, CallbackInfo ci) {
        if (effect.getEffectType().value() == BigBeacons.FLIGHT) {
            ServerPlayerEntity player = (ServerPlayerEntity) (Object)this;
            if (!player.isSpectator() && !player.isCreative()) {
                player.getAbilities().allowFlying = false;
                player.getAbilities().flying = false;
                player.sendAbilitiesUpdate();
                player.server.getPlayerManager().sendToAll(new PlayerListS2CPacket(PlayerListS2CPacket.Action.UPDATE_GAME_MODE, player));
            }
        }
    }


}
