package never.lose.alts;

import net.minecraft.client.session.Session;
import never.lose.mixins.accessor.MinecraftClientAccessor;

import java.util.Optional;

import static never.lose.NeverLose.mc;

public final class AltSession {

    private AltSession() {}

    public static void login(AltEntry entry) {
        Session session = new Session(
                entry.getName(),
                entry.getUuid(),
                "",
                Optional.empty(),
                Optional.empty(),
                Session.AccountType.LEGACY);
        ((MinecraftClientAccessor) mc).endless$setSession(session);
    }
}