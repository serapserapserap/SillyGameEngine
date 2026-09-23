package serap.sillyge.graphics.opengl.program;

import org.jetbrains.annotations.NotNull;
import serap.sillyge.graphics.exception.GLException;
import serap.sillyge.graphics.opengl.program.shader.Shader;

import static org.lwjgl.opengl.GL20.glAttachShader;

public class ShadingProgram extends Program {




    protected Shader fragmentShader;
    protected Shader vertexShader;
    //tesselationShaders can be another class
    //and geometry shaders are so utterly shit that i cbf to waste memory on a pointer that is almost always gonna be null





    //maybe try storing shaders in here in future? atm I don't see a need unless there's an error related to them
    @Override
    public void addShader(@NotNull Shader s) {

        if(this.locked) {
            throw new GLException("attempted to modify an already compiled program");
        }

        switch (s.getType()) {
            case VERTEX -> this.vertexShader = s;
            case FRAGMENT -> this.fragmentShader = s;
        }

        glAttachShader(this.identififer, s.getIdentifier());
    }

    @Override
    public void discardShaders() {
        this.vertexShader.close();
        this.fragmentShader.close();
    }


    //probably not really needed, since you should realistically just set this stuff manually
    //obvs you do this when you physically cannot do that (if the shader code is generated at runtime
    //but i lowk doubt we'll run into that issue
    protected void calculateShaderSubroutineUniforms() {
        /*if(this.fragmentShader.shouldQuerySubroutineUniforms()) {
            this.fragmentShader.setSubroutineUniforms(
                    glGetProgramStagei(this.identififer, this.fragmentShader.getType().identifier, GL_ACTIVE_SUBROUTINE_UNIFORMS)
            );
        }
        if(this.vertexShader.shouldQuerySubroutineUniforms()) {
            this.vertexShader.setSubroutineUniforms(
                    glGetProgramStagei(this.identififer, this.vertexShader.getType().identifier, GL_ACTIVE_SUBROUTINE_UNIFORMS)
            );
        }*/
    }

    //todo -> I don't quite think this works?
    //todo -> nevermind this stuff is REALLY confusing, I'll finish it later because it's pretty useful but damn
    /*public void setShaderSubroutines(@NotNull Shader shader, int... routineIndexesOrdered) {
        try(MemoryStack memoryStack = MemoryStack.stackPush()) {

            glUniformSubroutinesuiv(shader.getType().identifier, );
        }
    }*/
}

