package serap.sillyge.input.events;

import org.lwjgl.glfw.GLFW;
import serap.sillyge.input.GLFWMods;

import java.util.Objects;

public final class MousePressedEvent implements GeneralizableInputEvent {
    private final int button;
    private final boolean release;
    private final GLFWMods mods;

    public MousePressedEvent(int button, int action, GLFWMods mods) {
        this.button = button;
        this.mods = mods;
        this.release = action == GLFW.GLFW_RELEASE;
    }

    public boolean isLeft() {
        return this.button == GLFW.GLFW_MOUSE_BUTTON_1;
    }

    public boolean isRight() {
        return this.button == GLFW.GLFW_MOUSE_BUTTON_2;
    }

    public boolean released() {
        return release;
    }

    public boolean pressed() {
        return !release;
    }

    public boolean is(int buttonEnum) {
        return this.button == buttonEnum;
    }

    public boolean isButton(int buttonNumerical) {
        return this.button == buttonNumerical - 1;
    }

    public GLFWMods mods() {
        return mods;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == this) return true;
        if (obj == null || obj.getClass() != this.getClass()) return false;
        var that = (MousePressedEvent) obj;
        return this.button == that.button &&
                Objects.equals(this.mods, that.mods);
    }

    @Override
    public int hashCode() {
        return Objects.hash(button, mods);
    }

    public GenericPressEvent asGeneric() {
        int code = GenericPressEvent.getMouseGenericCode(this.button);
        return new GenericPressEvent(
                code,
                code,
                mods
        );
    }

    @Override
    public String toString() {
        return "MousePressedEvent[" +
                "button=" + button + ", " +
                "mods=" + mods + ']';
    }

}
