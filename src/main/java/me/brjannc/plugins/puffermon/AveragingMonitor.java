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

import java.util.ArrayDeque;
import java.util.Collection;
import java.util.logging.Logger;

public abstract class AveragingMonitor implements Runnable {

    private static final Logger log = Logger.getLogger("Minecraft.PufferMon");
    private final PufferMon plugin;
    private final int period;
    private final int periodTicks;
    private final int maxHistory;
    private double[] avg;
    private double[] weight;
    private ArrayDeque<Double> history;
    private int taskId;

    public AveragingMonitor(PufferMon plugin, int period, double initialValue) {
        this.plugin = plugin;
        this.period = period;
        periodTicks = period * 20;

        avg = new double[3];
        avg[0] = avg[1] = avg[2] = initialValue;

        weight = new double[3];
        weight[0] = Math.exp(-period / 60.0);
        weight[1] = Math.exp(-period / 300.0);
        weight[2] = Math.exp(-period / 900.0);

        maxHistory = 60 / period;
        history = new ArrayDeque<Double>(maxHistory);
        history.add(initialValue);

        taskId = -1;
    }

    public void start() {
        taskId = plugin.getServer().getScheduler().scheduleSyncRepeatingTask(plugin, this, periodTicks, periodTicks);
        if (taskId < 0) {
            log.severe("[PufferMon] Monitor scheduling failed");
        }
    }

    public void stop() {
        if (taskId >= 0) {
            plugin.getServer().getScheduler().cancelTask(taskId);
            taskId = -1;
        }
    }

    public int getPeriod() {
        return period;
    }

    public int getPeriodTicks() {
        return periodTicks;
    }

    public double[] getAverages() {
        return avg;
    }

    public Collection<Double> getHistory() {
        return history;
    }

    protected abstract double update();

    private double runningAverage(double avg, double weight, double value) {
        return (avg * weight) + (value * (1.0 - weight));
    }

    public synchronized void run() {
        double value = update();

        for (int i = 0; i < avg.length; ++i) {
            avg[i] = runningAverage(avg[i], weight[i], value);
        }

        if (history.size() == maxHistory) {
            history.removeLast();
        }
        history.addFirst(value);
    }
}
