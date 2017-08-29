package com.shuyu.apprecycler.special.fragment;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.Toast;

import com.jcodecraeer.xrecyclerview.other.ProgressStyle;
import com.jcodecraeer.xrecyclerview.XRecyclerView;
import com.shuyu.apprecycler.R;
import com.shuyu.apprecycler.special.utils.DataUtils;
import com.shuyu.common.CommonRecyclerAdapter;
import com.shuyu.common.CommonRecyclerManager;
import com.shuyu.common.listener.OnItemClickListener;
import com.shuyu.common.model.RecyclerBaseModel;
import com.shuyu.apprecycler.special.holder.ClickHolder;
import com.shuyu.apprecycler.special.holder.ImageHolder;
import com.shuyu.apprecycler.special.holder.TextHolder;
import com.shuyu.apprecycler.itemDecoration.DividerItemDecoration;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by shuyu on 2016/11/23.
 */

public class List2Fragment extends Fragment {


    @BindView(R.id.xRecycler)
    XRecyclerView xRecycler;

    StaggeredGridLayoutManager staggeredGridLayoutManager;

    private List<RecyclerBaseModel> dataList = new ArrayList<>();

    private CommonRecyclerAdapter commonRecyclerAdapter;

    private final Object lock = new Object();

    public List2Fragment() {

    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        final View view = inflater.inflate(R.layout.fragment_list_2, container, false);

        ButterKnife.bind(this, view);

        initView();

        return view;
    }

    private void initView() {
        //瀑布流管理器
        staggeredGridLayoutManager = new StaggeredGridLayoutManager(2, StaggeredGridLayoutManager.VERTICAL);
        //设置瀑布流管理器
        xRecycler.setLayoutManager(staggeredGridLayoutManager);
        //添加分割线，注意位置是会从下拉那个Item的实际位置开始的，所以如果支持下拉需要屏蔽下拉和HeaderView
        xRecycler.addItemDecoration(new DividerItemDecoration(dip2px(getActivity(), 10), DividerItemDecoration.GRID, 2));

        //是否屏蔽下拉
        //xRecycler.setPullRefreshEnabled(false);
        //上拉加载更多样式，也可以设置下拉
        xRecycler.setLoadingMoreProgressStyle(ProgressStyle.SysProgress);
        //设置管理器，关联布局与holder类名，不同id可以管理一个holder
        CommonRecyclerManager commonRecyclerManager = new CommonRecyclerManager();
        commonRecyclerManager.addType(ImageHolder.ID, ImageHolder.class.getName());
        commonRecyclerManager.addType(TextHolder.ID, TextHolder.class.getName());
        commonRecyclerManager.addType(ClickHolder.ID, ClickHolder.class.getName());
        //初始化通用管理器
        commonRecyclerAdapter = new CommonRecyclerAdapter(getActivity(), commonRecyclerManager, dataList);
        xRecycler.setAdapter(commonRecyclerAdapter);

        ImageView imageView = new ImageView(getActivity());
        imageView.setImageResource(R.drawable.xxx1);
        imageView.setScaleType(ImageView.ScaleType.CENTER_CROP);
        imageView.setMinimumHeight(dip2px(getActivity(), 100));
        //添加头部
        xRecycler.addHeaderView(imageView);
        //本身也支持设置空局部
        //xRecycler.setEmptyView();
        xRecycler.setLoadingListener(new XRecyclerView.LoadingListener() {
            @Override
            public void onRefresh() {
                xRecycler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        refresh();
                    }
                }, 2000);
            }

            @Override
            public void onLoadMore() {
                xRecycler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        loadMore();
                    }
                }, 1000);
            }
        });

        commonRecyclerAdapter.setOnItemClickListener(new OnItemClickListener() {
            @Override
            public void onItemClick(Context context, int position) {
                //需要减去你的header和刷新的view的数量
                Toast.makeText(getActivity(), "点击了！！　" + (position - 2), Toast.LENGTH_SHORT).show();
            }
        });
    }

    /**
     * dip转为PX
     */
    public static int dip2px(Context context, float dipValue) {
        float fontScale = context.getResources().getDisplayMetrics().density;
        return (int) (dipValue * fontScale + 0.5f);
    }

    public void setDatas(List datas) {
        this.dataList = datas;
        if (commonRecyclerAdapter != null) {
            commonRecyclerAdapter.setListData(datas);
        }
    }


    private void refresh() {
        List<RecyclerBaseModel> list = DataUtils.getRefreshData();
        //组装好数据之后，再一次性给list，在加多个锁，这样能够避免和上拉数据更新冲突
        //数据要尽量组装好，避免多个异步操作同个内存，因为多个异步更新一个数据源会有问题。
        synchronized (lock) {
            dataList = list;
            commonRecyclerAdapter.setListData(list);
            xRecycler.refreshComplete();
        }

    }

    private void loadMore() {
        List<RecyclerBaseModel> list = DataUtils.getLoadMoreData(dataList);
        //组装好数据之后，再一次性给list，在加多个锁，这样能够避免和上拉数据更新冲突
        //数据要尽量组装好，避免多个异步操作同个内存，因为多个异步更新一个数据源会有问题。
        synchronized (lock) {
            commonRecyclerAdapter.addListData(list);
            xRecycler.loadMoreComplete();
        }
    }

}
