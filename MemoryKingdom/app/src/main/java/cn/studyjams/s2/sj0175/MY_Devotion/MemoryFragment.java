package cn.studyjams.s2.sj0175.MY_Devotion;

import android.app.Fragment;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import lecho.lib.hellocharts.model.Axis;
import lecho.lib.hellocharts.model.AxisValue;
import lecho.lib.hellocharts.model.Line;
import lecho.lib.hellocharts.model.LineChartData;
import lecho.lib.hellocharts.model.PointValue;
import lecho.lib.hellocharts.model.Viewport;
import lecho.lib.hellocharts.view.LineChartView;

/**
 * Created by sao on 2017/3/3.
 */

public class MemoryFragment extends Fragment {
    /*
            1min 15min 30min 1h
            12h 1d 3d 7d
            15d 30d 60d 123d 356d
    */
    long[] time_table = {60, 900, 1800, 3600, 43200, 86400, 259200,
            604800, 1296000, 2592000, 5184000, 10627200, 30758400};
    int ntable = 13;
    int[] tablen = new int[13];
    double[] tabledata = new double[13], tablelv = new double[13];
    File learnfile, learncatfile; String learndata;
    public class Data{
        public String ques, ans;
        public int flag, level, val;
        public long time, calt, begintime;
        //level lv  val--记忆所用次数   time--上次记忆的时间
    }
    Data[] data = new Data[2017], newcatdata = new Data[2017];
    int newn;
    int[][] levelstack = new int[21][222];
    int[] lvstacktop = new int[21];
    public void stackadd(int x, int v) {
        levelstack[x][lvstacktop[x]++] = v;
    }
    public int stackpop(int x) {
        return levelstack[x][--lvstacktop[x]];
    }



    String ESD =  Environment.getExternalStorageDirectory().getPath()+"/MemoryPalace/";
    View view;

    public interface Mylistener {
        public void MFtoSF(String str);
    }
    public Mylistener listener;

    public int max(int a, int b) { return a < b ? b : a; }
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
    //ty = 1 返回答案， ty = 0返回下一个数字的出现位置  已更新--找不到数返回length
    public long getnumber(String str, int x, int ty) {
        long ans = 0, y = -1;
        boolean f = false;
        for(int i = x; i < str.length(); i++)
        {
            char ch = str.charAt(i);
            if(f && (ch < '0' || ch > '9')) break;
            if(ch < '0' || ch > '9') continue;
            ans = ans*10 + ch - '0';
            y = i; f = true;
        }
        if(ty == 1) return (y == -1) ? -1 : ans;
        return (y == -1) ? str.length() : y+1;
    }
    public void transtocatdata(String str){
        int n = 0, x = 0;
        while(x < str.length() && getnumber(str, x, 1) != -1){
            if(data[n] == null) data[n] = new Data();
            data[n].level = (int)getnumber(str, x, 1); x = (int)getnumber(str, x, 0);
            data[n++].time = getnumber(str, x, 1); x = (int)getnumber(str, x, 0);
        }
        for(int i = 0; i < 20; i++) lvstacktop[i] = 0;
        for(int i = 0; i < n; i++) {
            int lv = data[i].level;
            if (lvstacktop[lv-1] < 100) stackadd(lv-1, i);
        }
    }
    public void write(String output, File file) throws IOException {
        FileOutputStream fis = new FileOutputStream(file);
        byte[] words = output.getBytes();
        fis.write(words);
        fis.close();
    }
    public void rewritecat(File file) {
        StringBuffer temp = new StringBuffer();
        for(int i = 0; i < newn; i++) {
            temp.append(newcatdata[i].level); temp.append(" ");
            temp.append(newcatdata[i].time); temp.append(" ");
        }
        try {
            write(temp.toString(), file);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    public int atwhichtime(long t) {
        for(int i = 0; i < 13; i++)
            if(t <= time_table[i])
                return i;
        return 12;
    }
    public void transtotabledata(){
        for(int i = 0; i < ntable; i++) { tablen[i] = 0; tabledata[i] = 0; tablelv[i] = 0; }
        for(int i = 0; i < 20; i++)
        {
            while(lvstacktop[i] > 0)
            {
                int x = stackpop(i);
                if(data[x].time == 0) continue;
                int t = atwhichtime(data[x].time);
                tablen[t]++;
                tabledata[t] += data[x].time;
                tablelv[t] += data[x].level;
                newcatdata[newn++] = data[x];
            }
        }
        for(int i = 0; i < ntable; i++)
        {
            if(tablen[i] == 0) continue;
            tabledata[i] /= tablen[i];
            tablelv[i] /= tablen[i];
        }
    }
    public void drawline() {
        String input = "";
        if(!learncatfile.exists())
        {
            try {
                learncatfile.createNewFile();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        try {
            input = read(new FileInputStream(learncatfile));
        } catch (IOException e) {
            e.printStackTrace();
        }

        transtocatdata(input); newn = 0;
        transtotabledata();
        rewritecat(learncatfile);
        List<PointValue> tpoint = new ArrayList<PointValue>();
        tpoint.add(new PointValue(-1, 1));
        for(int i = 0; i < ntable; i++)
        {
            if(tablen[i] == 0) continue;
            tpoint.add(new PointValue((float)(i+tabledata[i]/time_table[i]), (float)tablelv[i]));
            Log.i("wtf", (float)i+tabledata[i]/time_table[i]+"");
        }
        Line line = new Line(tpoint).setColor(Color.parseColor("#FFCD41"));
        List<Line> lines = new ArrayList<>();
        line.setCubic(true);
        LineChartView linechart = (LineChartView) view.findViewById(R.id.linechart);
        lines.add(line);
        LineChartData linedata = new LineChartData();
        linedata.setLines(lines);
        /*
            1min 15min 30min 1h
            12h 1d 3d 7d
            15d 30d 60d 123d 356d
        */
        List<AxisValue> axisx = new ArrayList<>();
        axisx.add(new AxisValue(1).setLabel("1m"));
        axisx.add(new AxisValue(2).setLabel("15m"));
        axisx.add(new AxisValue(3).setLabel("30m"));
        axisx.add(new AxisValue(4).setLabel("1h"));
        axisx.add(new AxisValue(5).setLabel("12h"));
        axisx.add(new AxisValue(6).setLabel("1d"));
        axisx.add(new AxisValue(7).setLabel("3d"));
        axisx.add(new AxisValue(8).setLabel("7d"));
        axisx.add(new AxisValue(9).setLabel("15d"));
        axisx.add(new AxisValue(10).setLabel("30d"));
        axisx.add(new AxisValue(11).setLabel("60d"));
        axisx.add(new AxisValue(12).setLabel("123d"));
        axisx.add(new AxisValue(13).setLabel("356d"));

        Axis axisX = new Axis();
        axisX.setMaxLabelChars(1);
        axisX.setValues(axisx);
        axisX.setHasLines(true);
        axisX.setName("记忆时间");
        linedata.setAxisXBottom(axisX);

        Axis axisY = new Axis();
        axisY.setName("记忆等级");
        linedata.setAxisYLeft(axisY);

        linechart.setLineChartData(linedata);

        Viewport v = new Viewport(linechart.getMaximumViewport());
        v.left = (float)-1; v.right= 10;
        linechart.setCurrentViewport(v);
    }

    public class Settimelistener implements View.OnClickListener {
        public void onClick(View view) {
            listener.MFtoSF(learndata);
        }
    }

    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragmentmemory, container, false);
        listener = (Mylistener) getActivity();
        String input = " ";
        learndata = getArguments().get("data")+"";
        learncatfile = new File(ESD + learndata + "cat.txt");
        drawline();
        Button tosf = (Button) view.findViewById(R.id.MFtoSF);
        tosf.setOnClickListener(new Settimelistener());
        return view;
    }
}


 /*
    public boolean comparedata(Data a, Data b)
    {
        // <=
        if(a.time - a.begintime <= b.time - b.begintime) return false;
        if(a.flag == 2) return false;

        //>=
        if(a.time - a.begintime >= b.time - b.begintime) return true;
        if(b.flag == 2) return true;

        return true;
    }
    public void quicksort(Data[] a, int l, int r)
    {
        int ll = l, rr = r;
        boolean flag = true;
        Data base = a[ll];
        while(l < r)
        {
            while(l < r && (comparedata(a[r], base)))
            {
                r--;
                flag = false;
            }
            a[l] = a[r];
            while(l < r && !comparedata(a[l], base))
            {
                l++;
                flag = false;
            }
            a[r] = a[l];
        }
        a[l] = base;
        if(!flag)
        {
            quicksort(a, ll, l-1);
            quicksort(a, l+1, rr);
        }
    }
    */