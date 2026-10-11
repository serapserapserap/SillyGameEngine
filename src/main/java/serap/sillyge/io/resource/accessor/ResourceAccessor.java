package serap.sillyge.io.resource.accessor;

import serap.sillyge.io.resource.ResourceIdentifier;
import serap.sillyge.io.resource.type.Extension;

import java.io.InputStream;

public interface ResourceAccessor {
    InputStream getAsStream(Extension extension);
    ResourceIdentifier getIdentifier();
}
