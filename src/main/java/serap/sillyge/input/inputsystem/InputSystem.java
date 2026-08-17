package serap.sillyge.input.inputsystem;

import lombok.Getter;
import org.lwjgl.glfw.*;
import org.lwjgl.system.Callback;
import serap.sillyge.input.GLFWMods;
import serap.sillyge.input.events.*;
import serap.sillyge.graphics.window.Window;

import static org.lwjgl.glfw.GLFW.*;
import static serap.sillyge.graphics.window.Window.closeCallbackIfNotNull;

public abstract class InputSystem {
    @Getter
    private boolean rawMouseMotion = false;
    @Getter
    private boolean mouseGrabbed = false;
    private Window windowBoundTo;

    protected abstract void onCharInput(CharacterInputEvent characterInputEvent);
    protected abstract void onMousePress(MousePressedEvent mousePressedEvent);
    protected abstract void onKeyPressed(KeyPressedEvent keyPressedEvent);
    protected abstract void onMWheelScrolled(ScrollWheelScrolledEvent scrolledEvent);
    protected abstract void onGenericPress(GenericPressEvent genericPressEvent);
    protected abstract void onMouseMove(double xPos, double yPos);

    public void bindToWindow(Window w) {
        this.windowBoundTo = w;
        closeCallbackIfNotNull(
                glfwSetCursorPosCallback(
                        w.getIdentifier(),
                        this.generateCursorPosCallback()
                )
        );
        closeCallbackIfNotNull(
                glfwSetMouseButtonCallback(
                        w.getIdentifier(),
                        this.generateMouseCallback()
                )
        );
        closeCallbackIfNotNull(
                glfwSetScrollCallback(
                        w.getIdentifier(),
                        this.generateScrollCallback()
                )
        );
        closeCallbackIfNotNull(
                glfwSetKeyCallback(
                        w.getIdentifier(),
                        this.generateKeyCallback()
                )
        );
        closeCallbackIfNotNull(
                glfwSetCharCallback(
                        w.getIdentifier(),
                        this.generateCharCallback()
                )
        );
    }



    public Window getWindow() {
        return this.windowBoundTo;
    }

    public PressFiltering getMousePressedToGenericEventFilterFunction() {
        return PressFiltering.PRESS_ONLY;
    }

    public void setRawMouseMovementEnabled(boolean state) {
        if(state == rawMouseMotion) return;
        rawMouseMotion = state;
        glfwSetInputMode(this.getWindow().getIdentifier(), GLFW_RAW_MOUSE_MOTION, state ? GLFW_TRUE : GLFW_FALSE);
    }

    public void setMouseGrabbed(boolean state) {
        if(state == mouseGrabbed) return;
        mouseGrabbed = state;
        glfwSetInputMode(this.getWindow().getIdentifier(), GLFW_CURSOR, state ? GLFW_CURSOR_DISABLED : GLFW_CURSOR_NORMAL);
    }

    private boolean passesMousePressedToGenericEventFiltering(MousePressedEvent event) {
        return this.getMousePressedToGenericEventFilterFunction().shouldFilter(event);
    }

    private void onGenericPressInternal(GeneralizableInputEvent generalizableInputEvent) {
        this.onGenericPress(generalizableInputEvent.asGeneric());
    }

    private void dispatchScrollEvents(ScrollWheelScrolledEvent event) {
        this.onMWheelScrolled(event);
        this.onGenericPressInternal(event);
    }

    public GLFWKeyCallbackI generateKeyCallback() {
        return new GLFWKeyCallback() {
            @Override
            public void invoke(long handle, int keyCode, int scanCode, int action, int mods) {
                Window.ensureIsWindow(InputSystem.this.getWindow(), handle);
                KeyPressedEvent event = new KeyPressedEvent(keyCode, scanCode, GLFWMods.of(mods));

                InputSystem.this.onKeyPressed(event);
                InputSystem.this.onGenericPressInternal(event);
            }
        };
    }
    public GLFWMouseButtonCallbackI generateMouseCallback() {
        return new GLFWMouseButtonCallback() {
            @Override
            public void invoke(long handle, int button, int action, int mods) {
                Window.ensureIsWindow(InputSystem.this.getWindow(), handle);
                MousePressedEvent event = new MousePressedEvent(button, action, GLFWMods.of(mods));

                InputSystem.this.onMousePress(event);
                if(!InputSystem.this.passesMousePressedToGenericEventFiltering(event)) return;
                InputSystem.this.onGenericPressInternal(event);
            }
        };
    }
    public GLFWCharCallbackI generateCharCallback() {
        return new GLFWCharCallback() {
            @Override
            public void invoke(long handle, int codepoint) {
                Window.ensureIsWindow(InputSystem.this.getWindow(), handle);
                CharacterInputEvent event = CharacterInputEvent.of(codepoint);

                InputSystem.this.onCharInput(event);
            }
        };
    }
    public GLFWScrollCallbackI generateScrollCallback() {
        return new GLFWScrollCallback() {
            @Override
            public void invoke(long handle, double xoffset, double yoffset) {
                Window.ensureIsWindow(InputSystem.this.getWindow(), handle);
                if(xoffset != 0) {
                    ScrollWheelScrolledEvent event = new ScrollWheelScrolledEvent(handle, true, xoffset);
                    InputSystem.this.dispatchScrollEvents(event);
                }

                if(yoffset != 0) {
                    ScrollWheelScrolledEvent event = new ScrollWheelScrolledEvent(handle, false, yoffset);
                    InputSystem.this.dispatchScrollEvents(event);
                }
            }
        };
    }
    public GLFWCursorPosCallbackI generateCursorPosCallback() {
        return new GLFWCursorPosCallback() {
            @Override
            public void invoke(long handle, double xpos, double ypos) {
                Window.ensureIsWindow(InputSystem.this.getWindow(), handle);
                InputSystem.this.onMouseMove(xpos, ypos);
            }
        };
    }

     public enum PressFiltering {
        PRESS_ONLY(true, false),
        RELEASE_ONLY(false, true),
        BOTH(true, true);
        private final boolean allowPress;
        private final boolean allowRelease;
        PressFiltering(boolean allowPress, boolean allowRelease) {
            this.allowPress = allowPress;
            this.allowRelease = allowRelease;
        }

        public boolean shouldFilter(MousePressedEvent event) {
            return (event.pressed() && allowPress) || (event.released() && allowRelease);
        }
    }
}
