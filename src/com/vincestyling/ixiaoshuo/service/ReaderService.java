package com.vincestyling.ixiaoshuo.service;

import android.content.Context;
import com.duowan.mobile.netroid.NetroidError;
import com.duowan.mobile.netroid.Response;
import com.vincestyling.ixiaoshuo.db.AppDAO;
import com.vincestyling.ixiaoshuo.event.YYReader;
import com.vincestyling.ixiaoshuo.net.Netroid;
import com.vincestyling.ixiaoshuo.pojo.Book;
import com.vincestyling.ixiaoshuo.pojo.Chapter;
import com.vincestyling.ixiaoshuo.utils.IOUtil;

import java.util.ArrayList;
import java.util.List;

public class ReaderService implements YYReader.OnYYReaderListener {
	private Book mBook;
	private Chapter mReadingChapter;

	public void startReader(Context ctx, int bookId) {
		mBook = AppDAO.get().getBookOnReading(bookId);
		mReadingChapter = AppDAO.get().getReadingChapter(mBook.getBookId());
		mReadingChapter.ready(mBook.getBookId());
		YYReader.startReader(ctx, this);
	}

	@Override
	public String onGetBookName() {
		return mBook.getName();
	}

	@Override
	public int onGetTotalChapterCount() {
		// TODO : onGetTotalChapterCount 应该不用每次都查数据库
		return AppDAO.get().getBookChapterCount(mBook.getBookId());
	}

	@Override
	public int onGetUnReadChapterCount() {
		return onGetTotalChapterCount() - onGetCurrentChapterIndex() - 1;
	}

	@Override
	public int onGetCurrentChapterIndex() {
		// TODO : onGetCurrentChapterIndex 应该不用每次都查数据库
		return AppDAO.get().getBookChapterIndex(mBook.getBookId(), mReadingChapter.getChapterId());
	}

	@Override
	public Chapter onGetCurrentChapter() {
		return mReadingChapter;
	}

	@Override
	public Chapter onGetPrevChapter() {
		Chapter chapter = AppDAO.get().getPreviousChapter(mBook.getBookId(), mReadingChapter.getChapterId());
		if (chapter != null) chapter.ready(mBook.getBookId());
		return chapter;
	}

	@Override
	public Chapter onGetNextChapter() {
		Chapter chapter = AppDAO.get().getNextChapter(mBook.getBookId(), mReadingChapter.getChapterId());
		if (chapter != null) chapter.ready(mBook.getBookId());
		return chapter;
	}

	@Override
	public List<Chapter> onGetChapterList() {
		List<Chapter> list = AppDAO.get().getBookChapters(mBook.getBookId());
		List<Chapter> chapInfoList = new ArrayList<Chapter>(list.size());
		for (Chapter chapter : list) {
			chapInfoList.add(chapter);
		}
		return chapInfoList;
	}

	@Override
	public void onReadingChapter(Chapter chapter) {
		AppDAO.get().makeReadingChapter(mBook.getBookId(), chapter);
		mReadingChapter = chapter;
	}

	@Override
	public boolean isBookOnShelf() {
		return AppDAO.get().isBookOnShelf(mBook.getBookId());
	}

	@Override
	public boolean onAddToBookShelf() {
		return AppDAO.get().addToBookShelf(mBook.getBookId());
	}

	@Override
	public boolean onRemoveInBookShelf() {
		return AppDAO.get().deleteBook(mBook);
	}

	@Override
	public boolean onDownloadOneChapter(final Chapter chapter, final YYReader.OnDownloadChapterListener listener) {
		if (chapter == null || chapter.isNativeChapter() || listener == null) {
			return false;
		}

		listener.onDownloadStart(chapter);
		Netroid.downloadChapterContent(mBook.getBookId(), chapter.getChapterId(), new Response.Listener<String>() {
			@Override
			public void onResponse(String content) {
				boolean result = IOUtil.saveBookChapter(mBook.getBookId(), chapter.getChapterId(), content);
				if (result) chapter.ready(mBook.getBookId());
				listener.onDownloadComplete(chapter);
			}

			@Override
			public void onErrorResponse(NetroidError netroidError) {
				listener.onDownloadComplete(chapter);
			}
		});

		return true;
	}

}