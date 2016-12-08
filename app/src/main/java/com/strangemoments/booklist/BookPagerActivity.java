package com.strangemoments.booklist;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.widget.Toast;

import java.util.List;
import java.util.UUID;

/**
 * Created by Raphael on 5/13/2016.
 */
public class BookPagerActivity extends AppCompatActivity
        implements BookFragment.Callbacks{
    private static final String EXTRA_BOOK_ID = "com.strangemoments.booklist.book_id";

    private ViewPager mViewPager;
    private List<Book> mBooks;
    private UUID mBookId;

    public static Intent newIntent(Context packageContext, UUID bookId) {
        Intent intent = new Intent(packageContext, BookPagerActivity.class);
        intent.putExtra(EXTRA_BOOK_ID, bookId);
        return intent;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_book_pager);

        mBookId = (UUID) getIntent().getSerializableExtra(EXTRA_BOOK_ID);

        mViewPager = (ViewPager) findViewById(R.id.activity_book_pager_view_pager);

        mBooks = BookShelf.get(this).getBooks();
        FragmentManager fragmentManager = getSupportFragmentManager();
        mViewPager.setAdapter(new FragmentStatePagerAdapter(fragmentManager) {

            @Override
            public Fragment getItem(int position) {
                Book book = mBooks.get(position);
                return BookFragment.newInstance(book.getId());
            }

            @Override
            public int getCount() {
                return mBooks.size();
            }
        });

        for (int i = 0; i < mBooks.size(); i++) {
            if (mBooks.get(i).getId().equals(mBookId)) {
                mViewPager.setCurrentItem(i);
                break;
            }
        }
    }

    @Override
    public void onPause() {
        super.onPause();

        if(isFinishing()) {
            checkIfBookIsEmpty();
        }
    }


    private void checkIfBookIsEmpty() {
        BookFragment bookFragment;
        for (Fragment fragment : getSupportFragmentManager().getFragments()) {
            bookFragment = (BookFragment) fragment;
            if (fragment != null && fragment.isVisible()) {
                BookShelf.get(this).deleteEmptyBooks(bookFragment.mBook);
            }
        }
    }

    @Override
    public void onBackPressed() {
        checkIfBookIsEmpty();
        super.onBackPressed();

    }

    @Override
    public void onBookUpdated(Book book) {

    }

    @Override
    public void onBookDeleted(Book book) {

    }
}
