package com.strangemoments.booklist.database;

import android.database.Cursor;
import android.database.CursorWrapper;

import com.strangemoments.booklist.Book;
import com.strangemoments.booklist.database.BookDbSchema.BookTable;

import java.util.Date;
import java.util.UUID;

/**
 * Created by Raphael on 5/17/2016.
 */
public class BookCursorWrapper extends CursorWrapper {
    public BookCursorWrapper(Cursor cursor) {
        super(cursor);
    }

    public Book getBook() {
        String uuidString = getString(getColumnIndex(BookTable.Cols.UUID));
        String title = getString(getColumnIndex(BookTable.Cols.TITLE));
        String author = getString(getColumnIndex(BookTable.Cols.AUTHOR));
        int isRead = getInt(getColumnIndex(BookTable.Cols.READ));
        String note = getString(getColumnIndex(BookTable.Cols.NOTE));
        long date = getLong(getColumnIndex(BookTable.Cols.DATE));

        Book book = new Book(UUID.fromString(uuidString));
        book.setTitle(title);
        book.setAuthor(author);
        book.setRead(isRead != 0);
        book.setNote(note);
        book.setDate(new Date(date));

        return book;
    }
}
