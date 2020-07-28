package sekelsta.horse_colors.renderer;

import java.io.IOException;

import net.minecraft.client.renderer.texture.*;
import net.minecraft.resources.IResource;
import net.minecraft.resources.IResourceManager;
import net.minecraft.util.ResourceLocation;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class TextureLayer {
    private static final Logger LOGGER = LogManager.getLogger();

    public String name;
    public String description;
    public Type type;
    public int alpha;
    public int red;
    public int green;
    public int blue;
    // Don't go overboard and chain thousands of layers together
    // They all have to fit in memory at once and are rendered
    // recursively so there is potential for stack overflow
    public TextureLayer next;
    public TextureLayer() {
        name = null;
        description = null;
        type = Type.NORMAL;
        alpha = 255;
        red = 255;
        green = 255;
        blue = 255;
    }

    public enum Type {
        NORMAL,
        NO_ALPHA,
        MASK,
        SHADE,
        HIGHLIGHT,
        POWER,
        ROOT
    }

    public NativeImage getLayer(IResourceManager manager) {
        if (this.name == null) {
            LOGGER.error("Attempting to load unspecified texture (name is null)\n");
            return null;
        }
        try (IResource iresource = manager.getResource(new ResourceLocation(this.name))) {
            NativeImage image = net.minecraftforge.client.MinecraftForgeClient.getImageLayer(new ResourceLocation(this.name), manager);
            if (this.next != null) {
                colorLayer(image);
                this.next.combineLayers(image, this.next.getLayer(manager));
                // Avoid double multiply
                this.red = 0xff;
                this.green = 0xff;
                this.blue = 0xff;
            }
            return image;
        } catch (IOException ioexception) {
            LOGGER.error("Couldn't load layered image", (Throwable)ioexception);
        }
        return null;
    }

    public void combineLayers(NativeImage base, NativeImage image) {
        switch(this.type) {
            case NORMAL:
                blendLayer(base, image);
                break;
            case NO_ALPHA:
                blendLayerKeepAlpha(base, image);
                break;
            case MASK:
                maskLayer(base, image);
                break;
            case SHADE:
                shadeLayer(base, image);
                break;
            case HIGHLIGHT:
                highlightLayer(base, image);
                break;
            case POWER:
                powerLayer(base, image);
                break;
            case ROOT:
                rootLayer(base, image);
                break;
        }
    }

    public void blendLayer(NativeImage base, NativeImage image) {
        for(int i = 0; i < image.getHeight(); ++i) {
            for(int j = 0; j < image.getWidth(); ++j) {
                base.blendPixel(j, i, this.multiply(image.getPixelRGBA(j, i)));
            }
        }
    }

    public void blendLayerKeepAlpha(NativeImage base, NativeImage image) {
        for(int i = 0; i < image.getHeight(); ++i) {
            for(int j = 0; j < image.getWidth(); ++j) {
                int cb = base.getPixelRGBA(j, i);
                int ci = this.multiply(image.getPixelRGBA(j, i));
                float a = getAlpha(ci) / 255.0F;
                float r = getRed(ci);
                float g = getGreen(ci);
                float b = getBlue(ci);
                float br = getRed(cb);
                float bg = getGreen(cb);
                float bb = getBlue(cb);
                int fa = getAlpha(cb);
                int fr = (int)(r * a + br * (1.0F-a));
                int fg = (int)(g * a + bg * (1.0F-a));
                int fb = (int)(b * a + bb * (1.0F-a));
                base.setPixelRGBA(j, i, getCombined(fa, fb, fg, fr));
            }
        }
    }

    public void shadeLayer(NativeImage base, NativeImage image) {
        for(int i = 0; i < image.getHeight(); ++i) {
            for(int j = 0; j < image.getWidth(); ++j) {
                int color = base.getPixelRGBA(j, i);
                int shading = this.multiply(image.getPixelRGBA(j, i));
                base.setPixelRGBA(j, i, this.shade(color, shading));
            }
        }
    }

    public void highlightLayer(NativeImage base, NativeImage image) {
        for(int i = 0; i < image.getHeight(); ++i) {
            for(int j = 0; j < image.getWidth(); ++j) {
                int color = base.getPixelRGBA(j, i);
                int highlight = this.multiply(image.getPixelRGBA(j, i));
                base.setPixelRGBA(j, i, this.highlight(color, highlight));
            }
        }
    }

    public void maskLayer(NativeImage base, NativeImage image) {
        for(int i = 0; i < image.getHeight(); ++i) {
            for(int j = 0; j < image.getWidth(); ++j) {
                int color = base.getPixelRGBA(j, i);
                // Don't multiply here because that would do the wrong thing
                int mask = image.getPixelRGBA(j, i);
                int maskedColor = this.mask(color, mask);
                base.setPixelRGBA(j, i, maskedColor);
            }
        }
    }

    // Raise RGB values to an exponent >= 1
    public void powerLayer(NativeImage base, NativeImage image) {
        for(int i = 0; i < image.getHeight(); ++i) {
            for(int j = 0; j < image.getWidth(); ++j) {
                int color = base.getPixelRGBA(j, i);
                int exp = image.getPixelRGBA(j, i);
                exp = this.multiply(exp);
                base.blendPixel(j, i, this.power(color, exp));
            }
        }
    }

    // Raise RGB values to an exponent <= 1
    public void rootLayer(NativeImage base, NativeImage image) {
        for(int i = 0; i < image.getHeight(); ++i) {
            for(int j = 0; j < image.getWidth(); ++j) {
                int color = base.getPixelRGBA(j, i);
                int exp = image.getPixelRGBA(j, i);
                exp = this.multiply(exp);
                base.blendPixel(j, i, this.root(color, exp));
            }
        }
    }

    public void colorLayer(NativeImage image) {
        for(int i = 0; i < image.getHeight(); ++i) {
            for(int j = 0; j < image.getWidth(); ++j) {
                int color = image.getPixelRGBA(j, i);
                image.setPixelRGBA(j, i, this.multiply(color));
            }
        }
    }


    public int multiply(int color) {
        int a = getAlpha(color);
        a = (int)((float)a * (float)this.alpha / 255.0F);
        int r = getRed(color);
        r = (int)((float)r * (float)this.red / 255.0F);
        int g = getGreen(color);
        g = (int)((float)g * (float)this.green / 255.0F);
        int b = getBlue(color);
        b = (int)((float)b * (float)this.blue / 255.0F);
        return getCombined(a, b, g, r);
    }

    public int shade(int color, int shading) {
        float cr = getRed(color);
        float cg = getGreen(color);
        float cb = getBlue(color);
        float sr = getRed(shading);
        float sg = getGreen(shading);
        float sb = getBlue(shading);
        float a = (float)getAlpha(shading) / 255.0F;
        float avg = (float)(cr + cg + cb) / 255.0F / 3.0F;
        a *= 0.5f + 0.5f * (1f - avg) * (1f - avg);
        float na = 1.0F - a;
        float r = Math.max(0, Math.min(255.0F, sr * a + cr * na));
        float g = Math.max(0, Math.min(255.0F, sg * a + cg * na));
        float b = Math.max(0, Math.min(255.0F, sb * a + cb * na));
        int ca = getAlpha(color);
        return getCombined(ca, (int)b, (int)g, (int)r);
    }
    
    public int highlight(int color, int light) {
        float r0 = getRed(color);
        float g0 = getGreen(color);
        float b0 = getBlue(color);
        float r1 = getRed(light);
        float g1 = getGreen(light);
        float b1 = getBlue(light);
        float a = (float)getAlpha(light) / 255.0F;
        float avg = (float)(r0 + g0 + b0) / 255.0F / 3.0F;
        a *= 0.5f + 0.5f * avg * avg;
        float na = 1.0F - a;
        float r = Math.max(0, Math.min(255.0F, r1 * a + r0 * na));
        float g = Math.max(0, Math.min(255.0F, g1 * a + g0 * na));
        float b = Math.max(0, Math.min(255.0F, b1 * a + b0 * na));
        int ca = getAlpha(color);
        return getCombined(ca, (int)b, (int)g, (int)r);
    }

    // For each RGB value, raise color to the 1 / exp
    public int power(int color, int exp) {
        float r0 = getRed(color) / 255f;
        float g0 = getGreen(color) / 255f;
        float b0 = getBlue(color) / 255f;
        // No dividing by 0
        float r1 = Math.max(0.002f, getRed(exp) / 255f);
        float g1 = Math.max(0.002f, getGreen(exp) / 255f);
        float b1 = Math.max(0.002f, getBlue(exp) / 255f);
        int r = clamp((int)(255f * Math.pow(r0, 1f / r1)));
        int g = clamp((int)(255f * Math.pow(g0, 1f / g1)));
        int b = clamp((int)(255f * Math.pow(b0, 1f / b1)));
        int a = getAlpha(exp);
        return getCombined(a, b, g, r);
    }

    // For each RGB value, raise color to the exp
    public int root(int color, int exp) {
        float r0 = getRed(color) / 255f;
        float g0 = getGreen(color) / 255f;
        float b0 = getBlue(color) / 255f;
        float r1 = getRed(exp) / 255f;
        float g1 = getGreen(exp) / 255f;
        float b1 = getBlue(exp) / 255f;
        int r = clamp((int)(255f * Math.pow(r0, r1)));
        int g = clamp((int)(255f * Math.pow(g0, g1)));
        int b = clamp((int)(255f * Math.pow(b0, b1)));
        int a = getAlpha(exp);
        return getCombined(a, b, g, r);
    }

    public int mask(int color, int mask) {
        float a = getAlpha(color) * getAlpha(mask);
        a /= 255.0F;
        float weight = this.alpha / 255f;
        a = a * weight + getAlpha(color) * (1 - weight);
        int r = getRed(color);
        int g = getGreen(color);
        int b = getBlue(color);
        return getCombined((int)a, b, g, r);
    }

    // Restrict to range [0, 255]
    public void clamp() {
        this.red = clamp(this.red);
        this.green = clamp(this.green);
        this.blue = clamp(this.blue);
    }
    private int clamp(int x) {
        return Math.max(0, Math.min(x, 255));
    }

    private String getAbv(String s) {
        int i = s.lastIndexOf("/");
        if (i > -1) {
            s = s.substring(i + 1);
        }
        if (s.endsWith(".png")) {
            s = s.substring(0, s.length() - 4);
        }
        return s;
    }

    public String toString() {
        if (this.name == null) {
            return "";
        }
        String s = getAbv(this.name);
        s += "-" + this.type.toString();
        s += "-" + Integer.toHexString(this.alpha);
        s += Integer.toHexString(this.red);
        s += Integer.toHexString(this.green);
        s += Integer.toHexString(this.blue);
        return s;
    }

   public static int getAlpha(int col) {
      return col >> 24 & 255;
   }

   public static int getRed(int col) {
      return col >> 0 & 255;
   }

   public static int getGreen(int col) {
      return col >> 8 & 255;
   }

   public static int getBlue(int col) {
      return col >> 16 & 255;
   }

   public static int getCombined(int alpha, int blue, int green, int red) {
      return (alpha & 255) << 24 | (blue & 255) << 16 | (green & 255) << 8 | (red & 255) << 0;
   }
}
