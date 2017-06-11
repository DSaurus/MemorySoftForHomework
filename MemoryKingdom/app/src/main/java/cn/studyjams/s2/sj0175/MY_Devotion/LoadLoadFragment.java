package cn.studyjams.s2.sj0175.MY_Devotion;

import android.app.Fragment;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.SimpleAdapter;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by sao on 2017/3/10.
 */

public class LoadLoadFragment extends Fragment {
    View view;
    String content[] = new String[100], learncontent[] = new String[100];
    SimpleAdapter listadapter = null;
    ListView listview;
    int nctt;
    public long[] admintime_1 = {120, 240, 240, 240, 300};
    public long[] admintime_2 = {43200, 86400, 86400, 172800, 172800};
    public long[] admintime_3 = {604800, 604800, 1209600, 2419200, 2419200};
    long[] time_1 = new long[4], time_2 = new long[5], time_3 = new long[5];
    public class Data{
        public String ques, ans;
        public int flag;
        public int level, val;
        public long time, begintime;
        //flag level val time begintime
    }
    int newn, ndata;
    Data[] data = new Data[2017], newdata = new Data[2017];
    Map<String, Integer> map = new HashMap<>();
    Map<String, Integer> defaultmap = new HashMap<>();
    String ESD = Environment.getExternalStorageDirectory().getPath()+"/MemoryPalace/";
    String learndata;
    File learnfile, timefile;

    public interface Mylistener {
        public void DLFtoLF();
    }
    public Mylistener listener;

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
    public void write(String output, File file) throws IOException {
        FileOutputStream fis = new FileOutputStream(file);
        byte[] words = output.getBytes();
        fis.write(words);
        fis.close();
    }
    public String numarraytostr(long []a) {
        StringBuffer temp = new StringBuffer();
        for(int i = 0; i < a.length; i++) temp.append(a[i]+" "); temp.append("\n");
        return temp.toString();
    }

    //将文件保存到learnfile中 315
    public void savedata() {
        StringBuffer output = new StringBuffer();
        for(int i = 0; i < ndata; i++)
        {
            output.append(data[i].ques); output.append('?');
            output.append(data[i].ans); output.append('!');
            output.append(data[i].flag); output.append(" ");
            output.append(data[i].level); output.append(" ");
            output.append(data[i].val); output.append(" ");
            output.append(data[i].time); output.append(" ");
            output.append(data[i].begintime); output.append(" ");
        }
        try {
            write(output.toString(), learnfile);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    //将时间数据保存到timefile中 315
    public void savetimedata(){
        StringBuffer output = new StringBuffer();
        output.append(numarraytostr(time_1)); output.append("\n");
        output.append(numarraytostr(time_2)); output.append("\n");
        output.append(numarraytostr(time_3)); output.append("\n");
        try {
            write(output.toString(), timefile);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    //添加data，重复的问题更新答案（自动去重）,并添加时间 315
    public void rewritedata() {
        for(int i = 0; i < newn; i++)
        {
            if(map.containsKey(newdata[i].ques))
                data[map.get(newdata[i].ques)].ans = newdata[i].ans;
            else
                data[ndata++] = newdata[i];
        }
        savedata(); savetimedata();
    }

    public void rewritewhole(String str) {
        File file = new File(ESD + "_learn.txt");
        if(!file.exists()) try {
            file.createNewFile();
        } catch (IOException e) {
            e.printStackTrace();
        }
        try {
            String input = read(new FileInputStream(file));
            int n = transtoLcontent(input);
            for(int i = 0; i < n; i++) {
                if(str.equals(learncontent[i])) return;
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        try {
            String input = read(new FileInputStream(file));
            input = input + str + '!';
            write(input, file);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    public long getnumber(String str, int x, int ty) {
        long ans = 0, y = 0;
        boolean f = false;
        for(int i = x; i < str.length(); i++)
        {
            char ch = str.charAt(i);
            if(f && (ch < '0' || ch > '9')) break;
            if(ch < '0' || ch > '9') continue;
            ans = ans*10 + ch - '0';
            y = i; f = true;
        }
        if(ty == 1) return ans;
        return y+1;
    }

    //从str读取data 315
    public int transtodata(String str) {
        int n = 0;
        if(str.equals("")) return 0;
        StringBuffer cur = new StringBuffer();
        for(int i = 0; i < str.length(); i++)
        {
            char ch = str.charAt(i);
            if(ch == '?')
            {
                if(data[n] == null) data[n] = new Data();
                data[n].ques = cur.toString();
                cur = new StringBuffer();
            } else if(ch == '!')
            {
                data[n].ans = cur.toString();
                cur = new StringBuffer();
                int x = i+1;
                data[n].flag = (int)getnumber(str, x, 1); x = (int) getnumber(str, x, 0);
                data[n].level = (int)getnumber(str, x, 1); x = (int)getnumber(str, x, 0);
                data[n].val = (int)getnumber(str, x, 1);   x = (int)getnumber(str, x, 0);
                data[n].time = getnumber(str, x, 1);  x = (int)getnumber(str, x, 0);
                data[n].begintime = getnumber(str, x, 1); x = (int)getnumber(str, x, 0);
                n++;
                i = x;
            } else
            {
                cur.append(ch);
            }
        }
        return n;
    }
    //从str读取time 315
    public void trantotime(String str){
        if(str.equals("")) return;
        int start = 0;
        for(int i = 0; i < 4; i++) { time_1[i] = getnumber(str, start, 1); start = (int)getnumber(str, start, 0); }
        for(int i = 0; i < 5; i++) { time_2[i] = getnumber(str, start, 1); start = (int)getnumber(str, start, 0); }
        for(int i = 0; i < 5; i++) { time_3[i] = getnumber(str, start, 1); start = (int)getnumber(str, start, 0); }
    }

    public int transtonewdata(String str) {
        int n = 0;
        StringBuffer temp = new StringBuffer();
        for(int i = 0; i < str.length(); i++)
        {
            char ch = str.charAt(i);
            if(ch == '?')
            {
                if(newdata[n] == null) newdata[n] = new Data();
                newdata[n].ques = temp.toString();
                temp = new StringBuffer();
            } else if(ch == '!')
            {
                newdata[n++].ans = temp.toString();
                temp = new StringBuffer();
            } else
            {
                temp.append(ch);
            }
        }
        for(int i = 0; i < n; i++) {
            newdata[i].flag = 0;
            newdata[i].level = 1;
            newdata[i].val = 0;
            newdata[i].time = (long)1e12;
            newdata[i].begintime = 0;
        }
        return n;
    }


    public void defaultloadinit() {
        defaultmap.put("Englishbook2", R.raw.english_university_book2);
        defaultmap.put("明天会更好", R.raw.song_word);
    }

    public void Defaultload(int rawid, String name) {
        //写入新目录
        learndata = name;
        rewritewhole(learndata);

        //读取用户数据
        try {
            String input = read(getResources().openRawResource(rawid));
            newn = transtonewdata(input);
        } catch (IOException e) {
            e.printStackTrace();
        }

        //读取旧数据，并更新
        learnfile = new File(new File(ESD), learndata + ".txt");
        timefile = new File(new File(ESD), learndata + "time.txt");
        if(!learnfile.exists()) {
            try {
                learnfile.createNewFile();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        if(!timefile.exists()) {
            time_1 = admintime_1;
            time_2 = admintime_2;
            time_3 = admintime_3;
            try {
                timefile.createNewFile();
            } catch (IOException e) {
                e.printStackTrace();
            }
        } else{
            String input = null;
            try {
                input = read(new FileInputStream(timefile));
            } catch (IOException e) {
                e.printStackTrace();
            }
            trantotime(input);
        }
        try {
            String input = read(new FileInputStream(learnfile));
            ndata = transtodata(input);
        } catch (IOException e) {
            e.printStackTrace();
        }
        for(int i = 0; i < ndata; i++) map.put(data[i].ques, i);

        //写入新数据
        rewritedata();
    }

    public int transtocontent(String str) {
        StringBuffer temp = new StringBuffer();
        int n = 0;
        for(int i = 0; i < str.length(); i++)
        {
            char ch = str.charAt(i);
            if(ch == '!')
            {
                content[n++] = temp.toString();
                temp = new StringBuffer();
            } else if(ch != ' ')
                temp.append(ch);
        }
        return n;
    }
    public int transtoLcontent(String str) {
        StringBuffer temp = new StringBuffer();
        int n = 0;
        for(int i = 0; i < str.length(); i++)
        {
            char ch = str.charAt(i);
            if(ch == '!')
            {
                learncontent[n++] = temp.toString();
                temp = new StringBuffer();
            } else if(ch != ' ')
                temp.append(ch);
        }
        return n;
    }

    public List<Map<String, Object>> getData() {
        List<Map<String, Object>> list = new ArrayList<>();
        Map<String, Object> map = new HashMap<>();
        Log.i("num", nctt+"");
        for(int i = 0; i < nctt; i++)
        {
            map = new HashMap<>();
            map.put("img", R.drawable.mainicon);
            map.put("title", content[i]);
            list.add(map);
        }
        return list;
    }

    public class Listviewlistener implements AdapterView.OnItemClickListener {
        public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
            defaultloadinit();
            Defaultload(defaultmap.get(content[i]), content[i]);
            listener.DLFtoLF();
        }
    }
    /*
    1.新建raw文件
    2.添加到default list
    3.修改defaultinit
     */
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragmentloadload, container, false);
        listener = (Mylistener) getActivity();
        try {
            String input = read(getResources().openRawResource(R.raw.defaultlist));
            nctt = transtocontent(input);
        } catch (IOException e) {
            e.printStackTrace();
        }
        listview = (ListView) view.findViewById(R.id.defaultloadlist);
        listadapter = new SimpleAdapter(getActivity(), getData(),
                R.layout.learnlistview, new String[] { "img", "title" },
                new int[] { R.id.learnlistimg, R.id.learnlisttitle});
        listview.setAdapter(listadapter);
        listview.setOnItemClickListener(new Listviewlistener());

        return view;
    }
}
