package serap.sillyge.input.events;

import java.util.Objects;

public final class CharacterInputEvent {
    private final String stringValue;
    private final int codepoint;

    public static CharacterInputEvent of(int codepoint) {
        return new CharacterInputEvent(Character.toString(codepoint), codepoint);
    }

    public CharacterInputEvent(String stringValue, int codepoint) {
        this.stringValue = stringValue;//Character.toString(codepoint);
        this.codepoint = codepoint;
    }

    public String string() {
        return stringValue;
    }

    public int codepoint() {
        return codepoint;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == this) return true;
        if (obj == null || obj.getClass() != this.getClass()) return false;
        var that = (CharacterInputEvent) obj;
        return Objects.equals(this.stringValue, that.stringValue) &&
                this.codepoint == that.codepoint;
    }

    @Override
    public int hashCode() {
        return Objects.hash(stringValue, codepoint);
    }

    @Override
    public String toString() {
        return "CharacterInputEvent[" +
                "s=" + stringValue + ", " +
                "codepoint=" + codepoint + ']';
    }

}
