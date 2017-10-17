package com.cruxlab.sectionedrecyclerview.lib;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

/**
 * Interface for interaction with SectionedRecyclerView and managing its sections.
 * <p>
 * Each section is represented by a {@link SectionAdapter} or {@link SectionWithHeaderAdapter},
 * which provides a binding from an app-specific data set to views, and an optional
 * {@link SectionItemSwipeCallback}, which lets you control swipe behavior of each item view within
 * the section.
 * <p>
 * Most methods require an index of the section to interact with. It can be
 * received from adapter by calling {@link SectionAdapter#getSection()}.
 */
public interface SectionManager {

    /**
     * Returns the total number of sections in the SectionedRecyclerView.
     *
     * @return The total number of sections.
     */
    int getSectionCount();

    /**
     * Appends the section represented by the specified SectionAdapter to the end of the
     * SectionedRecyclerView.
     *
     * @param sectionAdapter SectionAdapter to represent the appended section.
     */
    void addSection(@NonNull SectionAdapter sectionAdapter);

    /**
     * Appends the section represented by the specified SectionWithHeaderAdapter to the end of the
     * SectionedRecyclerView.
     *
     * @param sectionWithHeaderAdapter SectionWithHeaderAdapter to represent the appended section.
     */
    void addSection(@NonNull SectionWithHeaderAdapter sectionWithHeaderAdapter);

    /**
     * Appends the section represented by the specified SectionAdapter and SectionItemSwipeCallback
     * to the end of the SectionedRecyclerView.
     *
     * @param sectionAdapter SectionAdapter to represent the appended section.
     * @param swipeCallback  SectionItemSwipeCallback to represent the appended section.
     */
    void addSection(@NonNull SectionAdapter sectionAdapter, SectionItemSwipeCallback swipeCallback);

    /**
     * Appends the section represented by the specified SectionWithHeaderAdapter and SectionItemSwipeCallback
     * to the end of the SectionedRecyclerView.
     *
     * @param sectionWithHeaderAdapter SectionWithHeaderAdapter to represent the appended section.
     * @param swipeCallback            SectionItemSwipeCallback to represent the appended section.
     */
    void addSection(@NonNull SectionWithHeaderAdapter sectionWithHeaderAdapter, SectionItemSwipeCallback swipeCallback);

    /**
     * Inserts the section represented by the specified SectionAdapter to the specified position
     * in the SectionedRecyclerView.
     *
     * @param section        Index at which the section is to be inserted.
     * @param sectionAdapter SectionAdapter to represent the inserted section.
     */
    void insertSection(int section, @NonNull SectionAdapter sectionAdapter);

    /**
     * Inserts the section represented by the specified SectionWithHeaderAdapter to the specified position
     * in the SectionedRecyclerView.
     *
     * @param section                  Index at which the section is to be inserted.
     * @param sectionWithHeaderAdapter SectionWithHeaderAdapter to represent the inserted section.
     */
    void insertSection(int section, @NonNull SectionWithHeaderAdapter sectionWithHeaderAdapter);

    /**
     * Inserts the section represented by the specified SectionAdapter and SectionItemSwipeCallback
     * to the specified position in the SectionedRecyclerView.
     *
     * @param section        Index at which the section is to be inserted.
     * @param sectionAdapter SectionAdapter to represent the inserted section.
     * @param swipeCallback  SectionItemSwipeCallback to represent the inserted section.
     */
    void insertSection(int section, @NonNull SectionAdapter sectionAdapter,
                       SectionItemSwipeCallback swipeCallback);


    /**
     * Inserts the section represented by the specified SectionWithHeaderAdapter and SectionItemSwipeCallback
     * to the specified position in the SectionedRecyclerView.
     *
     * @param section                  Index at which the section is to be inserted.
     * @param sectionWithHeaderAdapter SectionWithHeaderAdapter to represent the inserted section.
     * @param swipeCallback            SectionItemSwipeCallback to represent the inserted section.
     */
    void insertSection(int section, @NonNull SectionWithHeaderAdapter sectionWithHeaderAdapter,
                       SectionItemSwipeCallback swipeCallback);

    /**
     * Replaces the section at the specified position in the SectionedRecyclerView with the section
     * represented by the specified SectionAdapter.
     *
     * @param section        Index of the section to replace.
     * @param sectionAdapter SectionAdapter to represent the section to replace.
     */
    void replaceSection(int section, @NonNull SectionAdapter sectionAdapter);

    /**
     * Replaces the section at the specified position in the SectionedRecyclerView with the section
     * represented by the specified SectionWithHeaderAdapter.
     *
     * @param section                  Index of the section to replace.
     * @param sectionWithHeaderAdapter SectionWithHeaderAdapter to represent the section to replace.
     */
    void replaceSection(int section, @NonNull SectionWithHeaderAdapter sectionWithHeaderAdapter);

    /**
     * Replaces the section at the specified position in the SectionedRecyclerView with the section
     * represented by the specified SectionAdapter and SectionItemSwipeCallback.
     *
     * @param section        Index of the section to replace.
     * @param sectionAdapter SectionAdapter to represent the section to replace.
     * @param swipeCallback  SectionItemSwipeCallback to represent the section to replace.
     */
    void replaceSection(int section, @NonNull SectionAdapter sectionAdapter,
                        SectionItemSwipeCallback swipeCallback);

    /**
     * Replaces the section at the specified position in the SectionedRecyclerView with the section
     * represented by the specified SectionWithHeaderAdapter and SectionItemSwipeCallback.
     *
     * @param section                  Index of the section to replace.
     * @param sectionWithHeaderAdapter SectionWithHeaderAdapter to represent the section to replace.
     * @param swipeCallback            SectionItemSwipeCallback to represent the section to replace.
     */
    void replaceSection(int section, @NonNull SectionWithHeaderAdapter sectionWithHeaderAdapter,
                        SectionItemSwipeCallback swipeCallback);

    /**
     * Removes the section at the specified position in the SectionedRecyclerView.
     *
     * @param section Index of the section to remove.
     */
    void removeSection(int section);

    /**
     * Updates the section at the specified position in the SectionedRecyclerView.
     *
     * @param section Index of the section to update.
     */
    void updateSection(int section);

    /**
     * Sets the specified SectionItemSwipeCallback to the section at the specified position in the
     * SectionedRecyclerView.
     *
     * @param section       Index of the section to be represented by the callback.
     * @param swipeCallback SectionItemSwipeCallback to represent the specified section.
     */
    void setSwipeCallback(int section, @NonNull SectionItemSwipeCallback swipeCallback);

    /**
     * Removes SectionItemSwipeCallback from the section at the specified position in the
     * SectionedRecyclerView.
     *
     * @param section Index of the section to remove the callback from.
     */
    void removeSwipeCallback(int section);

    /**
     * Returns the SectionAdapter which represents the specifies section in the
     * SectionedRecyclerView.
     *
     * @param section Index of the section to be represented.
     * @return SectionAdapter which represents the specifies section.
     */
    SectionAdapter getSectionAdapter(int section);

    /**
     * Returns the SectionItemSwipeCallback which represents the specifies section in the
     * SectionedRecyclerView or null.
     *
     * @param section Index of the section to be represented.
     * @return SectionItemSwipeCallback which represents the specifies section or null.
     */
    @Nullable
    SectionItemSwipeCallback getSwipeCallback(int section);

}
