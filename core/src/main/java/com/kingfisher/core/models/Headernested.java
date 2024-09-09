package com.kingfisher.core.models;

import org.apache.sling.api.resource.Resource;
import org.apache.sling.models.annotations.DefaultInjectionStrategy;
import org.apache.sling.models.annotations.Model;
import org.apache.sling.models.annotations.injectorspecific.ValueMapValue;

@Model(adaptables = Resource.class, defaultInjectionStrategy = DefaultInjectionStrategy.OPTIONAL)
public class Headernested {

    @ValueMapValue
    private String tab;

    public String getTab() {
        return tab;
    }

    @ValueMapValue
    private String tablink;

    public String getTablink() {
        return tablink;
    }
}
