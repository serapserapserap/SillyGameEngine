package serap.sillyge.input.events;

import serap.sillyge.input.GLFWMods;

import java.util.Objects;

public final class GenericPressEvent {
    private final int keycode;
    private final int scancode;
    private final GLFWMods mods;

    GenericPressEvent(int keycode, int scancode, GLFWMods mods) {
        this.keycode = keycode;
        this.scancode = scancode;
        this.mods = mods;
    }

    static int getMouseGenericCode(int button) {
        return -(button + 2);
    }

    static int getScrollGenericCode(boolean scrollX) {
        int value = scrollX ? 0 : 1;
        //surely there won't be a mouse that has 200 buttons
        return -(value + 2000);
    }

    public static int getMouseButtonFromGenericCode(int genericCode) {
        return -(genericCode) - 2;
    }

    public int keycode() {
        return keycode;
    }

    public int scancode() {
        return scancode;
    }

    public GLFWMods mods() {
        return mods;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == this) return true;
        if (obj == null || obj.getClass() != this.getClass()) return false;
        var that = (GenericPressEvent) obj;
        return this.keycode == that.keycode &&
                this.scancode == that.scancode &&
                Objects.equals(this.mods, that.mods);
    }

    @Override
    public int hashCode() {
        return Objects.hash(keycode, scancode, mods);
    }

    @Override
    public String toString() {
        return "GenericPressEvent[" +
                "keycode=" + keycode + ", " +
                "scancode=" + scancode + ", " +
                "mods=" + mods + ']';
    }
}
