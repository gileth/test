// 
// Decompiled by Procyon v0.5.30
// 

package org.takeback.util;

import java.io.IOException;
import java.net.InetAddress;
import java.net.DatagramPacket;
import java.net.DatagramSocket;

class UdpGetClientMacAddr
{
    private String remoteAddr;
    private int remotePort;
    private byte[] buffer;
    private DatagramSocket ds;
    
    public UdpGetClientMacAddr(final String strAddr) throws Exception {
        this.remotePort = 137;
        this.buffer = new byte[1024];
        this.ds = null;
        this.remoteAddr = strAddr;
        this.ds = new DatagramSocket();
    }
    
    protected final DatagramPacket send(final byte[] bytes) throws IOException {
        final DatagramPacket dp = new DatagramPacket(bytes, bytes.length, InetAddress.getByName(this.remoteAddr), this.remotePort);
        this.ds.send(dp);
        return dp;
    }
    
    protected final DatagramPacket receive() throws Exception {
        final DatagramPacket dp = new DatagramPacket(this.buffer, this.buffer.length);
        this.ds.receive(dp);
        return dp;
    }
    
    protected byte[] GetQueryCmd() throws Exception {
        final byte[] t_ns = new byte[50];
        t_ns[0] = 0;
        t_ns[2] = (t_ns[1] = 0);
        t_ns[3] = 16;
        t_ns[4] = 0;
        t_ns[5] = 1;
        t_ns[7] = (t_ns[6] = 0);
        t_ns[9] = (t_ns[8] = 0);
        t_ns[11] = (t_ns[10] = 0);
        t_ns[12] = 32;
        t_ns[13] = 67;
        t_ns[14] = 75;
        for (int i = 15; i < 45; ++i) {
            t_ns[i] = 65;
        }
        t_ns[46] = (t_ns[45] = 0);
        t_ns[47] = 33;
        t_ns[48] = 0;
        t_ns[49] = 1;
        return t_ns;
    }
    
    protected final String getMacAddr(final byte[] brevdata) throws Exception {
        final int i = brevdata[56] * 18 + 56;
        String sAddr = "";
        final StringBuffer sb = new StringBuffer(17);
        for (int j = 1; j < 7; ++j) {
            sAddr = Integer.toHexString(0xFF & brevdata[i + j]);
            if (sAddr.length() < 2) {
                sb.append(0);
            }
            sb.append(sAddr.toUpperCase());
            if (j < 6) {
                sb.append(':');
            }
        }
        return sb.toString();
    }
    
    public final void close() {
        try {
            this.ds.close();
        }
        catch (Exception ex) {
            ex.printStackTrace();
        }
    }
    
    public final String getRemoteMacAddr() throws Exception {
        final byte[] bqcmd = this.GetQueryCmd();
        this.send(bqcmd);
        final DatagramPacket dp = this.receive();
        final String smac = this.getMacAddr(dp.getData());
        this.close();
        return smac;
    }
}
