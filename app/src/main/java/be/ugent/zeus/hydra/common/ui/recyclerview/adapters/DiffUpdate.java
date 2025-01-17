/*
 * Copyright (c) 2021 The Hydra authors
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

package be.ugent.zeus.hydra.common.ui.recyclerview.adapters;

import androidx.annotation.MainThread;
import androidx.annotation.Nullable;
import androidx.annotation.WorkerThread;
import androidx.recyclerview.widget.DiffUtil;

import java.util.*;

/**
 * Generic update for updated data.
 * <p>
 * This class supports both adding data for the first time, updating data and removing all data.
 *
 * @author Niko Strijbol
 */
class DiffUpdate<D> implements AdapterUpdate<D> {

    private final DiffUtil.ItemCallback<D> callback;

    private final List<D> newData;
    private final Set<Empty> status = EnumSet.noneOf(Empty.class);
    private DiffUtil.DiffResult result;
    private int existingDataSize = -1;

    DiffUpdate(@Nullable List<D> newData) {
        this(new EqualsItemCallback<>(), newData);
    }

    DiffUpdate(DiffUtil.ItemCallback<D> callback, @Nullable List<D> newData) {
        this.newData = newData;
        this.callback = callback;
    }

    @Nullable
    @Override
    @WorkerThread
    public List<D> getNewData(@Nullable List<D> existingData) {

        if (existingData == null || existingData.isEmpty()) {
            status.add(Empty.OLD_DATA);
        } else {
            existingDataSize = existingData.size();
        }

        if (newData == null || newData.isEmpty()) {
            status.add(Empty.NEW_DATA);
        }

        if (status.isEmpty()) {
            // Else we calculate a diff, as both are non-empty.
            result = DiffUtil.calculateDiff(new DiffUtil.Callback() {
                @Override
                public int getOldListSize() {
                    return existingData.size();
                }

                @Override
                public int getNewListSize() {
                    return newData.size();
                }

                @Override
                public boolean areItemsTheSame(int oldItemPosition, int newItemPosition) {
                    return callback.areItemsTheSame(existingData.get(oldItemPosition), newData.get(newItemPosition));
                }

                @Override
                public boolean areContentsTheSame(int oldItemPosition, int newItemPosition) {
                    return callback.areContentsTheSame(existingData.get(oldItemPosition), newData.get(newItemPosition));
                }
            }, true);
        }

        return newData;
    }

    @Override
    @MainThread
    public void applyUpdatesTo(ListUpdateCallback listUpdateCallback) {

        // Both are non-empty.
        if (status.isEmpty()) {
            Objects.requireNonNull(result).dispatchUpdatesTo(listUpdateCallback);
            return;
        }

        // Both are empty.
        if (status.containsAll(EnumSet.of(Empty.NEW_DATA, Empty.OLD_DATA))) {
            return;
        }

        // Only the new data is empty.
        if (status.contains(Empty.NEW_DATA)) {
            // Do remove.
            listUpdateCallback.onRemoved(0, existingDataSize);
            return;
        }

        // Only the old data is empty.
        if (status.contains(Empty.OLD_DATA)) {
            assert newData != null;
            listUpdateCallback.onInserted(0, newData.size());
            return;
        }

        throw new IllegalStateException("Illegal state in DataUpdate, one of the possibilities must happen.");
    }

    private enum Empty {
        NEW_DATA, OLD_DATA
    }
}