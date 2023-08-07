/*
 * This file is part of the Meteor Client distribution (https://github.com/MeteorDevelopment/meteor-client).
 * Copyright (c) Meteor Development.
 */

package io.github.racoondog.tokyo.systems.modules.clientsync;

import io.github.racoondog.tokyo.event.*;
import io.github.racoondog.tokyo.utils.c2c.C2CPacket;
import io.github.racoondog.tokyo.utils.c2c.C2CPacketHandler;
import io.github.racoondog.tokyo.utils.c2c.packets.*;
import meteordevelopment.meteorclient.MeteorClient;
import meteordevelopment.meteorclient.utils.network.MeteorExecutor;
import meteordevelopment.meteorclient.utils.player.ChatUtils;
import meteordevelopment.orbit.EventHandler;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.network.PacketByteBuf;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

@Environment(EnvType.CLIENT)
public class ClientSyncHost extends Thread {
    private ServerSocket socket;
    private final ClientSyncConnection[] clientConnections = new ClientSyncConnection[50];

    public ClientSyncHost(int port) {
        try {
            socket = new ServerSocket(port);
        } catch (IOException e) {
            socket = null;
            ChatUtils.errorPrefix("ClientSync", "Couldn't start a server on port %s.", port);
            e.printStackTrace();
        }

        if (socket != null) {
            start();
            MeteorClient.EVENT_BUS.subscribe(this);
        }
    }

    @Override
    public void run() {
        ChatUtils.infoPrefix("ClientSync", "Listening for incoming connections on port %s.", socket.getLocalPort());

        while (!isInterrupted()) {
            try {
                Socket connection = socket.accept();
                assignConnectionToSubServer(connection);
            } catch (IOException e) {
                ChatUtils.errorPrefix("ClientSync", "Error making a connection to worker.");
                e.printStackTrace();
            }
        }
    }

    public void assignConnectionToSubServer(Socket connection) {
        for (int i = 0; i < clientConnections.length; i++) {
            if (this.clientConnections[i] == null) {
                this.clientConnections[i] = new ClientSyncConnection(connection);
                break;
            }
        }
    }

    public void disconnect() {
        MeteorClient.EVENT_BUS.unsubscribe(this);

        for (ClientSyncConnection connection : clientConnections) {
            if (connection != null) connection.disconnect();
        }

        try {
            socket.close();
        } catch (IOException e) {
            e.printStackTrace();
        }

        ChatUtils.infoPrefix("ClientSync", "Server closed on port %s.", socket.getLocalPort());

        interrupt();
    }

    public void sendPacket(C2CPacket packet) {
        PacketByteBuf buf = C2CPacketHandler.writePacket(packet);

        MeteorExecutor.execute(() -> {
            for (ClientSyncConnection connection : clientConnections) {
                if (connection != null) {
                    connection.messageToSend = buf;
                }
            }
        });
    }

    @EventHandler
    private void onMouseButton(MouseButtonEvent event) {
        if (ClientSync.INSTANCE.syncInputs.get()) sendPacket(new InputSyncMouseButtonC2CPacket(event.button, event.action, event.modifiers));
    }

    @EventHandler
    private void onMouseScroll(MouseScrollEvent event) {
        if (ClientSync.INSTANCE.syncInputs.get()) sendPacket(new InputSyncMouseScrollC2CPacket(event.horizontalScroll, event.verticalScroll));
    }

    @EventHandler
    private void onMouseMove(MouseMoveEvent event) {
        if (ClientSync.INSTANCE.syncInputs.get()) sendPacket(new InputSyncMouseMoveC2CPacket(event.x, event.y));
    }

    @EventHandler
    private void onKeyPress(KeyPressEvent event) {
        if (ClientSync.INSTANCE.syncInputs.get()) sendPacket(new InputSyncKeyPressC2CPacket(event.key, event.scancode, event.action, event.modifiers));
    }

    @EventHandler
    private void onCharTyped(CharTypedEvent event) {
        System.out.println("Inside event handler :O");
        if (ClientSync.INSTANCE.syncInputs.get()) sendPacket(new InputSyncCharTypedC2CPacket(event.codePoint, event.modifiers));
    }
}
