package serap.sillyge.graphics.vertex;

import lombok.Getter;
import serap.sillyge.graphics.opengl.OpenGL;
import serap.sillyge.graphics.opengl.storage.VertexBuffer;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;
import java.util.function.Supplier;

import static org.lwjgl.opengl.GL11.GL_FLOAT;
import static org.lwjgl.opengl.GL20.glEnableVertexAttribArray;
import static org.lwjgl.opengl.GL20.glVertexAttribPointer;
import static org.lwjgl.opengl.GL30.glVertexAttribIPointer;
import static serap.sillyge.graphics.opengl.OpenGL.bindVertexBuffer;

public interface VertexFormat {
    void setupWithBuffers(VertexBuffer... buffers);
    int getExpectedBufferCount();

    class ProceduralVertexFormat implements VertexFormat {
        @Getter
        private final int expectedBufferCount;

        private ProceduralVertexFormat(Consumer<VertexBuffer[]>[] instructions, int bufferCount) {
            this.instructions = instructions;
            this.expectedBufferCount = bufferCount;
        }

        private final Consumer<VertexBuffer[]>[] instructions;
        @Override
        public void setupWithBuffers(VertexBuffer... buffers) {
            for (Consumer<VertexBuffer[]> instruction : this.instructions) {
                instruction.accept(buffers);
            }
        }
    }

    class Builder implements Supplier<VertexFormat> {
        private final List<Consumer<VertexBuffer[]>> instructions = new ArrayList<>();
        private int attribIndex = 0;
        private int bufferCount = 0;
        private int currentPointer;

        @SuppressWarnings("unchecked")
        public VertexFormat get() {
            return new ProceduralVertexFormat(instructions.toArray(new Consumer[0]), bufferCount);
        }
        //unsigned
        public Builder integer() {
            return integer(OpenGL.Type.INT, 1);
        }
        public Builder uint() {
            return integer(OpenGL.Type.UNSIGNED_INT, 1);
        }

        public Builder vec2() {
            return float32(2);
        }

        public Builder vec3() {
            return float32(3);
        }

        public Builder vec4() {
            return float32(4);
        }

        public Builder ivec4() {
            return integer(OpenGL.Type.INT, 4);
        }

        public Builder buffer(int index) {
            bufferCount++;
            this.instructions.add((buffers) -> bindVertexBuffer(buffers[index]));
            return this;
        }

        public Builder buffer(VertexBuffer buffer) {
            this.instructions.add((ignored) -> bindVertexBuffer(buffer));
            return this;
        }

        public Builder float32(int count) {
            int index = this.attribIndex;
            int pointer = this.currentPointer;
            instructions.add((ignored) -> {
                glEnableVertexAttribArray(index);
                glVertexAttribPointer(index, count, GL_FLOAT,false, this.currentPointer, pointer);
            });
            this.currentPointer += count * OpenGL.Type.FLOAT.bytes;
            this.attribIndex++;
            return this;
        }

        public Builder integer(int count) {
            return integer(OpenGL.Type.INT, count);
        }

        public Builder uint(int count) {
            return integer(OpenGL.Type.UNSIGNED_INT, count);
        }

        public Builder integer(OpenGL.Type integerTypeEnum, int count) {
            int index = this.attribIndex;
            int pointer = this.currentPointer;
            instructions.add((ignored) -> {
                glEnableVertexAttribArray(index);
                glVertexAttribIPointer(index, count, integerTypeEnum.identifier, this.currentPointer, pointer);
            });
            this.currentPointer += count * integerTypeEnum.bytes;
            this.attribIndex++;
            return this;
        }
    }
}
