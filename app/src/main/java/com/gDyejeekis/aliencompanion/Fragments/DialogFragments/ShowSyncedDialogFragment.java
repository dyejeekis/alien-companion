package com.gDyejeekis.aliencompanion.Fragments.DialogFragments;

import android.content.DialogInterface;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.gDyejeekis.aliencompanion.Activities.MainActivity;
import com.gDyejeekis.aliencompanion.Activities.SubredditActivity;
import com.gDyejeekis.aliencompanion.R;
import com.gDyejeekis.aliencompanion.Services.DownloaderService;
import com.gDyejeekis.aliencompanion.Utils.GeneralUtils;
import com.gDyejeekis.aliencompanion.Utils.ToastUtils;

import java.io.File;
import java.io.FilenameFilter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

/**
 * Created by sound on 4/12/2016.
 */
public class ShowSyncedDialogFragment extends ScalableDialogFragment implements AdapterView.OnItemClickListener, AdapterView.OnItemLongClickListener {

    private ListView syncedList;
    private List<String> filenames;
    private ArrayAdapter adapter;
    private ProgressBar pBar;
    private TextView message;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle bundle) {
        View view = inflater.inflate(R.layout.fragment_show_synced, container, false);

        syncedList = (ListView) view.findViewById(R.id.listView_synced_list);
        pBar = (ProgressBar) view.findViewById(R.id.progressBar_synced_list);
        message = (TextView) view.findViewById(R.id.textView_synced_list);
        syncedList.setOnItemClickListener(this);
        syncedList.setOnItemLongClickListener(this);

        FindSyncedTask task = new FindSyncedTask(this);
        task.execute();

        getDialog().setCanceledOnTouchOutside(true);
        getDialog().requestWindowFeature(Window.FEATURE_NO_TITLE);
        return view;
    }

    public void updateSyncedList(List<String> syncedStrings) {
        pBar.setVisibility(View.GONE);
        message.setVisibility(View.GONE);
        syncedList.setVisibility(View.VISIBLE);
        filenames = syncedStrings;
        adapter = new ArrayAdapter<>(getActivity(), android.R.layout.simple_list_item_1, filenames);
        syncedList.setAdapter(adapter);
    }

    @Override
    public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
        dismiss();
        String filename = adapter.getItem(i).toString();
        String subreddit = (filename.equals("frontpage")) ? null : filename;
        if(getActivity() instanceof MainActivity) {
            ((MainActivity) getActivity()).getListFragment().changeSubreddit(subreddit);
            ((MainActivity) getActivity()).getNavDrawerAdapter().notifyDataSetChanged();
        }
        else {
            ((SubredditActivity) getActivity()).getListFragment().changeSubreddit(subreddit);
        }
    }

    @Override
    public boolean onItemLongClick(AdapterView<?> adapterView, View view, int i, long l) {
        final String filename = adapter.getItem(i).toString();
        final int pos = i;
        String message = "Delete all synced posts, comments, images and articles for " + filename + "?";
        new AlertDialog.Builder(getActivity()).setMessage(message).setNegativeButton("No", null).setPositiveButton("Yes", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                GeneralUtils.clearSyncedPostsAndComments(getActivity(), filename);
                GeneralUtils.clearSyncedImages(getActivity(), filename);
                filenames.remove(pos);
                adapter.notifyDataSetChanged();
                ToastUtils.displayShortToast(getActivity(), "Synced posts for " + filename + " cleared");
            }
        }).show();
        return true;
    }

    public void noSyncedRedditsFound() {
        syncedList.setVisibility(View.GONE);
        pBar.setVisibility(View.GONE);
        message.setVisibility(View.VISIBLE);
    }

    public static class FindSyncedTask extends AsyncTask<Void, Void, List<String>> {

        private ShowSyncedDialogFragment dialog;

        public FindSyncedTask(ShowSyncedDialogFragment dialog) {
            this.dialog = dialog;
        }

        @Override
        public List<String> doInBackground(Void... unused) {
            FilenameFilter filenameFilter = new FilenameFilter() {
                @Override
                public boolean accept(File file, String s) {
                    if(s.endsWith(DownloaderService.LOCA_POST_LIST_SUFFIX)) return true;
                    return false;
                }
            };

            File[] files = dialog.getActivity().getFilesDir().listFiles(filenameFilter);
            if(files.length > 0) {
                Collections.sort(Arrays.asList(files), new Comparator<File>() {
                    @Override
                    public int compare(File f1, File f2) {
                        return Long.valueOf(f2.lastModified()).compareTo(Long.valueOf(f1.lastModified()));
                    }
                });
                List<String> strings = new ArrayList<>();
                for(File file : files) {
                    String name = file.getName().substring(0, file.getName().indexOf('-'));
                    strings.add(name);
                }
                return strings;
            }
            return null;
        }

        @Override
        public void onPostExecute(List<String> syncedStrings) {
            if(syncedStrings == null) {
                dialog.noSyncedRedditsFound();
            }
            else {
                dialog.updateSyncedList(syncedStrings);
            }
        }
    }
}