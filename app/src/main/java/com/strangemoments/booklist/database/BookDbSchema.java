package com.strangemoments.booklist.database;

/**
 * Created by Raphael on 5/17/2016.
 */
public class BookDbSchema {
    public static final class BookTable {
        public static final String NAME = "books";

        public static final class Cols {
            public static final String UUID = "uuid";
            public static final String TITLE = "title";
            public static final String AUTHOR = "author";
            public static final String READ = "read";
            public static final String NOTE = "note";
            public static final String DATE = "date";
        }
    }
}
