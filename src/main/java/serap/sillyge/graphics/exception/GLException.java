package serap.sillyge.graphics.exception;

import org.jetbrains.annotations.Nullable;

import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.opengl.GL11.GL_OUT_OF_MEMORY;
import static org.lwjgl.opengl.GL30.GL_INVALID_FRAMEBUFFER_OPERATION;

public final class GLException extends RuntimeException {
    public GLException(String string) {
        super(string);
    }
    public GLException() {}
    public GLException(String string, Throwable cause) {
        super(string, cause);
    }
    public GLException(Throwable cause) {super(cause);}
    public GLException(String string, boolean fetchGLErrs) {
        super(
                string + (fetchGLErrs ? " >>" + getErrorString() : "")
        );
    }

    @Nullable
    public static String getErrorString() {
        StringBuilder err = new StringBuilder("GL ERROR(S):");
        int error;
        boolean hasError = false;
        while ((error = glGetError()) != GL_NO_ERROR) {
            hasError = true;
            String subError = switch (error) {
                case GL_INVALID_ENUM -> "GL_INVALID_ENUM";
                case GL_INVALID_VALUE -> "GL_INVALID_VALUE";
                case GL_INVALID_OPERATION -> "GL_INVALID_OPERATION";
                case GL_STACK_OVERFLOW -> "GL_STACK_OVERFLOW";
                case GL_STACK_UNDERFLOW -> "GL_STACK_UNDERFLOW";
                case GL_OUT_OF_MEMORY -> "GL_OUT_OF_MEMORY";
                case GL_INVALID_FRAMEBUFFER_OPERATION -> "GL_INVALID_FRAMEBUFFER_OPERATION";
                default -> "UNSUPPORTED_ERROR";
            } + " >> " + error;
            err.append("\n - ").append(subError);
        }

        if(hasError) {
            return err.toString();
        } else {
            return null;
        }
    }
}

