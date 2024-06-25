package easton.bigbeacons.mixin;

import easton.bigbeacons.BigBeacons;
import net.minecraft.block.entity.BeaconBlockEntity;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.ingame.BeaconScreen;
import net.minecraft.client.gui.screen.ingame.HandledScreen;
import net.minecraft.client.gui.widget.ClickableWidget;
import net.minecraft.entity.effect.StatusEffect;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.screen.BeaconScreenHandler;
import net.minecraft.text.MutableText;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.*;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(BeaconScreen.class)
public abstract class BeaconScreenMixin extends HandledScreen<BeaconScreenHandler> {

    public BeaconScreenMixin(BeaconScreenHandler handler, PlayerInventory inventory, Text title) {
        super(handler, inventory, title);
    }

    @Redirect(
            method = "<clinit>",
            at = @At(value = "INVOKE",
                    target = "net/minecraft/util/Identifier.ofVanilla(Ljava/lang/String;)Lnet/minecraft/util/Identifier;",
                    ordinal = 0
            )
    )
    private static Identifier showNewSprite(String vanillaId) {
        return Identifier.of(BigBeacons.MOD_ID, "textures/gui/container/bigbeacon.png");
    }

    @ModifyConstant(method = "<init>", constant = @Constant(intValue = 219))
    private static int resizeGui(int curr) {
        return 233;
    }

    @Redirect(method = "drawBackground", at = @At(value = "INVOKE", target = "net/minecraft/client/gui/DrawContext.drawItem(Lnet/minecraft/item/ItemStack;II)V"))
    private void dontDrawItems(DrawContext ctx, ItemStack is, int x, int y) {}

    @ModifyConstant(method = "init", constant = @Constant(intValue = 164))
    private static int moveDoneButton(int curr) {
        return 61;
    }

    @ModifyConstant(method = "init", constant = @Constant(intValue = 190))
    private static int moveCancelButton(int curr) {
        return 87;
    }

    @ModifyArg(
            method = "init",
            at = @At(value = "INVOKE", ordinal = 0, target = "net/minecraft/client/gui/screen/ingame/BeaconScreen$EffectButtonWidget.<init>(Lnet/minecraft/client/gui/screen/ingame/BeaconScreen;IILnet/minecraft/registry/entry/RegistryEntry;ZI)V"),
            index = 1
    )
    private int realignEffectButtons(int curr) {
        int offset = curr - this.x - 76;
        if (offset == -11 || offset == -23) {   // only button in row or left hand button
            return this.x + 22;
        } else {
            return this.x + 22 + 24;    // right hand button
        }
    }

    @ModifyConstant(method = "init", constant = @Constant(intValue = 2, ordinal = 0))
    private static int changeNumberOfEffects(int curr) {
        return 7;
    }

    // this is necessary because of the jump from 3 to 5 on the effects on the left
    @ModifyArg(
            method = "init",
            at = @At(
                    value = "INVOKE",
                    target = "net/minecraft/client/gui/screen/ingame/BeaconScreen$EffectButtonWidget.<init>(Lnet/minecraft/client/gui/screen/ingame/BeaconScreen;IILnet/minecraft/registry/entry/RegistryEntry;ZI)V",
                    ordinal = 0
            ),
            index = 5
    )
    private int changeLevelOnAddedEffects(int level) {
        return level >= 3 ? level + 1 : level;
    }

    // Level 3 Button stuff

    @Shadow
    private <T extends ClickableWidget> void addButton(T button) {}

    @Shadow
    RegistryEntry<StatusEffect> primaryEffect;

    // just made an anonymous class instead of LevelThreeEffectButtonWidget
    @Inject(method = "init", at = @At("TAIL"))
    private void addLevelThreeButton(CallbackInfo ci) {
        BeaconScreen.EffectButtonWidget effectButtonWidget = ((BeaconScreen)(Object)this).new EffectButtonWidget(this.x + 157, this.y + 100, BeaconBlockEntity.EFFECTS_BY_LEVEL.get(0).get(0), false, 9) {

            @Override
            protected MutableText getEffectName(RegistryEntry<StatusEffect> effect) {
                return Text.translatable(effect.value().getTranslationKey()).append(" III");
            }

            @Override
            public void tick(int level) {
                if (BeaconScreenMixin.this.primaryEffect != null) {
                    this.visible = true;
                    this.init(BeaconScreenMixin.this.primaryEffect);
                    super.tick(level);
                } else {
                    this.visible = false;
                }
            }
        };
        effectButtonWidget.active = false;
        this.addButton(effectButtonWidget);
    }


    // Texture stuff (numbers and text)

    private static final Text TERTIARY_POWER_TEXT = Text.translatable("block.minecraft.beacon.tertiary");

    @Inject(method = "drawForeground", at = @At("TAIL"))
    private void drawNumbersAndText(DrawContext context, int mouseX, int mouseY, CallbackInfo ci) {
        context.drawCenteredTextWithShadow(this.textRenderer, TERTIARY_POWER_TEXT, 169, 84, 14737632);

        for (int i = 1; i < 9; i++) {
            context.drawCenteredTextWithShadow(this.textRenderer, Text.of("" + (i <= 3 ? i : i + 1)), 16, 29 + (i - 1) * 25, 14737632);
        }
        context.drawCenteredTextWithShadow(this.textRenderer, Text.of("" + 4), 138, 54, 14737632);
        context.drawCenteredTextWithShadow(this.textRenderer, Text.of("" + 10), 149, 108, 14737632);
    }

}
