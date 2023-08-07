/*
 * This file is part of the Meteor Client distribution (https://github.com/MeteorDevelopment/meteor-client).
 * Copyright (c) Meteor Development.
 */

package io.github.racoondog.tokyo.systems.modules.clientsync;

import io.github.racoondog.tokyo.utils.c2c.ConversionHelper;
import meteordevelopment.meteorclient.utils.player.ChatUtils;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.network.PacketByteBuf;

import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;

@Environment(EnvType.CLIENT)
public class ClientSyncConnection extends Thread {
    public final Socket socket;
    public PacketByteBuf messageToSend;

    public ClientSyncConnection(Socket socket) {
        this.socket = socket;
        start();
    }

    @Override
    public void run() {
        ChatUtils.infoPrefix("ClientSync", "New worker connected on %s.", getIp(socket.getInetAddress().getHostAddress()));

        try {
            DataOutputStream out = new DataOutputStream(socket.getOutputStream());

            while (!isInterrupted()) {
                if (messageToSend != null) {
                    try {
                        out.write(messageToSend.array());
                        out.flush();
                    } catch (Exception e) {
                        ChatUtils.errorPrefix("ClientSync", "Encountered error when sending command.");
                        e.printStackTrace();
                    }

                    messageToSend = null;
                }
            }

            out.close();
        } catch (IOException e) {
            ChatUtils.infoPrefix("ClientSync", "Error creating a connection with %s on port %s.", getIp(socket.getInetAddress().getHostAddress()), socket.getPort());
            e.printStackTrace();
        }
    }

    public void disconnect() {
        try {
            socket.close();
        } catch (IOException e) {
            e.printStackTrace();
        }

        ChatUtils.infoPrefix("ClientSync", "Worker disconnected on ip: %s.", socket.getInetAddress().getHostAddress());

        interrupt();
    }

    private String getIp(String ip) {
        return ip.equals("127.0.0.1") ? "localhost" : ip;
    }
}
