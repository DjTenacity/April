package dj.com.okhttpcache.download;

import android.content.Context;



public class DownloadFacade {
    private static final DownloadFacade sFacade = new DownloadFacade();

    private DownloadFacade(){}

    public static DownloadFacade getFacade() {
        return sFacade;
    }

    public void init(Context context){
        FileManager.manager().init(context);
        DaoManagerHelper.getManager().init(context);
    }

    public void startDownload(String url,DownloadCallback callback){
        DownloadDispatcher.getDispatcher().startDownload(url,callback);
    }
}
