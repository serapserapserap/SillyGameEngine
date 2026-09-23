package serap.sillyge.graphics.opengl.storage;

import lombok.Getter;
import lombok.Setter;
import org.jetbrains.annotations.NotNull;
import org.lwjgl.opengl.GL41;
import org.lwjgl.system.MemoryUtil;
import serap.sillyge.graphics.exception.GLException;
import serap.sillyge.graphics.opengl.OpenGL;

import java.nio.ByteBuffer;
import java.nio.IntBuffer;
import java.nio.ShortBuffer;

import static org.lwjgl.opengl.GL15.*;
import static org.lwjgl.opengl.GL15.GL_ELEMENT_ARRAY_BUFFER;
import static org.lwjgl.opengl.GL30.glMapBufferRange;
import static serap.sillyge.graphics.opengl.OpenGL.bindIndexBuffer;

public abstract class IndexBuffer implements OpenGLBuffer {

    @Override
    public int getType() {
        return GL_ELEMENT_ARRAY_BUFFER;
    }

    @NotNull
    public static IndexBuffer newBuffer(int allocation, int drawHint) {
        boolean streamBuffer = drawHint != GL_STATIC_DRAW;
        IndexBuffer buffer = streamBuffer ? new IndexBuffer.Stream(drawHint) : new IndexBuffer.Static();
        buffer.init();
        buffer.createBuffer(allocation, drawHint);
        return buffer;
    }

    @NotNull
    public static IndexBuffer uninitialised(int drawhint) {
        boolean streamBuffer = drawhint != GL_STATIC_DRAW;
        return streamBuffer ? new IndexBuffer.Stream(drawhint) : new IndexBuffer.Static();
    }

    @Getter
    protected int indexes;
    protected OpenGL.Type type = OpenGL.Type.UNSIGNED_BYTE;

    public int getGLType() {
        return this.type.identifier;
    }

    protected int identifier = -1;
    protected int currentAllocation;

    public void init() {
        this.identifier = glGenBuffers();
    }

    protected void createBuffer(int allocation, int drawHint) {
        bindIndexBuffer(this);
        glBufferData(GL_ELEMENT_ARRAY_BUFFER, allocation, drawHint);
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

    public abstract void uploadData(ByteBuffer buffer, OpenGL.Type type);
    public abstract void uploadData(IntBuffer buffer);
    public abstract void uploadData(ShortBuffer buffer);
    /*public void uploadData(IndexBuilder indexBuilder) {
        this.uploadData(indexBuilder.asByteBuffer(), indexBuilder.getIndexDatatype());
    }*/

    @Setter
    public static class Stream extends IndexBuffer {
        protected final int drawHint;
        protected Stream(int drawHint) {
            this.drawHint = drawHint;
            if(drawHint == GL_STATIC_DRAW) {
                throw new IllegalStateException("just use a static one vro");
            }
        }


        //find the access flags here
        //https://registry.khronos.org/OpenGL-Refpages/gl4/html/glMapBufferRange.xhtml
        private int writeAccess = GL41.GL_MAP_WRITE_BIT | GL41.GL_MAP_INVALIDATE_BUFFER_BIT;
        @Override
        public void uploadData(ByteBuffer buffer, OpenGL.Type type) {
            this.type = type;
            bindIndexBuffer(this);
            int length;
            if((length = VertexBuffer.calculateAllocation(buffer)) > this.currentAllocation / this.type.bytes) {
                glBufferData(GL_ELEMENT_ARRAY_BUFFER, buffer, GL_STATIC_DRAW);
            } else {
                ByteBuffer pointer = glMapBufferRange(
                        GL_ELEMENT_ARRAY_BUFFER,
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

                glUnmapBuffer(GL_ELEMENT_ARRAY_BUFFER);
            }
            this.indexes = length / this.type.bytes;
        }

        @Override
        public void uploadData(IntBuffer buffer) {
            this.type = OpenGL.Type.UNSIGNED_INT;
            bindIndexBuffer(this);
            int length;
            if((length = VertexBuffer.calculateAllocation(buffer)) > this.currentAllocation / this.type.bytes) {
                glBufferData(GL_ELEMENT_ARRAY_BUFFER, buffer, GL_STATIC_DRAW);
            } else {
                ByteBuffer pointer = glMapBufferRange(
                        GL_ELEMENT_ARRAY_BUFFER,
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

                glUnmapBuffer(GL_ELEMENT_ARRAY_BUFFER);
            }
            this.indexes = length;
        }

        @Override
        public void uploadData(ShortBuffer buffer) {
            this.type = OpenGL.Type.UNSIGNED_SHORT;
            bindIndexBuffer(this);
            int length;
            if((length = VertexBuffer.calculateAllocation(buffer)) > this.currentAllocation / this.type.bytes) {
                glBufferData(GL_ELEMENT_ARRAY_BUFFER, buffer, GL_STATIC_DRAW);
            } else {
                ByteBuffer pointer = glMapBufferRange(
                        GL_ELEMENT_ARRAY_BUFFER,
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

                glUnmapBuffer(GL_ELEMENT_ARRAY_BUFFER);
            }
            this.indexes = length;
        }
    }

    public static class Static extends IndexBuffer {
        @Override
        public void uploadData(ByteBuffer buffer, OpenGL.Type type) {
            this.type = type;
            int i;
            bindIndexBuffer(this);
            if((i = VertexBuffer.calculateAllocation(buffer)) > this.currentAllocation) {
                glBufferData(GL_ELEMENT_ARRAY_BUFFER, buffer, GL_STATIC_DRAW);
                this.currentAllocation = i;
            } else {
                glBufferSubData(GL_ELEMENT_ARRAY_BUFFER, 0, buffer);
            }
            this.indexes = i / type.bytes;
        }

        @Override
        public void uploadData(IntBuffer buffer) {
            this.type = OpenGL.Type.UNSIGNED_INT;
            int i;
            bindIndexBuffer(this);
            if((i = VertexBuffer.calculateAllocation(buffer)) > this.currentAllocation / this.type.bytes) {
                glBufferData(GL_ELEMENT_ARRAY_BUFFER, buffer, GL_STATIC_DRAW);
            } else {
                glBufferSubData(GL_ELEMENT_ARRAY_BUFFER, 0, buffer);
            }
            this.indexes = i;
        }

        @Override
        public void uploadData(ShortBuffer buffer) {
            this.type = OpenGL.Type.UNSIGNED_SHORT;
            int i;
            bindIndexBuffer(this);
            if((i = VertexBuffer.calculateAllocation(buffer)) > this.currentAllocation / this.type.bytes) {
                glBufferData(GL_ELEMENT_ARRAY_BUFFER, buffer, GL_STATIC_DRAW);
            } else {
                glBufferSubData(GL_ELEMENT_ARRAY_BUFFER, 0, buffer);
            }
            this.indexes = i;
        }
    }
}
