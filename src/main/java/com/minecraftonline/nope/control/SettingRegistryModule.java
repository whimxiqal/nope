package com.minecraftonline.nope.control;

import org.spongepowered.api.registry.CatalogRegistryModule;

import javax.annotation.Nonnull;
import java.util.Collection;

public interface SettingRegistryModule extends CatalogRegistryModule<Setting<?>> {
    @Nonnull
    Collection<Setting<?>> getByApplicability(Setting.Applicability applicability);
}
