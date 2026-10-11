package serap.sillyge.io.loaders.context.resourcereload;

import org.jetbrains.annotations.NotNull;
import serap.sillyge.SillyGameEngine;
import serap.sillyge.io.resource.ResourceIdentifier;
import serap.sillyge.io.resource.exception.ResourceLoadException;

import java.util.List;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Consumer;

public class ResourceReloadContext {
    private ExecutorService threadExecutor;
    private final List<Throwable> errors = new CopyOnWriteArrayList<>();
    private final ConcurrentLinkedQueue<GlThreadTask> glThreadTasks = new ConcurrentLinkedQueue<>();
    private int glTasksPerUpdate = 1;
    private int threads;
    private final AtomicInteger totalTasks = new AtomicInteger(0);
    final AtomicInteger finishedTasks = new AtomicInteger(0);

    public static final int COMPUTE_ALL_TASKS_ON_UPDATE = -1;

    public void setMaxGlTasksPerUpdate(int newMax) {
        this.glTasksPerUpdate = newMax;
    }

    public ResourceReloadContext(int numWorkerThreads, int glTasksPerUpdate) {
        this.threads = numWorkerThreads;
        this.setMaxGlTasksPerUpdate(glTasksPerUpdate);
        SillyGameEngine.debug("Created resource reload context with " + numWorkerThreads + " worker threads");
    }

    @NotNull
    private ExecutorService createThreadExecutor() {
        SillyGameEngine.debug("Created resource reload context thread executor with " + this.threads + " worker threads");
        return Executors.newFixedThreadPool(this.threads);
    }

    public void computeAsync(ResourceIdentifier identifier, Consumer<ComputeContext> r) {
        totalTasks.incrementAndGet();
        this.threadExecutor.submit(() -> this.submit(identifier, r));
    }

    void computeAsync(ComputeContext context, Consumer<ComputeContext> r) {
        this.threadExecutor.submit(() -> r.accept(context));
    }

    private void submit(ResourceIdentifier identifier, Consumer<ComputeContext> r) {
        ComputeContext context = new ComputeContext(this, identifier);
        try {
            r.accept(context);
        } catch (Exception throwable) {
            this.errors.add(new ResourceLoadException(identifier, throwable, false));
        }
    }

    public void prepareForResourceLoading() {
        if(this.threadExecutor != null) {
            SillyGameEngine.warn("Attempted to override live thread executor! something has likely gone wrong!");
            this.freeThreadExecutor();
        }

        this.threadExecutor = this.createThreadExecutor();
    }

    public boolean hasExceptions() {
        return !this.errors.isEmpty();
    }

    public void onFinishResourceLoading() {
        if(this.hasExceptions()) {
            SillyGameEngine.error("Resource loading finished exceptionally");
            this.logErrors();
            throw new RuntimeException("Encountered fatal error during resource loading");
        } else {
            SillyGameEngine.debug("Resource loading finished successfully");
        }
    }

    public void freeThreadExecutor() {
        this.threadExecutor.close();
        this.threadExecutor = null;
        SillyGameEngine.debug("Closed resource reload context thread executor");
    }

    private void logErrors() {
        this.errors.forEach(SillyGameEngine::error);
        this.errors.clear();
    }

    private int getGlThreadComputeNumberThisUpdate() {
        return this.glTasksPerUpdate == COMPUTE_ALL_TASKS_ON_UPDATE ? this.glThreadTasks.size() : Math.min(this.glTasksPerUpdate, this.glThreadTasks.size());
    }

    public void computeGlTasks() {
        int targetSize = this.getGlThreadComputeNumberThisUpdate();

        for(int i = 0 ; i < targetSize ; i++) {
            GlThreadTask task = this.glThreadTasks.poll();
            if(task == null) {
                SillyGameEngine.warn("obtained a null task to execute on the gl thread, skipping");
                continue;
            }
            try {
                task.run();
            } catch (Exception exception) {
                this.errors.add(new ResourceLoadException(task.getIdentifier(), exception, true));
            } finally {
                finishedTasks.incrementAndGet();
            }
        }
    }

    public boolean isDone() {
        return this.totalTasks.get() == this.finishedTasks.get();
    }

    public float getCompletionPercentage() {
        float totalTasks = this.totalTasks.get();
        float finishedTasks = this.finishedTasks.get();

        return finishedTasks / totalTasks;
    }

    public void computeOnGlThread(@NotNull ResourceIdentifier identifier, @NotNull Consumer<ComputeContext> r) {
        this.totalTasks.incrementAndGet();
        this.glThreadTasks.add(GlThreadTask.of(this, identifier, r));
    }

    void computeOnGlThread(@NotNull ComputeContext ctx, @NotNull Consumer<ComputeContext> r) {
        this.glThreadTasks.add(GlThreadTask.of(ctx, r));
    }

    private interface GlThreadTask extends Runnable {
        ResourceIdentifier getIdentifier();

        @NotNull
        private static GlThreadTask of(@NotNull ResourceReloadContext resourceReloadContext, @NotNull ResourceIdentifier identifier, @NotNull Consumer<ComputeContext> r) {
            return new GlThreadTask() {
                private final ComputeContext ctx = new ComputeContext(resourceReloadContext, identifier);
                public void run() {
                    r.accept(ctx);
                }

                @Override
                public ResourceIdentifier getIdentifier() {
                    return identifier;
                }
            };
        }

        @NotNull
        private static GlThreadTask of(@NotNull ComputeContext ctx, @NotNull Consumer<ComputeContext> r) {
            return new GlThreadTask() {
                @Override
                public ResourceIdentifier getIdentifier() {
                    return ctx.getIdentifier();
                }

                @Override
                public void run() {
                    r.accept(ctx);
                }
            };
        }
    }
}
