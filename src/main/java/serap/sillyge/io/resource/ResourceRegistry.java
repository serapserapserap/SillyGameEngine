package serap.sillyge.io.resource;

import serap.sillyge.core.Registry;
import serap.sillyge.graphics.opengl.program.Program;

/**
 * how to do resources
 * have resource supplies (or resource packs in video game terms) as a stack
 * start at the built-in textures and shaders
 * and work your way up the list, every time you get a hit on an asset you update the fallback resource
 * which will then make theh primary the found texture, and delligate the old primary to be the fallback
 * this makes it so nothing should every have a null texture or value
 * (unless i make an engine error or the type is something that cannot have an initial fallback associated with it (like a shader))
 */
public class ResourceRegistry {
    public static Registry.Mutable<Program, FallbackResource<Program>> SHADER_REGISTRY;

    public ResourceRegistry() {
        SHADER_REGISTRY = Registry.newMutableRegistry(
                "resource::shader",
                () -> new FallbackResource<>(
                        null
                )
        );
    }


}
