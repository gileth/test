// 
// Decompiled by Procyon v0.5.30
// 

package web.wx.utils;

import java.awt.image.BufferedImage;
import jp.sourceforge.qrcode.data.QRCodeImage;

public class TwoDimensionCodeImage implements QRCodeImage
{
    BufferedImage bufImg;
    
    public TwoDimensionCodeImage(final BufferedImage bufImg) {
        this.bufImg = bufImg;
    }
    
    public int getHeight() {
        return this.bufImg.getHeight();
    }
    
    public int getPixel(final int x, final int y) {
        return this.bufImg.getRGB(x, y);
    }
    
    public int getWidth() {
        return this.bufImg.getWidth();
    }
}
