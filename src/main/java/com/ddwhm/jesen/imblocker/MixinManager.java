package com.ddwhm.jesen.imblocker;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import net.fabricmc.loader.api.FabricLoader;
import org.objectweb.asm.tree.ClassNode;
import org.spongepowered.asm.mixin.extensibility.IMixinConfigPlugin;
import org.spongepowered.asm.mixin.extensibility.IMixinInfo;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class MixinManager implements IMixinConfigPlugin {

    private static final Map<String, String> mixinDeps = new HashMap<>();
    // private static final HashMap<String, int> versionMap = new HashMap<>();

    static {
        mixinDeps.put("com.ddwhm.jesen.imblocker.mixin.rei", "roughlyenoughitems");
        mixinDeps.put("com.ddwhm.jesen.imblocker.mixin.libgui", "libgui");
        mixinDeps.put("com.ddwhm.jesen.imblocker.mixin.replay", "replaymod");
        /*
        versionMap.put("1.19",759);
        versionMap.put("1.18.2",758);
        versionMap.put("1.18.1",757);
        versionMap.put("1.18",757);
        versionMap.put("1.17.1",756);
        versionMap.put("1.17",755);
        versionMap.put("1.16.5",754);
        versionMap.put("1.16.4",754);
        versionMap.put("1.16.3",753);
        versionMap.put("1.16.2",751);
        versionMap.put("1.16.1",736);
        versionMap.put("1.16",735);
        versionMap.put("1.15.2",578);
        versionMap.put("1.15.1",575);
        versionMap.put("1.15",573);
        versionMap.put("1.14.4",498);
        versionMap.put("1.14.3",490);
        versionMap.put("1.14.2",485);
        versionMap.put("1.14.1",480);
        versionMap.put("1.14",477);
        versionMap.put("1.13.2",404);
        */
    }
    public static int protocolVersion;

    private static int getGameVersion() {
        try (
                final InputStream stream = IMixinConfigPlugin.class.getResourceAsStream("/version.json");
                final Reader reader = new InputStreamReader(stream)
        ) {

            final JsonObject versions = new JsonParser().parse(reader).getAsJsonObject();
            return versions.get("protocol_version").getAsInt();
        } catch (IOException | NullPointerException e) {
            throw new RuntimeException("[IMBLOCKER] Couldn't get the game protocol_version", e);
        }
    }

    @Override
    public void onLoad(String mixinPackage) {
        protocolVersion = getGameVersion();
    }

    @Override
    public String getRefMapperConfig() {
        return null;
    }

    @Override
    public boolean shouldApplyMixin(String targetClassName, String mixinClassName) {
        if (mixinClassName.endsWith("mixin.MixinAbstractButtonWidget") && protocolVersion < 705) { // version < 1.16
            return false;
        }
        if (mixinClassName.endsWith("mixin.compat115.MixinAbstractButtonWidget") && protocolVersion >= 705) { //version >= 1.16
            return false;
        }
        // 1.18.2 compat
        if (mixinClassName.endsWith("mixin.MixinBookEditScreenLegacy") && protocolVersion >= 758) { // version >= 1.18.2
            return false;
        }
        if (mixinClassName.endsWith("mixin.MixinBookEditScreen") && protocolVersion < 758) { // version < 1.18.2
            return false;
        }
        if (mixinClassName.endsWith("mixin.MixinSignEditScreenLegacy") && protocolVersion >= 758) { // version >= 1.18.2
            return false;
        }
        if (mixinClassName.endsWith("mixin.MixinSignBlockEntityLegacy") && protocolVersion >= 758) { // version >= 1.18.2
            return false;
        }
        if (mixinClassName.endsWith("mixin.MixinSignEditScreen") && protocolVersion < 758) { // version < 1.18.2
            return false;
        }

        for (Map.Entry<String, String> entry: mixinDeps.entrySet()) {
            if (mixinClassName.startsWith(entry.getKey())) {
                return FabricLoader.getInstance().isModLoaded(entry.getValue());
            }
        }
        return true;
    }

    @Override
    public void acceptTargets(Set<String> myTargets, Set<String> otherTargets) {

    }

    @Override
    public List<String> getMixins() {
        return null;
    }

    @Override
    public void preApply(String targetClassName, ClassNode targetClass, String mixinClassName, IMixinInfo mixinInfo) {

    }

    @Override
    public void postApply(String targetClassName, ClassNode targetClass, String mixinClassName, IMixinInfo mixinInfo) {

    }
}
