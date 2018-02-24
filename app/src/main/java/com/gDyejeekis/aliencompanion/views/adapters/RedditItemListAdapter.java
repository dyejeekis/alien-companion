package com.gDyejeekis.aliencompanion.views.adapters;

import android.app.Activity;
import android.content.Context;
import android.graphics.PorterDuff;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.RecyclerView;
import android.text.Html;
import android.text.SpannableStringBuilder;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;

import com.gDyejeekis.aliencompanion.AppConstants;
import com.gDyejeekis.aliencompanion.enums.PostViewType;
import com.gDyejeekis.aliencompanion.utils.HtmlTagHandler;
import com.gDyejeekis.aliencompanion.utils.SpanUtils;
import com.gDyejeekis.aliencompanion.views.on_click_listeners.CommentItemOptionsListener;
import com.gDyejeekis.aliencompanion.views.on_click_listeners.CommentLinkListener;
import com.gDyejeekis.aliencompanion.views.on_click_listeners.MessageItemListener;
import com.gDyejeekis.aliencompanion.views.on_click_listeners.PostItemListener;
import com.gDyejeekis.aliencompanion.views.on_click_listeners.PostItemOptionsListener;
import com.gDyejeekis.aliencompanion.views.on_click_listeners.ShowMoreListener;
import com.gDyejeekis.aliencompanion.models.RedditItem;
import com.gDyejeekis.aliencompanion.models.ShowMore;
import com.gDyejeekis.aliencompanion.MyApplication;
import com.gDyejeekis.aliencompanion.utils.MyLinkMovementMethod;
import com.gDyejeekis.aliencompanion.R;
import com.gDyejeekis.aliencompanion.utils.ConvertUtils;
import com.gDyejeekis.aliencompanion.views.viewholders.PostCardViewHolder;
import com.gDyejeekis.aliencompanion.views.viewholders.PostClassicViewHolder;
import com.gDyejeekis.aliencompanion.views.viewholders.PostGalleryViewHolder;
import com.gDyejeekis.aliencompanion.views.viewholders.PostListViewHolder;
import com.gDyejeekis.aliencompanion.views.viewholders.PostSmallCardViewHolder;
import com.gDyejeekis.aliencompanion.views.viewholders.PostViewHolder;
import com.gDyejeekis.aliencompanion.api.entity.Comment;
import com.gDyejeekis.aliencompanion.api.entity.Message;
import com.gDyejeekis.aliencompanion.api.entity.Submission;
import com.gDyejeekis.aliencompanion.api.entity.Trophy;
import com.gDyejeekis.aliencompanion.api.entity.UserInfo;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by sound on 8/28/2015.
 */
public class RedditItemListAdapter extends RecyclerView.Adapter {

    public static final int VIEW_TYPE_POST = 0;

    public static final int VIEW_TYPE_USER_COMMENT = 1;

    public static final int VIEW_TYPE_USER_INFO = 2;

    public static final int VIEW_TYPE_SHOW_MORE = 3;

    public static final int VIEW_TYPE_MESSAGE = 4;

    private final Context context;

    public List<RedditItem> redditItems;

    private ShowMore showMoreButton;

    private int selectedPosition;

    public final int viewTypeValue;

    private boolean loadingMoreItems;

    public RedditItemListAdapter(Context context) {
        this.context = context;
        this.viewTypeValue = MyApplication.currentPostListView;
        selectedPosition = -1;
        loadingMoreItems = false;
        redditItems = new ArrayList<>();
        showMoreButton = new ShowMore();
    }

    public RedditItemListAdapter(Context context, List<RedditItem> items) {
        this.context = context;
        this.viewTypeValue = MyApplication.currentPostListView;
        selectedPosition = -1;
        loadingMoreItems = false;
        redditItems = new ArrayList<>();
        showMoreButton = new ShowMore();
        redditItems.addAll(items);
        //if(items.size() == RedditConstants.DEFAULT_LIMIT)
        if(!MyApplication.offlineModeEnabled) redditItems.add(showMoreButton);
    }

    public RedditItemListAdapter(Context context, int viewTypeValue) {
        this.context = context;
        this.viewTypeValue = viewTypeValue;
        selectedPosition = -1;
        loadingMoreItems = false;
        redditItems = new ArrayList<>();
        showMoreButton = new ShowMore();
    }

    public RedditItemListAdapter(Context context, int viewTypeValue, List<RedditItem> items) {
        this.context = context;
        this.viewTypeValue = viewTypeValue;
        selectedPosition = -1;
        loadingMoreItems = false;
        redditItems = new ArrayList<>();
        showMoreButton = new ShowMore();
        redditItems.addAll(items);
        //if(items.size() == RedditConstants.DEFAULT_LIMIT)
        if(!MyApplication.offlineModeEnabled) redditItems.add(showMoreButton);
    }

    public void updateItem(int position, RedditItem item) {
        selectedPosition = -1;
        redditItems.set(position, item);
        notifyItemChanged(position);
    }

    public void add(RedditItem item) {
        selectedPosition = -1;
        int position;
        if(redditItems.size()==0) {
            redditItems.add(item);
            position = 0;
        }
        else {
            position = redditItems.size()-1;
            redditItems.add(position, item);
        }
        notifyItemInserted(position);
    }

    public void add(RedditItem item, int position) {
        selectedPosition = -1;
        redditItems.add(position, item);
        notifyItemInserted(position);
    }

    public void addAll(List<RedditItem> items) {
        redditItems.remove(showMoreButton);
        redditItems.addAll(items);
            redditItems.add(showMoreButton);
        notifyItemRangeInserted(redditItems.size(), items.size());
    }

    //public void remove(RedditItem item) {
    //    redditItems.remove(item);
    //    selectedPosition = -1;
    //    notifyDataSetChanged();
    //}

    public void remove(RedditItem item) {
        selectedPosition = -1;
        int index = redditItems.indexOf(item);
        redditItems.remove(item);
        notifyItemRemoved(index);
    }

    public RedditItem getLastItem() { //TODO: check out of bounds index exception, probably related to load task
        return redditItems.get(redditItems.size()-2);
    }

    public void setLoadingMoreItems(boolean flag) {
        loadingMoreItems = flag;
        notifyItemChanged(redditItems.size() - 1);
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v;
        RecyclerView.ViewHolder viewHolder = null;
        switch (viewType) {
            case VIEW_TYPE_POST:
                int resource = PostViewType.getLayoutResource(viewTypeValue);
                if(MyApplication.dualPaneActive && resource == R.layout.post_list_item) resource = R.layout.post_list_item_reversed;
                v = LayoutInflater.from(parent.getContext())
                        .inflate(resource, parent, false);
                switch (resource) {
                    case R.layout.post_list_item_classic:
                        viewHolder = new PostClassicViewHolder(v);
                        break;
                    case R.layout.small_card_new:
                        viewHolder = new PostSmallCardViewHolder(v);
                        break;
                    case R.layout.post_list_item_card:
                        viewHolder = new PostCardViewHolder(v, false);
                        break;
                    case R.layout.post_list_item_gallery:
                        viewHolder = new PostGalleryViewHolder(v);
                        break;
                    default:
                        viewHolder = new PostListViewHolder(v);
                        break;
                }
                break;
            case VIEW_TYPE_USER_COMMENT:
                resource = R.layout.user_comment;
                v = LayoutInflater.from(parent.getContext())
                        .inflate(resource, parent, false);
                viewHolder = new UserCommentViewHolder(v);
                break;
            case VIEW_TYPE_USER_INFO:
                resource = R.layout.user_info;
                v = LayoutInflater.from(parent.getContext())
                        .inflate(resource, parent, false);
                viewHolder = new UserInfoViewHolder(v);
                break;
            case VIEW_TYPE_SHOW_MORE:
                resource = R.layout.footer_layout;
                v = LayoutInflater.from(parent.getContext())
                        .inflate(resource, parent, false);
                viewHolder = new FooterViewHolder(v);
                break;
            case VIEW_TYPE_MESSAGE:
                resource = R.layout.user_message;
                v = LayoutInflater.from(parent.getContext()).inflate(resource, parent, false);
                viewHolder = new MessageViewHolder(v);
                break;
        }

        return viewHolder;
    }

    private void toggleMenuBar(int position) {
        int previousSelected = selectedPosition;
        selectedPosition = (selectedPosition == position) ? -1 : position;
        notifyItemChanged(previousSelected);
        notifyItemChanged(selectedPosition);
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder viewHolder, final int position) {
        switch (getItemViewType(position)) {
            case VIEW_TYPE_POST:
                PostViewHolder postViewHolder = (PostViewHolder) viewHolder;
                final Submission post = (Submission) getItemAt(position);

                View.OnLongClickListener longListener;
                if(viewTypeValue == PostViewType.gallery.value()) {
                    longListener = new View.OnLongClickListener() {
                        @Override
                        public boolean onLongClick(View v) {
                            if(!post.isClicked()) {
                                post.setClicked(true);
                                notifyItemChanged(position);
                            }
                            PostItemListener.openComments(context, post);
                            return true;
                        }
                    };
                } else {
                    longListener = new View.OnLongClickListener() {
                        @Override
                        public boolean onLongClick(View view) {
                            toggleMenuBar(position);
                            return true;
                        }
                    };
                }

                postViewHolder.bindModel(context, post);

                PostItemListener listener = new PostItemListener(context, post, this, position);
                PostItemOptionsListener optionsListener = new PostItemOptionsListener(context, post, this, PostViewType.getViewType(viewTypeValue));
                postViewHolder.setClickListeners(listener, longListener, optionsListener);
                postViewHolder.setPostOptionsVisible(selectedPosition == position);
                break;
            case VIEW_TYPE_USER_COMMENT:
                Comment comment = (Comment) getItemAt(position);
                UserCommentViewHolder userCommentViewHolder = (UserCommentViewHolder) viewHolder;
                userCommentViewHolder.bindModel(context, comment);

                userCommentViewHolder.layoutComment.setOnLongClickListener(new View.OnLongClickListener() {
                    @Override
                    public boolean onLongClick(View view) {
                        toggleMenuBar(position);
                        return true;
                    }
                });
                userCommentViewHolder.layoutMenuBarToggle.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        toggleMenuBar(position);
                    }
                });

                CommentItemOptionsListener commentListener = new CommentItemOptionsListener(context, comment, this);
                if(selectedPosition == position) {
                    userCommentViewHolder.showCommentOptions(commentListener);
                }
                else {
                    userCommentViewHolder.hideCommentOptions();
                }
                break;
            case VIEW_TYPE_USER_INFO:
                UserInfo userInfo = (UserInfo) getItemAt(position);
                UserInfoViewHolder userInfoViewHolder = (UserInfoViewHolder) viewHolder;
                userInfoViewHolder.bindModel(context, userInfo);
                break;
            case VIEW_TYPE_SHOW_MORE:
                FooterViewHolder footerViewHolder = (FooterViewHolder) viewHolder;
                AppCompatActivity activity = (AppCompatActivity) context;
                footerViewHolder.showMoreButton.setOnClickListener(new ShowMoreListener(activity.getSupportFragmentManager().findFragmentByTag("listFragment")));
                if(loadingMoreItems) {
                    footerViewHolder.showMoreProgress.setVisibility(View.VISIBLE);
                    footerViewHolder.showMoreButton.setVisibility(View.GONE);
                }
                else {
                    footerViewHolder.showMoreProgress.setVisibility(View.GONE);
                    footerViewHolder.showMoreButton.setVisibility(View.VISIBLE);
                }
                break;
            case VIEW_TYPE_MESSAGE:
                MessageViewHolder messageViewHolder = (MessageViewHolder) viewHolder;
                Message message = (Message) getItemAt(position);
                messageViewHolder.bindModel(context, message);
                break;
        }
    }

    @Override
    public int getItemCount() {
        return redditItems.size();
    }

    @Override
    public int getItemViewType(int position) {
        return getItemAt(position).getViewType();
    }

    public RedditItem getItemAt(int position) {
        return redditItems.get(position);
    }

    public int indexOf(RedditItem item) {
        return redditItems.indexOf(item);
    }

    public static class MessageViewHolder extends RecyclerView.ViewHolder {

        public TextView subject;
        public TextView newMsg;
        public TextView body;
        public TextView age;
        public TextView author;
        public TextView dest;
        public LinearLayout layoutMessage;
        //public LinearLayout layoutMessageOptions;

        public MessageViewHolder(View itemView) {
            super(itemView);
            layoutMessage = (LinearLayout) itemView.findViewById(R.id.layout_message);
            //layoutMessageOptions = (LinearLayout) itemView.findViewById(R.id.layout_messageOptions);
            subject = (TextView) itemView.findViewById(R.id.txtView_msgSubject);
            newMsg = (TextView) itemView.findViewById(R.id.textView_new_message);
            body = (TextView) itemView.findViewById(R.id.txtView_messageBody);
            age = (TextView) itemView.findViewById(R.id.txtView_messageAge);
            author = (TextView) itemView.findViewById(R.id.textView_messageAuthor);
            dest = (TextView) itemView.findViewById(R.id.textView_dest);
        }

        public void bindModel(Context context, Message message) {
            subject.setText(message.subject);

            if(AppConstants.useMarkdownParsing) {

            }
            else {
                //parse body with fromHtml
                SpannableStringBuilder strBuilder = (SpannableStringBuilder) ConvertUtils.noTrailingwhiteLines(Html.fromHtml(message.bodyHTML, null,
                        new HtmlTagHandler(body.getPaint())));
                strBuilder = SpanUtils.modifyURLSpan(context, strBuilder);
                body.setText(strBuilder);
                body.setMovementMethod(MyLinkMovementMethod.getInstance());
            }

            age.setText(message.agePrepared);

            try {
                if (message.author.equals(MyApplication.currentUser.getUsername()) && !message.destination.equals(MyApplication.currentUser.getUsername())) {
                    dest.setText("to ");
                    author.setText(message.destination);
                } else {
                    dest.setText("from ");
                    author.setText(message.author);
                }
            } catch (Exception e) { // this shouldn't throw any exceptions now but just to be safe
                e.printStackTrace();
                dest.setText("from ");
                author.setText("[deleted]");
            }

            if(message.isNew) {
                newMsg.setVisibility(View.VISIBLE);
            }
            else {
                newMsg.setVisibility(View.GONE);
            }

            MessageItemListener listener = new MessageItemListener(context, message);
            layoutMessage.setOnClickListener(listener);
            layoutMessage.setOnLongClickListener(listener);
        }
    }

    public static class UserCommentViewHolder extends RecyclerView.ViewHolder {

        public LinearLayout layoutComment;
        public LinearLayout layoutMenuBarToggle;
        public LinearLayout layoutCommentOptions;
        public TextView postTitle;
        public TextView commentSubreddit;
        public TextView commentBody;
        public TextView commentScore;
        public TextView commentAge;
        public ImageView upvote;
        public ImageView downvote;
        public ImageView reply;
        public ImageView viewUser;
        public ImageView save;
        public ImageView share;
        public ImageView more;
        public ImageView menuBarToggle;

        private float defaultIconOpacity, defaultIconOpacityDisabled;

        public UserCommentViewHolder(View itemView) {
            super(itemView);
            defaultIconOpacity = MyApplication.currentBaseTheme == AppConstants.DARK_THEME_LOW_CONTRAST ? 0.6f : 1f;
            defaultIconOpacityDisabled = MyApplication.currentBaseTheme == AppConstants.DARK_THEME_LOW_CONTRAST ? 0.3f : 0.5f;
            layoutComment = (LinearLayout) itemView.findViewById(R.id.layout_comment);
            layoutMenuBarToggle = itemView.findViewById(R.id.layout_toggle_menu_bar);
            layoutCommentOptions = (LinearLayout) itemView.findViewById(R.id.layout_commentOptions);
            postTitle = (TextView) itemView.findViewById(R.id.txtView_postTitle);
            commentBody = (TextView) itemView.findViewById(R.id.txtView_commentBody);
            commentScore = (TextView) itemView.findViewById(R.id.txtView_commentScore);
            commentSubreddit = (TextView) itemView.findViewById(R.id.txtView_commentSubreddit);
            commentAge = (TextView) itemView.findViewById(R.id.txtView_commentAge);
            upvote = (ImageView) itemView.findViewById(R.id.btn_upvote);
            downvote = (ImageView) itemView.findViewById(R.id.btn_downvote);
            reply = (ImageView) itemView.findViewById(R.id.btn_reply);
            viewUser = (ImageView) itemView.findViewById(R.id.btn_view_user);
            save = (ImageView) itemView.findViewById(R.id.btn_save);
            share = (ImageView) itemView.findViewById(R.id.btn_share);
            more = (ImageView) itemView.findViewById(R.id.btn_more);
            menuBarToggle = itemView.findViewById(R.id.imageView_toggle_menu_bar);

            if (MyApplication.currentBaseTheme == AppConstants.LIGHT_THEME) menuBarToggle.setAlpha(0.54f);
            else if (MyApplication.currentBaseTheme == AppConstants.DARK_THEME_LOW_CONTRAST) menuBarToggle.setAlpha(0.6f);
            else menuBarToggle.setAlpha(1f);

            viewUser.setAlpha(defaultIconOpacity);
            share.setAlpha(defaultIconOpacity);
            more.setAlpha(defaultIconOpacity);

            reply.setVisibility(View.GONE);
            viewUser.setVisibility(View.GONE);
        }

        public void bindModel(Context context, Comment comment) {
            postTitle.setText(comment.getLinkTitle());
            commentSubreddit.setText(comment.getSubreddit());

            if(AppConstants.useMarkdownParsing) {

            }
            else {
                //parse html body using fromHTML
                SpannableStringBuilder strBuilder = (SpannableStringBuilder) ConvertUtils.noTrailingwhiteLines(Html.fromHtml(comment.getBodyHTML(), null,
                        new HtmlTagHandler(commentBody.getPaint())));
                strBuilder = SpanUtils.modifyURLSpan(context, strBuilder);
                commentBody.setText(strBuilder);
                commentBody.setMovementMethod(MyLinkMovementMethod.getInstance());
            }

            String scoreText = comment.isScoreHidden() ? "[score hidden]" : Long.toString(comment.getScore());
            commentScore.setText(scoreText);
            String ageString = " · " + comment.agePrepared;
            if (!comment.isScoreHidden()) ageString = " pts" + ageString;
            if (comment.getEdited()) ageString += "*";
            commentAge.setText(ageString);

            layoutComment.setOnClickListener(new CommentLinkListener(context, comment));

            layoutCommentOptions.setBackgroundColor(MyApplication.currentPrimaryColor);
            // user logged in
            if(MyApplication.currentUser != null) {
                reply.setAlpha(defaultIconOpacity);
                // check user vote
                if (comment.getLikes().equals("true")) {
                    commentScore.setTextColor(MyApplication.upvoteColor);
                    upvote.setImageResource(R.drawable.ic_arrow_upward_upvote_orange_48dp);
                    upvote.setAlpha(1f);
                    downvote.setImageResource(R.drawable.ic_arrow_upward_white_48dp);
                    downvote.setAlpha(defaultIconOpacity);
                }
                else if (comment.getLikes().equals("false")) {
                    commentScore.setTextColor(MyApplication.downvoteColor);
                    upvote.setImageResource(R.drawable.ic_arrow_upward_white_48dp);
                    upvote.setAlpha(defaultIconOpacity);
                    downvote.setImageResource(R.drawable.ic_arrow_downward_downvote_blue_48dp);
                    downvote.setAlpha(1f);
                }
                else {
                    commentScore.setTextColor(MyApplication.textSecondaryColor);
                    upvote.setImageResource(R.drawable.ic_arrow_upward_white_48dp);
                    downvote.setImageResource(R.drawable.ic_arrow_downward_white_48dp);
                    upvote.setAlpha(defaultIconOpacity);
                    downvote.setAlpha(defaultIconOpacity);
                }
                // check saved state
                if(comment.isSaved()) {
                    save.setImageResource(R.drawable.ic_star_border_yellow_500_48dp);
                    save.setAlpha(1f);
                }
                else {
                    save.setImageResource(R.drawable.ic_star_border_white_48dp);
                    save.setAlpha(defaultIconOpacity);
                }
            }
            // logged out
            else {
                reply.setAlpha(defaultIconOpacityDisabled);
                save.setAlpha(defaultIconOpacityDisabled);
                commentScore.setTextColor(MyApplication.textSecondaryColor);
                upvote.setImageResource(R.drawable.ic_arrow_upward_white_48dp);
                downvote.setImageResource(R.drawable.ic_arrow_downward_white_48dp);
                upvote.setAlpha(defaultIconOpacityDisabled);
                downvote.setAlpha(defaultIconOpacityDisabled);
            }
        }

        public void showCommentOptions(View.OnClickListener listener) {
            layoutComment.setBackgroundColor(MyApplication.colorPrimaryLight);
            menuBarToggle.setImageResource(MyApplication.nightThemeEnabled ? R.drawable.ic_expand_less_white_48dp : R.drawable.ic_expand_less_black_48dp);
            layoutCommentOptions.setVisibility(View.VISIBLE);
            upvote.setOnClickListener(listener);
            downvote.setOnClickListener(listener);
            reply.setOnClickListener(listener);
            viewUser.setOnClickListener(listener);
            save.setOnClickListener(listener);
            share.setOnClickListener(listener);
            more.setOnClickListener(listener);
        }

        public void hideCommentOptions() {
            layoutComment.setBackground(null);
            menuBarToggle.setImageResource(MyApplication.nightThemeEnabled ? R.drawable.ic_expand_more_white_48dp : R.drawable.ic_expand_more_black_48dp);
            layoutCommentOptions.setVisibility(View.GONE);
        }
    }

    public static class UserInfoViewHolder extends RecyclerView.ViewHolder {

        public LinearLayout layoutKarma;
        public TableLayout layoutTrophies;
        public TextView linkKarma;
        public TextView commentKarma;
        //private boolean trophiesAdded;

        public UserInfoViewHolder(View itemView) {
            super(itemView);
            //trophiesAdded = false;
            layoutKarma = (LinearLayout) itemView.findViewById(R.id.layout_karma);
            layoutTrophies = (TableLayout) itemView.findViewById(R.id.layout_trophies);
            linkKarma = (TextView) itemView.findViewById(R.id.txtView_linkKarma);
            commentKarma = (TextView) itemView.findViewById(R.id.txtView_commentKarma);
        }

        public void bindModel(Context context, UserInfo userInfo) {
            linkKarma.setText(Long.toString(userInfo.getLinkKarma()));
            commentKarma.setText(Long.toString(userInfo.getCommentKarma()));

            setupTrophiesView((Activity) context, layoutTrophies, userInfo.getTrophyList());
        }

        private void setupTrophiesView(Activity mActivity, TableLayout trophiesLayout, List<Trophy> trophiesList) {
            //trophiesAdded = true;
            trophiesLayout.removeAllViews();
            DisplayMetrics dm = new DisplayMetrics();
            mActivity.getWindowManager().getDefaultDisplay().getMetrics(dm);
            int width=dm.widthPixels;
            //int height=dm.heightPixels;
            int dens=dm.densityDpi;
            double wi=(double)width/(double)dens;
            if(MyApplication.dualPaneActive) wi /= 2;
            //double hi=(double)height/(double)dens;
            //double x = Math.pow(wi,2);
            //double y = Math.pow(hi,2);
            //double screenInches = Math.sqrt(x+y);

            //Log.d("screen width inches: ", Double.toString(wi));
            //Log.d("screen width pixels: ", Integer.toString(width));
//
            //Log.d("screen density in dpi: ", Integer.toString(dens));

            int margin = (int) Math.round(dens / 26.62);
            TableLayout.LayoutParams rowParams = new TableLayout.LayoutParams(TableLayout.LayoutParams.WRAP_CONTENT,TableLayout.LayoutParams.WRAP_CONTENT);
            rowParams.setMargins(0, 0, 0, margin);

            int size = (int) Math.round(dens / 3.2);
            TableRow.LayoutParams imageParams = new TableRow.LayoutParams(size, size);
            imageParams.setMargins(margin, 0, 0, 0);

            int count = 0;
            int MAX_TROPHY_COLUMNS = (int) Math.round(2.7 * wi);
            int mRows = trophiesList.size() / MAX_TROPHY_COLUMNS;
            if(trophiesList.size() % MAX_TROPHY_COLUMNS != 0)
                mRows++;
            for(int i=0; i<mRows; i++) {
                TableRow tr = new TableRow(mActivity);

                for(int j=0; j< MAX_TROPHY_COLUMNS; j++) {
                    if((count) == trophiesList.size()) break;
                    ImageView iv = new ImageView(mActivity);
                    Picasso.with(mActivity).load(trophiesList.get(count).getIcon70url()).into(iv);
                    iv.setLayoutParams(imageParams);
                    tr.addView(iv);
                    count++;
                }
                tr.setLayoutParams(rowParams);
                trophiesLayout.addView(tr);
            }
        }
    }

    public static class FooterViewHolder extends RecyclerView.ViewHolder {

        public Button showMoreButton;
        public ProgressBar showMoreProgress;

        public FooterViewHolder(View itemView) {
            super(itemView);
            showMoreButton = (Button) itemView.findViewById(R.id.showMore);
            showMoreProgress = (ProgressBar) itemView.findViewById(R.id.progressBar);
            showMoreProgress.getIndeterminateDrawable().setColorFilter(MyApplication.colorSecondary, PorterDuff.Mode.SRC_IN);
        }
    }

}
