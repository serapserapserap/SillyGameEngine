package serap.sillyge.core;

import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import lombok.Getter;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import serap.sillyge.io.resource.ResourceIdentifier;

import java.util.function.BiPredicate;
import java.util.function.Supplier;

/**
 * a helper class that allows for the storage and management of program resources
 * @param <T> Stored internal class of the registry
 */
public class Registry<T> {

    /**
     * Creates a new mutable registry
     * @param tag The tag for the registry to be identified under
     * @param pointerCreatFunc the supplier that the registry fetches from when registering a new entry
     * @return the resultant registry
     * @param <T> the stored type that the registry will hold
     * @param <P> the pointer type that the registry will use to hold the stored type {@code <T>}
     */
    @NotNull
    public static <T, P extends Pointer<T>> Registry.Mutable<T, P> newMutableRegistry(String tag, Supplier<P> pointerCreatFunc) {
        return new Mutable<>(tag) {
            @Override
            public P register(ResourceIdentifier identifier) {
                P pointer;
                this.register(identifier, pointer = pointerCreatFunc.get());
                return pointer;
            }
        };
    }

    /**
     * Creates a new regular registry
     * @param tag the tag for the registry to be identified under
     * @return the resultant registry
     * @param <T> the stored type that the registry will hold
     */
    @NotNull
    public static <T> Registry<T> newRegistry(String tag) {
        return new Registry<>(tag);
    }

    private Registry(String tag) {
        this.tag = tag;
    }

    private boolean writable = true;

    public void lock() {
        this.writable = false;
    }

    public boolean isLocked() {
        return this.writable;
    }

    @Getter
    private final String tag;
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

    /**
     * returns if the registry contains the supplied identifier
     * @param identifier the identifier to scan for
     * @return if the registry has a value associated with the supplied identifier
     */
    protected boolean containsIdentifier(ResourceIdentifier identifier) {
        return this.get(identifier) != null;
    }

    /**
     * registers an object and associates it with an identifier
     * @param identifier the identifier to be associated with the registrant
     * @param registrant the registrant being registered into the registry
     * @throws IllegalStateException if the identifier supplied already has an associated value
     * @throws IllegalStateException if the registry is locked
     */
    public void register(ResourceIdentifier identifier, T registrant) {
        if(this.isLocked()) {
            throw new IllegalStateException("Registry is locked");
        }

        if(this.containsIdentifier(identifier)) {
            throw new IllegalStateException("There is already a value associated with the identifier " + identifier.toString());
        }

        this.registrants.add(new Registrant<>(identifier, registrant));
    }

    protected static class Registrant<T> {
        protected ResourceIdentifier identifier;
        protected T registrant;

        public Registrant(ResourceIdentifier id, T registrant) {
            this.identifier = id;
            this.registrant = registrant;
        }
    }

    public static abstract class Mutable<T, P extends Pointer<T>> extends Registry<P> {
        public abstract P register(ResourceIdentifier identifier);

        public Mutable(String tag) {
            super(tag);
        }
    }
}
