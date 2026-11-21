/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 */
package ru.decide.manager.events.orbit.listeners;

import java.util.Arrays;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;
import java.util.NoSuchElementException;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

@Environment(value=EnvType.CLIENT)
public class ArrayBackedImmutableList<T>
implements List<T> {
    private final T[] array;

    public ArrayBackedImmutableList(T[] array) {
        this.array = array;
    }

    public ArrayBackedImmutableList(Collection<T> collection) {
        this(collection.toArray());
    }

    public static <T> ArrayBackedImmutableList<T> copy(T[] array) {
        return new ArrayBackedImmutableList<T>(Arrays.copyOf(array, array.length));
    }

    @Override
    public int size() {
        return this.array.length;
    }

    @Override
    public boolean isEmpty() {
        return this.array.length == 0;
    }

    @Override
    public boolean contains(Object o) {
        for (T obj : this.array) {
            if (o != obj) continue;
            return true;
        }
        return false;
    }

    @Override
    public Iterator<T> iterator() {
        return new ArrayBackedIterator<T>(this.array, 0);
    }

    @Override
    public Object[] toArray() {
        return Arrays.copyOf(this.array, this.array.length);
    }

    @Override
    public <T1> T1[] toArray(T1[] a) {
        return this.toArray();
    }

    @Override
    public boolean add(T t) {
        throw new UnsupportedOperationException();
    }

    @Override
    public boolean remove(Object o) {
        throw new UnsupportedOperationException();
    }

    @Override
    public boolean containsAll(Collection<?> c) {
        for (Object o : c) {
            if (this.contains(o)) continue;
            return false;
        }
        return true;
    }

    @Override
    public boolean addAll(Collection<? extends T> c) {
        throw new UnsupportedOperationException();
    }

    @Override
    public boolean addAll(int index, Collection<? extends T> c) {
        throw new UnsupportedOperationException();
    }

    @Override
    public boolean removeAll(Collection<?> c) {
        throw new UnsupportedOperationException();
    }

    @Override
    public boolean retainAll(Collection<?> c) {
        throw new UnsupportedOperationException();
    }

    @Override
    public void clear() {
        throw new UnsupportedOperationException();
    }

    @Override
    public T get(int index) {
        return this.array[index];
    }

    @Override
    public T set(int index, T element2) {
        throw new UnsupportedOperationException();
    }

    @Override
    public void add(int index, T element2) {
        throw new UnsupportedOperationException();
    }

    @Override
    public T remove(int index) {
        throw new UnsupportedOperationException();
    }

    @Override
    public int indexOf(Object o) {
        for (int i = 0; i < this.array.length; ++i) {
            if (o != this.array[i]) continue;
            return i;
        }
        return -1;
    }

    @Override
    public int lastIndexOf(Object o) {
        for (int i = this.array.length - 1; i >= 0; --i) {
            if (o != this.array[i]) continue;
            return i;
        }
        return -1;
    }

    @Override
    public ListIterator<T> listIterator() {
        return new ArrayBackedIterator<T>(this.array, 0);
    }

    @Override
    public ListIterator<T> listIterator(int index) {
        return new ArrayBackedIterator<T>(this.array, index);
    }

    @Override
    public List<T> subList(int fromIndex, int toIndex) {
        throw new UnsupportedOperationException();
    }

    @Environment(value=EnvType.CLIENT)
    private static final class ArrayBackedIterator<T>
    implements ListIterator<T> {
        private final T[] array;
        private int cursor;

        private ArrayBackedIterator(T[] array, int initialCursor) {
            this.array = array;
            this.cursor = initialCursor;
        }

        @Override
        public boolean hasNext() {
            return this.cursor < this.array.length;
        }

        @Override
        public boolean hasPrevious() {
            return this.cursor > 0;
        }

        @Override
        public T next() {
            if (!this.hasNext()) {
                throw new NoSuchElementException();
            }
            return this.array[this.cursor++];
        }

        @Override
        public T previous() {
            if (!this.hasPrevious()) {
                throw new NoSuchElementException();
            }
            return this.array[--this.cursor];
        }

        @Override
        public int nextIndex() {
            return this.cursor;
        }

        @Override
        public int previousIndex() {
            return this.cursor - 1;
        }

        @Override
        public void remove() {
            throw new UnsupportedOperationException();
        }

        @Override
        public void set(T t) {
            throw new UnsupportedOperationException();
        }

        @Override
        public void add(T t) {
            throw new UnsupportedOperationException();
        }
    }
}

