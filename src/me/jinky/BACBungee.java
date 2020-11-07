// 
// Decompiled by Procyon v0.5.36
// 

package me.jinky;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

import net.md_5.bungee.api.chat.BaseComponent;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.config.ServerInfo;
import net.md_5.bungee.api.connection.Server;
import net.md_5.bungee.api.event.PluginMessageEvent;
import net.md_5.bungee.api.plugin.Listener;
import net.md_5.bungee.api.plugin.Plugin;
import net.md_5.bungee.event.EventHandler;

public class BACBungee extends Plugin implements Listener
{
    public void onEnable() {
        getProxy().getPluginManager().registerListener((Plugin)this, (Listener)this);
        getProxy().registerChannel("BAC:BACPunish".toLowerCase());
        getProxy().registerChannel("BAC:BACAlert".toLowerCase());
        getProxy().getConsole().sendMessage((BaseComponent)new TextComponent("§a[BACBungee] Registered 2 message listeners across " + getProxy().getServers().size() + " linked servers."));
    }
    
    @EventHandler
    public void onPluginMessage(final PluginMessageEvent e) {
        final DataInputStream in = new DataInputStream(new ByteArrayInputStream(e.getData()));
        if (!e.getTag().startsWith("bac:")) {
            return;
        }
        final Server server = (Server)e.getSender();
        final ServerInfo srv = server.getInfo();
        if (e.getTag().equals("bac:bacpunish")) {
            try {
                final String cmd = in.readUTF();
                getProxy().getPluginManager().dispatchCommand(getProxy().getConsole(), cmd);
            }
            catch (IOException e2) {
                getProxy().getConsole().sendMessage((BaseComponent)new TextComponent("§c§lThere was an error reading the message from Basic Anti-Cheat [Ch: BACPunish] from '" + srv.getName() + "'"));
            }
        }
        else if (e.getTag().equals("bac:bacalert")) {
            try {
                final String alert = in.readUTF();
                for (final ServerInfo asi : getProxy().getServers().values()) {
                    if (!asi.getName().equals(srv.getName()) && asi.getPlayers().size() > 0) {
                        this.smsg("bac:bacalert", "§7[" + srv.getName() + "]§r" + alert, asi);
                    }
                }
            }
            catch (IOException e2) {
                getProxy().getConsole().sendMessage((BaseComponent)new TextComponent("§c§lThere was an error reading the message from Basic Anti-Cheat [Ch: BACAlert] from '" + srv.getName() + "'"));
            }
        }
    }
    
    public void smsg(final String channel, final String message, final ServerInfo server) {
        final ByteArrayOutputStream stream = new ByteArrayOutputStream();
        final DataOutputStream out = new DataOutputStream(stream);
        try {
            out.writeUTF(message);
        }
        catch (IOException e) {
            e.printStackTrace();
        }
        server.sendData(channel.toLowerCase(), stream.toByteArray());
    }
}
