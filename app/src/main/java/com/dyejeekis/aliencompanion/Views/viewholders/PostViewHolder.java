package com.dyejeekis.aliencompanion.Views.viewholders;

import android.content.Context;
import android.graphics.Color;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.dyejeekis.aliencompanion.Activities.MainActivity;
import com.dyejeekis.aliencompanion.ClickListeners.PostItemOptionsListener;
import com.dyejeekis.aliencompanion.Models.Thumbnail;
import com.dyejeekis.aliencompanion.R;
import com.dyejeekis.aliencompanion.Utils.ConvertUtils;
import com.dyejeekis.aliencompanion.api.entity.Submission;
import com.dyejeekis.aliencompanion.enums.PostViewType;
import com.squareup.picasso.Picasso;

/**
 * Created by sound on 8/28/2015.
 */
public class PostViewHolder extends RecyclerView.ViewHolder {

    public TextView title;
    public TextView score;
    public TextView age;
    public TextView author;
    public TextView domain;
    public TextView subreddit;
    public TextView commentCount;
    public TextView nsfw;
    public TextView selfText;
    public ImageView postImage;
    public LinearLayout commentsButton;
    public LinearLayout linkButton;
    public LinearLayout fullComments;
    public LinearLayout layoutPostOptions;
    public ImageView upvote;
    public ImageView downvote;
    public ImageView save;
    public ImageView hide;
    public ImageView viewUser;
    public ImageView openBrowser;
    public ImageView moreOptions;
    public ProgressBar commentsProgress;

    private PostViewType viewType;

    private static final int clickedColor = Color.GRAY;
    private static final int notClickedColor = Color.BLACK;

    public PostViewHolder(View itemView, PostViewType type) {
        super(itemView);
        this.viewType = type;

        title = (TextView) itemView.findViewById(R.id.txtView_postTitle);
        score = (TextView) itemView.findViewById(R.id.txtView_postScore);
        age = (TextView) itemView.findViewById(R.id.txtView_postAge);
        author = (TextView) itemView.findViewById(R.id.txtView_postAuthor);
        domain = (TextView) itemView.findViewById(R.id.txtView_postDomain);
        subreddit = (TextView) itemView.findViewById(R.id.txtView_postSubreddit);
        commentCount = (TextView) itemView.findViewById(R.id.txtView_postComments);
        nsfw = (TextView) itemView.findViewById(R.id.txtView_nsfw);
        postImage = (ImageView) itemView.findViewById(R.id.imgView_postImage);
        linkButton = (LinearLayout) itemView.findViewById(R.id.layout_postLinkButton);
        upvote =  (ImageView) itemView.findViewById(R.id.btn_upvote);
        layoutPostOptions = (LinearLayout) itemView.findViewById(R.id.layout_postOptions);
        downvote =  (ImageView) itemView.findViewById(R.id.btn_downvote);
        save =  (ImageView) itemView.findViewById(R.id.btn_save);
        hide =  (ImageView) itemView.findViewById(R.id.btn_hide);
        viewUser = (ImageView) itemView.findViewById(R.id.btn_view_user);
        openBrowser = (ImageView) itemView.findViewById(R.id.btn_open_browser);
        moreOptions =  (ImageView) itemView.findViewById(R.id.btn_more);

        switch (viewType) {
            case listItem:
                commentsButton = (LinearLayout) itemView.findViewById(R.id.layout_postCommentsButton);
                break;
            case details:
                selfText = (TextView) itemView.findViewById(R.id.txtView_selfText);
                fullComments = (LinearLayout) itemView.findViewById(R.id.fullLoad);
                commentsProgress = (ProgressBar) itemView.findViewById(R.id.pBar_comments);
                break;
        }
    }

    public void bindModel(Context context, Submission post) {
        title.setText(post.getTitle());
        score.setText(Long.toString(post.getScore()));
        author.setText(post.getAuthor());
        age.setText(ConvertUtils.getSubmissionAge(post.getCreatedUTC()));
        domain.setText(post.getDomain());
        commentCount.setText(Long.toString(post.getCommentCount()));
        if(post.isNSFW()) nsfw.setVisibility(View.VISIBLE);
        else nsfw.setVisibility(View.GONE);

        layoutPostOptions.setBackgroundColor(MainActivity.colorPrimary);

        Thumbnail postThumbnail = post.getThumbnailObject();
        if(postThumbnail == null) postThumbnail = new Thumbnail(); //TODO: check why thumbnail is null
        if (postThumbnail.hasThumbnail()){
            postImage.setLayoutParams(new LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.MATCH_PARENT, 1f));
            if (postThumbnail.isSelf()) {
                postImage.setImageResource(R.drawable.self_default2);
            } else if (post.isNSFW() && !MainActivity.prefs.getBoolean("showNSFWthumb", false)) {
                //postImage.setImageResource(R.drawable.nsfw2);
                postImage.setLayoutParams(new LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.MATCH_PARENT, 0f));
            } else {
                try {
                    //Get Post Thumbnail
                    Picasso.with(context).load(postThumbnail.getUrl()).placeholder(R.drawable.noimage).into(postImage);
                } catch (IllegalArgumentException e) {}
            }
        }
        else {
            postImage.setLayoutParams(new LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.MATCH_PARENT, 0f));
        }

        //user logged in
        if(MainActivity.currentUser != null) {
            //check user vote
            if (post.getLikes().equals("true")) {
                score.setTextColor(Color.parseColor("#FF6600"));
                upvote.setImageResource(R.mipmap.ic_action_upvote_orange);
                downvote.setImageResource(R.mipmap.ic_action_downvote);
            } else if (post.getLikes().equals("false")) {
                score.setTextColor(Color.BLUE);
                upvote.setImageResource(R.mipmap.ic_action_upvote);
                downvote.setImageResource(R.mipmap.ic_action_downvote_blue);
            } else {
                score.setTextColor(Color.BLACK);
                upvote.setImageResource(R.mipmap.ic_action_upvote);
                downvote.setImageResource(R.mipmap.ic_action_downvote);
            }
            //check saved post
            if(post.isSaved()) save.setImageResource(R.mipmap.ic_action_save_yellow);
            else save.setImageResource(R.mipmap.ic_action_save);
            //check hidden post
            if(post.isHidden()) hide.setImageResource(R.mipmap.ic_action_hide_red);
            else hide.setImageResource(R.mipmap.ic_action_hide);
        }

        switch (viewType) {
            case listItem:
                if(post.isSelf()) subreddit.setVisibility(View.GONE);
                else {
                    subreddit.setVisibility(View.VISIBLE);
                    subreddit.setText(post.getSubreddit() + " - ");
                }

                if(post.isClicked()) {
                    title.setTextColor(clickedColor);
                    commentCount.setTextColor(clickedColor);
                }
                else {
                    title.setTextColor(notClickedColor);
                    commentCount.setTextColor(notClickedColor);
                }
                break;
            case details:
                subreddit.setText(post.getSubreddit());
                break;
        }
    }

    public void showPostOptions(PostItemOptionsListener listener) {
        layoutPostOptions.setVisibility(View.VISIBLE);
        upvote.setOnClickListener(listener);
        downvote.setOnClickListener(listener);
        save.setOnClickListener(listener);
        hide.setOnClickListener(listener);
        viewUser.setOnClickListener(listener);
        openBrowser.setOnClickListener(listener);
        moreOptions.setOnClickListener(listener);
    }

    public void hidePostOptions() {
        layoutPostOptions.setVisibility(View.GONE);
    }
}