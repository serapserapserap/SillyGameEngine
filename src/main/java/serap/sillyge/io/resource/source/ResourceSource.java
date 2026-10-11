package serap.sillyge.io.resource.source;

import serap.sillyge.io.resource.ResourceIdentifier;
import serap.sillyge.io.resource.accessor.ResourceAccessor;

public interface ResourceSource {
    ResourceAccessor getAccessor(ResourceIdentifier identifier);
}
