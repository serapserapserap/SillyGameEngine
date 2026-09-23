package serap.sillyge.graphics.opengl;

import it.unimi.dsi.fastutil.objects.Object2IntOpenHashMap;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import org.jetbrains.annotations.NotNull;
import org.lwjgl.opengl.*;
import org.tinylog.Logger;
import serap.sillyge.graphics.exception.GLException;
import serap.sillyge.graphics.opengl.storage.*;
import serap.sillyge.graphics.opengl.texture.Texture;
import serap.sillyge.graphics.window.Window;

import static org.lwjgl.glfw.GLFW.glfwMakeContextCurrent;
import static org.lwjgl.opengl.GL.createCapabilities;
import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.opengl.GL11.GL_MAX_TEXTURE_SIZE;
import static org.lwjgl.opengl.GL11.glGetInteger;
import static org.lwjgl.opengl.GL12.GL_MAX_3D_TEXTURE_SIZE;
import static org.lwjgl.opengl.GL13.GL_TEXTURE0;
import static org.lwjgl.opengl.GL14.GL_DEPTH_COMPONENT16;
import static org.lwjgl.opengl.GL14.GL_DEPTH_COMPONENT24;
import static org.lwjgl.opengl.GL15.GL_ARRAY_BUFFER;
import static org.lwjgl.opengl.GL15.GL_ELEMENT_ARRAY_BUFFER;
import static org.lwjgl.opengl.GL20.GL_MAX_COMBINED_TEXTURE_IMAGE_UNITS;
import static org.lwjgl.opengl.GL30.*;
import static org.lwjgl.opengl.GL30.GL_R16F;
import static org.lwjgl.opengl.GL31.*;
import static org.lwjgl.opengl.GL40.GL_TESS_CONTROL_SHADER;
import static org.lwjgl.opengl.GL40.GL_TESS_EVALUATION_SHADER;
import static org.lwjgl.opengl.GL43.GL_COMPUTE_SHADER;
import static org.lwjgl.opengl.GL43.GL_SHADER_STORAGE_BUFFER;

public abstract class OpenGL {

    /*
     * planned render architecture
     *      Material + Pipeline based
     *      Pipelines decides render details, like OpenGL states, programs, etc
     *      Materials decide higher levels stuff like textures, and pbr values (metallicness, specular, etc) assuming i end up implementing pbr into this engine
     */

    @Getter
    @Setter
    private static int versionMinor;

    @Getter private static int MAX_TEXTURE_UNITS_PER_SHADER_STAGE;
    @Getter private static int MAX_TEXTURE_SLOTS;
    @Getter private static int MAX_TEXTURE_SIZE;
    @Getter private static int MAX_3D_TEXTURE_SIZE;
    @Getter private static int MAX_ARRAY_TEXTURE_LAYERS;
    @Getter private static int MAX_TEXTURE_BUFFER_SIZE;
    @Getter private static String graphicsCardVendor;
    @Getter private static String graphicsCard;
    private static Thread glThread;
    /**
     * If OpenGL 430 is supported, with version 430 comes support for {@link ShaderStorageBuffer}
     */
    @Getter private static boolean GL430;
    @Getter private static boolean GL440;
    @Getter private static boolean GL450;
    @Getter private static boolean GL460;
    @Getter private static boolean init;

    @Getter private static int activeTexture;

    @Getter private static VertexBuffer boundVertexBuffer = null;
    @Getter private static IndexBuffer boundIndexBuffer = null;
    @Getter private static ShaderStorageBuffer boundShaderStorageBuffer = null;
    @Getter private static VertexArray boundVertexArray = null;
    @Getter private static UniformBuffer boundUniformBuffer = null;
    @Getter private static TextureBuffer boundTextureBuffer = null;

    private static Texture[] boundTextures;
    private static final Object2IntOpenHashMap<String> slotNames = new Object2IntOpenHashMap<>();
    private static int currentSlot = 0;
    private static int offset = 0;


    public static void setActiveTexture(int slot) {
        if(!isValidTextureSlot(slot)) {
            throw new IndexOutOfBoundsException("unable to set active texture to slot " + slot + " as max slots is " + OpenGL.MAX_TEXTURE_UNITS_PER_SHADER_STAGE);
        }
        if(activeTexture == slot) return;
        GL13.glActiveTexture(GL_TEXTURE0 + slot);
        activeTexture = slot;
    }
    public static boolean isValidTextureSlot(int slot) {
        return slot < OpenGL.getMAX_TEXTURE_UNITS_PER_SHADER_STAGE() && slot > -1;
    }

    private static boolean isAllowedToAssignSlot(int slot) {
        return slot < OpenGL.getMAX_TEXTURE_UNITS_PER_SHADER_STAGE() - offset;
    }

    public static int nextSlotFromBack() {
        int slot = OpenGL.getMAX_TEXTURE_UNITS_PER_SHADER_STAGE() - 1 - offset;
        if(slot < currentSlot) {
            Logger.info("back slot is less than the front slot, this likely will result in errors!");
        }

        if(slot < 0) {
            throw new IllegalStateException("slot with negative index was assigned! this should not happen!");
        }

        return slot;
    }

    public static int nextSlot() {
        int slot = currentSlot;
        currentSlot++;
        if(!isAllowedToAssignSlot(slot)) {
            throw new GLException("graphics device ran out of texture units." + "used " + slot + ", has " + OpenGL.getMAX_TEXTURE_UNITS_PER_SHADER_STAGE());
        }
        //Logger.info("slot " + slot + " now in use");
        return slot;
    }

    public static void nameSlot(@NotNull String name, int slot) {
        slotNames.put(name, (int) slot);
    }

    public static int getSlot(@NotNull String name) {
        return slotNames.getInt(name);
    }

    public static void bindTexture(@NotNull Texture texture) {

        if(!texture.isInitialised()) throw new GLException("bound un-initialised texture :P");

        int activeTexture = OpenGL.getActiveTexture();
        if(boundTextures[activeTexture] != null && boundTextures[activeTexture].getIdentifier() == texture.getIdentifier()) {
            return;
        }
        //cache miss
        //time to bind it
        Texture old = boundTextures[activeTexture];
        if(old != null) {
            old.onUnbind();
        }
        boundTextures[activeTexture] = texture;
        texture.onBind(activeTexture);
        GL11.glBindTexture(texture.getTextureType(), texture.getIdentifier());
    }

    private static void collectConstants() {
        OpenGL.graphicsCardVendor = glGetString(GL_VENDOR);
        OpenGL.graphicsCard = glGetString(GL_RENDERER);
        OpenGL.MAX_TEXTURE_SLOTS = glGetInteger(GL_MAX_COMBINED_TEXTURE_IMAGE_UNITS);
        OpenGL.MAX_TEXTURE_UNITS_PER_SHADER_STAGE = OpenGL.MAX_TEXTURE_SLOTS / 5; //5 shader stages so you divide by 5
        OpenGL.MAX_TEXTURE_SIZE = glGetInteger(GL_MAX_TEXTURE_SIZE);
        OpenGL.MAX_3D_TEXTURE_SIZE = glGetInteger(GL_MAX_3D_TEXTURE_SIZE);
        OpenGL.MAX_ARRAY_TEXTURE_LAYERS = glGetInteger(GL_MAX_ARRAY_TEXTURE_LAYERS);
        OpenGL.MAX_TEXTURE_BUFFER_SIZE = glGetInteger(GL_MAX_TEXTURE_BUFFER_SIZE);
        GL430 = versionMinor >= 3;
        GL440 = versionMinor >= 4;
        GL450 = versionMinor >= 5;
        GL460 = versionMinor >= 6;
    }

    @RequiredArgsConstructor
    public enum Type {
        BYTE(1, GL_BYTE),
        UNSIGNED_BYTE(1, GL_UNSIGNED_BYTE),
        SHORT(2, GL_SHORT),
        UNSIGNED_SHORT(2, GL_UNSIGNED_SHORT),
        INT(4, GL_INT),
        UNSIGNED_INT(4, GL_UNSIGNED_INT),
        HALF_FLOAT(2, GL_HALF_FLOAT),
        FLOAT(4, GL_FLOAT),
        DOUBLE(8, GL_DOUBLE);
        public final int bytes;
        public final int identifier;
    }

    @RequiredArgsConstructor
    public enum InternalFormat {
        RGBA_8BIT(GL_RGBA8),
        R8(GL_R8),
        RGBA_FLOAT16(GL_RGBA16F),
        RGBA_FLOAT32(GL_RGBA32F),
        RGB_FLOAT32(GL_RGB32F),
        DEPTH32(GL_DEPTH_COMPONENT32F),
        DEPTH24(GL_DEPTH_COMPONENT24),
        DEPTH16(GL_DEPTH_COMPONENT16),
        R32F(GL_R32F),
        R16(GL_R16),
        R16F(GL_R16F);
        public final int identifier;
    }

    @RequiredArgsConstructor
    public enum ShaderType {
        VERTEX(GL_VERTEX_SHADER),
        FRAGMENT(GL_FRAGMENT_SHADER),
        //GEOMETRY(GL_GEOMETRY_SHADER), fuck off
        TESSELLATION_CONTROL(GL_TESS_CONTROL_SHADER),
        TESSELLATION_EVALUATION(GL_TESS_EVALUATION_SHADER),
        COMPUTE_SHADER(GL_COMPUTE_SHADER);
        public final int identifier;
    }

    public static boolean isGLThread() {
        return Thread.currentThread().equals(OpenGL.glThread);
    }

    @NotNull
    public static GLCapabilities init(@NotNull Window window) {
        glfwMakeContextCurrent(window.getIdentifier());
        var glCaps = createCapabilities();
        OpenGL.collectConstants();
        OpenGL.boundTextures = new Texture[OpenGL.getMAX_TEXTURE_SLOTS()];
        glThread = Thread.currentThread();
        VertexArray.EMPTY_VERTEX_ARRAY.init();
        init = true;
        return glCaps;
    }

    public static void bindVertexBuffer(@NotNull VertexBuffer vertexBuffer) {
        if(OpenGL.boundVertexBuffer != null && OpenGL.boundVertexBuffer.getIdentifier() == vertexBuffer.getIdentifier()) {
            return;
        }

        OpenGL.boundVertexBuffer = vertexBuffer;
        GL15.glBindBuffer(GL_ARRAY_BUFFER, vertexBuffer.getIdentifier());
    }

    public static void bindIndexBuffer(@NotNull IndexBuffer indexBuffer) {
        if(OpenGL.boundIndexBuffer != null && OpenGL.boundIndexBuffer.getIdentifier() == indexBuffer.getIdentifier()) {
            return;
        }

        OpenGL.boundIndexBuffer = indexBuffer;
        GL15.glBindBuffer(GL_ELEMENT_ARRAY_BUFFER, indexBuffer.getIdentifier());
    }


    public static void bindUniformBuffer(@NotNull UniformBuffer uniformBuffer) {
        if(OpenGL.boundUniformBuffer != null && OpenGL.boundUniformBuffer.getIdentifier() == uniformBuffer.getIdentifier()) {
            return;
        }

        OpenGL.boundUniformBuffer = uniformBuffer;
        GL15.glBindBuffer(GL_UNIFORM_BUFFER, uniformBuffer.getIdentifier());
    }

    public static void bindShaderStorageBuffer(@NotNull ShaderStorageBuffer buffer) {
        if(OpenGL.boundShaderStorageBuffer != null && OpenGL.boundShaderStorageBuffer.getIdentifier() == buffer.getIdentifier()) {
            return;
        }

        OpenGL.boundShaderStorageBuffer = buffer;
        GL15.glBindBuffer(GL_SHADER_STORAGE_BUFFER, buffer.getIdentifier());
    }

    public static void bindVertexArray(@NotNull VertexArray vertexArray) {
        if(boundVertexArray != null && boundVertexArray.getIdentifier() == vertexArray.getIdentifier()) {
            return;
        }
        boundVertexArray = vertexArray;
        GL30.glBindVertexArray(vertexArray.getIdentifier());
    }

    public static void bindTextureBuffer(@NotNull TextureBuffer textureBuffer) {
        if(OpenGL.boundTextureBuffer != null && OpenGL.boundTextureBuffer.getIdentifier() == textureBuffer.getIdentifier()) {
            return;
        }

        OpenGL.boundTextureBuffer = textureBuffer;
        GL15.glBindBuffer(GL_TEXTURE_BUFFER, textureBuffer.getIdentifier());
    }
}
