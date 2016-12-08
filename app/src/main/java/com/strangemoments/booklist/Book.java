package com.strangemoments.booklist;

import java.util.Date;
import java.util.UUID;

/**
 * Created by Raphael Gal on 2/17/2016.
 */
public class Book {

    private UUID mId;
    private String mTitle;
    private String mAuthor;
    private boolean mRead;
    private String mNote;
    private Date mDate;


    public Book() {
        this(UUID.randomUUID());
        setTitle("");
        setAuthor("");
        setDate();
    }


    public Book(UUID id) {
        mId = id;
    }


    public boolean isRead() {
        return mRead;
    }

    public void setRead(boolean mRead) {
        this.mRead = mRead;
    }

    public UUID getId() {
        return mId;
    }

    public String getTitle() {
        return mTitle;
    }

    public void setTitle(String title) {
        mTitle = title;
    }

    public String getAuthor() {
        return mAuthor;
    }

    public void setAuthor(String author) {
        mAuthor = author;
    }

    public String getNote() {
        return mNote;
    }

    public void setNote(String note) {
        mNote = note;
    }

    public Date getDate() {
        return mDate;
    }

    public void setDate(Date date) {
        mDate = date;
    }

    private void setDate() {
//        mDate = new Date();
        setDate(new Date());
    }


    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Book book = (Book) o;

        if (mRead != book.mRead) return false;
        if (mId != null ? !mId.equals(book.mId) : book.mId != null) return false;
        if (mTitle != null ? !mTitle.equals(book.mTitle) : book.mTitle != null) return false;
        if (mAuthor != null ? !mAuthor.equals(book.mAuthor) : book.mAuthor != null) return false;
        return mNote != null ? mNote.equals(book.mNote) : book.mNote == null;
//        if (mNote != null ? !mNote.equals(book.mNote) : book.mNote != null) return false;
//        return mDate != null ? mDate.equals(book.mDate) : book.mDate == null;

    }
}
