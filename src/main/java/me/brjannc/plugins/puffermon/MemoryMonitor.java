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

public class MemoryMonitor {

    private MemoryMonitor() {
    }

    // http://nagiosplug.sourceforge.net/developer-guidelines.html#AEN201
    // 'label'=value[UOM];[warn];[crit];[min];[max]
    public static String getStatus() {
        Runtime runtime = Runtime.getRuntime();
        double memUsed = (runtime.totalMemory() - runtime.freeMemory()) / 1048576.0;
        double memMax = runtime.maxMemory() / 1048576.0;

        return String.format("mem=%.1fMB;;;0;%.1f", memUsed, memMax);
    }
}
