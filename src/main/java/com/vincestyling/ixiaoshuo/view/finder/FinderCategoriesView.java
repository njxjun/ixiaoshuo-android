package com.vincestyling.ixiaoshuo.view.finder;

import android.util.Log;
import android.view.KeyEvent;
import com.vincestyling.ixiaoshuo.R;
import com.vincestyling.ixiaoshuo.reader.MainActivity;
import com.vincestyling.ixiaoshuo.ui.ScrollLayout;
import com.vincestyling.ixiaoshuo.view.ViewBuilder;

public class FinderCategoriesView extends ViewBuilder {
	private int mBookType;

	public FinderCategoriesView(int bookType, MainActivity activity, OnShowListener onShowListener) {
		mViewId = R.id.lotFinderCategoriesContent;
		mShowListener = onShowListener;
		setActivity(activity);
		mBookType = bookType;
	}

	@Override
	protected void build() {
		mView = getActivity().getLayoutInflater().inflate(R.layout.finder_book_categories, null);
	}

	@Override
	public void resume() {
		showView(new FinderCategoryListView(getActivity(), this));
		super.resume();
	}

	public void showView(ViewBuilder builder) {
		getScrollLayout().showView(builder);
	}

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		Log.i("YYReader_FinderBaseVeiw", "onKeyDown");
		return mView.onKeyDown(keyCode, event);
	}

	public int getBookType() {
		return mBookType;
	}

	private ScrollLayout getScrollLayout() {
		return (ScrollLayout) mView;
	}

	@Override
	public MainActivity getActivity() {
		return (MainActivity) super.getActivity();
	}

}