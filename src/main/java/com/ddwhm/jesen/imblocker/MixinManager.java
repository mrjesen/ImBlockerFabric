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
import java.util.List;
import java.util.Set;

public class MixinManager implements IMixinConfigPlugin {
    public static boolean isRoughlyEnoughItemsApiLoaded = false;
    private static final String ROUGHLY_ENOUGH_ITEMS_API_MOD_ID = "roughlyenoughitems-api";
    private static final String MIXIN_ROUGHLY_ENOUGH_ITEMS_API = "com.ddwhm.jesen.imblocker.mixin.rei";

    // rewrite the getversion function to fix the issue #12
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
        isRoughlyEnoughItemsApiLoaded = FabricLoader.getInstance().isModLoaded(ROUGHLY_ENOUGH_ITEMS_API_MOD_ID);
    }

    @Override
    public String getRefMapperConfig() {
        return null;
    }

    @Override
    public boolean shouldApplyMixin(String targetClassName, String mixinClassName) {
        int protocolVersion = getGameVersion();
        if (!(System.getProperty("os.name").toLowerCase().startsWith("win"))) {
            return false;
        }
        if (mixinClassName.endsWith("mixin.MixinAbstractButtonWidget") && protocolVersion < 705) {
            return false;
        }
        if (!isRoughlyEnoughItemsApiLoaded && mixinClassName.startsWith(MIXIN_ROUGHLY_ENOUGH_ITEMS_API)) {
            return false;
        }
        return !mixinClassName.endsWith("mixin.MixinAnvilScreen") || protocolVersion >= 705;
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
