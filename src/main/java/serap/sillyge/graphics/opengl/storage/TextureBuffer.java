package serap.sillyge.graphics.opengl.storage;

import lombok.Getter;
import lombok.Setter;
import org.jetbrains.annotations.NotNull;
import org.lwjgl.opengl.GL41;
import org.lwjgl.system.MemoryUtil;
import serap.sillyge.graphics.exception.GLException;
import serap.sillyge.graphics.opengl.OpenGL;
import serap.sillyge.graphics.opengl.texture.Texture;

import java.nio.ByteBuffer;
import java.nio.FloatBuffer;
import java.nio.IntBuffer;

import static org.lwjgl.opengl.GL15.*;
import static org.lwjgl.opengl.GL15.glBufferSubData;
import static org.lwjgl.opengl.GL30.glMapBufferRange;
import static org.lwjgl.opengl.GL31.GL_TEXTURE_BUFFER;
import static org.lwjgl.opengl.GL31.glTexBuffer;

public abstract class TextureBuffer implements OpenGLBuffer {

    private static class StorageTexture extends Texture {

        @Override
        public int getTextureType() {
            return GL_TEXTURE_BUFFER;
        }

        void bind(int textureUnit, @NotNull TextureBuffer parentBuffer) {
            OpenGL.setActiveTexture(textureUnit);
            OpenGL.bindTexture(this);
            glTexBuffer(GL_TEXTURE_BUFFER, parentBuffer.format.identifier, parentBuffer.getIdentifier());
        }
    }

    private StorageTexture storageTexture;

    @Override
    public int getType() {
        return GL_TEXTURE_BUFFER;
    }

    @NotNull
    public static TextureBuffer newBuffer(int allocation, int drawHint) {
        boolean streamBuffer = drawHint != GL_STATIC_DRAW;
        TextureBuffer buffer = streamBuffer ? new TextureBuffer.Stream(drawHint) : new TextureBuffer.Static();
        buffer.init();
        buffer.createBuffer(allocation, drawHint);
        return buffer;
    }

    @NotNull
    public static TextureBuffer uninitialised(int drawhint) {
        boolean streamBuffer = drawhint != GL_STATIC_DRAW;
        return streamBuffer ? new Stream(drawhint) : new Static();
    }

    protected int identifier = -1;
    @Getter
    protected int currentAllocation = -1;
    @Setter
    private OpenGL.InternalFormat format = OpenGL.InternalFormat.R32F;


    public void init() {
        this.identifier = glGenBuffers();
        this.storageTexture = new StorageTexture();
        this.storageTexture.init();
    }

    public boolean hasGPUMemoryAllocated() {
        return this.currentAllocation != -1;
    }

    protected void createBuffer(int allocation, int drawHint) {
        OpenGL.bindTextureBuffer(this);
        glBufferData(GL_TEXTURE_BUFFER, allocation, drawHint);
        this.currentAllocation = allocation;
    }

    public void createBuffer(int allocation) {
        this.createBuffer(allocation, this.getDrawHint());
    }

    protected abstract int getDrawHint();

    @Override
    public int getIdentifier() {
        return this.identifier;
    }

    protected void linkTexture(int texUnit) {
        this.storageTexture.bind(texUnit, this);
    }

    @Override
    public void close() {
        if(this.identifier != -1) {
            glDeleteBuffers(this.identifier);
            this.identifier = -1;
            this.currentAllocation = -1;
        }
    }

    public abstract void uploadData(int textureUnit, ByteBuffer buffer);
    public abstract void uploadData(int textureUnit, ByteBuffer buffer, int length);
    public abstract void uploadData(int textureUnit, FloatBuffer buffer);
    public abstract void uploadData(int textureUnit, IntBuffer buffer);

    @Setter
    public static class Stream extends TextureBuffer {
        protected final int drawHint;
        protected Stream(int hint) {
            this.drawHint = hint;
            if(hint == GL_STATIC_DRAW) {
                throw new IllegalStateException("just use a static one vro");
            }
        }

        //find the access flags here
        //https://registry.khronos.org/OpenGL-Refpages/gl4/html/glMapBufferRange.xhtml
        private int writeAccess = GL41.GL_MAP_WRITE_BIT | GL41.GL_MAP_INVALIDATE_BUFFER_BIT;

        @Override
        protected int getDrawHint() {
            return this.drawHint;
        }

        @Override
        public void uploadData(int textureUnit, ByteBuffer buffer) {
            this.uploadData(textureUnit, buffer, VertexBuffer.calculateAllocation(buffer));
        }

        @Override
        public void uploadData(int textureUnit, ByteBuffer buffer, int length) {
            OpenGL.bindTextureBuffer(this);
            if((length) > this.currentAllocation) {
                glBufferData(GL_TEXTURE_BUFFER, buffer, this.drawHint);
            } else {
                ByteBuffer pointer = glMapBufferRange(
                        GL_TEXTURE_BUFFER,
                        0,
                        this.currentAllocation,
                        writeAccess
                );

                if(pointer == null) {
                    throw new GLException("failed to map buffer range");
                }

                MemoryUtil.memCopy(
                        MemoryUtil.memAddress(buffer),
                        MemoryUtil.memAddress(pointer),
                        length
                );

                glUnmapBuffer(GL_TEXTURE_BUFFER);
            }
            this.linkTexture(textureUnit);
        }

        @Override
        public void uploadData(int textureUnit, FloatBuffer buffer) {
            OpenGL.bindTextureBuffer(this);
            int length;
            if((length = VertexBuffer.calculateAllocation(buffer)) > this.currentAllocation) {
                glBufferData(GL_TEXTURE_BUFFER, buffer, this.drawHint);
            } else {
                ByteBuffer pointer = glMapBufferRange(
                        GL_TEXTURE_BUFFER,
                        0,
                        this.currentAllocation,
                        writeAccess
                );

                if(pointer == null) {
                    throw new GLException("failed to map buffer range");
                }

                MemoryUtil.memCopy(
                        MemoryUtil.memAddress(buffer),
                        MemoryUtil.memAddress(pointer),
                        length
                );

                glUnmapBuffer(GL_TEXTURE_BUFFER);
            }
            this.linkTexture(textureUnit);
        }

        @Override
        public void uploadData(int textureUnit, IntBuffer buffer) {
            OpenGL.bindTextureBuffer(this);
            int length;
            if((length = VertexBuffer.calculateAllocation(buffer)) > this.currentAllocation) {
                glBufferData(GL_TEXTURE_BUFFER, buffer, this.drawHint);
            } else {
                ByteBuffer pointer = glMapBufferRange(
                        GL_TEXTURE_BUFFER,
                        0,
                        this.currentAllocation,
                        writeAccess
                );

                if(pointer == null) {
                    throw new GLException("failed to map buffer range");
                }

                MemoryUtil.memCopy(
                        MemoryUtil.memAddress(buffer),
                        MemoryUtil.memAddress(pointer),
                        length
                );

                glUnmapBuffer(GL_TEXTURE_BUFFER);
            }
            this.linkTexture(textureUnit);
        }

    }

    public static class Static extends TextureBuffer {

        @Override
        protected int getDrawHint() {
            return GL_STATIC_DRAW;
        }

        @Override
        public void uploadData(int textureUnit, ByteBuffer buffer) {
            this.uploadData(textureUnit, buffer, VertexBuffer.calculateAllocation(buffer));
        }

        @Override
        public void uploadData(int textureUnit, ByteBuffer buffer, int length) {
            OpenGL.bindTextureBuffer(this);
            if((length) > this.currentAllocation) {
                glBufferData(GL_TEXTURE_BUFFER, buffer, GL_STATIC_DRAW);
                this.currentAllocation = length;
            } else {
                glBufferSubData(GL_TEXTURE_BUFFER, 0, buffer);
            }
            this.linkTexture(textureUnit);
        }

        @Override
        public void uploadData(int textureUnit, FloatBuffer buffer) {
            OpenGL.bindTextureBuffer(this);
            if(VertexBuffer.calculateAllocation(buffer) > this.currentAllocation) {
                glBufferData(GL_TEXTURE_BUFFER, buffer, GL_STATIC_DRAW);
            } else {
                glBufferSubData(GL_TEXTURE_BUFFER, 0, buffer);
            }
            this.linkTexture(textureUnit);
        }

        @Override
        public void uploadData(int textureUnit, IntBuffer buffer) {
            OpenGL.bindTextureBuffer(this);
            if(VertexBuffer.calculateAllocation(buffer) > this.currentAllocation) {
                glBufferData(GL_TEXTURE_BUFFER, buffer, GL_STATIC_DRAW);
            } else {
                glBufferSubData(GL_TEXTURE_BUFFER, 0, buffer);
            }
            this.linkTexture(textureUnit);
        }
    }
}
