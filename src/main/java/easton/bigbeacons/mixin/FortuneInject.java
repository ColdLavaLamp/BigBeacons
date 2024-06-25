package easton.bigbeacons.mixin;

import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.enchantment.Enchantments;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.item.ItemStack;
import net.minecraft.loot.context.LootContext;
import net.minecraft.loot.context.LootContextParameters;
import net.minecraft.loot.function.ApplyBonusLootFunction;
import net.minecraft.registry.entry.RegistryEntry;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(ApplyBonusLootFunction.class)
abstract class FortuneInject {

    @Final
    @Shadow
    private RegistryEntry<Enchantment> enchantment;

    @Inject(method = "process(Lnet/minecraft/item/ItemStack;Lnet/minecraft/loot/context/LootContext;)Lnet/minecraft/item/ItemStack;", cancellable = true, at = @At(value = "HEAD"))
    private void inject(ItemStack stack, LootContext context, CallbackInfoReturnable<ItemStack> info) {

        if (context.get(LootContextParameters.THIS_ENTITY) instanceof LivingEntity) {
            LivingEntity entity = (LivingEntity) context.get(LootContextParameters.THIS_ENTITY);
            if (entity != null && entity.hasStatusEffect(StatusEffects.LUCK) && this.enchantment.getKey().get() == Enchantments.FORTUNE) {
                int i = EnchantmentHelper.getLevel(this.enchantment, entity.getMainHandStack());
                int count = stack.getCount();
                i = i + entity.getStatusEffect(StatusEffects.LUCK).getAmplifier() + 1;  // the +1 is because the amplifier is the number of levels over one
                int bonus = context.getRandom().nextInt(i + 2) - 1; //these lines just integrate fortune functionality
                if (bonus < 0) {
                    bonus = 0;
                }
                stack.setCount(count * (bonus + 1));
                info.setReturnValue(stack);
            }
        }

    }
}