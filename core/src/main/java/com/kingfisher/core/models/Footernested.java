package com.kingfisher.core.models;

import org.apache.sling.api.resource.Resource;
import org.apache.sling.models.annotations.DefaultInjectionStrategy;
import org.apache.sling.models.annotations.Model;
import org.apache.sling.models.annotations.injectorspecific.ValueMapValue;

@Model(adaptables = Resource.class, defaultInjectionStrategy = DefaultInjectionStrategy.OPTIONAL)
public class Footernested {

    @ValueMapValue
    private String foot_tab;

    public String getFoot_tab() {
        return foot_tab;
    }

    @ValueMapValue
    private String foot_tablink;

    public String getFoot_tablink() {
        return foot_tablink;
    }
}
