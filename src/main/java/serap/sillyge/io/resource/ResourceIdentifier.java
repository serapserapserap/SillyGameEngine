package serap.sillyge.io.resource;

public class ResourceIdentifier {
    public String resourceNamespace;
    public String resourceIdentifier;

    public String getNamespacedIdentifier() {
        return String.format("%s::%s", this.resourceNamespace, this.resourceIdentifier);
    }

    public String getIdentifier() {
        return this.resourceIdentifier;
    }

    public String getNamespace() {
        return this.resourceNamespace;
    }
}
