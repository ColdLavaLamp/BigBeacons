package easton.bigbeacons;

import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.attribute.AttributeContainer;
import net.minecraft.entity.effect.StatusEffect;
import net.minecraft.entity.effect.StatusEffectCategory;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.network.packet.s2c.play.PlayerListS2CPacket;
import net.minecraft.server.network.ServerPlayerEntity;

import java.util.List;

public class FlightEffect extends StatusEffect {

    public FlightEffect() {
        super(
                StatusEffectCategory.BENEFICIAL, // whether beneficial or harmful for entities
                0x30F8FF); // color in RGB
    }

    // This method is called every tick to check weather it should apply the status effect or not
    @Override
    public boolean canApplyUpdateEffect(int duration, int amplifier) {
        // In our case, we just make it return true so that it applies the status effect every tick.
        return true;
    }

    // This method is called when it applies the status effect. We implement custom functionality here.
    @Override
    public boolean applyUpdateEffect(LivingEntity entity, int amplifier) {
        if (entity instanceof PlayerEntity) {
            ((PlayerEntity) entity).getAbilities().allowFlying = true;
            ((PlayerEntity) entity).writeCustomDataToNbt(new NbtCompound());
        }
        return true;
    }

}