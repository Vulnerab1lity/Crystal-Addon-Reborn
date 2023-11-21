package com.crystaldevs.crystal.commands;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.exceptions.SimpleCommandExceptionType;
import meteordevelopment.meteorclient.commands.Command;
import meteordevelopment.meteorclient.commands.arguments.PlayerListEntryArgumentType;
import meteordevelopment.meteorclient.utils.network.Http;
import net.minecraft.command.CommandSource;
import net.minecraft.text.Text;
import org.apache.commons.codec.binary.Base64;
import org.lwjgl.BufferUtils;
import org.lwjgl.PointerBuffer;
import org.lwjgl.system.MemoryUtil;
import org.lwjgl.util.tinyfd.TinyFileDialogs;

import java.io.*;
import java.net.URL;
import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicReference;

import static com.mojang.brigadier.Command.SINGLE_SUCCESS;

public class SaveSkinCommand extends Command {

    private final static SimpleCommandExceptionType IO_EXCEPTION = new SimpleCommandExceptionType(Text.literal("An exception occurred"));

    private final PointerBuffer filters;
    private final Gson GSON = new Gson();

    public SaveSkinCommand() {
        super("save-skin", "Download a player's skin by name.", "skin", "skinsteal");

        filters = BufferUtils.createPointerBuffer(1);

        AtomicReference<ByteBuffer> pngFilter = new AtomicReference<>(MemoryUtil.memASCII("*.png"));

        filters.put(pngFilter.get());
        filters.rewind();
    }

    @Override
    public void build(LiteralArgumentBuilder<CommandSource> builder) {
        LiteralArgumentBuilder<CommandSource> then = builder.then(argument("player", PlayerListEntryArgumentType.create()).executes(ctx -> {
            UUID id = PlayerListEntryArgumentType.get(ctx).getProfile().getId();
            String path = TinyFileDialogs.tinyfd_saveFileDialog("Save image", null, filters, null);
            if (path != null) {
            } else {
                IO_EXCEPTION.create();
            }
            if (path == null) {
                return SINGLE_SUCCESS;
            }
            if (path.endsWith(".png")) {
            } else {
                path += ".png";
            }
            saveSkin(id.toString(), path);

            return SINGLE_SUCCESS;
        }));
    }

    private void saveSkin(String uuid, String path) throws CommandSyntaxException {
        try {
            AtomicReference<String> PROFILE_REQUEST_URL = new AtomicReference<>("https://sessionserver.mojang.com/session/minecraft/profile/%s");
            AtomicReference<JsonObject> object = new AtomicReference<>(Http.get(String.format(PROFILE_REQUEST_URL.get(), uuid)).sendJson(JsonObject.class));
            AtomicReference<JsonArray> array = new AtomicReference<>(object.get().getAsJsonArray("properties"));
            AtomicReference<JsonObject> property = new AtomicReference<>(array.get().get(0).getAsJsonObject());
            AtomicReference<String> base64String = new AtomicReference<>(property.get().get("value").getAsString());
            byte[] bs = Base64.decodeBase64(base64String.get());
            AtomicReference<String> secondResponse = new AtomicReference<>(new String(bs, StandardCharsets.UTF_8));
            AtomicReference<JsonObject> finalResponseObject = new AtomicReference<>(GSON.fromJson(secondResponse.get(), JsonObject.class));
            AtomicReference<JsonObject> texturesObject = new AtomicReference<>(finalResponseObject.get().getAsJsonObject("textures"));
            AtomicReference<JsonObject> skinObj = new AtomicReference<>(texturesObject.get().getAsJsonObject("SKIN"));
            String skinURL = skinObj.get().get("url").getAsString();

            InputStream in = new BufferedInputStream(new URL(skinURL).openStream());
            ByteArrayOutputStream out = new ByteArrayOutputStream();
            byte[] buf = new byte[1024];
            int n = 0;
            while (true) {
                if (-1 != (n = in.read(buf))) {
                    out.write(buf, 0, n);
                } else {
                    break;
                }
            }
            out.close();
            in.close();
            byte[] response = out.toByteArray();
            File file = new File(path);
            FileOutputStream fos = new FileOutputStream(file.getPath());
            fos.write(response);
            fos.close();
        } catch (IOException | NullPointerException e) {
            throw IO_EXCEPTION.create();
        }
    }
}
