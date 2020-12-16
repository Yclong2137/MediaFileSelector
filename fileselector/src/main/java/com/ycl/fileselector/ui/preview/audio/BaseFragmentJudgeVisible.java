package com.ycl.fileselector.ui.preview.audio;

import androidx.fragment.app.Fragment;

public class BaseFragmentJudgeVisible extends Fragment {
    protected boolean mIsVisibleToUser;
    private String TAG="";

    @Override
    public void onStart() {
        super.onStart();
        if (mIsVisibleToUser) {
            onVisible();
        }
    }

    @Override
    public void onStop() {
        super.onStop();
        if (mIsVisibleToUser) {
            onInVisible();
        }
    }

    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        super.setUserVisibleHint(isVisibleToUser);
        mIsVisibleToUser = isVisibleToUser;
        if (isResumed()) { // fragment have created
            if (mIsVisibleToUser) {
                onVisible();
            } else {
                onInVisible();
            }
        }
    }

    public void onVisible() {
//        Toast.makeText(getActivity(), TAG +"visible", Toast.LENGTH_SHORT).show();
    }

    public void onInVisible() {
//        Toast.makeText(getActivity(), TAG +"invisible", Toast.LENGTH_SHORT).show();
    }
}
