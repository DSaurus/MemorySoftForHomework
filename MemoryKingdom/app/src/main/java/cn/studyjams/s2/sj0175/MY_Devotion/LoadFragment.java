package cn.studyjams.s2.sj0175.MY_Devotion;

import android.app.Activity;
import android.app.Fragment;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import com.gc.materialdesign.views.ButtonRectangle;

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by sao on 2017/3/3.
 */

public class LoadFragment extends Fragment{
    View view;
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
    Data[] data = new Data[2017], newdata = new Data[2017];
    String []content = new String[100];
    int nctt;
    Map<String, Integer> map = new HashMap<>();
    EditText txtname;
    TextView loadtxt;
    ButtonRectangle loadbutton, loadconfirm;
    Switch loadtype;
    File location;
    String ESD = Environment.getExternalStorageDirectory().getPath()+"/MemoryPalace/";
    String learndata;
    File learnfile, timefile;

    int newn, ndata;
    public interface Mylistener {
        public void loadFtoLF();
        public void LoadFtoDLF();
    }
    public Mylistener listener;
    public void layoutinit() {
        txtname = (EditText) view.findViewById(R.id.editlearntxt);
        loadbutton = (ButtonRectangle) view.findViewById(R.id.loadbutton);
        loadconfirm = (ButtonRectangle) view.findViewById(R.id.loadconfirm);
        loadtxt = (TextView) view.findViewById(R.id.loadtxt);
        loadtype = (Switch) view.findViewById(R.id.loadtype);
        loadtype.setChecked(true);
    }

    public String read(InputStream fis) throws IOException {
        ByteArrayOutputStream outstream = new ByteArrayOutputStream();
        byte[] buffer = new byte[1024];
        int len = 0;
        while((len = fis.read(buffer)) != -1)
            outstream.write(buffer, 0, len);
        fis.close(); outstream.close();
        return outstream.toString();
    }
    public void write(String output, File file) throws IOException {
        FileOutputStream fis = new FileOutputStream(file);
        byte[] words = output.getBytes();
        fis.write(words);
        fis.close();
    }

    //读取用户提供的文件
    public String readuserload(InputStream fis) throws IOException {
        InputStreamReader tfis = new InputStreamReader(fis, "GB2312");
        BufferedReader bufr = new BufferedReader(tfis);//缓冲
        String line = null;
        StringBuffer ans = new StringBuffer();
        while((line = bufr.readLine())!=null){
            ans.append(line);
        }
        fis.close(); tfis.close(); bufr.close();
        return ans.toString();
    }

    public String numarraytostr(long []a) {
        StringBuffer temp = new StringBuffer();
        for(int i = 0; i < a.length; i++) temp.append(a[i]+" "); temp.append("\n");
        return temp.toString();
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
    //屏蔽空格，从str中读取目录
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
    //添加新学科到目录中，添加时合并重复学科
    public void rewritewhole(String str) {
        File file = new File(ESD + "_learn.txt");
        if(!file.exists()) try {
            file.createNewFile();
        } catch (IOException e) {
            e.printStackTrace();
        }
        try {
            String input = read(new FileInputStream(file));
            transtocontent(input);
            for(int i = 0; i < nctt; i++) {
                if(str.equals(content[i])) return;
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

    //从用户提供的str中读取新数据（并初始化）
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

    public class loadconfirmlistener implements View.OnClickListener {
        public void onClick(View view) {
            //非法操作
            if(txtname.getText().toString().length() == 0)
            {
                Toast.makeText(getActivity(), "学科名是什么啊", Toast.LENGTH_SHORT).show();
                return;
            }
            if(location == null || !location.exists())
            {
                Toast.makeText(getActivity(), "找不到文件怎么破", Toast.LENGTH_SHORT).show();
                return;
            }

            //写入新目录
            learndata = txtname.getText().toString();
            rewritewhole(learndata);

            //读取用户数据
            try {
                String input = readuserload(new FileInputStream(location));
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
            if(loadtype.isChecked())
            {
                try {
                    String input = read(new FileInputStream(learnfile));
                    ndata = transtodata(input);

                } catch (IOException e) {
                    e.printStackTrace();
                }
                for(int i = 0; i < ndata; i++) map.put(data[i].ques, i);
            }

            //写入新数据
            rewritedata();
            listener.loadFtoLF();
        }
    }
    public class loadbuttonlistener implements View.OnClickListener {
        public void onClick(View view) {
            Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
            intent.setType("*/*");  //设置类型，我这里是任意类型，任意后缀的可以这样写。
            intent.addCategory(Intent.CATEGORY_OPENABLE);
            startActivityForResult(intent, 1);
        }
    }
    public static String getRealFilePath(final Context context, final Uri uri ) {
        if ( null == uri ) return null;
        final String scheme = uri.getScheme();
        String data = null;
        if ( scheme == null )
            data = uri.getPath();
        else if ( ContentResolver.SCHEME_FILE.equals( scheme ) ) {
            data = uri.getPath();
        } else if ( ContentResolver.SCHEME_CONTENT.equals( scheme ) ) {
            Cursor cursor = context.getContentResolver().query( uri, new String[] { MediaStore.Images.ImageColumns.DATA }, null, null, null );
            if ( null != cursor ) {
                if ( cursor.moveToFirst() ) {
                    int index = cursor.getColumnIndex( MediaStore.Images.ImageColumns.DATA );
                    if ( index > -1 ) {
                        data = cursor.getString( index );
                    }
                }
                cursor.close();
            }
        }
        return data;
    }
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == Activity.RESULT_OK) {
            if (requestCode == 1) {
                Uri uri = data.getData();
                loadtxt.setText(("文件路径："+getRealFilePath(getActivity(), uri)));
                location = new File(getRealFilePath(getActivity(), uri));
            }
        }
    }

    public class Defaultloadlistener implements View.OnClickListener{
        public void onClick(View view) {
            listener.LoadFtoDLF();
        }
    }

    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragmentload, container, false);
        listener = (Mylistener) getActivity();
        layoutinit();
        loadconfirm.setOnClickListener(new loadconfirmlistener());
        loadbutton.setOnClickListener(new loadbuttonlistener());

        ButtonRectangle defaultload = (ButtonRectangle) view.findViewById(R.id.testloadbt);
        defaultload.setOnClickListener(new Defaultloadlistener());
        return view;
    }
}
