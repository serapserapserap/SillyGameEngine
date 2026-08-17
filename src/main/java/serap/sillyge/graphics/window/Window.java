package serap.sillyge.graphics.window;

import lombok.Getter;
import org.jetbrains.annotations.NotNull;
import org.lwjgl.glfw.GLFW;
import org.lwjgl.glfw.GLFWCursorEnterCallback;
import org.lwjgl.glfw.GLFWFramebufferSizeCallback;
import org.lwjgl.glfw.GLFWWindowCloseCallback;
import org.lwjgl.system.Callback;
import org.lwjgl.system.MemoryUtil;
import org.tinylog.Logger;
import serap.sillyge.input.inputsystem.InputSystem;
import serap.sillyge.graphics.opengl.LongIdentifiable;
import serap.sillyge.graphics.opengl.OpenGL;

import static org.lwjgl.glfw.GLFW.*;

public class Window implements LongIdentifiable {
    private long handle;
    protected boolean windowHasFocus;
    protected int windowWidth;
    protected int windowHeight;
    @Getter
    private InputSystem inputSystem;
    @Getter
    private WindowTitle title;

    public void setInputSystem(@NotNull InputSystem newSystem) {
        this.inputSystem = newSystem;
        newSystem.bindToWindow(this);
    }

    /**
     * Runs when the window is resized
     * @param newWidth the new window width
     * @param newHeight the new window height
     */
    protected void onResizeFramebuffer(int newWidth, int newHeight) {
        this.windowWidth = newWidth;
        this.windowHeight = newHeight;
    }

    /**
     * Runs when the mouse cursor enters the window's bounds
     */
    protected void onMouseEnterWindowBounds() {
        this.windowHasFocus = true;
    }

    /**
     * Runs when the mouse cursor exits the window's bounds
     */
    protected void onMouseExitWindowBounds() {
        this.windowHasFocus = false;
    }

    /**
     * Runs when the window is requested to close. You do not need to do anything to make the window close successfully
     */
    protected void onWindowClose() {

    }

    public static void ensureIsWindow(@NotNull Window target, long candidate) {
        if(target.is(candidate)) {
            return;
        }

        throw new IllegalStateException("Window assertion failed! [target=" + target.getIdentifier() + ", candidate=" + candidate + "]");
    }

    public static void closeCallbackIfNotNull(Callback cb) {
        if(cb != null) cb.close();
    }

    private void setCallbacks() {
        closeCallbackIfNotNull(
                glfwSetFramebufferSizeCallback(
                        this.handle,
                        new GLFWFramebufferSizeCallback() {
                                @Override
                                public void invoke(long window, int width, int height) {
                                    if(!Window.this.is(window)) {
                                        return;
                                    }

                                    Window.this.windowWidth = width;
                                    Window.this.windowHeight = height;
                                    onResizeFramebuffer(width, height);
                                }
                        }
                )
        );
        closeCallbackIfNotNull(
                glfwSetCursorEnterCallback(
                        this.handle,
                        new GLFWCursorEnterCallback() {
                            @Override
                            public void invoke(long window, boolean entered) {

                                if(!Window.this.is(window)) {
                                    return;
                                }

                                Runnable r = entered ? Window.this::onMouseEnterWindowBounds : Window.this::onMouseExitWindowBounds;
                                r.run();
                            }
                        }
                )
        );
        closeCallbackIfNotNull(
                glfwSetWindowCloseCallback(
                        this.handle,
                        new GLFWWindowCloseCallback() {
                            @Override
                            public void invoke(long window) {

                                if(!Window.this.is(window)) {
                                    return;
                                }

                                Window.this.onWindowClose();
                            }
                        }
                )
        );

        if(this.inputSystem == null) {
            Logger.warn("input system is null, all inputs will be ignored");
        }


    }

    public Window() {}

    @Override
    public void close() {
        glfwDestroyWindow(this.getIdentifier());
    }

    public void init(WindowTitle title, InputSystem inputSystem, boolean autoSize, int startWidth, int startHeight) {
        this.init(title, autoSize, startWidth, startHeight);
        this.setInputSystem(inputSystem);
    }

    public void init(WindowTitle title, boolean autoSize, int startWidth, int startHeight) {
        if(!GLFW.glfwInit()) {
            throw new IllegalStateException("Failed to init glfw");
        }

        this.title = title;

        long monitor = glfwGetPrimaryMonitor();
        var vidMode = glfwGetVideoMode(monitor);
        if (vidMode == null) {
            Logger.warn("Failed to find main monitor, using default window dimensions");
            this.windowWidth = startWidth;
            this.windowHeight = startHeight;
        } else if(autoSize){
            this.windowWidth = vidMode.width();
            this.windowHeight = vidMode.height();
        } else {
            this.windowWidth = startWidth;
            this.windowHeight = startHeight;
        }

        //these are for cross compatibility (so it works on mac)
        //not sure how to make it work without these really annoying settings


        //4.2 optimisations - immutable textures
        //4.3 optimisations - shader storage buffers (SSBO), compute shaders
        // nothing else super notable after those
        for(int i = 6 ; i > 0 ; i--) {
            try {
                glfwWindowHint(GLFW_CONTEXT_VERSION_MAJOR, 4);
                glfwWindowHint(GLFW_CONTEXT_VERSION_MINOR, i);
                glfwWindowHint(GLFW_OPENGL_PROFILE, GLFW_OPENGL_CORE_PROFILE);
                glfwWindowHint(GLFW_OPENGL_FORWARD_COMPAT, GLFW_TRUE);
                glfwWindowHint(GLFW_COCOA_RETINA_FRAMEBUFFER, GLFW_FALSE);

                this.handle = glfwCreateWindow(
                        this.windowWidth,
                        this.windowHeight,
                        this.title.getWindowTitle(),
                        MemoryUtil.NULL,
                        MemoryUtil.NULL
                );

                if(this.handle == MemoryUtil.NULL) {
                    throw new RuntimeException("Failed to create GLFW window");
                }
                Logger.info("Created openGLv4." + i + " context");
                OpenGL.setVersionMinor(i);
                break;

            } catch (RuntimeException e) {
                Logger.warn("Failed to create openGLv4." + i + " context");
            }
        }
    }

    @Override
    public long getIdentifier() {
        return this.handle;
    }
}
