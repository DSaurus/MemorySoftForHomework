package cn.studyjams.s2.sj0175.MY_Devotion;

import android.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.gc.materialdesign.views.ButtonRectangle;

/**
 * Created by sao on 2017/3/2.
 */

public class FinishFragment extends Fragment {
    public Mylistener listener;
    public interface Mylistener
    {
        public void FFtoCF(String str);
    }
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragmentfinish, container, false);
        listener = (Mylistener) getActivity();
        ButtonRectangle finish = (ButtonRectangle) view.findViewById(R.id.returnmain);
        final String str = getArguments().get("data")+"";
        finish.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                listener.FFtoCF(str);
            }
        });
        return view;
    }
}
