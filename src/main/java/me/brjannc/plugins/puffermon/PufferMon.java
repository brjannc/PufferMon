/*
 * Copyright (C) 2012 brjannc <brjannc at gmail.com>
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package me.brjannc.plugins.puffermon;

import java.io.IOException;
import java.util.List;
import java.util.logging.Logger;
import org.bukkit.configuration.Configuration;
import org.bukkit.plugin.java.JavaPlugin;

public class PufferMon extends JavaPlugin {

    private static final Logger log = Logger.getLogger("Minecraft.PufferMon");
    private final PlayerMonitor playerMonitor;
    private final TickMonitor tickMonitor;
    private final CpuMonitor cpuMonitor;
    private QueryServer queryServer;

    public PufferMon() {
        this.playerMonitor = new PlayerMonitor(this);
        this.tickMonitor = new TickMonitor(this);
        this.cpuMonitor = new CpuMonitor(this);
    }

    @Override
    public void onEnable() {
        startup();

        log.info(this + " is now enabled");
    }

    @Override
    public void onDisable() {
        shutdown();

        log.info(this + " is now disabled");
    }

    public String getStatus() {
        StringBuilder response = new StringBuilder();
        response.append(playerMonitor.getStatus()).append(" ");
        response.append(MemoryMonitor.getStatus()).append(" ");
        response.append(tickMonitor.getStatus()).append(" ");
        response.append(cpuMonitor.getStatus()).append("\n");
        return response.toString();
    }

    private void startup() {
        Configuration config = getConfig();
        if (config.getKeys(true).isEmpty()) {
            config.options().copyDefaults(true);
        }

        tickMonitor.start();
        cpuMonitor.start();

        try {
            String host = getServer().getIp();
            if (host.isEmpty()) {
                host = "0.0.0.0";
            }
            int port = config.getInt("query-port");

            queryServer = new QueryServer(this, host, port);
            queryServer.allowAll((List<String>) config.getList("whitelist"));
            queryServer.start();
        } catch (IOException e) {
            log.severe("[PufferMon] Couldn't start query server: " + e);
        }
    }

    private void shutdown() {
        tickMonitor.stop();
        cpuMonitor.stop();

        saveConfig();
    }
}
