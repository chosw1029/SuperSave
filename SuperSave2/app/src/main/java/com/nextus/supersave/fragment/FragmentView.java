package com.nextus.supersave.fragment;

import android.support.v4.app.Fragment;
import android.view.View;
import android.view.ViewGroup;

import com.nextus.supersave.view.ViewMapper;

/**
 * Created by chosw on 2016-06-27.
 */
public class FragmentView extends Fragment {

    public View inflateView(int layout, ViewGroup container)
    {
        View view = ViewMapper.inflateLayout(getContext(), container, layout);
        return view;
    }
}
