package cn.studyjams.s2.sj0175.MY_Devotion;

import android.app.Fragment;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Environment;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Date;

/**
 * Created by sao on 2017/2/25.
 */

public class BeisongFragment extends Fragment implements View.OnClickListener{
    long[] time_1 = new long[4], time_2 = new long[5], time_3 = new long[5];
    public int max(int a, int b){ return a < b ? b : a; }
    public int min(int a, int b){ return a < b ? a : b; }
    public class Data{
        public String ques, ans;
        public int flag, level, val;
        public long time, calt, begintime;
        //level lv  val--记忆所用次数   time--上次记忆的时间
    }
    Data []data = new Data[1000];
    String learndata;
    String ESD =  Environment.getExternalStorageDirectory().getPath()+"/MemoryPalace/";
    File learnfile, learncatfile, timefile;  //该学科的数据文件
    public Mylistener listener;

    //与activity进行通讯
    public interface Mylistener {
        public void BFtoFF(String learndata);
    }


    //获得知识点等级需要的间隔时间 315
    public long getleveltime(int k) {
        if(k <= 4) return time_1[k-1];
        if(k <= 9) return time_2[k-5];
        if(k <= 14) return time_3[k-10];
        return (long)1e12;
    }

    //获得当前时间（秒）
    public long gettime()
    {
        return new Date().getTime()/1000;
    }

    //数组转换成字符串
    public String numarraytostr(long []a) {
        StringBuffer temp = new StringBuffer();
        for(int i = 0; i < a.length; i++) temp.append(a[i]+" "); temp.append("\n");
        return temp.toString();
    }

    //读入
    public String read(InputStream fis) throws IOException {
        ByteArrayOutputStream outstream = new ByteArrayOutputStream();
        byte[] buffer = new byte[1024];
        int len = 0;
        while((len = fis.read(buffer)) != -1)
            outstream.write(buffer, 0, len);
        fis.close(); outstream.close();
        return outstream.toString();
    }

    //向file写入output
    public void write(String output, File file) throws IOException {
        FileOutputStream fis = new FileOutputStream(file);
        byte[] words = output.getBytes();
        fis.write(words);
        fis.close();
    }

    //从字符串获得从x位置的第一个数字  ty = 1 返回答案， ty = 0返回下一个数字的出现位置
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

    //按照需要提问的时间进行排序，新知识点放在后面
    public void quickSort(Data[] a, int l, int r) {
        int ll = l, rr = r;
        boolean flag = true;
        Data base = a[ll];
        while(l < r)
        {
            while(l < r && (a[r].calt >= base.calt || a[r].flag == 0))
            {
                r--;
                flag = false;
            }
            a[l] = a[r];
            while(l < r && (a[l].calt <= base.calt || base.flag == 0))
            {
                l++;
                flag = false;
            }
            a[r] = a[l];
        }
        a[l] = base;
        if(!flag)
        {
            quickSort(a, ll, l-1);
            quickSort(a, l+1, rr);
        }
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

    //学习模式有关变量
    int nowx = 0;  //当前处于的序列的单词位置
    int newn, learnmode = 0, Qn, QQn, ndata, ggn, ggkn;
    //typemode 新词和巩固          learnmode 提问和答案
    int[] Q = new int[2017], QQ = new int[2017]; //Q当前序列，QQ备用序列

    //layout设置
    View view;
    TextView q, a, mode, texttime, level, selectlevel;
    Button memory_1, memory_2, memory_3, nextwordbutton;
    ProgressBar beisongbar;

    public void layoutinit() {
        listener = (Mylistener) getActivity();
        mode = (TextView) view.findViewById(R.id.beisongMode);
        texttime = (TextView) view.findViewById(R.id.beisongtime);
        q = (TextView) view.findViewById(R.id.Question);
        a = (TextView) view.findViewById(R.id.Answer);
        level = (TextView) view.findViewById(R.id.beisonglevel);
        memory_1 = (Button) view.findViewById(R.id.memory_1); memory_2 = (Button) view.findViewById(R.id.memory_2);
        memory_3 = (Button) view.findViewById(R.id.memory_3);
        nextwordbutton = (Button) view.findViewById(R.id.nextwordbutton);
        memory_1.setOnClickListener(new memorylistener_1()); memory_1.setOnTouchListener(new memorylistener_1());
        memory_2.setOnClickListener(new memorylistener_2()); memory_2.setOnTouchListener(new memorylistener_2());
        memory_3.setOnClickListener(new memorylistener_3()); memory_3.setOnTouchListener(new memorylistener_3());
        nextwordbutton.setOnClickListener(new nextwordbuttonlistener());
        beisongbar = (ProgressBar) view.findViewById(R.id.beisongprogress);
        selectlevel = (TextView) view.findViewById(R.id.selectlevel);
    }

    //获取并分析排序数据，并获得这次任务需要提问的单词
    public void analysedata() {
        String input = " ", input2 = "";
        try {
            input = read(new FileInputStream(learnfile));
            input2 = read(new FileInputStream(timefile));
        } catch (IOException e) {
            e.printStackTrace();
        }
        ndata = transtodata(input); trantotime(input2);
        for(int i = 0; i < ndata; i++) data[i].calt = data[i].time + getleveltime(data[i].level);
        quickSort(data, 0, ndata-1);
        newn = (int)getnumber(getArguments().get("number")+"", 0, 1); //获得新词
        int i = 0; Qn = 0;
        for(; i < ndata; i++) if(data[i].calt < gettime()) Q[Qn++] = i; else break;
        for(int j = ndata-1; j >= i && newn > 0; j--, newn--)  Q[Qn++] = j;
    }


    //实时保存数据
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
    public void savecatdata(int lv, long t) {
        try {
            if(!learncatfile.exists()) learncatfile.createNewFile();
            String output = read(new FileInputStream(learncatfile));
            if(output.length() > 66666) return;
            output = output + " " + lv + " " + t + " ";
            write(output, learncatfile);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    //获得队列里下一个最小的时间，返回的是在Q中的下标  并统计当前进度
    public int getnext(int[] f, int n) {
        long tmin = (long)1e15;
        int k = -1;
        ggn = ggkn = 0;
        for(int i = 0; i < n; i++){
            if(data[f[i]].level <= 4) {
                ggn++;
                if (tmin > data[f[i]].time + getleveltime(data[f[i]].level)) {
                    tmin = data[f[i]].time + getleveltime(data[f[i]].level);
                    k = i;
                }
            }
            if(data[f[i]].level > 1) ggkn++;
        }
        return k;
    }

    //日期格式
    String dateform(long t) {
        StringBuffer temp = new StringBuffer();
        //3600 1h   86400 1d
        int tt = 0;
        temp.append("时间间隔：");
        if(t > 86400)
        {
            temp.append(t/86400+"天");
            t = t%86400;
            tt++;
        }
        if(t > 3600)
        {
            temp.append(t/3600+"小时");
            t = t%3600;
            tt++;
            if(tt > 1) return temp.toString();
        }
        if(t > 60)
        {
            temp.append(t/60+"分钟");
            t = t%60;
            tt++;
            if(tt > 1) return temp.toString();
        }
        temp.append(t+"秒");
        return temp.toString();

    }
    public float textsize(String str) {
        if(str.length() <= 10) return 30;
        else if(str.length() <= 30) return 25;
        else if(str.length() <= 50) return 20;
        return 15;
    }

    public String delendl(String str) {
        StringBuffer temp = new StringBuffer();
        for(int i = 0; i < str.length(); i++)
            if(str.charAt(i) != '\n') temp.append(str.charAt(i));
        return temp.toString();
    }

    //ty = 1 提问   ty = 2 答案
    public void datashow(int i, int ty) {
        if(ty == 1)  //初始化对i的提问页面
        {
            q.setTextSize(textsize(data[i].ques)); q.setText(delendl(data[i].ques));
            a.setText("");
            level.setText(("lv:"+data[i].level));
            if(data[i].flag == 0) texttime.setText("新知识");
            else texttime.setText((dateform(gettime() - data[i].time)+"  次数:"+data[i].val));
        } else
        {
            q.setTextSize(textsize(data[i].ques)); q.setText(delendl(data[i].ques));
            a.setText(data[i].ans);
            level.setText(("lv:"+data[i].level));
            if(data[i].flag == 0) texttime.setText("新知识");
            else texttime.setText((dateform(gettime() - data[i].time)+"  次数:"+data[i].val));
        }
    }
    //按钮默认样式
    public void buttonbackground_default() {
        memory_1.setBackgroundResource(R.drawable.notpressedcathand);
        memory_2.setBackgroundResource(R.drawable.notpressedcathand);
        memory_3.setBackgroundResource(R.drawable.notpressedcathand);
        selectlevel.setText("");
    }

    public void nextwordtab() {
        //learnmode 0 -> 鉴定  1 -> 巩固
        int i = Q[nowx];
        if(data[i].flag == 2) savecatdata(data[i].level, gettime() - data[i].begintime);
        savedata();

        if(nowx == Qn-1) {
            learnmode = 1;
            Q = QQ; Qn = QQn;
            mode.setText(("巩固模式：  进度 0/" + Qn));
            beisongbar.setMax(Qn);
        }
        if(learnmode == 0){
            nowx++; i = Q[nowx];
            mode.setText(("鉴定模式:  进度 " + nowx + "/" + Qn));
            beisongbar.setProgress(nowx);
            datashow(i, 1);
        }
        if(learnmode == 1){
            nowx = getnext(Q, Qn);
            if(nowx == -1) { listener.BFtoFF(learndata); return; }
            mode.setText(("巩固模式:  进度 " + (Qn-ggn) + "/" + ggkn + "/" + Qn));
            beisongbar.setProgress(Qn-ggn);
            beisongbar.setSecondaryProgress(ggkn);
            i = Q[nowx];
            datashow(i, 1);
        }
    }

    public void nowwordtab() {
        memory_1.setVisibility(View.GONE);
        memory_2.setVisibility(View.GONE);
        memory_3.setVisibility(View.GONE);
        nextwordbutton.setVisibility(View.VISIBLE);
        datashow(Q[nowx], 2);
    }

    public class nextwordbuttonlistener implements View.OnClickListener{
        @Override
        public void onClick(View view) {
            nextwordtab();
            buttonbackground_default();
            memory_1.setVisibility(View.VISIBLE);
            memory_2.setVisibility(View.VISIBLE);
            memory_3.setVisibility(View.VISIBLE);
            nextwordbutton.setVisibility(View.GONE);
        }
    }

    public class memorylistener_1 implements View.OnClickListener, View.OnTouchListener{
        @Override
        public void onClick(View view) {
            int i = Q[nowx];
            data[i].level = 1;
            if(learnmode == 0) QQ[QQn++] = i;
            data[i].val++;  data[i].time = gettime();
            if(data[i].flag == 0){
                data[i].begintime = gettime();
                data[i].flag = 2;
            }
            nowwordtab();
        }
        @Override
        public boolean onTouch(View view, MotionEvent motionEvent) {
            datashow(Q[nowx], 1);
            switch(motionEvent.getAction()) {
                case MotionEvent.ACTION_DOWN:
                    memory_1.setBackgroundResource(R.drawable.onecathand);
                    selectlevel.setText("陌生"); selectlevel.setTextColor(Color.GRAY);
                    break;
                case MotionEvent.ACTION_UP: buttonbackground_default(); break;
            }
            return false;
        }
    }
    public class memorylistener_2 implements View.OnClickListener, View.OnTouchListener{
        @Override
        public void onClick(View view) {
            int i = Q[nowx];
            if(learnmode == 0) {
                if (data[i].level >= 5 && data[i].level <= 9) data[i].level++;
                else if (data[i].level < 5) data[i].level = 5;
                else if (data[i].level >= 10) data[i].level--;
            } else{
                data[i].level += 2;
                data[i].level = min(data[i].level, 5);
            }
            if(data[i].flag == 0) {
                data[i].flag = 1;
                data[i].level = 9;
            }
            data[i].val++;  data[i].time = gettime();
            nextwordtab();
        }
        @Override
        public boolean onTouch(View view, MotionEvent motionEvent) {
            switch (motionEvent.getAction()) {
                case MotionEvent.ACTION_DOWN:
                    memory_1.setBackgroundResource(R.drawable.twocathand);
                    memory_2.setBackgroundResource(R.drawable.twocathand);
                    datashow(Q[nowx], 2);
                    selectlevel.setText("比较熟悉"); selectlevel.setTextColor(Color.parseColor("#FFA500"));
                    break;
                case MotionEvent.ACTION_UP: buttonbackground_default(); break;
            }
            return false;
        }
    }
    public class memorylistener_3 implements View.OnClickListener, View.OnTouchListener{
        @Override
        public void onClick(View view) {
            int i = Q[nowx];
            if(learnmode == 0) {
                if (data[i].level >= 10 && data[i].level <= 14) data[i].level++;
                else if (data[i].level <= 9) data[i].level = 10;
            } else {
                data[i].level += 3;
                data[i].level = min(data[i].level, 5);
            }
            if(data[i].flag == 0) {
                data[i].flag = 1;
                data[i].level = 14;
            }
            data[i].val++;  data[i].time = gettime();
            nextwordtab();
        }
        @Override
        public boolean onTouch(View view, MotionEvent motionEvent) {
            switch(motionEvent.getAction()) {
               case MotionEvent.ACTION_DOWN:
                   memory_1.setBackgroundResource(R.drawable.threecathand);
                   memory_2.setBackgroundResource(R.drawable.threecathand);
                   memory_3.setBackgroundResource(R.drawable.threecathand);
                   selectlevel.setText("非常熟悉"); selectlevel.setTextColor(Color.parseColor("#FA8072"));
                   datashow(Q[nowx], 2);
                   break;
                case MotionEvent.ACTION_UP: buttonbackground_default();break;
            }
            return false;
        }
    }

    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.beisong, container, false);

        learndata = getArguments().get("data")+"";
        learnfile = new File(ESD + learndata + ".txt");
        learncatfile = new File(ESD + learndata + "cat.txt");
        timefile = new File(ESD + learndata + "time.txt");
        layoutinit();
        buttonbackground_default();
        analysedata();
        datashow(Q[0], 1);
        mode.setText(("鉴定模式:  进度 " + nowx + "/" + Qn)); mode.setTextColor(Color.BLACK);
        beisongbar.setMax(Qn);
        memory_1.setVisibility(View.VISIBLE);
        memory_2.setVisibility(View.VISIBLE);
        memory_3.setVisibility(View.VISIBLE);
        nextwordbutton.setVisibility(View.GONE);

        return view;
    }

    public void onClick(View view) {
        /*
        int i = Q[nowx];
        if(learnmode == 1 && view.getId() == R.id.memory_4)  //点击下一个并展示新的单词
        {
            if(typemode == 2)   //巩固模式
            {
                nowx = getnext(Q, Qn); //获取下次提问时间最小的单词
                if(nowx == -1) { listener.BFtoFF(learndata); return; }
                datashow(Q[nowx], 1);
            } else {    //新词模式
                nowx++;
                if(nowx >= Qn) //转化为巩固模式
                {
                    Qn = QQn; typemode = 2;
                    if(Qn == 0) { listener.BFtoFF(learndata); return; }
                    Q = QQ; nowx = getnext(Q, Qn);
                    datashow(Q[nowx], 1);
                    mode.setText("巩固");
                } else {
                    datashow(Q[nowx], 1);
                }
            }
            layouttab(0);
            return;
        }
        if(typemode == 1)  //新词模式
        {
            switch(view.getId()) {
                case R.id.memory_1:
                    data[i].level = 1;
                    if(data[i].flag == 0)   //完全陌生的单词设为2，用于统计数据
                    {
                        data[i].flag = 2;
                        data[i].begintime = gettime();
                    }
                    QQ[QQn++] = i;
                    break;
                case R.id.memory_2:
                    data[i].level = max(5, data[i].level);
                    if(data[i].level <= 10) data[i].level++;
                    else data[i].level = 6;
                    QQ[QQn++] = i;
                    break;
                case R.id.memory_3:
                    data[i].level = max(10, data[i].level);
                    if(data[i].level <= 15) data[i].level++;
                    break;
                case R.id.memory_4:
                    data[i].level = max(15, data[i].level);
                    data[i].level++;
                    data[i].level = min(20, data[i].level);
                    break;
            }
        } else      //强化模式
        {
            switch(view.getId()) {
                case R.id.memory_1:
                    if(data[i].level > 5) data[i].level--;
                    break;
                case R.id.memory_2:
                    break;
                case R.id.memory_3:
                    data[i].level = max(5, data[i].level);
                    data[i].level++;
                    break;
                case R.id.memory_4:
                    data[i].level = max(10, data[i].level);
                    data[i].level++;
                    data[i].level = min(20, data[i].level);
                    break;
            }
        }
        if(data[i].flag == 0) data[i].flag = 1;
        if(data[i].flag == 2) savecatdata(data[i].level, gettime() - data[i].begintime);
        data[i].val++;
        data[i].time = gettime();
        layouttab(1);
        datashow(i, 2);
        savedata(learnfile);*/
    }
}
