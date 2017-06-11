package cn.studyjams.s2.sj0175.MY_Devotion;

import android.app.Fragment;
import android.os.Bundle;
import android.os.Environment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.SeekBar;
import android.widget.SimpleAdapter;
import android.widget.TextView;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by sao on 2017/3/3.
 */

public class LearnFragment extends Fragment{
    View view;
    public String read(InputStream fis) throws IOException {
        ByteArrayOutputStream outstream = new ByteArrayOutputStream();
        byte[] buffer = new byte[1024];
        int len = 0;
        while((len = fis.read(buffer)) != -1)
            outstream.write(buffer, 0, len);
        fis.close(); outstream.close();
        byte[] data = outstream.toByteArray();
        return new String(data);
    }
    String []content = new String[100];
    int nctt;
    SimpleAdapter listadapter = null;
    String ESD = Environment.getExternalStorageDirectory().getPath()+"/MemoryPalace/";
    public void transtocontent(String str) {
        nctt = 0;
        StringBuffer temp = new StringBuffer();
        for(int i = 0; i < str.length(); i++)
        {
            char ch = str.charAt(i);
            if(ch == '!')
            {
                content[nctt++] = temp.toString();
                temp = new StringBuffer();
                continue;
            }
            if(ch != ' ') temp.append(ch);
        }
    }
    public void learninit() {
        File file = new File(ESD);
        if(!file.exists()) file.mkdirs();
        file = new File(file, "_learn.txt");
        if(file.exists())
        {
            String input = " ";
            try {
                input = read(new FileInputStream(file));
            } catch (IOException e) {
                e.printStackTrace();
            }
            transtocontent(input);
        } else
        {
            try {
                file.createNewFile();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public interface Mylistener {
        public void LFtoloadF();
        public void LFtoCF(String str);
    }
    public Mylistener listener;

    ListView listview;
    public class Loadbarlistener implements SeekBar.OnSeekBarChangeListener {
        @Override
        public void onProgressChanged(SeekBar seekBar, int i, boolean b) {
            if(i >= 90)  { listener.LFtoloadF(); seekBar.setProgress(0); }
        }
        @Override
        public void onStartTrackingTouch(SeekBar seekBar) {

        }
        @Override
        public void onStopTrackingTouch(SeekBar seekBar) {

        }
    }

    public class Listviewlistener implements AdapterView.OnItemClickListener{
        public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
            listener.LFtoCF(content[i]);
        }
    }

    //adpter获取数据
    public List<Map<String, Object>> getData() {
        List<Map<String, Object>> list = new ArrayList<>();
        Map<String, Object> map = new HashMap<>();
        for(int i = 0; i < nctt; i++)
        {
            map = new HashMap<>();
            map.put("img", R.drawable.mainicon);
            map.put("title", content[i]);
            list.add(map);
        }
        return  list;
    }


    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragmentlearn, container, false);
        listener = (Mylistener)getActivity();
        learninit();
        //listview初始化
        listview = (ListView) view.findViewById(R.id.learnlist);
        TextView emptyView = (TextView) view.findViewById(R.id.learnempty);
        listview.setEmptyView(emptyView);

        if(nctt > 0) listadapter = new SimpleAdapter(getActivity(), getData(),
                R.layout.learnlistview, new String[] { "img", "title" },
                new int[] { R.id.learnlistimg, R.id.learnlisttitle});
        listview.setAdapter(listadapter);
        SeekBar download = (SeekBar) view.findViewById(R.id.learnloadbar);

        download.setOnSeekBarChangeListener(new Loadbarlistener());
        listview.setOnItemClickListener(new Listviewlistener());
        return view;
    }
}
