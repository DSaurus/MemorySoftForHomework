package cn.studyjams.s2.sj0175.MY_Devotion;

import android.app.Fragment;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Environment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import com.gc.materialdesign.views.ButtonRectangle;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import lecho.lib.hellocharts.listener.PieChartOnValueSelectListener;
import lecho.lib.hellocharts.model.PieChartData;
import lecho.lib.hellocharts.model.SliceValue;
import lecho.lib.hellocharts.view.PieChartView;

/**
 * Created by sao on 2017/2/25.
 */

public class CountryFragment extends Fragment {
    long[] time_1 = new long[4], time_2 = new long[5], time_3 = new long[5];
    int nlearn = 0;
    public class Data{
        public String ques, ans;
        public int flag;
        public int level, val;
        public long time, begintime;
        //flag level val time begintime
    }
    Data[] data = new Data[2017];
    String learndata = null;
    File learnfile, timefile;
    String ESD =  Environment.getExternalStorageDirectory().getPath()+"/MemoryPalace/";
    public int max(int a, int b) { return a < b ? b : a; }
    public float dmax(float a, float b) { return a < b ? b : a; }
    public int min(int a, int b) { return a > b ? b : a; }


    //与activity进行通信
    public Mylistener listener;
    public interface Mylistener {
        public void CFtoBF(int x, String str);
        public void CFtoMF(String str);
    }

    //将output写入文件wtf
    public void write(String output) throws IOException {
        File path = new File("storage/emulated/0/wtf");// TODO: 2017/3/2
        File file = new File(path, "data.txt");
        //if(!file.exists())Toast.makeText(getActivity(), Environment.getExternalStorageDirectory().getPath(), Toast.LENGTH_LONG).show();
        FileOutputStream fis = new FileOutputStream(file);
        byte[] words = output.getBytes();
        fis.write(words);
        fis.close();
    }
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

    //获得知识点等级需要的间隔时间 315
    public long getleveltime(int k) {
        if(k <= 4) return time_1[k-1];
        if(k <= 9) return time_2[k-5];
        if(k <= 14) return time_3[k-10];
        return (long)1e12;
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

    //通过滑动条获取单词量
    TextView barword;
    public class Wordnumlistener implements SeekBar.OnSeekBarChangeListener {

        @Override
        public void onProgressChanged(SeekBar seekBar, int i, boolean b) {
            barword.setText(("今日要背诵新的知识量："+i/10+"个"));
        }

        @Override
        public void onStartTrackingTouch(SeekBar seekBar) {}

        @Override
        public void onStopTrackingTouch(SeekBar seekBar) {}
    }

    //layout布局设置
    View view;

    ButtonRectangle tasks;
    ButtonRectangle memory;
    PieChartView datachartview;
    PieChartData datachart;
    SeekBar seekbar;
    List<SliceValue> datachartval = new ArrayList<>();

    PieChartView newdatachartview;
    PieChartData newdatachart;
    List<SliceValue> newdatachartval = new ArrayList<>();

    public void layoutinit()
    {
        listener = (Mylistener)getActivity();
        tasks = (ButtonRectangle) view.findViewById(R.id.todaytask);
        memory = (ButtonRectangle) view.findViewById(R.id.memoryfunction);
        datachartview = (PieChartView) view.findViewById(R.id.dataanalysechart);
        newdatachartview = (PieChartView) view.findViewById(R.id.newdatalearnchart);
        datachartview.setOnValueTouchListener(new datachartlistener());
        newdatachartview.setOnValueTouchListener(new newdatachartlistener());
    }

    //切换到memoryline页面
    public class Memorylistener implements View.OnClickListener {
        @Override
        public void onClick(View view) {
            listener.CFtoMF(learndata);
        }
    }

    public void piechartdata(int n1, int n2, int n3) {
        datachartval.clear();
        datachartval.add(new SliceValue(dmax((float) 0.0001, (float)n1), Color.GRAY));
        datachartval.add(new SliceValue(dmax((float) 0.0001, (float)n2), Color.parseColor("#FF9900")));
        datachartval.add(new SliceValue(dmax((float) 0.0001, (float)n3), Color.RED));

        datachart = new PieChartData();
        datachart.setHasLabels(true);//显示表情
        datachart.setHasLabelsOnlyForSelected(false);//不用点击显示占的百分比
        datachart.setHasLabelsOutside(false);//占的百分比是否显示在饼图外面
        datachart.setHasCenterCircle(true);//是否是环形显示
        datachart.setValues(datachartval);//填充数据
        datachart.setCenterCircleColor(Color.WHITE);//设置环形中间的颜色
        datachart.setCenterCircleScale(0.5f);//设置环形的大小级别
        datachart.setCenterText1("知识点记忆情况");//环形中间的文字1
        datachart.setCenterText1Color(Color.BLACK);//文字颜色
        datachart.setCenterText1FontSize(8);//文字大小

        datachartview.setPieChartData(datachart);
        datachartview.setValueSelectionEnabled(true);//选择饼图某一块变大
        datachartview.setAlpha(0.9f);//设置透明度
        datachartview.setCircleFillRatio(1f);//设置饼图大小
    }

    public void newpiechartdata(int n1, int n2, int n3) {
        newdatachartval.clear();
        newdatachartval.add(new SliceValue(dmax((float) 0.0001, (float)n1), Color.GRAY));
        newdatachartval.add(new SliceValue(dmax((float) 0.0001, (float)n2), Color.BLUE));
        newdatachartval.add(new SliceValue(dmax((float) 0.0001, (float)n3), Color.parseColor("#FF9900")));

        newdatachart = new PieChartData();
        newdatachart.setHasLabels(true);//显示表情
        newdatachart.setHasLabelsOnlyForSelected(false);//不用点击显示占的百分比
        newdatachart.setHasLabelsOutside(false);//占的百分比是否显示在饼图外面
        newdatachart.setHasCenterCircle(true);//是否是环形显示
        newdatachart.setValues(newdatachartval);//填充数据
        newdatachart.setCenterCircleColor(Color.WHITE);//设置环形中间的颜色
        newdatachart.setCenterCircleScale(0.5f);//设置环形的大小级别
        newdatachart.setCenterText1("复习情况");//环形中间的文字1
        newdatachart.setCenterText1Color(Color.BLACK);//文字颜色
        newdatachart.setCenterText1FontSize(8);//文字大小

        newdatachartview.setPieChartData(newdatachart);
        newdatachartview.setValueSelectionEnabled(true);//选择饼图某一块变大
        newdatachartview.setAlpha(0.9f);//设置透明度
        newdatachartview.setCircleFillRatio(1f);//设置饼图大小
    }

    //分析当前数据  获得学习单词量
    public void analysedata() {
        String input = "", input2 = "";
        try {
            input = read(new FileInputStream(learnfile));
            input2 = read(new FileInputStream(timefile));
        } catch (IOException e) {
            e.printStackTrace();
        }
        int n = transtodata(input), n1, n2, n3, newn; newn = nlearn = n1 = n2 = n3 = 0;
        trantotime(input2);
        for(int i = 0; i < n; i++)
        {
            if(data[i].level <= 4) n1++;
            else if(data[i].level <= 9) n2++;
            else n3++;
            if(data[i].time + getleveltime(data[i].level) < new Date().getTime()/1000) nlearn++;
            if(data[i].flag == 0) newn++;
        }
        barword = (TextView) view.findViewById(R.id.countrybar);
        barword.setText(("今日要背诵新的知识量："+0+"个"));
        seekbar = (SeekBar) view.findViewById(R.id.beisongbar);
        seekbar.setMax(newn*10);
        seekbar.setOnSeekBarChangeListener(new Wordnumlistener());

        piechartdata(n1, n2, n3);
        newpiechartdata(newn, n-newn-nlearn, nlearn);
    }

    //进入新的任务
    public class Taskslistener implements  View.OnClickListener {
        @Override
        public void onClick(View view) {
            int x = (int)getnumber(barword.getText().toString(), 0, 1);
            if(x+nlearn <= 0) { Toast.makeText(getActivity(), "没有知识点可以背呀", Toast.LENGTH_LONG).show(); return; }
            if(listener != null) {
                listener.CFtoBF(x, learndata);
            }
        }
    }
    public class datachartlistener implements PieChartOnValueSelectListener{
        @Override
        public void onValueSelected(int arcIndex, SliceValue value) {
            TextView temp = (TextView) view.findViewById(R.id.datacharttext);
            String str = " ";
            switch (arcIndex)
            {
                case 0: str = "陌生:";break;
                case 1: str = "比较熟悉:";break;
                case 2: str = "非常熟悉:";break;
            }
            temp.setTextColor(value.getColor());
            temp.setText(str + value.getValue());
        }
        @Override
        public void onValueDeselected() {
            TextView temp = (TextView) view.findViewById(R.id.datacharttext);
            temp.setText("");
        }
    }
    public class newdatachartlistener implements PieChartOnValueSelectListener{
        @Override
        public void onValueSelected(int arcIndex, SliceValue value) {
            TextView temp = (TextView) view.findViewById(R.id.newdatacharttext);
            String str = " ";
            switch (arcIndex)
            {
                case 0: str = "新知识点:";break;
                case 1: str = "不需要复习:";break;
                case 2: str = "需要复习:";break;
            }
            temp.setTextColor(value.getColor());
            temp.setText(str + value.getValue());
        }
        @Override
        public void onValueDeselected() {
            TextView temp = (TextView) view.findViewById(R.id.newdatacharttext);
            temp.setText("");
        }
    }

    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragmentcountry, container, false);
        layoutinit();
        learndata = getArguments().get("data")+"";
        learnfile = new File(ESD + learndata +".txt");
        timefile =  new File(ESD + learndata + "time.txt");
        TextView wtf = (TextView) view.findViewById(R.id.textView);
        wtf.setText(learndata);
        analysedata();
        memory.setOnClickListener(new Memorylistener());
        tasks.setOnClickListener(new Taskslistener());
        return view;
    }
}
