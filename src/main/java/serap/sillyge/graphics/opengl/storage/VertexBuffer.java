package serap.sillyge.graphics.opengl.storage;

import lombok.Setter;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.lwjgl.opengl.GL41;
import org.lwjgl.system.MemoryUtil;
import serap.sillyge.graphics.exception.GLException;
import serap.sillyge.graphics.opengl.OpenGL;

import java.nio.Buffer;
import java.nio.ByteBuffer;
import java.nio.FloatBuffer;
import java.nio.IntBuffer;

import static org.lwjgl.opengl.GL15.*;
import static org.lwjgl.opengl.GL30.glMapBufferRange;
import static serap.sillyge.graphics.opengl.OpenGL.bindVertexBuffer;

public abstract class VertexBuffer implements OpenGLBuffer {

    @Override
    public int getType() {
        return GL_ARRAY_BUFFER;
    }

    @NotNull
    public static VertexBuffer newBuffer(int allocation, int drawHint) {
        boolean streamBuffer = drawHint != GL_STATIC_DRAW;
        VertexBuffer buffer = streamBuffer ? new VertexBuffer.Stream(drawHint) : new VertexBuffer.Static();
        buffer.init();
        buffer.createBuffer(allocation, drawHint);
        return buffer;
    }

    @NotNull
    public static VertexBuffer uninitialised(int drawhint) {
        boolean streamBuffer = drawhint != GL_STATIC_DRAW;
        return streamBuffer ? new Stream(drawhint) : new Static();
    }

    protected int identifier = -1;
    protected int currentAllocation;

    public void init() {
        this.identifier = glGenBuffers();
    }

    protected void createBuffer(int allocation, int drawHint) {
        bindVertexBuffer(this);
        glBufferData(GL_ARRAY_BUFFER, allocation, drawHint);
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

    @Contract(pure = true)
    protected static int calculateAllocation(@NotNull Buffer buffer) {
        return buffer.capacity();
    }

    public abstract void uploadData(ByteBuffer buffer);
    public abstract void uploadData(FloatBuffer buffer);
    public abstract void uploadData(IntBuffer buffer);

    @Setter
    public static class Stream extends VertexBuffer {
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
            bindVertexBuffer(this);
            int length;
            if((length = VertexBuffer.calculateAllocation(buffer)) > this.currentAllocation) {
                glBufferData(GL_ARRAY_BUFFER, buffer, this.drawHint);
            } else {
                ByteBuffer pointer = glMapBufferRange(
                        GL_ARRAY_BUFFER,
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

                glUnmapBuffer(GL_ARRAY_BUFFER);
            }
        }

        @Override
        public void uploadData(FloatBuffer buffer) {
            bindVertexBuffer(this);
            int length;
            if((length = VertexBuffer.calculateAllocation(buffer)) > this.currentAllocation) {
                glBufferData(GL_ARRAY_BUFFER, buffer, this.drawHint);
            } else {
                ByteBuffer pointer = glMapBufferRange(
                        GL_ARRAY_BUFFER,
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

                glUnmapBuffer(GL_ARRAY_BUFFER);
            }
        }

        @Override
        public void uploadData(IntBuffer buffer) {
            bindVertexBuffer(this);
            int length;
            if((length = VertexBuffer.calculateAllocation(buffer)) > this.currentAllocation) {
                glBufferData(GL_ARRAY_BUFFER, buffer, this.drawHint);
            } else {
                ByteBuffer pointer = glMapBufferRange(
                        GL_ARRAY_BUFFER,
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

                glUnmapBuffer(GL_ARRAY_BUFFER);
            }
        }
    }

    public static class Static extends VertexBuffer {

        @Override
        public void uploadData(ByteBuffer buffer) {
            bindVertexBuffer(this);
            int i = 0;
            if((i = VertexBuffer.calculateAllocation(buffer)) > this.currentAllocation) {
                glBufferData(GL_ARRAY_BUFFER, buffer, GL_STATIC_DRAW);
                this.currentAllocation = i;
            } else {
                glBufferSubData(GL_ARRAY_BUFFER, 0, buffer);
            }
        }

        @Override
        public void uploadData(FloatBuffer buffer) {
            bindVertexBuffer(this);
            if(VertexBuffer.calculateAllocation(buffer) > this.currentAllocation) {
                glBufferData(GL_ARRAY_BUFFER, buffer, GL_STATIC_DRAW);
            } else {
                glBufferSubData(GL_ARRAY_BUFFER, 0, buffer);
            }
        }

        @Override
        public void uploadData(IntBuffer buffer) {
            bindVertexBuffer(this);
            if(VertexBuffer.calculateAllocation(buffer) > this.currentAllocation) {
                glBufferData(GL_ARRAY_BUFFER, buffer, GL_STATIC_DRAW);
            } else {
                glBufferSubData(GL_ARRAY_BUFFER, 0, buffer);
            }
        }
    }
}

