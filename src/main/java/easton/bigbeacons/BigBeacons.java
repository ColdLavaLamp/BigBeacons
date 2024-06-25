package easton.bigbeacons;

import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.networking.v1.PayloadTypeRegistry;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.fabricmc.fabric.impl.registry.sync.packet.DirectRegistryPacketHandler;
import net.minecraft.entity.effect.StatusEffect;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.util.Identifier;

public class BigBeacons implements ModInitializer {

	public static final String MOD_ID = "bigbeacons";
	public static final StatusEffect FLIGHT = new FlightEffect();
	public static final Identifier PACKET_ID = Identifier.of("bigbeacons", "mod-check");


	@Override
	public void onInitialize() {
		Registry.register(Registries.STATUS_EFFECT, Identifier.of(MOD_ID, "flight"), FLIGHT);

		PayloadTypeRegistry.playS2C().register(BasicPayload.ID, BasicPayload.CODEC);
		PayloadTypeRegistry.playC2S().register(BasicPayload.ID, BasicPayload.CODEC);

		ServerPlayNetworking.registerGlobalReceiver(BasicPayload.ID, (payload, context) -> {
			((PlayerModdedDuck)context.player()).setHasMod(true);
		});

	}
}
