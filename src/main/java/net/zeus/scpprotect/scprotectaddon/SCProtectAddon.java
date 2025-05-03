import net.minecraftforge.fml.common.Mod;
import org.spongepowered.asm.mixin.Mixins;

@Mod("scprotect_addon")
public class SCProtectAddon {
    public SCProtectAddon() {
        Mixins.addConfiguration("mixins.scpprotect_addon.json");
    }
}