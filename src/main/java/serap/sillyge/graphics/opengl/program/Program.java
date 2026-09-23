package serap.sillyge.graphics.opengl.program;

import lombok.Getter;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.joml.Matrix4f;
import org.lwjgl.opengl.GL41;
import org.tinylog.Logger;
import serap.sillyge.graphics.exception.GLException;
import serap.sillyge.graphics.opengl.IntegerIdentifiable;
import serap.sillyge.graphics.opengl.OpenGL;
import serap.sillyge.graphics.opengl.program.shader.Shader;
import serap.sillyge.io.ResourceUtils;

import java.nio.FloatBuffer;
import java.nio.file.Files;

import static org.lwjgl.opengl.GL11.GL_FALSE;
import static org.lwjgl.opengl.GL20.*;
import static org.lwjgl.opengl.GL20.glUniform1i;
import static org.lwjgl.opengl.GL31.GL_INVALID_INDEX;
import static org.lwjgl.opengl.GL31.glGetUniformBlockIndex;

public abstract class Program implements IntegerIdentifiable {

    @NotNull
    @Deprecated
    public static ShadingProgram newProgram(@NotNull String path, boolean tessellationProgram) {
        ShadingProgram resultantProgram;
        try {
            String vertexSrc = Files.readString(ResourceUtils.getResourceAsPath(path + "/vertex.vsh"));
            String fragmentSrc = Files.readString(ResourceUtils.getResourceAsPath(path + "/fragment.fsh"));

            Shader vertexShader = new Shader(GL.Shader.VERTEX, vertexSrc);
            Shader fragmentShader = new Shader(GL.Shader.FRAGMENT, fragmentSrc);

            vertexShader.init();
            fragmentShader.init();

            fragmentShader.createShader(true);
            String errLog = fragmentShader.getShaderCreationErrorLog();

            if(errLog != null) {
                throw new GLException(errLog);
            }

            vertexShader.createShader(true);
            errLog = vertexShader.getShaderCreationErrorLog();

            if(errLog != null) {
                throw new GLException(errLog);
            }

            if(tessellationProgram) {
                String tessellationControlSrc = Files.readString(ResourceUtils.getResourceAsPath(path + "/tessellationControl.glsl"));
                String tessellationEvaluationSrc = Files.readString(ResourceUtils.getResourceAsPath(path + "/tessellationEvaluation.glsl"));

                Shader tessellationControlShader = new Shader(OpenGL.ShaderType.TESSELLATION_CONTROL, tessellationControlSrc);
                Shader tessellationEvaluationShader = new Shader(OpenGL.ShaderType.TESSELLATION_EVALUATION, tessellationEvaluationSrc);

                tessellationControlShader.init();
                tessellationEvaluationShader.init();

                tessellationControlShader.createShader(true);
                errLog = tessellationControlShader.getShaderCreationErrorLog();

                if(errLog != null) {
                    throw new GLException(errLog);
                }

                tessellationEvaluationShader.createShader(true);
                errLog = tessellationEvaluationShader.getShaderCreationErrorLog();

                if(errLog != null) {
                    throw new GLException(errLog);
                }

                resultantProgram = new TessellationProgram();
                resultantProgram.init();
                resultantProgram.addShader(tessellationControlShader);
                resultantProgram.addShader(tessellationEvaluationShader);

            } else {

                resultantProgram = new ShadingProgram();
                resultantProgram.init();
            }


            resultantProgram.addShader(vertexShader);
            resultantProgram.addShader(fragmentShader);
            resultantProgram.link(true);

        } catch (Exception e) {
            Logger.error(e);
            throw new IllegalStateException(e);
        }

        return resultantProgram;
    }

    @NotNull
    public static ShadingProgram newProgram(@NotNull String path) {
        return Program.newProgram(path, false);
    }

    @NotNull
    public static TessellationProgram newTessellationProgram(@NotNull String path) {
        return (TessellationProgram) Program.newProgram(path, true);
    }

    @Getter
    private boolean shadersAllocated;
    protected boolean locked = false;
    protected int identififer;

    public abstract void addShader(@NotNull Shader s);

    public void init() {
        this.identififer = glCreateProgram();
    }

    public void link(boolean discardShaders) {
        this.locked = true;
        glLinkProgram(this.identififer);
        var infoLog = this.getLinkErrorLog();

        if(infoLog != null) {
            throw new GLException(infoLog);
        }

        if(discardShaders) {
            this.shadersAllocated = false;
            this.discardShaders();
        }
    }

    @Nullable
    public String validateAndGetErrorLog() {
        glValidateProgram(this.identififer);
        int[] success = new int[1];
        glGetProgramiv(this.identififer, GL_VALIDATE_STATUS, success);
        if(success[0] == GL_FALSE) {
            return  glGetProgramInfoLog(this.identififer);
        }
        return null;
    }

    @Nullable
    private String getLinkErrorLog() {
        int[] success = new int[1];
        glGetProgramiv(this.identififer, GL_LINK_STATUS, success);
        if(success[0] == GL_FALSE) {
            return String.format(
                    "failed to link program: %s", glGetProgramInfoLog(this.identififer)
            );
        }
        return null;
    }

    @Override
    public int getIdentifier() {
        return this.identififer;
    }

    public int getUniformBlockIndex(@NotNull final String block) {
        var index = glGetUniformBlockIndex(this.getIdentifier(), block);
        if(index == GL_INVALID_INDEX) {
            throw new GLException("Shader does not contain uniform block by name " + block);
        }
        return index;
    }

    @Deprecated
    public void matrix4f(String name, Matrix4f matrix) {
        GL41.glUniformMatrix4fv(
                this.getUniform(name),
                false,
                matrix.get(new float[16])
        );
    }

    public void matrix4f(@NotNull UniformLocation location, @NotNull Matrix4f matrix) {
        GL41.glUniformMatrix4fv(
                location.location(),
                false,
                matrix.get(new float[16])
        );
    }

    public void matrix4f(@NotNull UniformLocation location, @NotNull FloatBuffer matrix) {
        glUniformMatrix4fv(
                location.location(),
                false,
                matrix
        );
    }

    public void matrix4f(@NotNull UniformLocation location, @NotNull float[] matrix) {
        glUniformMatrix4fv(
                location.location(),
                false,
                matrix
        );
    }

    public void integer(@NotNull UniformLocation location, int i) {
        glUniform1i(
                location.location(),
                i
        );
    }

    public abstract void discardShaders();

    @Override
    public void deallocate() {

        if(this.shadersAllocated) {
            this.shadersAllocated = false;
            this.discardShaders();
        }

        if(this.identififer != -1) {
            glDeleteProgram(this.identififer);
            this.identififer = -1;
        }
    }

    public int getUniform(@NotNull String name) {
        return GL41.glGetUniformLocation(this.getIdentifier(), name);
    }

    @Deprecated
    public void sampler(@NotNull String name, int slot) {
        glUniform1i(this.getUniform(name), slot);
    }

    public void sampler(@NotNull UniformLocation location, int slot) {
        glUniform1i(location.location(), slot);
    }
}

