package serap.sillyge.graphics.opengl.storage;

import lombok.Setter;
import org.jetbrains.annotations.NotNull;
import org.joml.*;
import org.lwjgl.BufferUtils;
import org.lwjgl.opengl.GL41;
import org.lwjgl.system.MemoryUtil;
import serap.sillyge.graphics.exception.GLException;
import serap.sillyge.graphics.opengl.OpenGL;

import java.nio.ByteBuffer;

import static org.lwjgl.opengl.GL15.*;
import static org.lwjgl.opengl.GL30.glMapBufferRange;
import static org.lwjgl.opengl.GL31.GL_UNIFORM_BUFFER;
import static serap.sillyge.graphics.opengl.OpenGL.bindUniformBuffer;

public abstract class UniformBuffer implements OpenGLBuffer {

    protected ByteBuffer uniformData;

    @Override
    public int getType() {
        return GL_UNIFORM_BUFFER;
    }

    @NotNull
    public static UniformBuffer newBuffer(int allocation, int drawHint) {
        boolean streamBuffer = drawHint != GL_STATIC_DRAW;
        UniformBuffer buffer = streamBuffer ? new UniformBuffer.Stream(drawHint) : new UniformBuffer.Static();
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
        bindUniformBuffer(this);
        glBufferData(GL_UNIFORM_BUFFER, allocation, drawHint);
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
        }
    }

    public static int claculateStd140Bytes(@NotNull Object... shit) {
        int i = 0;
        for(var shat : shit) {
            if(shat instanceof Vector4f || shat instanceof Vector3f) {
                i+=16;
            } else if(shat instanceof Float || shat instanceof Integer) {
                i+= 4;
            } else if(shat instanceof Vector2f) {
                i+= 8;
            } else if(shat instanceof Matrix4f) {
                i+= 48;
            } else {
                throw new IllegalStateException("(probably) illegal type used: " + shat.getClass());
            }
        }
        return i;
    }

    public UniformBuffer allocate(int bytes) {
        this.uniformData = BufferUtils.createByteBuffer(bytes);
        return this;
    }

    public UniformBuffer put(int i) {
        this.uniformData.putInt(i);
        return this;
    }
    public UniformBuffer put(float f) {
        this.uniformData.putFloat(f);
        return this;
    }
    public UniformBuffer put(@NotNull Vector3f vec) {
        vec.get(this.uniformData);
        this.uniformData.putFloat(0f);
        return this;
    }
    public UniformBuffer put(@NotNull Vector4f vec) {
        vec.get(this.uniformData);
        return this;
    }
    public UniformBuffer put(@NotNull Matrix4f mat) {
        mat.get(this.uniformData);
        return this;
    }
    public UniformBuffer put(@NotNull Matrix3f mat) {
        //todo -> this is farked, won't work, although who the fuck uses 3x3 matrices?
        mat.get(this.uniformData);
        return this;
    }
    public UniformBuffer flip() {
        this.uniformData.flip();
        return this;
    }

    //IMPORTANT
    //THE SIZES ARE AS FOLLOWS
    //FLOAT, INT, UINT, BYTE, SHORT, BOOL, USHORT, UBYTE - 4 BYTES (so aim to bitpack some stuff)
    //VEC2 - 8 BYTES
    //VEC3 & VEC4 - 16 BYTES
    public abstract void uploadData(ByteBuffer buffer);
    public void uploadData() {
        this.uploadData(this.uniformData);
    }

    @Setter
    public static class Stream extends UniformBuffer {
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
            bindUniformBuffer(this);
            int length;
            if((length = VertexBuffer.calculateAllocation(buffer)) > this.currentAllocation) { //todo -> this might be fucked
                glBufferData(GL_UNIFORM_BUFFER, buffer, this.drawHint);
            } else {
                ByteBuffer pointer = glMapBufferRange(
                        GL_UNIFORM_BUFFER,
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

                glUnmapBuffer(GL_UNIFORM_BUFFER);
            }
        }
    }

    public static class Static extends UniformBuffer {

        @Override
        public void uploadData(ByteBuffer buffer) {
            bindUniformBuffer(this);
            if(VertexBuffer.calculateAllocation(buffer) > this.currentAllocation) {
                glBufferData(GL_UNIFORM_BUFFER, buffer, GL_STATIC_DRAW);
            } else {
                glBufferSubData(GL_UNIFORM_BUFFER, 0, buffer);
            }
        }
    }
}

