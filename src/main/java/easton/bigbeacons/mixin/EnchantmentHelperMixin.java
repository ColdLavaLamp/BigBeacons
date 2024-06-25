package easton.bigbeacons.mixin;

import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.enchantment.Enchantments;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.registry.entry.RegistryEntry;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(EnchantmentHelper.class)
public class EnchantmentHelperMixin {

    // Adds luck level to looting level, kind of a weird place to do this, not totally sure looting checks are always routed through this method
    @Inject(method = "getEquipmentLevel", at = @At("RETURN"), cancellable = true)
    private static void addLuckToLooting(RegistryEntry<Enchantment> enchantment, LivingEntity entity, CallbackInfoReturnable<Integer> cir) {
        if (enchantment.getKey().isPresent() && enchantment.getKey().get() == Enchantments.LOOTING && entity.hasStatusEffect(StatusEffects.LUCK)) {
            int luckLevel = entity.getStatusEffect(StatusEffects.LUCK).getAmplifier() + 1;
            cir.setReturnValue(cir.getReturnValueI() + luckLevel);
        }
    }

}
