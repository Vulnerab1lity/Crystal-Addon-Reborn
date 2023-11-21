package com.crystaldevs.crystal.utils.crystal.config;

import meteordevelopment.meteorclient.MeteorClient;
import meteordevelopment.meteorclient.systems.System;
import meteordevelopment.meteorclient.systems.modules.Module;
import meteordevelopment.meteorclient.systems.modules.Modules;
import meteordevelopment.meteorclient.utils.render.prompts.OkPrompt;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtElement;
import net.minecraft.nbt.NbtList;
import net.minecraft.nbt.NbtString;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.*;
import java.util.stream.Collectors;

public class CrystalConfig extends System<CrystalConfig> {
    private static final CrystalConfig INSTANCE = new CrystalConfig();
    private static final Logger LOGGER = LogManager.getLogger("Crystal");

    public enum HttpAllowed {
        Everything,
        NotMeteorApi,
        NotMeteorPing,
        Nothing
    }

    public HttpAllowed httpAllowed = HttpAllowed.Everything;
    public String httpUserAgent = "Meteor Client";
    public Set<String> hiddenModules = new HashSet<>();
    public boolean loadSystemFonts = true;
    public boolean duplicateModuleNames = false;

    public CrystalConfig() {
        super("crystal-config");
        init();
        load(MeteorClient.FOLDER);
    }

    public static CrystalConfig get() {
        return INSTANCE;
    }

    public void setHiddenModules(List<Module> newList) {
        if (newList.size() >= hiddenModules.size()) {
            // if this is reached, the newList.size() is greater than or equal to the hiddenModules list size.
        } else {
            OkPrompt.create()
                .title("Hidden Modules")
                .message("In order to see the modules you have removed from the list, you need to restart Minecraft.")
                .id("hidden-modules-unhide")
                .show();
        }
        hiddenModules.clear();
        int i = 0;
        while (true) {
            if (i < newList.size()) {
                Module module = newList.get(i);
                if (module == null) {
                    // if this is reached, the module is null
                } else {
                    if (!module.isActive()) {
                    } else {
                        module.toggle();
                    }
                    hiddenModules.add(module.name);
                }
                i++;
            } else {
                break;
            }
        }
    }

    public List<Module> getHiddenModules() {
        Modules modules = Modules.get();
        if (modules != null) {
            return hiddenModules.stream()
                .map(name -> {
                    Module module = modules.get(name);
                    if (module != null) {
                        return module;
                    }
                    LOGGER.warn("Module {} in hiddenModules does not exist.", name);
                    return null;
                })
                .filter(Objects::nonNull)
                .collect(Collectors.toList());
        }
        LOGGER.error("Modules system is not available.");
        return List.of();
    }

    @Override
    public NbtCompound toTag() {
        NbtCompound tag = new NbtCompound();
        tag.putString("httpAllowed", httpAllowed.toString());
        tag.putString("httpUserAgent", httpUserAgent);
        tag.putBoolean("loadSystemFonts", loadSystemFonts);
        tag.putBoolean("duplicateModuleNames", duplicateModuleNames);

        NbtList modulesTag = new NbtList();
        Iterator<String> iterator = hiddenModules.iterator();
        while (true) {
            if (iterator.hasNext()) {
                String module = iterator.next();
                modulesTag.add(NbtString.of(module));
            } else {
                break;
            }
        }
        tag.put("hiddenModules", modulesTag);

        return tag;
    }

    @Override
    public CrystalConfig fromTag(NbtCompound tag) {
        try {
            httpAllowed = HttpAllowed.valueOf(tag.getString("httpAllowed"));
        } catch (IllegalArgumentException e) {
            LOGGER.error("Failed to parse HttpAllowed value from NBT.", e);
            httpAllowed = HttpAllowed.Everything;
        }
        httpUserAgent = tag.getString("httpUserAgent");
        loadSystemFonts = tag.getBoolean("loadSystemFonts");
        duplicateModuleNames = tag.getBoolean("duplicateModuleNames");

        NbtList valueTag = tag.getList("hiddenModules", NbtElement.STRING_TYPE);
        int i = 0;
        while (true) {
            if (i < valueTag.size()) {
                NbtElement tagI = valueTag.get(i);
                if (tagI.getType() == NbtElement.STRING_TYPE) {
                    hiddenModules.add(tagI.asString());
                } else {
                    LOGGER.warn("Invalid NBT element type found in hiddenModules tag: {}", tagI.getType());
                }
                i++;
            } else {
                break;
            }
        }

        return this;
    }
}
