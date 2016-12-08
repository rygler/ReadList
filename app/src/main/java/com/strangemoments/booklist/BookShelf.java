package com.strangemoments.booklist;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.widget.Switch;

import com.strangemoments.booklist.database.BookBaseHelper;
import com.strangemoments.booklist.database.BookCursorWrapper;
import com.strangemoments.booklist.database.BookDbSchema.*;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.UUID;

/**
 * Created by Raphael on 4/12/2016.
 */
public class BookShelf {
    private static BookShelf sBookShelf;

    private String[] mSortedBooks;
    private UUID[] mSortedIds;
    private UUID[] mUnsortedIds;
    private int arrayLength;
    private HashMap<UUID, Book> mBookKeys;
    private Context mContext;
    private SQLiteDatabase mDatabase;

    public static BookShelf get(Context context) {
        if (sBookShelf == null) {
            sBookShelf = new BookShelf(context);
        }
        return sBookShelf;
    }

    private BookShelf(Context context) {
        mContext = context.getApplicationContext();
        mDatabase = new BookBaseHelper(mContext).getWritableDatabase();
    }

    public void addBook(Book c) {
        ContentValues values = getContentValues(c);

        mDatabase.insert(BookTable.NAME, null, values);
    }

    public void deleteEmptyBooks(Book b) {
        if (b != null) {
            updateBook(b);
        }
        for (Book book : getBooks()) {
            if (isNoTitle(book) && isNoAuthor(book)) {
                deleteBook(book);
            }
        }
    }

    public void deleteAllBooks() {
        for (Book book : getBooks()) {
            deleteBook(book);
        }
    }

    private boolean isNoAuthor(Book book) {
        return book.getAuthor() == null || book.getAuthor().equals("");
    }

    private boolean isNoTitle(Book book) {
        return book.getTitle() == null || book.getTitle().equals("");
    }


    public void deleteBook(Book c) {
        deleteBook(c.getId());
    }

    public void deleteBook(UUID id) {
        String uuidString = id.toString();
        mDatabase.delete(BookTable.NAME, BookTable.Cols.UUID + " = ?", new String[]{uuidString});
    }

    public List<Book> getBooks() {
        List<Book> books = new ArrayList<>();

        BookCursorWrapper cursor = queryBooks(null, null);

        try {
            cursor.moveToFirst();
            while (!cursor.isAfterLast()) {
                books.add(cursor.getBook());
                cursor.moveToNext();
            }
        } finally {
            cursor.close();
        }

        return books;
    }

    public Book getBook(UUID id) {
        BookCursorWrapper cursor = queryBooks(
                BookTable.Cols.UUID + " = ?",
                new String[]{id.toString()}
        );

        try {
            if (cursor.getCount() == 0) {
                return null;
            }

            cursor.moveToFirst();
            return cursor.getBook();
        } finally {
            cursor.close();
        }
    }

    public void updateBook(Book book) {
        String uuidString = book.getId().toString();
        ContentValues values = getContentValues(book);

        mDatabase.update(BookTable.NAME, values,
                BookTable.Cols.UUID + " = ?",
                new String[]{uuidString});
    }


    public void sortBooks(SortType sortType) {

        ArrayList<Book> books = (ArrayList<Book>) getBooks();
        mUnsortedIds = new UUID[books.size()];
        mBookKeys = new HashMap<>();
        Book temp;
        for (int i = 0; i < books.size(); i++) {
            temp = books.get(i);
            mUnsortedIds[i] = temp.getId();
            mBookKeys.put(temp.getId(), temp);
        }

        switch (sortType) {
            case TITLE:
                Collections.sort(books, new TitleComparator());
                break;
            case AUTHOR:
                Collections.sort(books, new AuthorComparator());
                break;
            case DATE:
                Collections.sort(books, new DateComparator());
        }
        mSortedIds = new UUID[books.size()];
        for (int i = 0; i < books.size(); i++) {
            mSortedIds[i] = books.get(i).getId();
        }
        updateBookPositions(mSortedIds, mUnsortedIds);

    }


    private void updateBookPositions(UUID [] sortedIds, UUID[] unsortedIds) {
        ArrayList<Book> books = (ArrayList<Book>) getBooks();
        ContentValues values;
//        for (UUID id : mSortedIds) {
        for (int i = 0; i < sortedIds.length; i++) {
            deleteBook(unsortedIds[i]);

//            values = getContentValues(mBookKeys.get(sortedIds[i]));
//
//            mDatabase.update(BookTable.NAME, values,
//                    BookTable.Cols.UUID + " = ?",
//                    new String[]{unsortedIds[i].toString()});
        }
        for (int i = 0; i < sortedIds.length; i++) {
            addBook(mBookKeys.get(sortedIds[i]));
        }
    }

    private static ContentValues getContentValues(Book book) {
        ContentValues values = new ContentValues();
        values.put(BookTable.Cols.UUID, book.getId().toString());
        values.put(BookTable.Cols.TITLE, book.getTitle());
        values.put(BookTable.Cols.AUTHOR, book.getAuthor());
        values.put(BookTable.Cols.READ, book.isRead() ? 1 : 0);
        values.put(BookTable.Cols.NOTE, book.getNote());
        values.put(BookTable.Cols.DATE, book.getDate().getTime());

        return values;
    }

    private BookCursorWrapper queryBooks(String whereClause, String[] whereArgs) {
        Cursor cursor = mDatabase.query(
                BookTable.NAME,
                null, // Columns - null selects all columns
                whereClause,
                whereArgs,
                null, // groupBy
                null, // having
                null  // orderBy
        );

        return new BookCursorWrapper(cursor);
    }

    class DateComparator implements Comparator<Book> {
        @Override
        public int compare(Book book1, Book book2) {
            return book1.getDate().compareTo(book2.getDate());

        }
    }

    class TitleComparator implements Comparator<Book> {
        @Override
        public int compare(Book book1, Book book2) {

            return book1.getTitle().compareTo(book2.getTitle());

        }
    }

    class AuthorComparator implements Comparator<Book> {
        @Override
        public int compare(Book book1, Book book2) {

            return book1.getAuthor().compareTo(book2.getAuthor());

        }
    }
}
