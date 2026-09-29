package serap.sillyge.graphics.opengl.program;

import org.jetbrains.annotations.NotNull;
import serap.sillyge.graphics.exception.GLException;
import serap.sillyge.graphics.opengl.program.shader.Shader;

import static org.lwjgl.opengl.GL20.glAttachShader;

public class TessellationProgram extends ShadingProgram {
    private Shader tesselationControlShader;
    private Shader tessellationEvaluationShader;

    @Override
    public void addShader(@NotNull Shader s) {

        if(this.locked) {
            throw new GLException("attempted to modify an already compiled program");
        }

        switch (s.getType()) {
            case VERTEX -> this.vertexShader = s;
            case FRAGMENT -> this.fragmentShader = s;
            case TESSELLATION_CONTROL -> this.tesselationControlShader = s;
            case TESSELLATION_EVALUATION -> this.tessellationEvaluationShader = s;
        }

        glAttachShader(this.identififer, s.getIdentifier());
    }

    @Override
    public void calculateShaderSubroutineUniforms() {
        super.calculateShaderSubroutineUniforms();
        //todo -> can't be fucked
    }

    @Override
    public void discardShaders() {
        super.discardShaders();
        this.tessellationEvaluationShader.close();
        this.tesselationControlShader.close();
    }
}
