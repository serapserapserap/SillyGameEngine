package serap.sillyge.core;

import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import serap.sillyge.io.resource.ResourceIdentifier;

import java.util.function.BiPredicate;

/**
 * a helper class that allows for the storage and management of program resources
 * @param <T> Stored internal class of the registry
 */
public abstract class Registry<T> {
    protected final ObjectArrayList<Registrant<T>> registrants = new ObjectArrayList<>();

    /**
     * Attempts to access a registered object
     * @param identifier the identifier of the registered object that you're trying to access
     * @return the object registered under the supplied identifier, or null if no object is registered under the supplied identifier
     */
    @Nullable
    public T get(ResourceIdentifier identifier) {
        return findFirstWhere((objectIdentifier, obj) -> objectIdentifier.equals(identifier));
    }

    /**
     * Attempts to access a registered object
     * @param id the un-namespaced id of the registered object that you're trying to access
     * @return the first object found with the supplied identifier, or null if no object is registered under the supplied identifier
     */
    public T getFirst(String id) {
        return findFirstWhere((identifier, obj) -> identifier.getIdentifier().equals(id));
    }

    /**
     * finds all registered objects that pass the supplied predicate
     * @param predicate the predicate to test the registrants against
     * @return an {@link ObjectArrayList} of all the registered objects that pass the supplied {@link BiPredicate}
     */
    @NotNull
    public ObjectArrayList<T> findAllWhere(BiPredicate<ResourceIdentifier, T> predicate) {
        ObjectArrayList<T> resultant = new ObjectArrayList<>();

        for(Registrant<T> registrant : this.registrants) {
            if(predicate.test(registrant.identifier, registrant.registrant)) {
                resultant.add(registrant.registrant);
            }
        }

        return resultant;
    }

    /**
     * returns the first object that passes the supplied predicate
     * @param predicate the predicate to test registry registrants against
     * @return the first found registrant that passes the {@link BiPredicate} supplied, or null if no registrants passed
     */
    @Nullable
    public T findFirstWhere(BiPredicate<ResourceIdentifier, T> predicate) {
        for(Registrant<T> registrant : this.registrants) {
            if(predicate.test(registrant.identifier, registrant.registrant)) {
                return registrant.registrant;
            }
        }
        return null;
    }

    protected static class Registrant<T> {
        protected ResourceIdentifier identifier;
        protected T registrant;
    }
}
