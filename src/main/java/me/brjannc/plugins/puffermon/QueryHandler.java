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

import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.util.logging.Logger;

public class QueryHandler implements Runnable {

    private static final Logger log = Logger.getLogger("Minecraft.PufferMon");
    private final PufferMon plugin;
    private final Socket socket;

    public QueryHandler(PufferMon plugin, Socket socket) {
        this.plugin = plugin;
        this.socket = socket;
    }

    @Override
    public void run() {
        try {
            //BufferedReader reader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            //String request = reader.readLine();
            DataOutputStream out = new DataOutputStream(socket.getOutputStream());
            out.writeBytes(plugin.getStatus());
            socket.close();
        } catch (IOException e) {
            log.severe("[PufferMon] Request thread terminating due to error: " + e);
        }
    }
}
