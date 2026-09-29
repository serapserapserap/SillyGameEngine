package serap.sillyge.core;

import serap.sillyge.io.resource.ResourceRegistry;

public abstract class Managers {

    private static ResourceRegistry currentResourceManager;

    public static void initManagers() {

    }

    private static void createManagers() {

    }

    public static ResourceRegistry getResourceManager() {
        return currentResourceManager;
    }
}
