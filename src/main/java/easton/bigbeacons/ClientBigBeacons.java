package easton.bigbeacons;

import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;

@Environment(EnvType.CLIENT)
public class ClientBigBeacons implements ClientModInitializer {
    @Override
    public void onInitializeClient() {

        ClientPlayNetworking.registerGlobalReceiver(BasicPayload.ID, (payload, context) -> {
            context.client().execute(() -> {
                ClientPlayNetworking.send(new BasicPayload(true));
            });
        });
    }
}
