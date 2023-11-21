package com.crystaldevs.crystal.utils.mc.account;

import com.mojang.authlib.exceptions.AuthenticationException;
import com.mojang.authlib.minecraft.MinecraftSessionService;
import meteordevelopment.meteorclient.MeteorClient;
import meteordevelopment.meteorclient.mixin.MinecraftClientAccessor;
import meteordevelopment.meteorclient.systems.accounts.Account;
import meteordevelopment.meteorclient.systems.accounts.AccountType;
import meteordevelopment.meteorclient.utils.misc.NbtException;
import net.minecraft.client.session.Session;
import net.minecraft.nbt.NbtCompound;

import static meteordevelopment.meteorclient.MeteorClient.mc;

public class CustomAccount extends Account<CustomAccount> {
    private String password, server;

    public CustomAccount(String name, String password, String server) {
        super(AccountType.Cracked, name);
        this.password = password;
        this.server = server;
    }

    @Override
    public boolean fetchInfo() {
        try {
            Session session = CustomLogin.login(name, password, server);

            cache.username = session.getUsername();
            cache.uuid = String.valueOf(session.getUuidOrNull());

            return true;
        } catch (AuthenticationException e) {
            return false;
        }
    }

    @Override
    public boolean login() {
        try {
            CustomLogin.LocalYggdrasilAuthenticationService service = new CustomLogin.LocalYggdrasilAuthenticationService(((MinecraftClientAccessor) mc).getProxy(), server);
            MinecraftSessionService sessService = new CustomLogin.LocalYggdrasilMinecraftSessionService(service, service.server);
            applyLoginEnvironment(service, sessService);

            Session session = CustomLogin.login(name, password, server);
            setSession(session);
            cache.username = session.getUsername();
            cache.loadHead();
            return true;
        } catch (AuthenticationException e) {
            if (e.getMessage().contains("Invalid username or password") || e.getMessage().contains("account migrated"))
                MeteorClient.LOG.error("Wrong password.");
            else MeteorClient.LOG.error("Failed to contact the authentication server.");
            return false;
        }
    }

    @Override
    public NbtCompound toTag() {
        NbtCompound tag = super.toTag();

        tag.putString("password", password);
        tag.putString("server", server);

        return tag;
    }

    @Override
    public CustomAccount fromTag(NbtCompound tag) {
        super.fromTag(tag);
        if (!tag.contains("password")) {
            throw new NbtException();
        }

        password = tag.getString("password");
        server = tag.getString("server");

        return this;
    }

    @Override
    public boolean equals(Object o) {
        if (!(o instanceof CustomAccount)) return false;
        return ((CustomAccount) o).name.equals(this.name);
    }
}
