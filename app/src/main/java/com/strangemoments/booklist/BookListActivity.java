package com.strangemoments.booklist;

import android.content.Intent;
import android.support.v4.app.Fragment;

/**
 * Created by Raphael on 4/6/2016.
 */
public class BookListActivity extends SingleFragmentActivity
    implements BookListFragment.Callbacks, BookFragment.Callbacks {

    @Override
    protected Fragment createFragment() {
        return new BookListFragment();
    }

    @Override
    public int getLayoutResId() {
        return R.layout.activity_masterdetail;
    }

    @Override
    public void onBookSelected(Book book) {
        if (findViewById(R.id.detail_fragment_container) == null) {
            Intent intent = BookPagerActivity.newIntent(this, book.getId());
            startActivity(intent);
        } else {
            Fragment newDetail = BookFragment.newInstance(book.getId());

            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.detail_fragment_container, newDetail)
                    .commit();
        }
    }

    @Override
    public void onBookUpdated(Book book) {
        BookListFragment listFragment = (BookListFragment)
                getSupportFragmentManager()
                .findFragmentById(R.id.fragment_container);
        listFragment.updateUI(true);
    }

    @Override
    public void onBookDeleted(Book book) {
        Fragment newDetail = BookFragment.newInstance(book.getId());
        getSupportFragmentManager().beginTransaction()
                .replace(R.id.detail_fragment_container, newDetail)
                .commit();
    }
}
