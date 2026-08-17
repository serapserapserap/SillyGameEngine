package serap.sillyge.input.events;

import serap.sillyge.input.GLFWMods;

import java.util.Objects;

public final class KeyPressedEvent implements GeneralizableInputEvent {
    private final int keycode;
    private final int scanCode;
    private final GLFWMods mods;

    public KeyPressedEvent(int keycode, int scanCode, GLFWMods mods) {
        this.keycode = keycode;
        this.scanCode = scanCode;
        this.mods = mods;
    }

    public int keycode() {
        return keycode;
    }

    public int scanCode() {
        return scanCode;
    }

    public GLFWMods mods() {
        return mods;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == this) return true;
        if (obj == null || obj.getClass() != this.getClass()) return false;
        var that = (KeyPressedEvent) obj;
        return this.keycode == that.keycode &&
                this.scanCode == that.scanCode &&
                Objects.equals(this.mods, that.mods);
    }

    @Override
    public int hashCode() {
        return Objects.hash(keycode, scanCode, mods);
    }

    public GenericPressEvent asGeneric() {
        return new GenericPressEvent(
                this.keycode,
                this.scanCode,
                mods
        );
    }

    @Override
    public String toString() {
        return "KeyPressedEvent[" +
                "keycode=" + keycode + ", " +
                "scanCode=" + scanCode + ", " +
                "mods=" + mods + ']';
    }

}
