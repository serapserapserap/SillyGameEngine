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

    @Override
    public boolean equals(Object other) {
        if(other == this) return true;

        if(other instanceof ResourceIdentifier otherResourceIdentifier) {
            return this.resourceNamespace.equals(otherResourceIdentifier.resourceNamespace) && this.resourceIdentifier.equals(otherResourceIdentifier.resourceIdentifier);
        }

        return false;
    }

    @Override
    public String toString() {
        return String.format(
                "ResourceIdentifier[namespace=%s, identifier=%s]",
                this.resourceNamespace,
                this.resourceIdentifier
        );
    }
}
