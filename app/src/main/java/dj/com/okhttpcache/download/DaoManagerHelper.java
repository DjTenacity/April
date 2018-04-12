package dj.com.okhttpcache.download;

import android.content.Context;


import java.util.List;

import dj.com.okhttpcache.download.db.DaoSupportFactory;
import dj.com.okhttpcache.download.db.DownloadEntity;
import dj.com.okhttpcache.download.db.IDaoSupport;


final class DaoManagerHelper {
    private final static DaoManagerHelper sManager = new DaoManagerHelper();
    IDaoSupport<DownloadEntity> mDaoSupport;

    private DaoManagerHelper() {

    }

    public static DaoManagerHelper getManager() {
        return sManager;
    }

    public void init(Context context) {
        DaoSupportFactory.getFactory().init(context);
        mDaoSupport = DaoSupportFactory.getFactory().getDao(DownloadEntity.class);
    }

    public void addEntity(DownloadEntity entity) {
        long delete = mDaoSupport.delete("url = ? and threadId = ?", entity.getUrl(), entity.getThreadId() + "");
        long size = mDaoSupport.insert(entity);
    }

    public List<DownloadEntity> queryAll(String url) {
        return mDaoSupport.querySupport().selection("url = ?").selectionArgs(url).query();
    }

    public void remove(String url) {
        mDaoSupport.delete("url = ?", url);
    }
}
