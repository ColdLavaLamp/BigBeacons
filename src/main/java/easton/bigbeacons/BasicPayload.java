package easton.bigbeacons;

import net.minecraft.network.RegistryByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.codec.PacketCodecs;
import net.minecraft.network.packet.CustomPayload;
import net.minecraft.util.Identifier;

public record BasicPayload(boolean dummy) implements CustomPayload {
    public static final CustomPayload.Id<BasicPayload> ID = new CustomPayload.Id<>(BigBeacons.PACKET_ID);
    public static final PacketCodec<RegistryByteBuf, BasicPayload> CODEC = PacketCodec.tuple(PacketCodecs.BOOLEAN, BasicPayload::dummy, BasicPayload::new);

    @Override
    public CustomPayload.Id<? extends CustomPayload> getId() {
        return ID;
    }
}
