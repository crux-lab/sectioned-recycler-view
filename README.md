# Sectioned RecyclerView with pinnable (floating/sticky) headers

[![Jitpack](https://jitpack.io/v/crux-lab/sectioned-recycler-view.svg)](https://jitpack.io/#crux-lab/sectioned-recycler-view)
[![Nuget](https://img.shields.io/nuget/v/Karamunting.Android.CruxLab.SectionedRecyclerView?color=bright-green)](https://www.nuget.org/packages/Karamunting.Android.CruxLab.SectionedRecyclerView/1.0.3)
[![API](https://img.shields.io/badge/API-16%2B-brightgreen.svg?style=flat)](https://android-arsenal.com/api?level=16)

This library allows you to divide items in your RecyclerView into groups called sections. Each section is represented by an adapter and can have a header. SectionAdapter is similar to Android’s [RecyclerView.Adapter](https://developer.android.com/reference/androidx/recyclerview/widget/RecyclerView.Adapter), which creates and binds ViewHolders. The header can be pinned, which means, that it will be displayed at the top of the RecyclerView above the corresponding section items. Pinned headers change automatically while scrolling or after dataset changes. You can also customize item swiping behavior for each section individually.

## Demo

![](https://thumbs.gfycat.com/FewDependableBassethound-size_restricted.gif)

You can find demo project code [here](https://github.com/crux-lab/sectioned-recycler-view/tree/master/app/src/main/java/com/cruxlab/sectionedrecyclerview/demo). 

## Advantages

* **Simplicilty.** Classes provided by this library are similar to Android ones.
* **Flexibility.** Your RecyclerView stays compatible with almost any external third-party library or API. 
* **Floating headers feature.** An iOS style floating header behaves the same way as an item in the RecyclerView, so you don't have to handle the interaction with it separately.
* **Swiping feature.** You can customize item swiping behavior for each section individually.
* **ViewHolders reusing.** List item ViewHolders are reused as usual, pinned headers are cached and reused with the help of own implementation.

## Usage

### Setup
1. Add the JitPack repository to your project's `build.gradle` file:

```gradle
allprojects {
    repositories {
        ...
        maven { url 'https://jitpack.io' }
    }
}
```

2. Add the dependency to your module's `build.gradle` file:

```gradle
implementation 'com.github.crux-lab:sectioned-recycler-view:1.1.1'
```
Note: previously the library was available via jCenter as `com.cruxlab:sectionedrecyclerview:x.y.z`.

### Initialization

Initialize your `RecyclerView` with a successor of `LinearLayoutManager`:

```java
RecyclerView recyclerView = findViewById(R.id.recycler_view);
LinearLayoutManager layoutManager = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);
recyclerView.setLayoutManager(layoutManager);
recyclerView.setHasFixedSize(false);
```

Create `SectionDataManager` and set its adapter to the `RecyclerView`:

```java
SectionDataManager sectionDataManager = new SectionDataManager();
RecyclerView.Adapter adapter = sectionDataManager.getAdapter();
recyclerView.setAdapter(adapter);
```  

After that you can use `SectionDataManager` that implements `SectionManager` interface to add/remove/replace sections in your `RecyclerView`.

```java
sectionDataManager.addSection(new MySimpleAdapter());
int cnt = sectionDataManager.getSectionCount();
sectionDataManager.removeSection(cnt - 1);
```  

### Adapters

This is an example of an adapter for a simple section without header:

```java
public class MySimpleAdapter extends SimpleSectionAdapter<MyItemViewHolder> {

    private ArrayList<String> items = new ArrayList<>(Arrays.asList("First", "Second", "Third"));
    
    @Override
    public int getItemCount() {
        return items.size();
    }

    @Override
    public MyItemViewHolder onCreateItemViewHolder(ViewGroup parent, short type) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_vh_layout, parent, false);
        return new MyItemViewHolder(view);
    }

    @Override
    public void onBindItemViewHolder(MyItemViewHolder holder, int position) {
        holder.bindItem(items.get(position));
    }
}
```  

It uses the following `ItemViewHolder`: 

```java
public class MyItemViewHolder extends BaseSectionAdapter.ItemViewHolder {

    public TextView textView;

    public MyItemViewHolder(View itemView) {
        super(itemView);
        this.textView = itemView.findViewById(R.id.text_view);
    }

    public void bindItem(String text) {
        textView.setText(text);
    }

}
```

As you can see, these classes are similar to Android’s [RecyclerView.Adapter](https://developer.android.com/reference/androidx/recyclerview/widget/RecyclerView.Adapter) and [RecyclerView.ViewHolder](https://developer.android.com/reference/androidx/recyclerview/widget/RecyclerView.ViewHolder).

Adapter for a section with header has some additional methods to override:

```java
public class MyAdapter extends SectionAdapter<MyItemViewHolder, MyHeaderViewHolder> {

    /* Similar methods */

    public MyAdapter(boolean isHeaderVisible, boolean isHeaderPinned) {
        super(isHeaderVisible, isHeaderPinned);
    }

    @Override
    public MyHeaderViewHolder onCreateHeaderViewHolder(ViewGroup parent) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.header_vh_layout, parent, false);
        return new MyHeaderViewHolder(view);
    }

    @Override
    public void onBindHeaderViewHolder(MyHeaderViewHolder holder) {
        holder.bindHeader();
    }
}
```

When different sections have the same header (`HeaderViewHolder` and its view), it can be reused by the `RecyclerView`. To indicate it, you should pass equal header types to the `SectionDataManager` when adding your `SectionAdapter` and different ones otherwise:

```java
sectionDataManager.addSection(new MyAdapter(true, true), HEADER_TYPE);
sectionDataManager.addSection(new AdapterWithTheSameHeader(false, false), HEADER_TYPE);
sectionDataManager.insertSection(0, new AdapterWithDifferentHeader(true, false), ANOTHER_HEADER_TYPE);
```

### Floating headers

To use floating headers feature, you have to place your `RecyclerView` into `SectionHeaderLayout` in your xml file:

```xml
<com.cruxlab.sectionedrecyclerview.lib.SectionHeaderLayout
        android:id="@+id/section_header_layout"
        android:layout_width="match_parent"
        android:layout_height="match_parent">

        <androidx.recyclerview.widget.RecyclerView
            android:id="@+id/recycler_view"
            android:layout_width="match_parent"
            android:layout_height="match_parent" />
            
 </com.cruxlab.sectionedrecyclerview.lib.SectionHeaderLayout>
```

To enable displaying pinned headers, attach `SectionHeaderLayout` to your `RecyclerView` and `SectionDataManager`:

```java
SectionHeaderLayout sectionHeaderLayout = findViewById(R.id.section_header_layout);
sectionHeaderLayout.attachTo(recyclerView, sectionDataManager);
```

Now you can manage header pinned state with your adapter:

```java
myAdapter.updateHeaderPinnedState(true);
```

You can disable disaplying pinned headers any time by calling:

```java
sectionHeaderLayout.detach();
```

Note, that you should NOT update header view contents manually (e.g., while handling click event), because when header is pinned to the top, its view is duplicated and these changes won't affect the original item in the `RecyclerView`. You should call `notifyHeaderChanged()` instead to guarantee that your changes will be applied to both views while binding.

### Swiping behavior

You can customize item swiping behavior for each section individually. To enable this feature, create `ItemTouchHelper` initialized with `SectionDataManager`'s callback and attach it to your `RecyclerView`:

```java
ItemTouchHelper.Callback callback = sectionDataManager.getSwipeCallback();
ItemTouchHelper itemTouchHelper = new ItemTouchHelper(callback);
itemTouchHelper.attachToRecyclerView(recyclerView);
```

Implement `SectionItemSwipeCallback`, which is similar to Android's [ItemTouchHelper.Callback](https://developer.android.com/reference/androidx/recyclerview/widget/ItemTouchHelper.Callback):

```java
public class MySwipeCallback extends SectionItemSwipeCallback {

    private ColorDrawable background;

    public DemoSwipeCallback() {
        this.background = new ColorDrawable();
    }

    @Override
    public int getSwipeDirFlags(RecyclerView recyclerView, BaseSectionAdapter.ItemViewHolder viewHolder) {
        return ItemTouchHelper.LEFT;
    }

    @Override
    public void onSwiped(BaseSectionAdapter.ItemViewHolder viewHolder, int direction) {
        // Do something
        MyItemViewHolder itemViewHolder = (MyItemViewHolder) viewHolder;
        int sectionPos = itemViewHolder.getSectionAdapterPosition();
        // This method can return -1 when getAdapterPosition() of the corresponding
        // RecyclerView.ViewHolder returns -1 or when this ViewHolder isn't used
        // in any RecyclerView.
        if (sectionPos == -1) return;
        System.out.println("Swiped item on position " +  sectionPos);
    }

    @Override
    public void onChildDraw(Canvas c, RecyclerView recyclerView, BaseSectionAdapter.ItemViewHolder viewHolder, float dX, float dY, int actionState, boolean isCurrentlyActive) {
        // Draw something on canvas
        View itemView = viewHolder.itemView;
        background.setColor(Color.RED);
        background.setBounds((int) (itemView.getRight() + dX), itemView.getTop(), itemView.getRight(), itemView.getBottom());
        background.draw(c);
        super.onChildDraw(c, recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive);
    }

}
```

You can specify it when adding a section or set/remove it later via `SectionDataManager`:

```java
sectionDataManager.addSection(new MySimpleAdapter(), new MySwipeCallback());
sectionDataManager.removeSwipeCallback(0);
```

Note, that section headers are unswipeable.

### GridLayoutManager

When using `GridLayoutManager` set `SpanSizeLookup` as follows to display full width section headers:
```java
gridLayoutManager.setSpanSizeLookup(new GridLayoutManager.SpanSizeLookup() {
    @Override
    public int getSpanSize(int position) {
        if (posManager.isHeader(position)) {
            return gridLayoutManager.getSpanCount();
        } else {
            return 1;
        }
    }
 });
```
It uses `SectionDataManager`'s implementation of `PositionManager` to determine whether the item at the given position is a header.

### Extra

* The number of sections you can add to the `SectionDataManager` during its lifetime is limited to 32,767.
* Sections added via `SectionDataManager` are indexed beginning with the zero subscript and can be accessed by their index later.
* Any `ViewHolder` can call `getGlobalAdapterPosition()` or `getGlobalLayoutPosition()` to access its positions in the global `RecyclerView` adapter among items of all sections including their headers. It also can get the index of the section it belongs to, which is calculated based on the adapter position, calling `getSection()`.
* `ItemViewHolder` can retrieve its position in the corresponding adapter by calling `getSectionAdapterPosition()`.
The methods above can return -1 when the `ViewHolder` is not used in any `RecyclerView`.
* For compatibility with future Android `RecyclerView` APIs and other libraries you can use `PositionManager` interface to manage positions (e.g., retrieved from real `ViewHolder`) yourself.

## License

This project is licensed under the MIT License - see the LICENSE [file](https://github.com/crux-lab/sectioned-recycler-view/blob/master/LICENSE) for details.

---

Made with :heart: by Elizabeth Popova

Contact me: elizaveta.popova@cruxlab.com
