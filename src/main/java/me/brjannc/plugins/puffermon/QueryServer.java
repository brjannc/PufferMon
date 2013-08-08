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
import java.net.*;
import java.util.Collection;
import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.logging.Logger;

public class QueryServer extends Thread {

    private static final Logger log = Logger.getLogger("Minecraft.PufferMon");
    private final PufferMon plugin;
    private Set<InetAddress> whitelist;
    private final ServerSocket serverSocket;
    private final ExecutorService pool;

    public QueryServer(PufferMon plugin, String host, int port) throws IOException {
        this.plugin = plugin;
        this.whitelist = new HashSet<InetAddress>();
        InetSocketAddress address = new InetSocketAddress(host, port);
        serverSocket = new ServerSocket();
        serverSocket.bind(address);
        pool = Executors.newCachedThreadPool();

        log.info("[PufferMon] Query server listening on " + host + ":" + port);
    }

    @Override
    public void run() {
        try {
            while (true) {
                Socket socket = serverSocket.accept();
                if (!checkAccept(socket)) {
                    socket.close();
                    continue;
                }

                pool.execute(new QueryHandler(plugin, socket));
            }
        } catch (IOException e) {
            pool.shutdown();
            log.severe("[PufferMon] Query server terminating due to error: " + e);
        }
    }

    public void allow(String host) {
        try {
            InetAddress address = InetAddress.getByName(host);
            whitelist.add(address);
        } catch (UnknownHostException e) {
            log.warning("[PufferMon] Unknown host '" + host + "' in whitelist");
        }
    }

    public void allowAll(Collection<String> hosts) {
        if (hosts == null) {
            return;
        }

        for (String host : hosts) {
            allow(host);
        }
    }

    private boolean checkAccept(Socket socket) {
        InetAddress remoteAddress = socket.getInetAddress();
        if (whitelist.contains(remoteAddress)) {
            return true;
        }

        //log.info("[PufferMon] Refusing connection from " + remoteAddress.getHostAddress());
        return false;
    }
}
