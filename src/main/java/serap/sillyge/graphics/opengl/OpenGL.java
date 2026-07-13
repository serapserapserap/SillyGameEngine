package serap.sillyge.graphics.opengl;

import lombok.Getter;
import lombok.Setter;

import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.opengl.GL11.GL_MAX_TEXTURE_SIZE;
import static org.lwjgl.opengl.GL11.glGetInteger;
import static org.lwjgl.opengl.GL12.GL_MAX_3D_TEXTURE_SIZE;
import static org.lwjgl.opengl.GL20.GL_MAX_COMBINED_TEXTURE_IMAGE_UNITS;
import static org.lwjgl.opengl.GL30.GL_MAX_ARRAY_TEXTURE_LAYERS;
import static org.lwjgl.opengl.GL31.GL_MAX_TEXTURE_BUFFER_SIZE;

public abstract class OpenGL {

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
    @Getter private static boolean GL430;
    @Getter private static boolean GL440;
    @Getter private static boolean GL450;
    @Getter private static boolean GL460;
    @Getter private static boolean init;

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
}
