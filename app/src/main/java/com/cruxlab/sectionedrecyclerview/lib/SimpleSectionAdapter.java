package com.cruxlab.sectionedrecyclerview.lib;

/**
 * Base Adapter class for a section without header.
 * <p>
 * Only provides item views displayed within a RecyclerView in an individual section.
 *
 * @param <IVH> A class that extends ItemViewHolder that will be used by the adapter to manage item views.
 */
public abstract class SimpleSectionAdapter<IVH extends BaseSectionAdapter.ItemViewHolder> extends BaseSectionAdapter<IVH> {

}
