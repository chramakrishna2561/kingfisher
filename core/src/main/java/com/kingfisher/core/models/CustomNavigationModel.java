// package com.kingfisher.core.models;

// import org.apache.sling.api.resource.Resource;
// import org.apache.sling.api.resource.ResourceResolver;
// import org.apache.sling.models.annotations.DefaultInjectionStrategy;
// import org.apache.sling.models.annotations.Model;
// import org.apache.sling.models.annotations.injectorspecific.ChildResource;
// import org.apache.sling.models.annotations.injectorspecific.ValueMapValue;

// import com.adobe.cq.wcm.core.components.models.NavigationItem;

// import java.util.ArrayList;
// import java.util.Iterator;
// import java.util.List;

// @Model(adaptables = Resource.class, defaultInjectionStrategy =
// DefaultInjectionStrategy.OPTIONAL)
// public class CustomNavigationModel {

// @ValueMapValue
// private String basePath;

// @ChildResource
// private List<CustomNavigationItem> manualNavItems;

// public List<CustomNavigationItem> getManualNavItems() {
// return manualNavItems;
// }

// public List<NavigationItem> getAutoNavItems() {
// List<NavigationItem> autoNavItems = new ArrayList<>();
// if (basePath != null) {
// Resource baseResource = getResourceResolver().getResource(basePath);
// if (baseResource != null) {
// Iterator<Resource> children = baseResource.listChildren();
// while (children.hasNext()) {
// Resource child = children.next();
// String path = child.getPath();
// String title = child.getName(); // Adjust this according to your needs
// autoNavItems.add(new CustomNavigationItem(path, title));
// }
// }
// }
// return autoNavItems;
// }

// // Add a method to get the ResourceResolver if needed
// private ResourceResolver getResourceResolver() {
// return resourceResolver;
// }
// }
