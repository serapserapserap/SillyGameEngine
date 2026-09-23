package serap.sillyge.graphics.opengl.storage;

import lombok.Setter;
import org.jetbrains.annotations.NotNull;
import org.joml.*;
import org.lwjgl.opengl.GL41;
import org.lwjgl.system.MemoryUtil;
import serap.sillyge.graphics.exception.GLException;
import serap.sillyge.graphics.opengl.OpenGL;

import java.nio.ByteBuffer;

import static org.lwjgl.opengl.GL15.*;
import static org.lwjgl.opengl.GL30.glMapBufferRange;
import static org.lwjgl.opengl.GL42.GL_BUFFER_UPDATE_BARRIER_BIT;
import static org.lwjgl.opengl.GL42.glMemoryBarrier;
import static org.lwjgl.opengl.GL43.GL_SHADER_STORAGE_BUFFER;
import static serap.sillyge.graphics.opengl.OpenGL.bindShaderStorageBuffer;

//todo The laptop I do most of my work on (apple m2) has opengl deprecated in favour of metal & vulkan, i need to test this on my windows/linux machine
//todo these comments will be removed once that is done (and the deprecated annotation)
@Deprecated
public abstract class ShaderStorageBuffer implements OpenGLBuffer {

    public ShaderStorageBuffer() {
        if(!OpenGL.isGL430()) {
            throw new GLException("unable to initialise shader storage buffer as they are not supported in openGLv4." + OpenGL.getVersionMinor());
        }
    }

    @Override
    public int getType() {
        return GL_SHADER_STORAGE_BUFFER;
    }

    @NotNull
    public static ShaderStorageBuffer newBuffer(int allocation, int drawHint) {
        boolean streamBuffer = drawHint != GL_STATIC_DRAW;
        ShaderStorageBuffer buffer = streamBuffer ? new ShaderStorageBuffer.Stream(drawHint) : new ShaderStorageBuffer.Static();
        buffer.init();
        buffer.createBuffer(allocation, drawHint);
        return buffer;
    }

    protected int identifier = -1;
    protected int currentAllocation;

    public void init() {
        this.identifier = glGenBuffers();
    }

    protected void createBuffer(int allocation, int drawHint) {
        bindShaderStorageBuffer(this);
        glBufferData(GL_SHADER_STORAGE_BUFFER, allocation, drawHint);
        this.currentAllocation = allocation;
    }

    @Override
    public int getIdentifier() {
        return this.identifier;
    }

    @Override
    public void close() {
        if(this.identifier != -1) {
            glDeleteBuffers(this.identifier);
            this.identifier = -1;
        }
    }


    //todo... i think? i was drunk when i wrote this
    public ByteBuffer readData() {
        bindShaderStorageBuffer(this);
        glMemoryBarrier(GL_BUFFER_UPDATE_BARRIER_BIT);
        ByteBuffer pointer = glMapBuffer(GL_SHADER_STORAGE_BUFFER, GL_READ_ONLY);

        if(pointer == null) {
            throw new GLException("Failed to map shader storage buffer");
        }

        return null;
    }


    //IMPORTANT
    //THE SIZES ARE AS FOLLOWS
    //FLOAT, INT, UINT, BYTE, SHORT, BOOL, USHORT, UBYTE - 4 BYTES (so aim to bitpack some stuff)
    //VEC2 - 8 BYTES
    //VEC3 & VEC4 -16 BYTES
    public abstract void uploadData(ByteBuffer buffer);

    @Setter
    public static class Stream extends ShaderStorageBuffer {
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
        public void uploadData(ByteBuffer buffer) {
            bindShaderStorageBuffer(this);
            int length;
            if((length = VertexBuffer.calculateAllocation(buffer)) > this.currentAllocation) { //todo -> this might be fucked
                glBufferData(GL_SHADER_STORAGE_BUFFER, buffer, this.drawHint);
            } else {
                ByteBuffer pointer = glMapBufferRange(
                        GL_SHADER_STORAGE_BUFFER,
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

                glUnmapBuffer(GL_SHADER_STORAGE_BUFFER);
            }
        }
    }

    public static class Static extends ShaderStorageBuffer {

        @Override
        public void uploadData(ByteBuffer buffer) {
            bindShaderStorageBuffer(this);
            if(VertexBuffer.calculateAllocation(buffer) > this.currentAllocation) {
                glBufferData(GL_SHADER_STORAGE_BUFFER, buffer, GL_STATIC_DRAW);
            } else {
                glBufferSubData(GL_SHADER_STORAGE_BUFFER, 0, buffer);
            }
        }
    }

    public static int claculateStd430Bytes(@NotNull Object... shit) {
        int i = 0;
        for(var shat : shit) {
            if(shat instanceof Vector4f || shat instanceof Vector3f) {
                i+=16;
            } else if(shat instanceof Float || shat instanceof Integer) {
                i+= 4;
            } else if(shat instanceof Vector2f) {
                i+= 8;
            } else if(shat instanceof Matrix4f) {
                i+= 64;
            } else if(shat instanceof Matrix3f) {
                i+= 48;
            } else if(shat instanceof Matrix2f) {
                i+= 24;
            } else {
                throw new IllegalStateException("(probably) illegal type used: " + shat.getClass());
            }
        }
        return i;
    }
}
