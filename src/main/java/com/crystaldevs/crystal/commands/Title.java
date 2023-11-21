package com.crystaldevs.crystal.commands;

import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import meteordevelopment.meteorclient.commands.Command;
import meteordevelopment.meteorclient.systems.config.Config;
import net.minecraft.command.CommandSource;

import static com.mojang.brigadier.Command.SINGLE_SUCCESS;
public class Title extends Command {
    public Title() {
        super("title", "Edits the window title.");
    }

    @Override
    public void build(LiteralArgumentBuilder<CommandSource> builder) {
        builder.then(argument("title", StringArgumentType.greedyString()).executes(context -> {
                String newTitle = context.getArgument("title", String.class);
                if(!newTitle.isEmpty()) {
                    Config.get().customWindowTitle.set(true);
                    Config.get().customWindowTitleText.set(newTitle);
                    return SINGLE_SUCCESS;
                } else {
                    error("Incomplete command, .title {title}");
                    return SINGLE_SUCCESS;
                }
        }));
    }
}
