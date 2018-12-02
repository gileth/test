// 
// Decompiled by Procyon v0.5.30
// 

package org.takeback.util.context.beans;

import java.lang.management.RuntimeMXBean;
import java.lang.management.ThreadMXBean;
import java.lang.management.OperatingSystemMXBean;
import java.lang.management.MemoryUsage;
import java.lang.management.MemoryMXBean;
import java.lang.management.ManagementFactory;
import java.util.HashMap;

public class JVMStatBean
{
    public HashMap<String, Long> getMemoryUsageInfo() {
        final HashMap<String, Long> result = new HashMap<String, Long>();
        final MemoryMXBean m = ManagementFactory.getMemoryMXBean();
        MemoryUsage usage = m.getHeapMemoryUsage();
        result.put("heapCommitted", usage.getCommitted());
        result.put("heapInit", usage.getInit());
        result.put("heapMax", usage.getMax());
        result.put("heapUsed", usage.getUsed());
        usage = m.getNonHeapMemoryUsage();
        result.put("noHeapCommitted", usage.getCommitted());
        result.put("noHeapInit", usage.getInit());
        result.put("noHeapMax", usage.getMax());
        result.put("noHeapUsed", usage.getUsed());
        return result;
    }
    
    public HashMap<String, Long> gc() {
        final MemoryMXBean m = ManagementFactory.getMemoryMXBean();
        m.gc();
        return this.getMemoryUsageInfo();
    }
    
    public HashMap<String, Object> getOpSystemInfo() {
        final HashMap<String, Object> result = new HashMap<String, Object>();
        final OperatingSystemMXBean m = ManagementFactory.getOperatingSystemMXBean();
        result.put("arch", m.getArch());
        result.put("processors", m.getAvailableProcessors());
        result.put("name", m.getName());
        result.put("version", m.getVersion());
        result.put("loadAverage", m.getSystemLoadAverage());
        return result;
    }
    
    public HashMap<String, Object> getThreadsInfo() {
        final HashMap<String, Object> result = new HashMap<String, Object>();
        final ThreadMXBean m = ManagementFactory.getThreadMXBean();
        result.put("deamonTheadCount", m.getDaemonThreadCount());
        result.put("peakThreadCount", m.getPeakThreadCount());
        result.put("threadCount", m.getThreadCount());
        result.put("totalStartedThreadCount", m.getTotalStartedThreadCount());
        return result;
    }
    
    public long[] getThreadIds() {
        final ThreadMXBean m = ManagementFactory.getThreadMXBean();
        return m.getAllThreadIds();
    }
    
    public HashMap<String, Object> getRuntimeInfo() {
        final HashMap<String, Object> result = new HashMap<String, Object>();
        final RuntimeMXBean m = ManagementFactory.getRuntimeMXBean();
        result.put("name", m.getName());
        result.put("startTime", m.getStartTime());
        result.put("upTime", m.getUptime());
        result.put("specName", m.getSpecName());
        result.put("specVendor", m.getSpecVendor());
        result.put("specVersion", m.getSpecVersion());
        result.put("vmName", m.getVmName());
        result.put("vmVendor", m.getVmVendor());
        result.put("vmVersion", m.getVmVersion());
        result.put("managementSpecVersion", m.getManagementSpecVersion());
        return result;
    }
}
