package com.strangemoments.booklist;

import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.TextView;

import java.util.List;

/**
 * Created by Raphael on 4/6/2016.
 */

enum SortType {
    TITLE, AUTHOR, DATE;
}
public class BookListFragment extends Fragment {

    private static final String SAVED_SUBTITLE_VISIBLE = "subtitle";
    private static final String SELECTED_POSITION = "position";
    private static final String COUNTER = "counter";
    private static final String DELETE_ALL_DIALOG_VISIBLE = "about dialog";

    private final String mPREFS = "PREFS";
    private final String mKEY_SORT_BY_TITLE = "sort by title";
    private final String mKEY_SORT_BY_AUTHOR = "sort by author";
    private final String mKEY_SORT_BY_DATE = "sort by date";
    private RecyclerView mBookRecyclerView;
    private BookAdapter mAdapter;
    private boolean mSubtitleVisible;
    private boolean mDeleteAllDialogVisible;
    private Callbacks mCallbacks;
    private int mSelectedPosition = 0;
    private FloatingActionButton mFloatingAddButton;
    private int mCounter = 0;
    private SortType mSortTypePref = SortType.DATE;

    /**
     * Required interface for hosting activities
     */
    public interface Callbacks {
        void onBookSelected(Book book);
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        mCallbacks = (Callbacks) context;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
        restoreSortPrefs();
    }

    private void restoreSortPrefs() {
        SharedPreferences preferences = getContext().getSharedPreferences(mPREFS, getContext().MODE_PRIVATE);

        if (preferences.getBoolean(mKEY_SORT_BY_TITLE, false)) {
            mSortTypePref = SortType.TITLE;

        } else if (preferences.getBoolean(mKEY_SORT_BY_AUTHOR, false)) {
            mSortTypePref = SortType.AUTHOR;
        } else {
            mSortTypePref = SortType.DATE;
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_book_list, container, false);

        mBookRecyclerView = (RecyclerView) view.findViewById(R.id.book_recycler_view);
        mBookRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));

        mFloatingAddButton = (FloatingActionButton) view.findViewById(R.id.floating_add_button);
        mFloatingAddButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Book book = new Book();
                BookShelf.get(getActivity()).addBook(book);
                replaceNullsWithEmptyString(book);
                updateUI(false);
                mCallbacks.onBookSelected(book);
            }
        });

        if (savedInstanceState != null) {
            mSubtitleVisible = savedInstanceState.getBoolean(SAVED_SUBTITLE_VISIBLE);
            mSelectedPosition = savedInstanceState.getInt(SELECTED_POSITION);
            mCounter = savedInstanceState.getInt(COUNTER);
            mDeleteAllDialogVisible = savedInstanceState.getBoolean(DELETE_ALL_DIALOG_VISIBLE);
        }

        updateUI(false);

        return view;
    }

    private void replaceNullsWithEmptyString(Book book) {
        if (book.getTitle() == null)
            book.setTitle("");
        if (book.getAuthor() == null)
            book.setAuthor("");
    }

    @Override
    public void onResume() {
        super.onResume();
        updateUI(true);
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putBoolean(SAVED_SUBTITLE_VISIBLE, mSubtitleVisible);
        outState.putInt(SELECTED_POSITION, mSelectedPosition);
        outState.putInt(COUNTER, mCounter);
        outState.putBoolean(DELETE_ALL_DIALOG_VISIBLE, mDeleteAllDialogVisible);
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mCallbacks = null;
    }

    private void saveSortPrefs() {
        SharedPreferences preferences = getContext().getSharedPreferences(mPREFS, getContext().MODE_PRIVATE);

        SharedPreferences.Editor editor = preferences.edit();

        editor.clear();

        editor.putBoolean(mKEY_SORT_BY_TITLE, mSortTypePref == SortType.TITLE);
        editor.putBoolean(mKEY_SORT_BY_AUTHOR, mSortTypePref == SortType.AUTHOR);
        editor.putBoolean(mKEY_SORT_BY_DATE, mSortTypePref == SortType.DATE);

        editor.apply();
    }

    @Override
    public void onPause() {
        super.onPause();
        saveSortPrefs();
    }

    @Override
    public void onPrepareOptionsMenu(Menu menu) {
        super.onPrepareOptionsMenu(menu);

        menu.findItem(R.id.menu_item_sort_title).setChecked(mSortTypePref == SortType.TITLE);
        menu.findItem(R.id.menu_item_sort_author).setChecked(mSortTypePref == SortType.AUTHOR);
    }
    private Menu mMenu;

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        inflater.inflate(R.menu.fragment_book_list, menu);
        mMenu = menu;

        MenuItem subtitleItem = menu.findItem(R.id.menu_item_show_subtitle);
        if (mSubtitleVisible) {
            subtitleItem.setTitle(R.string.hide_subtitle);
        } else {
            subtitleItem.setTitle(R.string.show_subtitle);
        }

        switch (mSortTypePref) {
            case TITLE:
                menu.findItem(R.id.menu_item_sort_title).setCheckable(true).setChecked(true);
                break;
            case AUTHOR:
                menu.findItem(R.id.menu_item_sort_author).setCheckable(true).setChecked(true);
                break;
            case DATE:
                menu.findItem(R.id.menu_item_sort_date).setCheckable(true).setChecked(true);
                break;
            default:
                break;

        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {


        switch (item.getItemId()) {
            case R.id.menu_item_show_subtitle:
                mSubtitleVisible = !mSubtitleVisible;
                getActivity().invalidateOptionsMenu();
                updateSubtitle();
                return true;
            case R.id.menu_item_sort_title:
                toggleItemCheck(item, mMenu.getItem(2), mMenu.getItem(3));
                mSortTypePref = SortType.TITLE;
//                BookShelf.get(getActivity()).sortBooks(mSortTypePref);
                updateUI(true);
                return true;
            case R.id.menu_item_sort_author:
                toggleItemCheck(item, mMenu.getItem(1), mMenu.getItem(3));
                mSortTypePref = SortType.AUTHOR;
//                BookShelf.get(getActivity()).sortBooks(mSortTypePref);
                updateUI(true);
                return true;
            case R.id.menu_item_sort_date:
                mSortTypePref = SortType.DATE;
                toggleItemCheck(item, mMenu.getItem(1), mMenu.getItem(2));
                updateUI(true);
                return true;
            case R.id.menu_item_delete_all:
                //TODO: Keep dialog on rotate
                mDeleteAllDialogVisible = !mDeleteAllDialogVisible;
                DialogInterface.OnClickListener deleteAllClickListener = new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        switch (which){
                            case DialogInterface.BUTTON_POSITIVE:
                                //Yes button clicked
                                mDeleteAllDialogVisible = !mDeleteAllDialogVisible;
                                BookShelf.get(getContext()).deleteAllBooks();
                                updateUI(false);
                                break;

                            case DialogInterface.BUTTON_NEGATIVE:
                                //No button clicked
                                mDeleteAllDialogVisible = !mDeleteAllDialogVisible;
                                break;
                        }
                    }
                };

                AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
                builder.setMessage("Are you sure?").setPositiveButton("Yes", deleteAllClickListener)
                        .setNegativeButton("No", deleteAllClickListener).show();
                return true;
            case R.id.menu_item_about:
                showTTTDialog(getString(R.string.aboutDialogTitle),
                        getString(R.string.aboutDialog_banner)
                );

            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private void toggleItemCheck(MenuItem... items) {
        for (int i = 0; i < items.length; i++) {
            if (i == 0) {
                items[i].setCheckable(!items[i].isCheckable());
                items[i].setChecked(!items[i].isChecked());

            } else {
                items[i].setChecked(false);
                items[i].setCheckable(false);
            }
        }
    }

    private void showTTTDialog(String title, String message) {
        DialogInterface.OnClickListener dialogOnClickListener =
                createTTTOnClickListener();

        AlertDialog TTTAlertDialog = initDialog(title, message, dialogOnClickListener);

        TTTAlertDialog.show();
    }

    private DialogInterface.OnClickListener createTTTOnClickListener() {
        return new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                // nothing to do
            }
        };
    }

    private AlertDialog initDialog(String title, String message,
                                   DialogInterface.OnClickListener dialogOnClickListener) {
        AlertDialog TTTAlertDialog;
        TTTAlertDialog = new AlertDialog.Builder(getActivity()).create();
        TTTAlertDialog.setTitle(title);
        TTTAlertDialog.setIcon(R.mipmap.ic_launcher);
        TTTAlertDialog.setMessage(message);
        TTTAlertDialog.setButton(DialogInterface.BUTTON_NEUTRAL,
                getString(R.string.OK), dialogOnClickListener);
        return TTTAlertDialog;
    }

    private void updateSubtitle() {
        BookShelf bookShelf = BookShelf.get(getActivity());
        int bookCount = bookShelf.getBooks().size();
        String subtitle = getString(R.string.subtitle_format, bookCount);

        if (!mSubtitleVisible) {
            subtitle = null;
        }

        AppCompatActivity activity = (AppCompatActivity) getActivity();
        activity.getSupportActionBar().setSubtitle(subtitle);
    }

    public void updateUI(boolean shouldSort) {
        BookShelf bookShelf = BookShelf.get(getActivity());

        if (shouldSort) {
            bookShelf.sortBooks(mSortTypePref);
        }

        List<Book> books = bookShelf.getBooks();

        if (mAdapter == null) {
            mAdapter = new BookAdapter(books);
            mBookRecyclerView.setAdapter(mAdapter);
        } else {
            mAdapter.setBooks(books);
            mAdapter.notifyDataSetChanged();
        }

        updateSubtitle();
    }

    private class BookHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        private Book mBook;
        private TextView mTitleTextView;
        private TextView mAuthorTextView;
        private CheckBox mReadCheckBox;

        public BookHolder(View itemView) {
            super(itemView);
            itemView.setOnClickListener(this);

            mTitleTextView = (TextView) itemView.findViewById(R.id.list_item_book_title_text_view);
            mAuthorTextView = (TextView) itemView.findViewById(R.id.list_item_book_author_text_view);
            mReadCheckBox = (CheckBox) itemView.findViewById(R.id.list_item_book_read_check_box);
            mReadCheckBox.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    mBook.setRead(!mBook.isRead());
                    BookShelf.get(getActivity()).updateBook(mBook);
                }
            });
        }

        private void bindBook(Book book, int position) {
            mBook = book;
            mTitleTextView.setText(mBook.getTitle());
            mAuthorTextView.setText(mBook.getAuthor());
            mReadCheckBox.setChecked(mBook.isRead());
        }

        @Override
        public void onClick(View v) {
            mCallbacks.onBookSelected(mBook);
        }
    }

    private class BookAdapter extends RecyclerView.Adapter<BookHolder> {

        private List<Book> mBooks;

        public BookAdapter(List<Book> books) {
            mBooks = books;
        }

        @Override
        public BookHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            LayoutInflater layoutInflater = LayoutInflater.from(getActivity());
            View view = layoutInflater.inflate(R.layout.list_item_book, parent, false);
            return new BookHolder(view);
        }

        @Override
        public void onBindViewHolder(final BookHolder holder, final int position) {
            final Book book = mBooks.get(position);

//            if (!(mCounter++ == 0)) {
            if (getActivity().findViewById(R.id.detail_fragment_container) != null) {

                if (mSelectedPosition == position) {
                    holder.itemView.setBackgroundColor(ContextCompat.getColor(getContext(), R.color.primary));
                    holder.mTitleTextView.setTextColor(ContextCompat.getColor(getContext(), R.color.primary_light));
                    holder.mTitleTextView.setHintTextColor(ContextCompat.getColor(getContext(), R.color.primary_light));
                    holder.mAuthorTextView.setTextColor(ContextCompat.getColor(getContext(), R.color.primary_light));
                    holder.mAuthorTextView.setHintTextColor(ContextCompat.getColor(getContext(), R.color.primary_light));
                } else {
                    holder.itemView.setBackgroundColor(ContextCompat.getColor(getContext(), R.color.primary_light));
                    holder.mTitleTextView.setTextColor(ContextCompat.getColor(getContext(), R.color.primary_text));
                    holder.mTitleTextView.setHintTextColor(ContextCompat.getColor(getContext(), R.color.primary_text));
                    holder.mAuthorTextView.setTextColor(ContextCompat.getColor(getContext(), R.color.primary_text));
                    holder.mAuthorTextView.setHintTextColor(ContextCompat.getColor(getContext(), R.color.primary_text));
                }

                holder.itemView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        // Updating old as well as new positions
                        notifyItemChanged(mSelectedPosition);
                        mSelectedPosition = position;
                        notifyItemChanged(mSelectedPosition);

                        // Do your another stuff for your onClick
                        holder.bindBook(book, position);
                        holder.onClick(holder.itemView);
                    }
                });
            }

            holder.bindBook(book, position);
        }

        @Override
        public int getItemCount() {
            return mBooks.size();
        }

        public void setBooks(List<Book> books) {
            mBooks = books;
        }
    }
}
