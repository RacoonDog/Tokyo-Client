/*
 * This file is part of the Meteor Client distribution (https://github.com/MeteorDevelopment/meteor-client).
 * Copyright (c) Meteor Development.
 */

package io.github.racoondog.tokyo.systems.modules.clientsync;

import io.github.racoondog.tokyo.utils.c2c.C2CNetworkHandler;
import io.github.racoondog.tokyo.utils.c2c.ConversionHelper;
import meteordevelopment.meteorclient.utils.player.ChatUtils;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.block.Block;

import java.io.DataInputStream;
import java.io.IOException;
import java.net.Socket;

@Environment(EnvType.CLIENT)
public class ClientSyncWorker extends Thread {
    private Socket socket;
    public Block target;

    public ClientSyncWorker(String ip, int port) {
        try {
            socket = new Socket(ip, port);
        } catch (Exception e) {
            socket = null;
            ChatUtils.warningPrefix("ClientSync", "Server not found at '%s' on port %s.", ip, port);
            e.printStackTrace();
        }

        if (socket != null) start();
    }

    @Override
    public void run() {
        ChatUtils.infoPrefix("ClientSync", "Connected to ClientSync host on at '%s' on port %s.", getIp(socket.getInetAddress().getHostAddress()), socket.getPort());

        try {
            DataInputStream in = new DataInputStream(socket.getInputStream());


            while (!isInterrupted()) {
                byte[] read = in.readAllBytes();

                if (read.length != 0) {
                    ChatUtils.infoPrefix("ClientSync", "Received command: (highlight)%s", ConversionHelper.BaseUTF8.toUnicode(read));

                    C2CNetworkHandler.handle(read);
                }
            }

            in.close();
        } catch (IOException e) {
            ChatUtils.errorPrefix("ClientSync", "Error connecting to host.");
            e.printStackTrace();
            disconnect();
        }
    }

    public void disconnect() {
        try {
            socket.close();
        } catch (IOException e) {
            e.printStackTrace();
        }

        //todo baritone api
        //BaritoneAPI.getProvider().getPrimaryBaritone().getPathingBehavior().cancelEverything();

        ChatUtils.infoPrefix("ClientSync", "Disconnected from host.");

        interrupt();
    }

    public void tick() {
        if (target == null) return;
        //BaritoneAPI.getProvider().getPrimaryBaritone().getPathingBehavior().cancelEverything();
        //BaritoneAPI.getProvider().getPrimaryBaritone().getMineProcess().mine(target);
        target = null;
    }

    public String getConnection() {
        return getIp(socket.getInetAddress().getHostAddress()) + ":" + socket.getPort();
    }

    private String getIp(String ip) {
        return ip.equals("127.0.0.1") ? "localhost" : ip;
    }
}
