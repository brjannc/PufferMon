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

import java.util.Collection;
import java.util.Collections;
import java.util.logging.Logger;

public class TickMonitor extends AveragingMonitor {

    private static final Logger log = Logger.getLogger("Minecraft.PufferMon");
    private long startMillis;

    public TickMonitor(PufferMon plugin) {
        super(plugin, 5, 20.0);
    }

    @Override
    public void start() {
        startMillis = System.currentTimeMillis();
        super.start();
    }

    // http://nagiosplug.sourceforge.net/developer-guidelines.html#AEN201
    // 'label'=value[UOM];[warn];[crit];[min];[max]
    public synchronized String getStatus() {
        double[] avg = getAverages();
        Collection<Double> history = getHistory();

        double min = (Double) Collections.min(history);
        return String.format("tps1=%.1f;;;0; tps1_min=%.1f;;;0; tps5=%.1f;;;0; tps15=%.1f;;;0;", avg[0], min, avg[1], avg[2]);
    }

    @Override
    protected double update() {
        long currentMillis = System.currentTimeMillis();
        long elapsedMillis = currentMillis - startMillis;

        double currentTps = 1000.0 * getPeriodTicks() / elapsedMillis;

        startMillis = currentMillis;
        return currentTps;
    }
}
