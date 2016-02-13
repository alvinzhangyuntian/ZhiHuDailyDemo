package www.zhihudemo.com.zhihudemo.activity;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.widget.SwipeRefreshLayout;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.webkit.WebView;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.Toast;
import android.widget.ViewFlipper;

import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.assist.ImageScaleType;
import com.quentindommerc.superlistview.SuperListview;

import java.util.List;

import www.zhihudemo.com.zhihudemo.R;
import www.zhihudemo.com.zhihudemo.adapter.NewsAdapter;
import www.zhihudemo.com.zhihudemo.entity.News;
import www.zhihudemo.com.zhihudemo.task.LoadNewsTask;
import www.zhihudemo.com.zhihudemo.utility.NetUtil;
import www.zhihudemo.com.zhihudemo.utility.UIUtils;
import www.zhihudemo.com.zhihudemo.widget.LoadingDialog;

public class MainActivity extends AppCompatActivity
        implements View.OnTouchListener,NavigationView.OnNavigationItemSelectedListener, SwipeRefreshLayout.OnRefreshListener ,Animation.AnimationListener{

    private Context mContext;
    private LoadingDialog pd;
    private LinearLayout bannerView;
    private SuperListview sListView;
    private DisplayImageOptions options;
    // 左右滑动时手指按下的X坐标
    private float touchDownX;

    // 左右滑动时手指松开的X坐标
    // private float touchUpX;

    // 从左向右进入动画
    private Animation enter_lefttoright;

    // 从左向右退出动画
    private Animation exit_lefttoright;

    // 从右向左进入动画
    private Animation enter_righttoleft;

    // 从右向左退出动画
    private Animation exit_righttoleft;

    // 从右向左进入动画自动
    private Animation enter_righttoleft_auto;

    // 从右向左退出动画自动
    private Animation exit_righttoleft_auto;

    // 首页广告是否翻转
    private boolean isAdsFlipping = false;
    private LinearLayout adv_ind_layout;
    private FrameLayout home_adv_view;
    private ViewFlipper mviewFlipper;
    private NewsAdapter adapter;
    private Advadapter advadapter;
    private View homeView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        homeView = getLayoutInflater().inflate(R.layout.activity_main,null);
        setContentView(homeView);
        mContext = this;
        options = new DisplayImageOptions.Builder()
                .showImageOnLoading(R.drawable.loading)
                .showImageForEmptyUri(R.drawable.loading)
                .showImageOnFail(R.drawable.loading)
                .resetViewBeforeLoading(true)
                .imageScaleType(ImageScaleType.EXACTLY)
                .bitmapConfig(Bitmap.Config.RGB_565).cacheOnDisk(true)
                .cacheInMemory(true).build();
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, getResources().getString(R.string.fab_toast_main), Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        initView();
    }
    private void initView() {
        pd = new LoadingDialog(mContext, "请稍等...", true, false);
        // gv_home = (SuperHeaderGridview) homView.findViewById(R.id.gv_home);
        bannerView = (LinearLayout) LayoutInflater.from(mContext).inflate(
                R.layout.banner_view_home, null);
        // 循环广告控件
        home_adv_view = (FrameLayout) bannerView
                .findViewById(R.id.home_adv_view);
        int screenWidth = UIUtils.getScreenWidth((Activity)mContext);
        home_adv_view.setLayoutParams(new LinearLayout.LayoutParams(
                screenWidth, screenWidth * 2 / 3));
        mviewFlipper = (ViewFlipper) bannerView
                .findViewById(R.id.home_advFlipper);
        adv_ind_layout = (LinearLayout) bannerView.findViewById(R.id.adv_ind);

        LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(
                screenWidth, screenWidth * 260 / 640);
        layoutParams.topMargin = getResources().getDimensionPixelSize(
                R.dimen.home_theme_margin_vertical);

        sListView = (SuperListview)findViewById(R.id.superlistview_home);
        sListView.setRefreshListener(this);
        sListView.setupMoreListener(null, 1);
        sListView.getList().addHeaderView(bannerView);
        adapter = new NewsAdapter(this, R.layout.listview_item);
        sListView.setAdapter(adapter);
        sListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> parent, View view,
                                    int position, long id) {
                NewsDetailActivity.startActivity(mContext, adapter.getItem(position));
            }
        });
        if (NetUtil.netWorkConnection(mContext)) {
            pd.show();
            new LoadNewsTask( new LoadNewsTask.onFinishListener() {
                @Override
                public void afterTaskFinish(List<News> newsList) {
                    if(mContext!=null&&pd!=null&&pd.isShowing()){
                        pd.dismiss();
                    }
                    adapter.refreshNewsList(newsList);
                    advadapter = new Advadapter(mContext, newsList);
                    setFlipperViewShow(homeView, newsList);
                }
            }).execute();
        } else {
            Toast.makeText(mContext, "请打开网络连接", Toast.LENGTH_SHORT).show();
        }
    }
    @Override
    public void onRefresh() {
        if (NetUtil.netWorkConnection(mContext)) {
            new LoadNewsTask(new LoadNewsTask.onFinishListener() {
                @Override
                public void afterTaskFinish(List<News> newsList) {
                    if(mContext!=null&&pd!=null&&pd.isShowing()){
                        pd.dismiss();
                    }
                    adapter.refreshNewsList(newsList);
                    advadapter = new Advadapter(mContext, newsList);
                    setFlipperViewShow(homeView,newsList);
                }
            }).execute();
        } else {
            Toast.makeText(mContext, "请打开网络连接", Toast.LENGTH_SHORT).show();
        }
    }
    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_camara) {
            // Handle the camera action
        } else if (id == R.id.nav_gallery) {

        } else if (id == R.id.nav_slideshow) {

        } else if (id == R.id.nav_manage) {

        } else if (id == R.id.nav_share) {

        } else if (id == R.id.nav_send) {

        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    class Advadapter extends BaseAdapter {

        private List<News> advlists;

        int[] drawables = null;

        private Context mcontext;

        private ImageLoader imageLoader = ImageLoader.getInstance();

        public Advadapter(Context context, List<News> list) {
            mcontext = context;
            advlists = list;
        }

        @Override
        public int getCount() {
            return advlists.size();
        }

        @Override
        public Object getItem(int position) {
            return advlists.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            View view = LayoutInflater.from(mcontext).inflate(
                    R.layout.fragment_home_adv_flipper, null);
            ImageView im = (ImageView) view.findViewById(R.id.adv_image);
            final News homeAdvViewBean = advlists.get(position);
            if (homeAdvViewBean.getImage() != null) {
                // imageLoader.DisplayImage(homeAdvViewBean.pic_url, im,
                // "adv_http");
                imageLoader.displayImage(homeAdvViewBean.getImage(), im, options);
            } else {
                // 放默认图片
                im.setImageDrawable(getResources().getDrawable(
                        R.drawable.loading));
            }

            // im.setOnClickListener(new OnClickListener() {
            //
            // @Override
            // public void onClick(View arg0) {
            // // TODO Auto-generated method stub
            // //Log.e("advView", "广告图片被点击了");
            // advItemClick(homeAdvViewBean);
            // }
            // });

            return view;
        }

    }
    @Override
    public boolean onTouch(View v, MotionEvent event) {
        if (event.getAction() == MotionEvent.ACTION_DOWN) {
            // 取得左右滑动时手指按下的X坐标
            touchDownX = event.getX();
            if (isAdsFlipping) {
                mviewFlipper.getParent().requestDisallowInterceptTouchEvent(
                        true);
            }
            return true;
        } else if (event.getAction() == MotionEvent.ACTION_UP) {
            // 取得左右滑动时手指松开的X坐标
            float mvDistX = event.getX() - touchDownX;
            // 从左往右，看前一个View
            if (isAdsFlipping && mvDistX > 100) {
                // 设置View切换的动画
                // mviewFlipper.setInAnimation(AnimationUtils.loadAnimation(mcontext,
                // R.anim.slide_in_from_left));
                // mviewFlipper.setOutAnimation(AnimationUtils.loadAnimation(mcontext,
                // R.anim.slide_out_from_right));
                // mviewFlipper.getInAnimation().setAnimationListener(this);
                // 控件进入动画效果
                mviewFlipper.setInAnimation(enter_lefttoright);
                // 控件退出动画效果
                mviewFlipper.setOutAnimation(exit_lefttoright);
                mviewFlipper.getInAnimation().setAnimationListener(this);

                // 显示下一个View
                mviewFlipper.showPrevious();
                mviewFlipper.stopFlipping();
                mviewFlipper.startFlipping();

                mviewFlipper.setInAnimation(enter_righttoleft_auto);
                mviewFlipper.setOutAnimation(exit_righttoleft_auto);
                // 从右往左，看后一个View
            } else if (isAdsFlipping && mvDistX < -100) {
                // 设置View切换的动画
                // 由于Android没有提供slide_out_left和slide_in_right，
                // 所以仿照slide_in_left和slide_out_right编写了slide_out_left和slide_in_right
                mviewFlipper.setInAnimation(enter_righttoleft);
                mviewFlipper.setOutAnimation(exit_righttoleft);
                mviewFlipper.getInAnimation().setAnimationListener(this);
                // 显示前一个View
                mviewFlipper.showNext();
                mviewFlipper.stopFlipping();
                mviewFlipper.startFlipping();

                mviewFlipper.setInAnimation(enter_righttoleft_auto);
                mviewFlipper.setOutAnimation(exit_righttoleft_auto);
            } else if (Math.abs(mvDistX) < 30) {
                // Log.i("advView", "广告图片被点击了");
                try {
                    News homeAdvViewBean = (News) advadapter
                            .getItem(mviewFlipper.getDisplayedChild());
                    advItemClick(homeAdvViewBean);
                } catch (Exception ex) {
                    ex.printStackTrace();
                }
            }
            // mviewFlipper.setInAnimation(AnimationUtils.loadAnimation(mcontext,
            // R.anim.slide_in_from_right));
            // mviewFlipper.setOutAnimation(AnimationUtils.loadAnimation(mcontext,
            // R.anim.slide_out_from_left));
            // mviewFlipper.getInAnimation().setAnimationListener(this);
            return true;
        }
        return false;
    }

    @Override
    public void onAnimationEnd(Animation arg0) {

    }

    @Override
    public void onAnimationRepeat(Animation arg0) {

    }

    @Override
    public void onAnimationStart(Animation arg0) {
        for (int i = 0; i < mviewFlipper.getChildCount(); i++) {
            // 添加图片源
            // ImageView iv = (ImageView) adv_ind_layout.getChildAt(i);
            // if (i == mviewFlipper.getDisplayedChild()) {
            // iv.setImageResource(R.drawable.home_adv_ind_sel);
            // } else {
            // iv.setImageResource(R.drawable.home_adv_ind_nor);
            // }
            if (adv_ind_layout == null) {
                return;
            }
            RadioButton dot = (RadioButton) adv_ind_layout.getChildAt(i);
            if (dot == null) {
                return;
            }
            if (i == mviewFlipper.getDisplayedChild()) {
                dot.setChecked(true);
            } else {
                dot.setChecked(false);
            }
        }

    }

    private void advItemClick(News advBean) {
        NewsDetailActivity.startActivity(mContext,advBean);
    }

    private void setFlipperViewShow(View view,List<News> adList) {
        mviewFlipper.removeAllViews();
        adv_ind_layout.removeAllViews();
        if (adList != null && adList.size() > 1) {
            isAdsFlipping = true;
        }
        for (int i = 0; i < adList.size(); i++) { // 添加图片源
            mviewFlipper.addView(advadapter.getView(i, null, null));
        }

        adv_ind_layout = (LinearLayout) view.findViewById(R.id.adv_ind);
        // 加载动画效果
        enter_lefttoright = AnimationUtils.loadAnimation(mContext,
                R.anim.enter_lefttoright);
        exit_lefttoright = AnimationUtils.loadAnimation(mContext,
                R.anim.exit_lefttoright);
        enter_righttoleft = AnimationUtils.loadAnimation(mContext,
                R.anim.enter_righttoleft);
        exit_righttoleft = AnimationUtils.loadAnimation(mContext,
                R.anim.exit_righttoleft);
        enter_righttoleft_auto = AnimationUtils.loadAnimation(mContext,
                R.anim.enter_righttoleft_auto);
        exit_righttoleft_auto = AnimationUtils.loadAnimation(mContext,
                R.anim.exit_righttoleft_auto);

        // 单张海报不翻转
        if (isAdsFlipping) {
            mviewFlipper.setAutoStart(true); // 设置自动播放功能（点击事件，前自动播放）
            mviewFlipper.setInAnimation(enter_righttoleft_auto);
            mviewFlipper.setOutAnimation(exit_righttoleft_auto);
            // mviewFlipper.setAnimation(null);
            mviewFlipper.getInAnimation().setAnimationListener(this);
            // 更改为5s
            mviewFlipper.setFlipInterval(5000);

            mviewFlipper.setAnimationCacheEnabled(false);
            if (mviewFlipper.isAutoStart() && !mviewFlipper.isFlipping()) {
                mviewFlipper.startFlipping();
            }

            AbsListView.LayoutParams lpDot = new AbsListView.LayoutParams(25, 25);
            for (int j = 0; j < mviewFlipper.getChildCount(); j++) {
                RadioButton dot = new RadioButton(mContext);
                dot.setButtonDrawable(R.drawable.res_radio_home_page);
                dot.setGravity(Gravity.CENTER);
                dot.setLayoutParams(lpDot);
                dot.setEllipsize(TextUtils.TruncateAt.MARQUEE);
                dot.setPadding(0, 0, 0, 0);
                if (adv_ind_layout != null) {
                    adv_ind_layout.addView(dot);
                }
            }
        }
        mviewFlipper.setOnTouchListener(this);

    }

}
