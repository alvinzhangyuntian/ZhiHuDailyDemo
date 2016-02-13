package www.zhihudemo.com.zhihudemo.task;

import android.os.AsyncTask;


import org.json.JSONException;

import java.io.IOException;
import java.util.List;

import www.zhihudemo.com.zhihudemo.adapter.NewsAdapter;
import www.zhihudemo.com.zhihudemo.entity.News;
import www.zhihudemo.com.zhihudemo.http.Http;
import www.zhihudemo.com.zhihudemo.http.JsonHelper;

public class LoadNewsTask extends AsyncTask<Void, Void, List<News>> {
    private onFinishListener listener;

    public LoadNewsTask() {
        super();
    }

    public LoadNewsTask( onFinishListener listener) {
        super();
        this.listener = listener;
    }

    @Override
    protected List<News> doInBackground(Void... params) {
        List<News> newsList = null;
        try {
            newsList = JsonHelper.parseJsonToList(Http.getLastNewsList());
        } catch (IOException | JSONException e) {

        } finally {
            return newsList;
        }
    }

    @Override
    protected void onPostExecute(List<News> newsList) {
        if (listener != null) {
            listener.afterTaskFinish(newsList);
        }

    }

    public interface onFinishListener {
        public void afterTaskFinish(List<News> newsList);
    }
}
