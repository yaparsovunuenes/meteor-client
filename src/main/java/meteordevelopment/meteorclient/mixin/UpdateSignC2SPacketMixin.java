/*
 * This file is part of the Meteor Client distribution (https://github.com/MeteorDevelopment/meteor-client).
 * Copyright (c) Meteor Development.
 */

package meteordevelopment.meteorclient.mixin;

import net.minecraft.network.packet.c2s.play.UpdateSignC2SPacket;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyVariable;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

@Mixin(UpdateSignC2SPacket.class)
public abstract class UpdateSignC2SPacketMixin {
    private static final String INIT = "<init>(Lnet/minecraft/util/math/BlockPos;ZLjava/lang/String;Ljava/lang/String;Ljava/lang/String;Ljava/lang/String;)V";

    @ModifyVariable(method = INIT, at = @At("HEAD"), argsOnly = true, ordinal = 0)
    private static String sanitizeLine0(String line) {
        return sanitize(line);
    }

    @ModifyVariable(method = INIT, at = @At("HEAD"), argsOnly = true, ordinal = 1)
    private static String sanitizeLine1(String line) {
        return sanitize(line);
    }

    @ModifyVariable(method = INIT, at = @At("HEAD"), argsOnly = true, ordinal = 2)
    private static String sanitizeLine2(String line) {
        return sanitize(line);
    }

    @ModifyVariable(method = INIT, at = @At("HEAD"), argsOnly = true, ordinal = 3)
    private static String sanitizeLine3(String line) {
        return sanitize(line);
    }

    // Ensure sign lines contain only plain text, stripping any JSON-encoded Text components.
    @Unique
    private static String sanitize(String line) {
        if (line == null || line.isEmpty()) return line;

        // Strip any JSON-encoded Text (e.g. {"translate":"key"} or {"keybind":"key"}) back to the raw key.
        // Such strings could arrive here if the sign text was not fully sanitised by AbstractSignEditScreenMixin.
        if (line.startsWith("{")) {
            try {
                JsonObject obj = JsonParser.parseString(line).getAsJsonObject();
                if (obj.has("translate")) return obj.get("translate").getAsString();
                if (obj.has("keybind")) return obj.get("keybind").getAsString();
                if (obj.has("text")) return obj.get("text").getAsString();
            } catch (Exception ignored) {}
        }

        return line;
    }
}
