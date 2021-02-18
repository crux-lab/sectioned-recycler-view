/*
 * MIT License
 *
 * Copyright (c) 2017 Cruxlab, Inc.
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */

package com.cruxlab.sectionedrecyclerview.lib;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

/**
 * Interface for interaction with RecyclerView and managing its sections.
 * <p>
 * Each section is represented by a {@link SimpleSectionAdapter} or {@link SectionAdapter},
 * which provides a binding from an app-specific data set to views, and an optional
 * {@link SectionItemSwipeCallback}, which lets you control swipe behavior of each item view within
 * the section.
 * <p>
 * Most of the methods require an index of the section to interact with. It can be received from
 * adapter by calling {@link BaseSectionAdapter#getSection()}.
 */
public interface SectionManager {

    /**
     * Returns the total number of sections in the RecyclerView.
     *
     * @return The total number of sections.
     */
    int getSectionCount();

    /**
     * Appends the section represented by the specified SimpleSectionAdapter to the end of the
     * RecyclerView.
     *
     * @param simpleSectionAdapter SimpleSectionAdapter to represent the appended section.
     */
    void addSection(@NonNull SimpleSectionAdapter simpleSectionAdapter);

    /**
     * Appends the section represented by the specified SectionAdapter to the end of the RecyclerView.
     * <p>
     * Header type is used to cache {@link BaseSectionAdapter.HeaderViewHolder}s displayed at the top
     * of the {@link SectionHeaderLayout}, so different SectionAdapters with the same header view
     * should return the same value. It can be any integer except {@link SectionAdapter#NO_HEADER_TYPE}.
     *
     * @param sectionAdapter SectionAdapter to represent the appended section.
     * @param headerType     Type to represent its header view.
     */
    void addSection(@NonNull SectionAdapter sectionAdapter, short headerType);

    /**
     * Appends the section represented by the specified SimpleSectionAdapter and SectionItemSwipeCallback
     * to the end of the RecyclerView.
     *
     * @param simpleSectionAdapter SimpleSectionAdapter to represent the appended section.
     * @param swipeCallback        SectionItemSwipeCallback to represent the appended section.
     */
    void addSection(@NonNull SimpleSectionAdapter simpleSectionAdapter, SectionItemSwipeCallback swipeCallback);

    /**
     * Appends the section represented by the specified SectionAdapter and SectionItemSwipeCallback
     * to the end of the RecyclerView.
     * <p>
     * Header type is used to cache {@link BaseSectionAdapter.HeaderViewHolder}s displayed at the top
     * of the {@link SectionHeaderLayout}, so different SectionAdapters with the same header view
     * should return the same value. It can be any integer except {@link SectionAdapter#NO_HEADER_TYPE}.
     *
     * @param sectionAdapter SectionAdapter to represent the appended section.
     * @param swipeCallback  SectionItemSwipeCallback to represent the appended section.
     * @param headerType     Type to represent its header view.
     */
    void addSection(@NonNull SectionAdapter sectionAdapter, SectionItemSwipeCallback swipeCallback, short headerType);

    /**
     * Inserts the section represented by the specified SimpleSectionAdapter to the specified position
     * in the RecyclerView.
     *
     * @param section              Index at which the section is to be inserted.
     * @param simpleSectionAdapter SimpleSectionAdapter to represent the inserted section.
     */
    void insertSection(int section, @NonNull SimpleSectionAdapter simpleSectionAdapter);

    /**
     * Inserts the section represented by the specified SectionAdapter to the specified position
     * in the RecyclerView.
     * <p>
     * Header type is used to cache {@link BaseSectionAdapter.HeaderViewHolder}s displayed at the top
     * of the {@link SectionHeaderLayout}, so different SectionAdapters with the same header view
     * should return the same value. It can be any integer except {@link SectionAdapter#NO_HEADER_TYPE}.
     *
     * @param section        Index at which the section is to be inserted.
     * @param sectionAdapter SectionAdapter to represent the inserted section.
     * @param headerType     Type to represent its header view.
     */
    void insertSection(int section, @NonNull SectionAdapter sectionAdapter, short headerType);

    /**
     * Inserts the section represented by the specified SimpleSectionAdapter and SectionItemSwipeCallback
     * to the specified position in the RecyclerView.
     *
     * @param section              Index at which the section is to be inserted.
     * @param simpleSectionAdapter SimpleSectionAdapter to represent the inserted section.
     * @param swipeCallback        SectionItemSwipeCallback to represent the inserted section.
     */
    void insertSection(int section, @NonNull SimpleSectionAdapter simpleSectionAdapter,
                       SectionItemSwipeCallback swipeCallback);


    /**
     * Inserts the section represented by the specified SectionAdapter and SectionItemSwipeCallback
     * to the specified position in the RecyclerView.
     * <p>
     * Header type is used to cache {@link BaseSectionAdapter.HeaderViewHolder}s displayed at the top
     * of the {@link SectionHeaderLayout}, so different SectionAdapters with the same header view
     * should return the same value. It can be any integer except {@link SectionAdapter#NO_HEADER_TYPE}.
     *
     * @param section        Index at which the section is to be inserted.
     * @param sectionAdapter SectionAdapter to represent the inserted section.
     * @param swipeCallback  SectionItemSwipeCallback to represent the inserted section.
     * @param headerType     Type to represent its header view.
     */
    void insertSection(int section, @NonNull SectionAdapter sectionAdapter,
                       SectionItemSwipeCallback swipeCallback, short headerType);

    /**
     * Replaces the section at the specified position in the RecyclerView with the section
     * represented by the specified SimpleSectionAdapter.
     *
     * @param section              Index of the section to replace.
     * @param simpleSectionAdapter SimpleSectionAdapter to represent the section to replace.
     */
    void replaceSection(int section, @NonNull SimpleSectionAdapter simpleSectionAdapter);

    /**
     * Replaces the section at the specified position in the RecyclerView with the section
     * represented by the specified SectionAdapter.
     * <p>
     * Header type is used to cache {@link BaseSectionAdapter.HeaderViewHolder}s displayed at the top
     * of the {@link SectionHeaderLayout}, so different SectionAdapters with the same header view
     * should return the same value. It can be any integer except {@link SectionAdapter#NO_HEADER_TYPE}.
     *
     * @param section        Index of the section to replace.
     * @param sectionAdapter SectionAdapter to represent the section to replace.
     * @param headerType     Type to represent its header view.
     */
    void replaceSection(int section, @NonNull SectionAdapter sectionAdapter, short headerType);

    /**
     * Replaces the section at the specified position in the RecyclerView with the section
     * represented by the specified SimpleSectionAdapter and SectionItemSwipeCallback.
     *
     * @param section              Index of the section to replace.
     * @param simpleSectionAdapter SimpleSectionAdapter to represent the section to replace.
     * @param swipeCallback        SectionItemSwipeCallback to represent the section to replace.
     */
    void replaceSection(int section, @NonNull SimpleSectionAdapter simpleSectionAdapter,
                        SectionItemSwipeCallback swipeCallback);

    /**
     * Replaces the section at the specified position in the RecyclerView with the section
     * represented by the specified SectionAdapter and SectionItemSwipeCallback.
     * <p>
     * Header type is used to cache {@link BaseSectionAdapter.HeaderViewHolder}s displayed at the top
     * of the {@link SectionHeaderLayout}, so different SectionAdapters with the same header view
     * should return the same value. It can be any integer except {@link SectionAdapter#NO_HEADER_TYPE}.
     *
     * @param section        Index of the section to replace.
     * @param sectionAdapter SectionAdapter to represent the section to replace.
     * @param swipeCallback  SectionItemSwipeCallback to represent the section to replace.
     * @param headerType     Type to represent its header view.
     */
    void replaceSection(int section, @NonNull SectionAdapter sectionAdapter,
                        SectionItemSwipeCallback swipeCallback, short headerType);

    /**
     * Removes the section at the specified position in the RecyclerView.
     *
     * @param section Index of the section to remove.
     */
    void removeSection(int section);

    /**
     * Updates the section at the specified position in the RecyclerView.
     * <p>
     * In case section items count is changed, it will handle it as insert/remove of difference
     * and update of intersection. For example, changing from 5 items to 3 triggers remove
     * event for 4,5 and update event for 1, 2, 3.
     * <p>
     * Will also trigger header update, if section has one.
     *
     * @param section Index of the section to update.
     */
    void updateSection(int section);

    /**
     * Sets the specified SectionItemSwipeCallback to the section at the specified position in the
     * RecyclerView.
     *
     * @param section       Index of the section to be represented by the callback.
     * @param swipeCallback SectionItemSwipeCallback to represent the specified section.
     */
    void setSwipeCallback(int section, @NonNull SectionItemSwipeCallback swipeCallback);

    /**
     * Removes SectionItemSwipeCallback from the section at the specified position in the
     * RecyclerView.
     *
     * @param section Index of the section to remove the callback from.
     */
    void removeSwipeCallback(int section);

    /**
     * Returns the successor of the BaseSectionAdapter which represents the specifies section in the
     * RecyclerView.
     *
     * @param section Index of the section to be represented.
     * @return Successor of the BaseSectionAdapter which represents the specifies section.
     */

    <T extends BaseSectionAdapter> T getSectionAdapter(int section);

    /**
     * Returns the SectionItemSwipeCallback which represents the specifies section in the
     * RecyclerView or null.
     *
     * @param section Index of the section to be represented.
     * @return SectionItemSwipeCallback which represents the specifies section or null.
     */
    @Nullable
    SectionItemSwipeCallback getSwipeCallback(int section);

}
