package serap.sillyge.graphics.opengl;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import serap.sillyge.graphics.opengl.storage.IndexBuffer;
import serap.sillyge.graphics.opengl.storage.VertexBuffer;
import serap.sillyge.graphics.vertex.VertexFormat;

import static org.lwjgl.opengl.GL30.*;

public class VertexArray implements IntegerIdentifiable{
    public static final VertexArray EMPTY_VERTEX_ARRAY = new VertexArray();

    protected int identifier = -1;
    protected VertexBuffer[] vertexBuffers;
    @Nullable
    protected IndexBuffer indexBuffer;

    public boolean hasIndexBuffer() {
        return this.indexBuffer != null;
    }

    public void format(VertexFormat format, @Nullable IndexBuffer indexBuffer, @NotNull VertexBuffer... vertexBuffers) {
        OpenGL.bindVertexArray(this);
        if(format.getExpectedBufferCount() != vertexBuffers.length) {
            throw new IllegalStateException("vertex array formatting failed: vertex format expected " + format.getExpectedBufferCount() + " buffers but only received " + vertexBuffers.length);
        }

        if(indexBuffer != null) {
            OpenGL.bindIndexBuffer(indexBuffer);
        }

        format.setupWithBuffers(vertexBuffers);
    }

    public void format(VertexFormat format, @NotNull VertexBuffer... vertexBuffers) {
        this.format(format, null, vertexBuffers);
    }


    public void init() {
        this.identifier = glGenVertexArrays();
    }

    @Override
    public int getIdentifier() {
        return this.identifier;
    }

    @Override
    public void close() {
        if(this.identifier != -1) {
            glDeleteVertexArrays(this.identifier);
        }
    }
}
