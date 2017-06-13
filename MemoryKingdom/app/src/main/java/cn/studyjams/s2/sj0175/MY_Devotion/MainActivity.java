package cn.studyjams.s2.sj0175.MY_Devotion;

import android.app.AlertDialog;
import android.app.FragmentTransaction;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import  com.google.firebase.analytics.FirebaseAnalytics;
import java.util.Stack;

public class MainActivity extends AppCompatActivity implements CountryFragment.Mylistener,
        BeisongFragment.Mylistener, FinishFragment.Mylistener, LearnFragment.Mylistener,
        LoadFragment.Mylistener, MemoryFragment.Mylistener, LoadLoadFragment.Mylistener {
    private FirebaseAnalytics mFirebaseAnalytics;
    public void onBackPressed() {
        if (getFragmentManager().getBackStackEntryCount() > 0) {
            getFragmentManager().popBackStack();
            stack.pop();
            changelayout(stack.peek());
        } else {
            super.onBackPressed();
        }
    }
    TextView titletext;
    ImageButton helper;
    Toolbar toolbar;
    String helpword = "";
    String[] helpwordarr = new String[8];
    Stack<Integer> stack = new Stack<Integer>();
    public class helperlistener implements View.OnClickListener {
        public void onClick(View view) {
            new AlertDialog.Builder(MainActivity.this).setTitle("Cat Helper   _(:з」∠)_ ")//设置对话框标题
                    .setMessage(helpword)//设置显示的内容
                    .setNegativeButton("返回", new DialogInterface.OnClickListener() {//添加返回按钮
                        public void onClick(DialogInterface dialog, int which) {//响应事件
                        }
                    }).show();//在按键响应事件中显示此对话框
        }
    }

    public void helperinit(){
        helpwordarr[0] = "这里是学习列表，在这里你可以选择想要学习的学科。\n" +
                "下面有一只咸鱼，把它喂给猫就可以导入新的知识点~" +
                "\n点击列表中的学科就可以进入相应的学科\n"+
                "导入失败的话，请检查是否开启了该应用读取存储卡的权限0..0";
        helpwordarr[1] = "这里是对当前学科的总览，首先你会发现有2个圆环，它们分别对应了" +
                "知识点学习情况和复习情况，你可以点击圆环来获取相应的信息。\n知识点的圆环会" +
                "告诉你知识点掌握的熟悉程度，复习情况的圆环会告诉你有多少应该进行复习巩固的知识点~" +
                "\n下面可以用滑动来调整新学习多少知识点，任务的信息就是新学习的知识点、和需要复习的知识点。" +
                "\n以及记忆管理也是个神奇的功能";
        helpwordarr[2] = "这里是记忆曲线，它会记录那些完全陌生的知识点是怎样被记忆的，也就是新知识点"+
                "在鉴定时选择陌生后，它以后的记忆情况就会被记录在这里。\n通过这个你可以了解自己大概"+
                "多长时间需要复习一次，需要多长时间来掌握一个陌生的知识点。以及自己的短期记忆和长期记忆"+
                "的情况。";
        helpwordarr[3] = "这里是比较复杂的一个地方，可以对记忆计划进行修改，这里只针对长期记忆" +
                "（即比较熟悉和非常熟悉）。\n每种程度都有5个阶段，对应了一个时间。没错，这个时间就是" +
                "复习的间隔时间，比如比较熟悉的知识点第一阶段15秒就需要复习一次，而非常熟练时一个月可能" +
                "只需要复习一次。\n这些都是可以根据自己的计划和记忆情况自己设定的！以后还会开发出自动" +
                "调整的功能（又挖了一个大坑orz）";
        helpwordarr[4] = "这里是背诵任务界面，会给出问题，下面对应了3个不同选项。\n大体可以这样分:"+
                "陌生->完全不知道；比较熟练->会但是感觉记忆不了太长时间；非常熟练->很久都忘不了的知识。\n"+
                "记忆模式就是先对各知识点进行大体的鉴定，然后会对于不会的知识进行巩固复习。后两个选项"+
                "按住会显示答案，松开就会进入下一个知识点（不用点2下了）。";
        helpwordarr[5] = "天呐，这种地方也要打开help吗？如你所见，这是完成任务的界面。很感谢您能参与测试！"+
                "以后这里会有一些奖励和成就收集的（又挖了一个大坑orz）";
        helpwordarr[6] = "这里是导入数据的地方，先给学科起个名字，然后把知识点文件导入就可以在学习列表见到它们了。\n"+
                "如果没有数据可以先使用测试导入~。知识点文件需要有一定格式，不然记忆猫是分不清哪个是问题，哪个是答案的"+
                "（毕竟是喵星人233）。格式很简单，问题用？，答案用！标识一下就好，文件格式改为txt就可以导入了。"+
                "如: wtf?什么鬼!なに?什么! 这样就可以导入2个知识点了。\n自动去重可以自动去除重复知识点，推荐勾上!";
        helpwordarr[7] = "这里提供一些默认导入的数据，可以进行选择导入";
    }


    public void changelayout(int i) {
        //0:LF 1:CF 2:MF 3:SF 4:BF 5:FF 6:LoadF
        helpword = helpwordarr[i];
    }

    public void switchanime(FragmentTransaction begin) {
        begin.setCustomAnimations(R.animator.fragment_slide_right_in, R.animator.fragment_slide_left_out,
                R.animator.fragment_slide_left_in, R.animator.fragment_slide_right_out);
    }

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mFirebaseAnalytics = FirebaseAnalytics.getInstance(this);
        setContentView(R.layout.activity_main);

        helperinit();
        toolbar=(Toolbar)findViewById(R.id.toolbar);
        toolbar.setTitle("MemoryKingdom");
        setSupportActionBar(toolbar);

        toolbar.setOnMenuItemClickListener(new Toolbar.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                switch (item.getItemId()) {
                    case R.id.action_settings:
                        new AlertDialog.Builder(MainActivity.this).setTitle("Cat Helper   _(:з」∠)_ ")//设置对话框标题
                                .setMessage(helpword)//设置显示的内容
                                .setNegativeButton("返回", new DialogInterface.OnClickListener() {//添加返回按钮
                                    public void onClick(DialogInterface dialog, int which) {//响应事件
                                    }
                                }).show();//在按键响应事件中显示此对话框
                        break;

                    default:
                        break;

                }
                return true;
            }
        });
       // titletext = (TextView) findViewById(R.id.titletext);
        //helper = (ImageButton) findViewById(R.id.helper);
        //helper.setOnClickListener(new helperlistener());
        stack.add(0); changelayout(stack.peek());

        FragmentTransaction begin = getFragmentManager().beginTransaction();
        switchanime(begin);
        begin.replace(R.id.main, new LearnFragment());
        begin.commit();
    }

    @Override
    public void CFtoBF(int x, String str) {
        stack.add(4); changelayout(stack.peek());

        BeisongFragment BF = new BeisongFragment();
        FragmentTransaction begin = getFragmentManager().beginTransaction();
        switchanime(begin);
        begin.addToBackStack(null);


        Bundle bundle = new Bundle();
        bundle.putString("number", x+"");
        bundle.putString("data", str);
        BF.setArguments(bundle);

        begin.replace(R.id.main, BF);
        begin.commit();
    }
    public void CFtoMF(String str) {
        stack.add(2); changelayout(stack.peek());

        FragmentTransaction begin = getFragmentManager().beginTransaction();
        switchanime(begin);
        begin.addToBackStack(null);


        MemoryFragment MF = new MemoryFragment();
        Bundle bundle = new Bundle();
        bundle.putString("data", str);
        MF.setArguments(bundle);

        begin.replace(R.id.main, MF);
        begin.commit();
    }
    //完成任务显示完成页面
    public void BFtoFF(String str) {
        onBackPressed();
        FragmentTransaction begin = getFragmentManager().beginTransaction();
        switchanime(begin);
        stack.add(5); changelayout(stack.peek());

        begin.addToBackStack(null);

        FinishFragment FF = new FinishFragment();
        Bundle bundle = new Bundle();
        bundle.putString("data", str);
        FF.setArguments(bundle);

        begin.replace(R.id.main, FF);
        begin.commit();
    }
    //回到课程页面
    public void FFtoCF(String str) {
        onBackPressed();
    }
    public void LFtoloadF() {
        stack.add(6); changelayout(stack.peek());

        FragmentTransaction begin = getFragmentManager().beginTransaction();
        switchanime(begin);
        begin.addToBackStack(null);
        begin.replace(R.id.main, new LoadFragment());
        begin.commit();
    }

    public void loadFtoLF() {
        onBackPressed();
    }

    public void LFtoCF(String str) {
        stack.add(1); changelayout(stack.peek());

        CountryFragment CF = new CountryFragment();
        FragmentTransaction begin = getFragmentManager().beginTransaction();
        switchanime(begin);
        begin.addToBackStack(null);

        Bundle bundle = new Bundle();
        bundle.putString("data", str);
        CF.setArguments(bundle);

        begin.replace(R.id.main, CF);
        begin.commit();
    }

    public void MFtoSF(String str) {
        stack.add(3); changelayout(stack.peek());

        SettimeFragment SF = new SettimeFragment();
        FragmentTransaction begin = getFragmentManager().beginTransaction();
        switchanime(begin);
        begin.addToBackStack(null);

        Bundle bundle = new Bundle();
        bundle.putString("data", str);
        SF.setArguments(bundle);

        begin.replace(R.id.main, SF);
        begin.commit();
    }
    public void LoadFtoDLF(){
        stack.add(7); changelayout(stack.peek());
        FragmentTransaction begin = getFragmentManager().beginTransaction();
        switchanime(begin);
        begin.addToBackStack(null);
        begin.replace(R.id.main, new LoadLoadFragment());
        begin.commit();
    }
    public boolean onCreateOptionsMenu(Menu menu) {

        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.main, menu);
        return true;
    }
    public void DLFtoLF() {
        onBackPressed();
        onBackPressed();
    }
}
