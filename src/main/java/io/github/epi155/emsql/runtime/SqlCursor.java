package io.github.epi155.emsql.runtime;

import java.sql.SQLException;
import java.util.Iterator;
import java.util.NoSuchElementException;

/**
 * Class to manage an SQL cursor and retrieve rows from a {@link java.sql.ResultSet ResultSet} one at a time
 *
 * @param <T> ResultSet data type
 */
public abstract class SqlCursor<T> implements AutoCloseable, Iterable<T> {
    /**
     * Returns {@code true} if the cursor has more elements.
     * <p>
     * (In other words, returns {@code true} if {@link #fetchNext()} would return an element rather than throwing an exception.)
     *
     * @return {@code true} if the cursor has more elements
     * @throws SQLException on SQL Error
     */
    public abstract boolean hasNext() throws SQLException;

    /**
     * Returns the next element fetched from the cursor.
     *
     * @return next element fetched from the cursor
     * @throws SQLException on SQL Error
     */
    public abstract T fetchNext() throws SQLException;

    /**
     * Close the cursor.
     *
     * @throws SQLException on SQL Error
     */
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
