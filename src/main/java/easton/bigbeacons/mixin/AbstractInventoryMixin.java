package easton.bigbeacons.mixin;

import easton.bigbeacons.BigBeacons;
import net.minecraft.client.gui.screen.ingame.AbstractInventoryScreen;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.registry.entry.RegistryEntry;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyVariable;

import java.util.Collection;
import java.util.Iterator;

@Mixin(AbstractInventoryScreen.class)
public class AbstractInventoryMixin {

    @ModifyVariable(method = "drawStatusEffects", ordinal = 0, at = @At(value = "STORE"))
    private Collection<StatusEffectInstance> dontRenderFlight(Collection<StatusEffectInstance> collection) {
        collection.removeIf(inst -> inst.getEffectType().value().equals(BigBeacons.FLIGHT));
        return collection;
    }
}
