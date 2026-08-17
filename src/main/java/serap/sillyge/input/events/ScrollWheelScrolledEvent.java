package serap.sillyge.input.events;

import serap.sillyge.input.GLFWMods;

public class ScrollWheelScrolledEvent implements GeneralizableInputEvent{
    private final double amount;
    private final boolean x;
    private final GLFWMods mods;

    public ScrollWheelScrolledEvent(long windowPointer, boolean scrollX, double scrollAmnt) {
        this.amount = scrollAmnt;
        this.x = scrollX;
        mods = GLFWMods.sourceless(windowPointer);
    }

    public boolean isScrollX() {
        return x;
    }

    public double getScrollAmount() {
        return this.amount;
    }

    @Override
    public GenericPressEvent asGeneric() {
        int code = GenericPressEvent.getScrollGenericCode(this.isScrollX());
        return new GenericPressEvent(
                code,
                code,
                mods
        );
    }
}
