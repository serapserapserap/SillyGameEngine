package serap.sillyge.graphics.opengl.texture;

import lombok.Getter;
import lombok.Setter;
import org.jetbrains.annotations.NotNull;
import org.lwjgl.BufferUtils;
import serap.sillyge.graphics.opengl.IntegerIdentifiable;

import java.awt.image.BufferedImage;
import java.nio.ByteBuffer;

import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.opengl.GL11.GL_TEXTURE_WRAP_T;
import static org.lwjgl.opengl.GL12.GL_CLAMP_TO_EDGE;
import static org.lwjgl.opengl.GL30.glGenerateMipmap;
import static serap.sillyge.graphics.opengl.OpenGL.bindTexture;

public abstract class Texture implements IntegerIdentifiable {
    @Getter
    protected int width, height;

    private int identifier = -1;
    @Getter
    @Setter
    protected int boundAtTextureUnit = -1;

    public void onUnbind() {
        this.boundAtTextureUnit = -1;
    }

    public void onBind(int slot) {
        this.boundAtTextureUnit = slot;
    }

    public void init() {
        this.identifier = glGenTextures();
    }

    @Override
    public int getIdentifier() {
        return this.identifier;
    }

    @Override
    public void close() {
        if(this.identifier != -1) {
            glDeleteTextures(this.identifier);
            this.identifier = -1;
        }
    }

    public void defaultParams() {
        this.setTextureParameters(GL_LINEAR, GL_LINEAR, GL_CLAMP_TO_EDGE, GL_CLAMP_TO_EDGE);
    }

    public void setTextureParameters(int minimisationFilter, int magnificationFilter, int textureWrapSide, int textureWrapTop) {
        bindTexture(this);
        glTexParameteri(this.getTextureType(), GL_TEXTURE_MIN_FILTER, minimisationFilter);
        glTexParameteri(this.getTextureType(), GL_TEXTURE_MAG_FILTER, magnificationFilter);

        //I think S and T means side and top?
        glTexParameteri(this.getTextureType(), GL_TEXTURE_WRAP_S, textureWrapSide);
        glTexParameteri(this.getTextureType(), GL_TEXTURE_WRAP_T, textureWrapTop);
    }

    public void generateMipMap() {
        glGenerateMipmap(this.getTextureType());
    }

    /*
        mipmapping notes
        - you SHOULD mipmap sprites that are used in 3d, but you should apply padding to the respective atlas to prevent texture bleeding
        - you should NOT apply mipmapping to sprites that are used in 2d (item icons, gui textures, etc)
     */

    public abstract int getTextureType();

    @SuppressWarnings("all") //shut the FUCK up
    protected boolean isResolutionSet() {
        return this.height != -1 && this.width != -1;
    }

    public Texture() {
        this(-1,-1);
    }
    public Texture(int width, int height) {
        this.width = width;
        this.height = height;
    }

    public void setResolution(int width, int height) {
        this.width = width;
        this.height = height;
    }

    @Override
    public boolean equals(Object other) {
        if(other instanceof Texture texture) {
            if(!this.isInitialised() || !texture.isInitialised()) {
                return super.equals(other);
            }
            return this.getIdentifier() == texture.getIdentifier();
        }
        return false;
    }

    @NotNull
    public static ByteBuffer extractOpenGLSuitablePixelData(@NotNull BufferedImage img) {
        int w,h;
        w = img.getWidth();
        h = img.getHeight();
        int[] data = new int[w * h];
        img.getRGB(0,0, w, h, data, 0, w);
        //img area * 4 (4 because R, G, B & A is each a byte)
        ByteBuffer buff = BufferUtils.createByteBuffer(w * h * 4);

        for(int y = h - 1 ; y >= 0 ; y--) {
            for(int x = 0 ; x < w ; x++) {
                int pixel = data[y * w + x];
                buff.putInt(pixel);
            }
        }
        return buff.flip();
    }

    @NotNull
    public static int[] extractOpenGLSuitablePixelDataI(@NotNull BufferedImage img) {
        int w = img.getWidth(), h = img.getHeight();
        int[] data = new int[w * h];
        img.getRGB(0,0, w, h, data, 0, w);

        int[] openGLSuitableData = new int[w * h * 4];
        int index = 0;

        for(int y = h - 1 ; y >= 0 ; y--) {
            for(int x = 0 ; x < w ; x++) {
                int pixel = data[y * w + x];
                openGLSuitableData[index + 1] = (byte) ((pixel >> 16) & 0xFF);
                openGLSuitableData[index + 2] = (byte) ((pixel >> 8) & 0xFF);
                openGLSuitableData[index + 3] = (byte) (pixel & 0xFF);
                openGLSuitableData[index    ] = (byte) ((pixel >> 24) & 0xFF);
                index = index + 4;
            }
        }
        return openGLSuitableData;
    }
}

