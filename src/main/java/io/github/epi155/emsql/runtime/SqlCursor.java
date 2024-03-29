package io.github.epi155.emsql.runtime;

import java.sql.SQLException;
import java.util.Iterator;
import java.util.NoSuchElementException;

public abstract class SqlCursor<T> implements AutoCloseable, Iterable<T> {
    public abstract boolean hasNext() throws SQLException;
    public abstract T fetchNext() throws SQLException;
    @Override
    public abstract void close() throws SQLException;

    public Iterator<T> iterator(final java.util.function.Function<SQLException, RuntimeException> ex) {
        return new Iterator<T>() {
            private T readyItem;

            @Override
            public boolean hasNext() {
                try {
                    if (SqlCursor.this.hasNext()) {
                        readyItem = SqlCursor.this.fetchNext();
                        return true;
                    } else {
                        readyItem = null;
                        return false;
                    }
                } catch (SQLException e) {
                    throw ex.apply(e);
                }
            }

            @Override
            public T next() {
                if (readyItem != null)
                    return readyItem;
                else
                    throw new NoSuchElementException();
            }
        };
    }
    public Iterator<T> iterator() {
        return new Iterator<T>() {
            private T readyItem;

            @Override
            public boolean hasNext() {
                try {
                    if (SqlCursor.this.hasNext()) {
                        readyItem = SqlCursor.this.fetchNext();
                        return true;
                    } else {
                        readyItem = null;
                        return false;
                    }
                } catch (SQLException e) {
                    throw new RuntimeException(e);
                }
            }

            @Override
            public T next() {
                if (readyItem != null)
                    return readyItem;
                else
                    throw new NoSuchElementException();
            }
        };
    }
}
