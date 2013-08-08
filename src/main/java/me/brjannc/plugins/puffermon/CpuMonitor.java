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

import java.lang.management.ManagementFactory;
import java.util.Collection;
import java.util.Collections;
import java.util.logging.Logger;

public class CpuMonitor extends AveragingMonitor {

    private static final Logger log = Logger.getLogger("Minecraft.PufferMon");
    private final com.sun.management.OperatingSystemMXBean osmx;
    private long startNanos;
    private long startCpuNanos;

    public CpuMonitor(PufferMon plugin) {
        super(plugin, 5, 0.0);

        java.lang.management.OperatingSystemMXBean osmx = ManagementFactory.getOperatingSystemMXBean();
        if (osmx instanceof com.sun.management.OperatingSystemMXBean) {
            this.osmx = (com.sun.management.OperatingSystemMXBean) osmx;
        } else {
            log.warning("[PufferMon] CPU monitoring is not available on this platform");
            this.osmx = null;
        }
    }

    private long cpuTimeNanos() {
        if (osmx == null) {
            return -1;
        }
        return osmx.getProcessCpuTime();
    }

    @Override
    public void start() {
        if (osmx == null) {
            return;
        }
        startNanos = System.nanoTime();
        startCpuNanos = cpuTimeNanos();
        super.start();
    }

    // http://nagiosplug.sourceforge.net/developer-guidelines.html#AEN201
    // 'label'=value[UOM];[warn];[crit];[min];[max]
    public synchronized String getStatus() {
        double[] avg = getAverages();
        Collection<Double> history = getHistory();

        double max = (Double) Collections.max(history);
        return String.format("cpu1=%.1f%%;;;0; cpu1_max=%.1f%%;;;0; cpu5=%.1f%%;;;0; cpu15=%.1f%%;;;0;", avg[0], max, avg[1], avg[2]);
    }

    @Override
    protected double update() {
        long currentNanos = System.nanoTime();
        long elapsedNanos = currentNanos - startNanos;

        long currentCpuNanos = cpuTimeNanos();
        long elapsedCpuNanos = currentCpuNanos - startCpuNanos;

        double currentCpuPct = (double) elapsedCpuNanos / elapsedNanos;

        startNanos = currentNanos;
        startCpuNanos = currentCpuNanos;
        return currentCpuPct * 100;
    }
}
