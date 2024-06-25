package easton.bigbeacons.mixin;

import easton.bigbeacons.BasicPayload;
import net.fabricmc.fabric.api.networking.v1.PacketByteBufs;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.entity.Entity;
import net.minecraft.network.ClientConnection;
import net.minecraft.server.PlayerManager;
import net.minecraft.server.network.ConnectedClientData;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.Identifier;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(PlayerManager.class)
public class PlayerManagerMixin {

    @Inject(method = "onPlayerConnect", at = @At("TAIL"))
    private void sendModCheckPacket(ClientConnection connection, ServerPlayerEntity player, ConnectedClientData clientData, CallbackInfo ci) {
        ServerPlayNetworking.send(player, new BasicPayload(true));
    }

    @Inject(method = "respawnPlayer", at = @At("TAIL"))
    private void sendModCheckPacketOnRespawn(ServerPlayerEntity player, boolean alive, Entity.RemovalReason reason, CallbackInfoReturnable<ServerPlayerEntity> cir) {
        ServerPlayNetworking.send(cir.getReturnValue(), new BasicPayload(true));
    }

}