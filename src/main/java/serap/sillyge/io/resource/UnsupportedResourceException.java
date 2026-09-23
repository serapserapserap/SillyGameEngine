package serap.sillyge.io.resource;

public final class UnsupportedResourceException extends RuntimeException {
    public UnsupportedResourceException(String string) {
        super(string);
    }
    public UnsupportedResourceException() {}
    public UnsupportedResourceException(String string, Throwable cause) {
        super(string, cause);
    }
    public UnsupportedResourceException(Throwable cause) {super(cause);}
}
