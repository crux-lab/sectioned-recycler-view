package com.cruxlab.sectionedrecyclerview.lib;

import android.support.annotation.NonNull;

public interface SectionManager {

    int getSectionCount();
    void addSection(@NonNull SectionAdapter sectionAdapter);
    void addSection(@NonNull SectionAdapter sectionAdapter, SectionItemSwipeCallback swipeCallback);
    void insertSection(int section, @NonNull SectionAdapter sectionAdapter);
    void insertSection(int section, @NonNull SectionAdapter sectionAdapter, SectionItemSwipeCallback swipeCallback);
    void replaceSection(int section, @NonNull SectionAdapter sectionAdapter);
    void replaceSection(int section, @NonNull SectionAdapter sectionAdapter, SectionItemSwipeCallback swipeCallback);
    void removeSection(int section);
    void updateSection(int section);

}
