package com.vincestyling.ixiaoshuo.net.request;

import com.duowan.mobile.netroid.Listener;
import com.duowan.mobile.netroid.NetroidError;
import com.duowan.mobile.netroid.NetworkResponse;
import com.duowan.mobile.netroid.Response;
import com.vincestyling.ixiaoshuo.db.AppDBOverseer;
import com.vincestyling.ixiaoshuo.pojo.Book;
import com.vincestyling.ixiaoshuo.pojo.Chapter;

import java.util.List;

public class AddingBookDBRequest extends BasicRequest<Integer> {
    private boolean result;
    private Book book;
    private int bookId;
    private boolean temporaryFlag;
    private List<Chapter> chapterList;

    public AddingBookDBRequest(Book book, boolean temporaryFlag, List<Chapter> chapterList, Listener<Integer> listener) {
        super(listener);
        this.book = book;
        this.chapterList = chapterList;
        this.temporaryFlag = temporaryFlag;
    }

    @Override
    public NetworkResponse perform() {
        bookId = AppDBOverseer.get().addBook(book, temporaryFlag);
        if (bookId > 0) {
            result = AppDBOverseer.get().saveBookChapters(bookId, chapterList);
        }
        return new NetworkResponse(null, null);
    }

    @Override
    protected Response<Integer> parseNetworkResponse(NetworkResponse response) {
        if (result) return Response.success(bookId, response);
        return Response.error(new NetroidError());
    }
}
