package ru.testsimpleapps.coloraudioplayer.managers.explorer.Data;

import java.util.ArrayList;
import java.util.List;

public class ContainerData<Item> {

    private final List<Item> mContainer;

    public ContainerData() {
        mContainer = new ArrayList<>();
    }

    public Item get(final int index) {
        if (index < 0 || index >= mContainer.size()) {
            return null;
        }
        return mContainer.get(index);
    }

    public boolean add(final Item item) {
        return mContainer.add(item);
    }

    public void clear() {
        mContainer.clear();
    }

    public int size() {
        return mContainer.size();
    }

}
