// 
// Decompiled by Procyon v0.5.30
// 

package web.wx.utils;

import java.awt.Image;
import java.awt.image.ImageObserver;
import org.apache.commons.lang.StringUtils;
import java.io.InputStream;
import jp.sourceforge.qrcode.exception.DecodingFailedException;
import java.io.IOException;
import jp.sourceforge.qrcode.data.QRCodeImage;
import jp.sourceforge.qrcode.QRCodeDecoder;
import java.awt.Graphics2D;
import java.awt.Color;
import com.swetake.util.Qrcode;
import java.awt.image.BufferedImage;
import java.awt.image.RenderedImage;
import javax.imageio.ImageIO;
import java.io.File;
import java.io.OutputStream;

public class TwoDimensionCode
{
    public void encoderQRCode(final String content, final String imgPath) {
        this.encoderQRCode(content, imgPath, "png", 7);
    }
    
    public void encoderQRCode(final String content, final OutputStream output) {
        this.encoderQRCode(content, output, "png", 7);
    }
    
    public void encoderQRCode(final String content, final String imgPath, final String imgType) {
        this.encoderQRCode(content, imgPath, imgType, 7);
    }
    
    public void encoderQRCode(final String content, final OutputStream output, final String imgType) {
        this.encoderQRCode(content, output, imgType, 7);
    }
    
    public void encoderQRCode(final String content, final String imgPath, final String imgType, final int size) {
        try {
            final BufferedImage bufImg = this.qRCodeCommon(content, imgType, size);
            final File imgFile = new File(imgPath);
            ImageIO.write(bufImg, imgType, imgFile);
        }
        catch (Exception e) {
            e.printStackTrace();
        }
    }
    
    public void encoderQRCode(final String content, final OutputStream output, final String imgType, final int size) {
        try {
            final BufferedImage bufImg = this.qRCodeCommon(content, imgType, size);
            ImageIO.write(bufImg, imgType, output);
        }
        catch (Exception e) {
            e.printStackTrace();
        }
    }
    
    private BufferedImage qRCodeCommon(final String content, final String imgType, final int size) {
        BufferedImage bufImg = null;
        try {
            final Qrcode qrcodeHandler = new Qrcode();
            qrcodeHandler.setQrcodeErrorCorrect('M');
            qrcodeHandler.setQrcodeEncodeMode('B');
            qrcodeHandler.setQrcodeVersion(size);
            final byte[] contentBytes = content.getBytes("utf-8");
            final int imgSize = 67 + 12 * (size - 1);
            bufImg = new BufferedImage(imgSize, imgSize, 1);
            final Graphics2D gs = bufImg.createGraphics();
            gs.setBackground(Color.WHITE);
            gs.clearRect(0, 0, imgSize, imgSize);
            gs.setColor(Color.BLACK);
            final int pixoff = 2;
            if (contentBytes.length <= 0 || contentBytes.length >= 800) {
                throw new Exception("QRCode content bytes length = " + contentBytes.length + " not in [0, 800].");
            }
            final boolean[][] codeOut = qrcodeHandler.calQrcode(contentBytes);
            for (int i = 0; i < codeOut.length; ++i) {
                for (int j = 0; j < codeOut.length; ++j) {
                    if (codeOut[j][i]) {
                        gs.fillRect(j * 3 + pixoff, i * 3 + pixoff, 3, 3);
                    }
                }
            }
            gs.dispose();
            bufImg.flush();
        }
        catch (Exception e) {
            e.printStackTrace();
        }
        return bufImg;
    }
    
    public String decoderQRCode(final String imgPath) {
        final File imageFile = new File(imgPath);
        BufferedImage bufImg = null;
        String content = null;
        try {
            bufImg = ImageIO.read(imageFile);
            final QRCodeDecoder decoder = new QRCodeDecoder();
            content = new String(decoder.decode((QRCodeImage)new TwoDimensionCodeImage(bufImg)), "utf-8");
        }
        catch (IOException e) {
            System.out.println("Error: " + e.getMessage());
            e.printStackTrace();
        }
        catch (DecodingFailedException dfe) {
            System.out.println("Error: " + dfe.getMessage());
            dfe.printStackTrace();
        }
        return content;
    }
    
    public String decoderQRCode(final InputStream input) {
        BufferedImage bufImg = null;
        String content = null;
        try {
            bufImg = ImageIO.read(input);
            final QRCodeDecoder decoder = new QRCodeDecoder();
            content = new String(decoder.decode((QRCodeImage)new TwoDimensionCodeImage(bufImg)), "utf-8");
        }
        catch (IOException e) {
            System.out.println("Error: " + e.getMessage());
            e.printStackTrace();
        }
        catch (DecodingFailedException dfe) {
            System.out.println("Error: " + dfe.getMessage());
            dfe.printStackTrace();
        }
        return content;
    }
    
    public static int createQRCode(final String content, final String imgPath, final String ccbPath) {
        try {
            final Qrcode qrcodeHandler = new Qrcode();
            qrcodeHandler.setQrcodeErrorCorrect('M');
            qrcodeHandler.setQrcodeEncodeMode('B');
            qrcodeHandler.setQrcodeVersion(7);
            final byte[] contentBytes = content.getBytes("gb2312");
            final BufferedImage bufImg = new BufferedImage(140, 140, 1);
            final Graphics2D gs = bufImg.createGraphics();
            gs.setBackground(Color.WHITE);
            gs.clearRect(0, 0, 140, 140);
            gs.setColor(Color.BLACK);
            final int pixoff = 2;
            if (contentBytes.length <= 0 || contentBytes.length >= 120) {
                System.err.println("QRCode content bytes length = " + contentBytes.length + " not in [ 0,120 ]. ");
                return -1;
            }
            final boolean[][] codeOut = qrcodeHandler.calQrcode(contentBytes);
            for (int i = 0; i < codeOut.length; ++i) {
                for (int j = 0; j < codeOut.length; ++j) {
                    if (codeOut[j][i]) {
                        gs.fillRect(j * 3 + pixoff, i * 3 + pixoff, 3, 3);
                    }
                }
            }
            if (!StringUtils.isEmpty(ccbPath)) {
                final Image img = ImageIO.read(new File(ccbPath));
                gs.drawImage(img, 55, 55, null);
                gs.dispose();
                bufImg.flush();
                gs.drawImage(img, 55, 55, null);
                gs.dispose();
                bufImg.flush();
            }
            final File imgFile = new File(imgPath);
            ImageIO.write(bufImg, "png", imgFile);
        }
        catch (Exception e) {
            e.printStackTrace();
            return -100;
        }
        return 0;
    }
}
