package com.george.redditreader.Fragments;


import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.PopupMenu;
import android.widget.ProgressBar;

import com.george.redditreader.Activities.MainActivity;
import com.george.redditreader.Activities.SubmitPostActivity;
import com.george.redditreader.Adapters.RedditItemListAdapter;
import com.george.redditreader.LoadTasks.LoadPostsTask;
import com.george.redditreader.Views.DividerItemDecoration;
import com.george.redditreader.enums.LoadType;
import com.george.redditreader.R;
import com.george.redditreader.api.retrieval.params.SubmissionSort;
import com.george.redditreader.api.retrieval.params.TimeSpan;
import com.george.redditreader.enums.SubmitPostType;


/**
 * A simple {@link Fragment} subclass.
 */
public class PostListFragment extends Fragment {

    public RedditItemListAdapter postListAdapter;
    public ProgressBar mainProgressBar;
    public RecyclerView contentView;
    public String subreddit;
    private AppCompatActivity activity;
    public SubmissionSort submissionSort;
    private SubmissionSort tempSort;
    public TimeSpan timeSpan;
    public boolean hasPosts;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRetainInstance(true);
        setHasOptionsMenu(true);

        subreddit = activity.getIntent().getStringExtra("subreddit");
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle bundle) {
        View view = inflater.inflate(R.layout.fragment_post_list, container, false);
        mainProgressBar = (ProgressBar) view.findViewById(R.id.progressBar2);
        contentView = (RecyclerView) view.findViewById(R.id.recyclerView_postList);

        contentView.setLayoutManager(new LinearLayoutManager(getActivity()));
        contentView.setHasFixedSize(true);
        contentView.addItemDecoration(new DividerItemDecoration(getActivity(), DividerItemDecoration.VERTICAL_LIST));

        setSubmissionSort(SubmissionSort.HOT);
        LoadPostsTask task = new LoadPostsTask(activity, this, LoadType.init);
        task.execute();

        //if(postListAdapter == null) {
        //    //Log.d("PostListFragment", "Loading posts...");
        //    setSubmissionSort(SubmissionSort.HOT);
        //    LoadPostsTask task = new LoadPostsTask(activity, this, LoadType.init);
        //    task.execute();
        //}
        //else {
        //    mainProgressBar.setVisibility(View.GONE);
        //    contentView.setAdapter(postListAdapter);
        //}

        //Log.d("geo debug", "oncreateview called");
        return view;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch(item.getItemId()) {
            case R.id.action_refresh:
                refreshList();
                return true;
            case R.id.action_sort:
                showSortPopup(activity.findViewById(R.id.action_sort));
                return true;
            case R.id.action_search:
                SearchRedditDialogFragment searchDialog = new SearchRedditDialogFragment();
                Bundle args = new Bundle();
                args.putString("subreddit", subreddit);
                searchDialog.setArguments(args);
                searchDialog.show(activity.getFragmentManager(), "dialog");
                return true;
            case R.id.action_toggle_hidden:
                MainActivity.showHiddenPosts = !MainActivity.showHiddenPosts;
                if(MainActivity.showHiddenPosts) item.setChecked(true);
                else item.setChecked(false);
                refreshList();
                return true;
            case R.id.action_hide_read:
                postListAdapter.hideReadPosts();
                return true;
            case R.id.action_submit_post:
                showSubmitPopup(activity.findViewById(R.id.action_refresh)); //TODO: put correct anchor
                return true;
        }

        return super.onOptionsItemSelected(item);
    }

    private void showSubmitPopup(View v) {
        PopupMenu popupMenu = new PopupMenu(activity, v);
        popupMenu.inflate(R.menu.menu_post_type);
        popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                Intent intent = new Intent(activity, SubmitPostActivity.class);
                intent.putExtra("subreddit", subreddit);
                switch (item.getItemId()) {
                    case R.id.action_submit_link:
                        intent.putExtra("postType", SubmitPostType.link);
                        startActivity(intent);
                        return true;
                    case R.id.action_submit_text:
                        intent.putExtra("postType", SubmitPostType.self);
                        startActivity(intent);
                        return true;
                    //case R.id.action_submit_image: //TODO: implement direct image posting
                    //    return true;
                    default:
                        return false;
                }
            }
        });
        popupMenu.show();
    }

    private void showSortPopup(View v) {
        PopupMenu popupMenu = new PopupMenu(activity, v);
        popupMenu.inflate(R.menu.menu_posts_sort);
        popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                switch (item.getItemId()) {
                    case R.id.action_sort_hot:
                        setSubmissionSort(SubmissionSort.HOT);
                        refreshList();
                        return true;
                    case R.id.action_sort_new:
                        setSubmissionSort(SubmissionSort.NEW);
                        refreshList();
                        return true;
                    case R.id.action_sort_rising:
                        setSubmissionSort(SubmissionSort.RISING);
                        refreshList();
                        return true;
                    case R.id.action_sort_top:
                        //setSubmissionSort(SubmissionSort.TOP);
                        //refreshList();
                        tempSort = SubmissionSort.TOP;
                        showSortTimePopup(activity.findViewById(R.id.action_sort));
                        return true;
                    case R.id.action_sort_controversial:
                        //setSubmissionSort(SubmissionSort.CONTROVERSIAL);
                        //refreshList();
                        tempSort = SubmissionSort.CONTROVERSIAL;
                        showSortTimePopup(activity.findViewById(R.id.action_sort));
                        return true;
                    default:
                        return false;
                }
            }
        });
        popupMenu.show();
    }

    private void showSortTimePopup(View v) {
        PopupMenu popupMenu = new PopupMenu(activity, v);
        popupMenu.inflate(R.menu.menu_posts_sort_time);
        popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                switch (item.getItemId()) {
                    case R.id.action_sort_hour:
                        setSubmissionSort(TimeSpan.HOUR);
                        refreshList();
                        return true;
                    case R.id.action_sort_day:
                        setSubmissionSort(TimeSpan.DAY);
                        refreshList();
                        return true;
                    case R.id.action_sort_week:
                        setSubmissionSort(TimeSpan.WEEK);
                        refreshList();
                        return true;
                    case R.id.action_sort_month:
                        setSubmissionSort(TimeSpan.MONTH);
                        refreshList();
                        return true;
                    case R.id.action_sort_year:
                        setSubmissionSort(TimeSpan.YEAR);
                        refreshList();
                        return true;
                    case R.id.action_sort_all:
                        setSubmissionSort(TimeSpan.ALL);
                        refreshList();
                        return true;
                    default:
                        return false;
                }
            }
        });
        popupMenu.show();
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        this.activity = (AppCompatActivity) activity;
    }

    @Override
    public void onDetach() {
        super.onDetach();
        this.activity = null;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        activity = null;
    }

    @Override
    public void onActivityCreated(Bundle bundle) {
        super.onActivityCreated(bundle);
        setActionBarTitle();
        setActionBarSubtitle();
        //createFooter();
        //if(!hasPosts)
        //    showMore.setVisibility(View.GONE);
        //else
        //    showMore.setVisibility(View.VISIBLE);
    }

    //private void createFooter() {
    //    LayoutInflater inflater = getActivity().getLayoutInflater();
    //    View view = inflater.inflate(R.layout.footer_layout, null);
    //    contentView.addFooterView(view);
    //    footerProgressBar = (ProgressBar) view.findViewById(R.id.progressBar);
    //    footerProgressBar.setVisibility(View.GONE);
    //    showMore = (Button) view.findViewById(R.id.showMore);
    //    showMore.setOnClickListener(new SubredditFooterListener(activity, this));
    //}

    //Reload Posts List
    public void refreshList() {
        Log.d("PostListFragment", "Refreshing posts...");
        contentView.setVisibility(View.GONE);
        mainProgressBar.setVisibility(View.VISIBLE);
        LoadPostsTask task = new LoadPostsTask(activity, this, LoadType.refresh);
        task.execute();
    }

    public void setSubreddit(String subreddit) {
        this.subreddit = subreddit;
        setActionBarTitle();
    }

    public void setSubmissionSort(SubmissionSort sort) {
        this.timeSpan = null;
        this.submissionSort = sort;
        setActionBarSubtitle();
    }

    public void setSubmissionSort(TimeSpan time) {
        this.submissionSort = tempSort;
        this.timeSpan = time;
        setActionBarSubtitle();
    }

    public void setActionBarTitle() {
        String title = (subreddit == null) ? "frontpage" : subreddit;
        activity.getSupportActionBar().setTitle(title);
    }

    public void setActionBarSubtitle() {
        if(timeSpan == null) {
            activity.getSupportActionBar().setSubtitle(submissionSort.value());
        }
        else {
            activity.getSupportActionBar().setSubtitle(submissionSort.value()+": "+timeSpan.value());
        }
    }

}
