package serap.sillyge.io.loaders.context.resourcereload;

import serap.sillyge.SillyGameEngine;
import serap.sillyge.io.resource.ResourceIdentifier;

import java.util.function.Consumer;

public class ComputeContext {
    private final ResourceReloadContext parentReloadContext;
    private ResourceIdentifier identifier;
    private boolean isFinished = false;

    ResourceIdentifier  getIdentifier() {
        return this.identifier;
    }

    boolean isFinished() {
        return this.isFinished;
    }

    ComputeContext(ResourceReloadContext context, ResourceIdentifier identifier) {
        this.parentReloadContext = context;
    }

    public void followUpWithAsyncCompute(Consumer<ComputeContext> r) {
        if(assertFinished()) return;
        this.parentReloadContext.computeAsync(this, r);
    }

    public void followUpWithGlCompute(Consumer<ComputeContext> r) {
        if(assertFinished()) return;
        this.parentReloadContext.computeOnGlThread(this, r);
    }

    private boolean assertFinished() {
        if(this.isFinished()) {
            SillyGameEngine.warn("Attempted to submit another compute after original compute was marked as finished, skipping");
            return true;
        }
        return false;
    }

    void markComputeAsFinished() {
        this.isFinished = true;
        this.parentReloadContext.finishedTasks.incrementAndGet();
    }
}
