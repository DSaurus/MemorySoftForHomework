package cn.studyjams.s2.sj0175.MY_Devotion;

import android.app.Fragment;
import android.os.Bundle;
import android.os.Environment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.SeekBar;
import android.widget.TabHost;
import android.widget.TextView;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;

import static java.lang.Math.min;
import static java.lang.StrictMath.max;

/**
 * Created by sao on 2017/3/4.
 */

public class SettimeFragment extends Fragment {
    View view;
    String learndata;
    File timefile;
    String ESD = Environment.getExternalStorageDirectory().getPath()+"/MemoryPalace/";
    long[] time_1 = new long[4], time_2 = new long[5], time_3 = new long[5];
    long[] timemin = new long[5], timemax = new long[5], nowtime = new long[5];
    TabHost tablearn;
    TextView[] settimetext = new TextView[5];
    SeekBar[] settimebar = new SeekBar[5];
    Button[] settimebt = new Button[5];
    int nowsetlayout, ndata;
    String dateform(long t) {
        StringBuffer temp = new StringBuffer();
        //3600 1h   86400 1d
        int tt = 0;
        if (t > 86400) {
            temp.append(t / 86400 + "天");
            t = t % 86400;
            tt++;
        }
        if (t > 3600) {
            temp.append(t / 3600 + "小时");
            t = t % 3600;
            tt++;
            if (tt > 1) return temp.toString();
        }
        if (t > 60) {
            temp.append(t / 60 + "分钟");
            t = t % 60;
            tt++;
            if (tt > 1) return temp.toString();
        }
        temp.append(t + "秒");
        return temp.toString();

    }
    public String read(InputStream fis) throws IOException {
        ByteArrayOutputStream outstream = new ByteArrayOutputStream();
        byte[] buffer = new byte[1024];
        int len = 0;
        while ((len = fis.read(buffer)) != -1)
            outstream.write(buffer, 0, len);
        fis.close();
        outstream.close();
        return outstream.toString();
    }
    public long getnumber(String str, int x, int ty) {
        long ans = 0, y = 0;
        boolean f = false;
        for (int i = x; i < str.length(); i++) {
            char ch = str.charAt(i);
            if (f && (ch < '0' || ch > '9')) break;
            if (ch < '0' || ch > '9') continue;
            ans = ans * 10 + ch - '0';
            y = i;
            f = true;
        }
        if (ty == 1) return ans;
        return y + 1;
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
    //从str读取time 315
    public void trantotime(String str){
        if(str.equals("")) return;
        int start = 0;
        for(int i = 0; i < 4; i++) { time_1[i] = getnumber(str, start, 1); start = (int)getnumber(str, start, 0); }
        for(int i = 0; i < 5; i++) { time_2[i] = getnumber(str, start, 1); start = (int)getnumber(str, start, 0); }
        for(int i = 0; i < 5; i++) { time_3[i] = getnumber(str, start, 1); start = (int)getnumber(str, start, 0); }
    }

    public void analysetimedata() {
        try {
            String input = read(new FileInputStream(timefile));
            trantotime(input);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    public int getbarid(int x) {
        switch (x){
            case R.id.settimebar1:return 0;
            case R.id.settimebar2:return 1;
            case R.id.settimebar3:return 2;
            case R.id.settimebar4:return 3;
            case R.id.settimebar5:return 4;
        }
        return 0;
    }
    public int getbtid(int x) {
        switch (x){
            case R.id.settimeconfirm1:return 0;
            case R.id.settimeconfirm2:return 1;
            case R.id.settimeconfirm3:return 2;
            case R.id.settimeconfirm4:return 3;
            case R.id.settimeconfirm5:return 4;
        }
        return 0;
    }

    public class Timebarlistener implements SeekBar.OnSeekBarChangeListener {
        public void onProgressChanged(SeekBar seekBar, int i, boolean b) {
            int x = getbarid(seekBar.getId());
            settimetext[x].setText("第"+(x+1)+"阶段:  " + dateform(i + timemin[x]));
        }
        public void onStartTrackingTouch(SeekBar seekBar) {

        }
        public void onStopTrackingTouch(SeekBar seekBar) {

        }
    }

    //确认并重新设置
    public void resettime(int i) {
        long t = settimebar[i].getProgress();
        nowtime[i] = t + timemin[i];
        timemin[i] = max(1, nowtime[i]/2); timemax[i] = min((long)1e9, nowtime[i]*2);
        settimebar[i].setMax((int)(timemax[i] - timemin[i]));
        settimebar[i].setProgress((int)(nowtime[i]-timemin[i]));
        settimetext[i].setText("第"+(i+1)+"阶段:  "+ dateform(nowtime[i]));
    }

    public class Timebuttonlistener implements View.OnClickListener {
        public void onClick(View view) {
            int x = getbtid(view.getId());
            resettime(x);
            switch (nowsetlayout) {
                case 2:time_2 = nowtime; break;
                case 3:time_3 = nowtime; break;
            }
            savetimedata();
        }
    }

    public void settimelayout(long[] settime) {
        nowtime = settime;
        for(int i = 0; i < settime.length; i++)
        {
            settimetext[i].setText("第"+(i+1)+"阶段:  "+ dateform(nowtime[i]));
            timemin[i] = max(1, nowtime[i]/2); timemax[i] = min((long)1e9, nowtime[i]*2);
            settimebar[i].setMax((int)(timemax[i] - timemin[i]));
            settimebar[i].setProgress((int)(nowtime[i] - timemin[i]));
            settimebar[i].setOnSeekBarChangeListener(new Timebarlistener());
            settimebt[i].setOnClickListener(new Timebuttonlistener());
        }
    }

    public class Learntablistener implements TabHost.OnTabChangeListener {
        public void onTabChanged(String s) {
            if(s.equals("tab1")){
                settimelayout(time_2);
                nowsetlayout = 2;
            } else if(s.equals("tab2")){
                settimelayout(time_3);
                nowsetlayout = 3;
            }
        }
    }

    public void layoutinit() {
        settimetext[0] = (TextView) view.findViewById(R.id.settimetext1);
        settimetext[1] = (TextView) view.findViewById(R.id.settimetext2);
        settimetext[2] = (TextView) view.findViewById(R.id.settimetext3);
        settimetext[3] = (TextView) view.findViewById(R.id.settimetext4);
        settimetext[4] = (TextView) view.findViewById(R.id.settimetext5);
        settimebar[0] = (SeekBar) view.findViewById(R.id.settimebar1);
        settimebar[1] = (SeekBar) view.findViewById(R.id.settimebar2);
        settimebar[2] = (SeekBar) view.findViewById(R.id.settimebar3);
        settimebar[3] = (SeekBar) view.findViewById(R.id.settimebar4);
        settimebar[4] = (SeekBar) view.findViewById(R.id.settimebar5);
        settimebt[0] = (Button) view.findViewById(R.id.settimeconfirm1);
        settimebt[1] = (Button) view.findViewById(R.id.settimeconfirm2);
        settimebt[2] = (Button) view.findViewById(R.id.settimeconfirm3);
        settimebt[3] = (Button) view.findViewById(R.id.settimeconfirm4);
        settimebt[4] = (Button) view.findViewById(R.id.settimeconfirm5);
        for(int i = 0; i < 5; i++) settimebt[i].setText("修改");
    }


    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragmentsettime, container, false);
        learndata = getArguments().get("data")+"";
        timefile = new File(ESD + learndata+ "time.txt");
        analysetimedata();
        layoutinit();
        tablearn = (TabHost) view.findViewById(R.id.learntab);
        tablearn.setup();
        tablearn.setOnTabChangedListener(new Learntablistener());
        tablearn.addTab(tablearn.newTabSpec("tab1").setIndicator("比较熟悉").setContent(R.id.leveltype1));
        tablearn.addTab(tablearn.newTabSpec("tab2").setIndicator("非常熟悉").setContent(R.id.leveltype1));
        tablearn.setCurrentTab(1); tablearn.setCurrentTab(0);
        settimelayout(time_2); nowsetlayout = 2;
        return view;
    }
}
