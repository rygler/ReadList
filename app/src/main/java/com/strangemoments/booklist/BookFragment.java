package com.strangemoments.booklist;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.TextInputEditText;
import android.support.design.widget.TextInputLayout;
import android.support.v4.app.Fragment;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.LinearLayout;
import android.widget.Toast;

import java.util.List;
import java.util.UUID;

/**
 * Created by Raphael Gal on 2/17/2016.
 */
public class BookFragment extends Fragment {

    private static final String ARG_BOOK_ID = "book_id";
    private static final String ARG_DIRTY_BIT = "dirty_bit";
    protected Book mBook;
    private TextInputEditText mTitleField;

    private TextInputEditText mAuthorField;
    private CheckBox mSolvedCheckBox;
    private TextInputEditText mNoteField;
    protected boolean mDirtyBit;
    private TextInputLayout mInputLayout;
    private Button googleSearch;
    private Button amazonSearch;
    private Callbacks mCallbacks;

    /**
     * Required interface for hosting activities
     */
    public interface Callbacks {
        void onBookUpdated(Book book);

        void onBookDeleted(Book book);
    }

    public static BookFragment newInstance(UUID bookID) {
        Bundle args = new Bundle();
        args.putSerializable(ARG_BOOK_ID, bookID);

        BookFragment fragment = new BookFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        mCallbacks = (Callbacks) context;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        UUID bookID = (UUID) getArguments().getSerializable(ARG_BOOK_ID);
        mBook = BookShelf.get(getActivity()).getBook(bookID);
        replaceNullsWithEmptyString();
//        setHasOptionsMenu(mBook.getTitle() != null);
        setHasOptionsMenu(true);
        mDirtyBit = true;
    }

    private void replaceNullsWithEmptyString() {
        if (mBook.getTitle() == null)
            mBook.setTitle("");
        if (mBook.getAuthor() == null)
            mBook.setAuthor("");
    }

    @Override
    public void onPause() {
        super.onPause();

        BookShelf.get(getActivity())
                .updateBook(mBook);

//        if (mDirtyBit && !getActivity().isFinishing()) {
        if (mDirtyBit) {
            BookShelf.get(getActivity()).deleteEmptyBooks(mBook);
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mCallbacks = null;
//        BookShelf.get(getActivity()).deleteEmptyBooks(mBook);
//        BookListFragment.updateUI();
    }


    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putBoolean(ARG_DIRTY_BIT, mDirtyBit);
        outState.putSerializable(ARG_BOOK_ID, mBook.getId());
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_book, container, false);

        if (savedInstanceState != null) {
            mDirtyBit = savedInstanceState.getBoolean(ARG_DIRTY_BIT);
        }

        mInputLayout = (TextInputLayout) v.findViewById(R.id.book_title_input_layout);
        if (mBook.getTitle() != null && !mBook.getTitle().equals("")) {
            mInputLayout.setHintEnabled(false);
        }

        mTitleField = (TextInputEditText) v.findViewById(R.id.book_title);
        mTitleField.setText(mBook.getTitle());
        if (mDirtyBit && (mBook.getTitle() != null || mBook.getTitle().equals(""))) {
            mDirtyBit = false;
        }
        mTitleField.addTextChangedListener(new TextListener(mTitleField));

        mInputLayout = (TextInputLayout) v.findViewById(R.id.book_author_input_layout);
        if (mBook.getAuthor() != null && !mBook.getAuthor().equals("")) {
            mInputLayout.setHintEnabled(false);
        }

        mAuthorField = (TextInputEditText) v.findViewById(R.id.book_author);
        mAuthorField.setText(mBook.getAuthor());
        mAuthorField.addTextChangedListener(new TextListener(mAuthorField));

        mSolvedCheckBox = (CheckBox) v.findViewById(R.id.book_read);
        mSolvedCheckBox.setChecked(mBook.isRead());
        mSolvedCheckBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                mBook.setRead(isChecked);
                updateBook();
            }
        });

        mInputLayout = (TextInputLayout) v.findViewById(R.id.book_note_input_layout);
        if (mBook.getNote() != null) {
            mInputLayout.setHintEnabled(false);
        }

        mNoteField = (TextInputEditText) v.findViewById(R.id.book_note);
        mNoteField.setText(mBook.getNote());
        mNoteField.addTextChangedListener(new TextListener(mNoteField));

        googleSearch = (Button) v.findViewById(R.id.search_google);
        googleSearch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(Intent.ACTION_VIEW,
                        Uri.parse(getString(R.string.google_search_query)
                                + mBook.getTitle() + " "
                                + mBook.getAuthor()));
                startActivity(i);
            }
        });

        amazonSearch = (Button) v.findViewById(R.id.search_amazon);
        amazonSearch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String[] title = mBook.getTitle().split(" ");
                StringBuilder titleBuilder = new StringBuilder();
                for (String s : title) {
                    titleBuilder.append(s + "+");
                }

                String[] author = mBook.getAuthor().split(" ");
                StringBuilder authorBuilder = new StringBuilder();
                for (String s : author) {
                    authorBuilder.append(s + "+");
                }
                authorBuilder.append("\b");

                Intent i = new Intent(Intent.ACTION_VIEW,
                        Uri.parse(getString(R.string.amazon_search_query)
                                + titleBuilder.toString()
                                + authorBuilder.toString()));
                startActivity(i);
            }
        });

        return v;
    }


    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        inflater.inflate(R.menu.fragment_book, menu);

        MenuItem deleteItem = menu.findItem(R.id.menu_item_delete_book);
        deleteItem.setTitle(R.string.delete_book);

        MenuItem shareItem = menu.findItem(R.id.menu_item_share_book);
        shareItem.setTitle(R.string.share_button_text);

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.menu_item_delete_book:
                BookShelf.get(getActivity()).deleteBook(mBook);
                Toast.makeText(getActivity(), R.string.book_deleted, Toast.LENGTH_LONG).show();
                if (getActivity().findViewById(R.id.detail_fragment_container) == null) {
                    getActivity().finish();
                } else {
                    updateBook();
                    List<Book> books = BookShelf.get(getActivity()).getBooks();
                    if (!books.isEmpty()) {
                        mCallbacks.onBookDeleted(books.get(0));

                    } else {
                        // If no crimes hide the fragment crime
                        LinearLayout detailLayout = (LinearLayout) getActivity().findViewById(R.id.fragment_book_layout);
                        detailLayout.setVisibility(View.GONE);

                    }
                }
                return true;
            case R.id.menu_item_share_book:
                Intent i = new Intent(Intent.ACTION_SEND);
                i.setType("text/plain");
                i.putExtra(Intent.EXTRA_TEXT, getBookReport());
                i = Intent.createChooser(i, getString(R.string.send_report));
                startActivity(i);
                return true;
            default:
                BookShelf.get(getActivity()).deleteEmptyBooks(mBook);
//                getActivity().onBackPressed();
                return super.onOptionsItemSelected(item);
        }
    }

    private void deleteBook() {
        BookShelf.get(getActivity()).deleteEmptyBooks(mBook);
        mCallbacks.onBookUpdated(mBook);
    }

    private String getBookReport() {
        String title = getString(R.string.book_report_title) + " " +
                (mBook.getTitle() != null && !mBook.getTitle().equals("") ? mBook.getTitle() : "Unknown Title");
        String author = "\n" + getString(R.string.book_report_author) + " " +
                (mBook.getAuthor() != null && !mBook.getAuthor().equals("") ? mBook.getAuthor() : "Unknown Author");
        String note = "\n" + getString(R.string.book_report_note) + " " +
                (mBook.getNote() != null && !mBook.getNote().equals("") ? mBook.getNote() : "No Note");
        return title + "\n" + author + "\n" + note + "\n\n" + getString(R.string.sent_by);
    }

    private void updateBook() {
        BookShelf.get(getActivity()).updateBook(mBook);
        mCallbacks.onBookUpdated(mBook);
    }

    private class TextListener implements TextWatcher {

        private View view;

        private TextListener(View view) {
            this.view = view;
        }

        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            // This space intentionally left blank
        }

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {
            switch (view.getId()) {
                case R.id.book_title:
                    mBook.setTitle(s.toString());
                    mDirtyBit = false;
                    break;
                case R.id.book_author:
                    mBook.setAuthor(s.toString());
                    mDirtyBit = false;
                    break;
                case R.id.book_note:
                    mBook.setNote(s.toString());
                    break;
                default:
                    break;

            }
            updateBook();
        }

        @Override
        public void afterTextChanged(Editable s) {
            // This one too
        }
    }
}
