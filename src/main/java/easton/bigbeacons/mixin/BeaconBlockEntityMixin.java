package easton.bigbeacons.mixin;

import easton.bigbeacons.BigBeacons;
import easton.bigbeacons.PlayerModdedDuck;
import net.minecraft.block.entity.BeaconBlockEntity;
import net.minecraft.entity.effect.StatusEffect;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.registry.Registries;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.*;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;
import org.spongepowered.asm.mixin.injection.invoke.arg.Args;

import java.util.List;
import java.util.Objects;

@Mixin(BeaconBlockEntity.class)
public class BeaconBlockEntityMixin {

    @Redirect(
            method = "<clinit>()V",
            at = @At(value = "INVOKE", target = "Ljava/util/List;of(Ljava/lang/Object;Ljava/lang/Object;Ljava/lang/Object;Ljava/lang/Object;)Ljava/util/List;")
    )
    private static List<List<RegistryEntry<StatusEffect>>> addEffectsToBeacon(Object a, Object b, Object c, Object d) {
        return List.of(
                List.of(StatusEffects.SPEED, StatusEffects.HASTE),
                List.of(StatusEffects.RESISTANCE, StatusEffects.JUMP_BOOST),
                List.of(StatusEffects.STRENGTH),
                List.of(StatusEffects.REGENERATION),
                List.of(StatusEffects.FIRE_RESISTANCE),
                List.of(StatusEffects.SATURATION),
                List.of(StatusEffects.ABSORPTION),
                List.of(StatusEffects.LUCK)
        );
    }

    @ModifyConstant(method = "updateLevel", constant = @Constant(intValue = 4))
    private static int moreLevels(int curr) {
        return 16;
    }

    @Inject(method = "applyPlayerEffects", at=@At("TAIL"), locals = LocalCapture.CAPTURE_FAILSOFT)
    private static void applyLevelThreeEffects(World world, BlockPos pos, int beaconLevel, RegistryEntry<StatusEffect> primaryEffect, RegistryEntry<StatusEffect> secondaryEffect, CallbackInfo ci, double d, int i, int j, Box box, List<PlayerEntity> list) {
        if (beaconLevel >= 10 && Objects.equals(primaryEffect, secondaryEffect)) {
            for (PlayerEntity playerEntity : list) {
                playerEntity.addStatusEffect(new StatusEffectInstance(primaryEffect, j, 2, true, true));
            }
        }
        if (beaconLevel >= 16) {
            RegistryEntry<StatusEffect> flight = Registries.STATUS_EFFECT.getEntry(BigBeacons.FLIGHT);
            for (PlayerEntity player : list) {
                // we can't give the effect to vanilla players, it will cause them to disconnect since they don't know what it is
                // so we check if they have the mod locally (not an SPE) or if the server has them as having the mod
                if (!(player instanceof ServerPlayerEntity) || ((PlayerModdedDuck) player).hasMod()) {
                    player.addStatusEffect(new StatusEffectInstance(flight, j, 0, true, false));
                }
            }
        }
    }

}
