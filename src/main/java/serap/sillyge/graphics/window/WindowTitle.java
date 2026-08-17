package serap.sillyge.graphics.window;

import lombok.Getter;

public class WindowTitle {
    private String windowTitle;
    @Getter
    private boolean synced = false;

    public WindowTitle(String initialTitle) {
        this.windowTitle = initialTitle;
    }

    public String getWindowTitle() {
        this.synced = true;
        return this.windowTitle;
    }

    public void setWindowTitle(String newTitle) {
        this.windowTitle = newTitle;
        synced = false;
    }
}
