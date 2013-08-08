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

public class PlayerMonitor {

    private final PufferMon plugin;

    public PlayerMonitor(PufferMon plugin) {
        this.plugin = plugin;
    }

    // http://nagiosplug.sourceforge.net/developer-guidelines.html#AEN201
    // 'label'=value[UOM];[warn];[crit];[min];[max]
    public String getStatus() {
        int players = plugin.getServer().getOnlinePlayers().length;
        int maxPlayers = plugin.getServer().getMaxPlayers();

        return String.format("players=%d;;;0;%d", players, maxPlayers);
    }
}
