package serap.sillyge.input;

import lombok.Getter;

import static org.lwjgl.glfw.GLFW.*;
import static org.lwjgl.glfw.GLFW.GLFW_MOD_NUM_LOCK;

@Getter
public class GLFWMods {
    private final boolean altPressed;
    private final boolean ctrlPressed;
    private final boolean capsLockPressed;
    private final boolean shiftPressed;
    private final boolean superPressed;
    private final boolean numLockPressed;

    public static GLFWMods sourceless(long window) {
        boolean shift = (glfwGetKey(window, GLFW_KEY_LEFT_SHIFT) == GLFW_PRESS) || (glfwGetKey(window, GLFW_KEY_RIGHT_SHIFT) == GLFW_PRESS);
        boolean ctrl = (glfwGetKey(window, GLFW_KEY_LEFT_CONTROL) == GLFW_PRESS) || (glfwGetKey(window, GLFW_KEY_RIGHT_CONTROL) == GLFW_PRESS);
        boolean alt = (glfwGetKey(window, GLFW_KEY_LEFT_ALT) == GLFW_PRESS) || (glfwGetKey(window, GLFW_KEY_RIGHT_ALT) == GLFW_PRESS);
        boolean supr = (glfwGetKey(window, GLFW_KEY_LEFT_SUPER) == GLFW_PRESS) || (glfwGetKey(window, GLFW_KEY_RIGHT_SUPER) == GLFW_PRESS);
        boolean numLock = (glfwGetKey(window, GLFW_KEY_NUM_LOCK) == GLFW_PRESS);
        boolean capsLock = (glfwGetKey(window, GLFW_KEY_CAPS_LOCK) == GLFW_PRESS);
        return new GLFWMods(
                alt,
                ctrl,
                capsLock,
                shift,
                supr,
                numLock
        );
    }

    public static GLFWMods of(int source) {
        return new GLFWMods(
                (source & GLFW_MOD_ALT) == GLFW_MOD_ALT,
                (source & GLFW_MOD_CONTROL) == GLFW_MOD_CONTROL,
                (source & GLFW_MOD_CAPS_LOCK) == GLFW_MOD_CAPS_LOCK,
                (source & GLFW_MOD_SHIFT) == GLFW_MOD_SHIFT,
                (source & GLFW_MOD_SUPER) == GLFW_MOD_SUPER,
                (source & GLFW_MOD_NUM_LOCK) == GLFW_MOD_NUM_LOCK
        );
    }

    public GLFWMods(boolean altPressed, boolean ctrlPressed, boolean capsLockPressed, boolean shiftPressed, boolean superPressed, boolean numLockPressed) {
        this.altPressed = altPressed;
        this.ctrlPressed = ctrlPressed;
        this.capsLockPressed = capsLockPressed;
        this.shiftPressed = shiftPressed;
        this.superPressed = superPressed;
        this.numLockPressed = numLockPressed;
    }

    @Override
    public String toString() {
        return String.format(
                "GLFWMods[SHIFT=%s, ALT=%s, CTRL=%s, CAPSLOCK=%s, SUPER=%s, NUMLOCK=%s]",
                this.isShiftPressed(),
                this.isAltPressed(),
                this.isCtrlPressed(),
                this.isCapsLockPressed(),
                this.isSuperPressed(),
                this.isNumLockPressed()
        );
    }
}
