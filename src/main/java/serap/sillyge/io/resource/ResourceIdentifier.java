package serap.sillyge.io.resource;

import serap.sillyge.SillyGameEngine;

public class ResourceIdentifier {

    public static ResourceIdentifier engineIdentifierOf(String identifier) {
        return new ResourceIdentifier(SillyGameEngine.ENGINE_NAME_SHORT, identifier);
    }

    public String resourceNamespace;
    public String resourceIdentifier;

    public String getNamespacedIdentifier() {
        return String.format("%s::%s", this.resourceNamespace, this.resourceIdentifier);
    }

    public ResourceIdentifier(String namespace, String identifier) {
        this.resourceNamespace = namespace;
        this.resourceIdentifier = identifier;
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
