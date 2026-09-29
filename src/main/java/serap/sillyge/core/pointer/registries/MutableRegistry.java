package serap.sillyge.core.pointer.registries;

import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import serap.sillyge.core.Pointer;
import serap.sillyge.core.Registry;
import serap.sillyge.io.resource.ResourceIdentifier;

import java.util.function.BiPredicate;

/**
 * A registry where the contents are now always constant, registry contents can have their value modified.
 * @param <T> The class that the registry will store
 * @param <P> The type of pointer that the registry takes
 */
public class MutableRegistry<T, P extends Pointer<T>> extends Registry<P> {
}
