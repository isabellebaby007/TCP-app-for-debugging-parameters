package com.fdk.debugpara;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.media.MediaPlayer;
import android.media.Ringtone;
import android.media.RingtoneManager;
import android.media.SoundPool;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.StrictMode;
import android.text.method.ScrollingMovementMethod;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

public class LoginActivity extends Activity implements OnClickListener {

    private SoundPool soundPool;
    private Button read_btn,mall_btn;
    private Button startButton; //连接按钮
    private Button  sudu_p0_BTN,sudu_p1_BTN,sudu_i_BTN,enable_on,enable_off, current_p_BTN, current_i_BTN;
    private Button weizhi_p_btn,current_btn,sudu_btn,frequency_btn,wendu_btn;
    private TextView sudu_p_Client, sudu_p1_Client, sudu_i_Client,current_p_Client,current_i_Client;//显示  数据
    private  TextView weizhi_p_Client,current_Client,sudu_Client,frequency_Client,wendu_Client;
    private EditText sudu_p_correct, sudu_p1_correct, sudu_i_correct,adress,current_p_correct,current_i_correct;   //显示  修改的数值
    private EditText weizhi_p_correct,current_correct,sudu_correct,frequency_correct;

    private Context mContext; //上下文
    private boolean isConnecting = false; //初始化
    private int mport;//端口号
    private String ip; //ip地址
    private Thread mThreadClient = null;//客户端线程
    private Socket mSocketClient = null;
    static BufferedReader mBufferedReaderClient = null;
    static PrintWriter mPrintWriterClient = null;
    private String recvMessageClient = "";
    private String mistakeMessage = "";
    private EditText dialog_ip;  //editText ip地址
    private EditText dialog_port;  //editText   端口号
  //  public  boolean duqu_flag=false;
    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        mContext = this;//

       //read_btn.setEnabled(false);//活动创建之后，初始化读取数据按钮使能
        /**********防止TCP通讯报错***************/
       StrictMode.setThreadPolicy(new StrictMode.ThreadPolicy.Builder()
                .detectDiskReads()        //
                .detectDiskWrites()
                .detectNetwork()   // or .detectAll() for all detectable problems
                .penaltyLog()
                .build());
        StrictMode.setVmPolicy(new StrictMode.VmPolicy.Builder()
                .detectLeakedSqlLiteObjects()
                .penaltyLog()
                .penaltyDeath()
                .build());
      InitView();//初始化各个组件
        startButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View arg0) {
                // TODO Auto-generated method stub
              Connection();
            }
        });
    }

  public void InitView(){
        startButton= (Button)findViewById(R.id.StartConnect);//开始连接
        sudu_p0_BTN=(Button)findViewById(R.id.sudu_p0_btn);//确认修改   速度环比例P0
        sudu_p1_BTN=(Button)findViewById(R.id.sudu_p1_btn);//确认修改  速度环比例P1
        sudu_i_BTN=(Button)findViewById(R.id.sudu_i_btn);  //确认修改  速度环积分
        enable_on=(Button)findViewById(R.id.enable_1);
        enable_off=(Button)findViewById(R.id.enable_2);
        current_p_BTN=(Button)findViewById(R.id.current_p_btn);
        current_i_BTN=(Button)findViewById(R.id.current_i_btn);
        weizhi_p_btn=(Button)findViewById(R.id.weizhi_p_btn);
        current_btn=(Button)findViewById(R.id.current_btn);
        sudu_btn=(Button) findViewById(R.id.sudu_btn) ;
        frequency_btn=(Button)findViewById(R.id.frequency_btn);
        wendu_btn=(Button)findViewById(R.id.wendu_btn);
        read_btn=(Button)findViewById(R.id.read_btn);
        mall_btn=(Button)findViewById(R.id.mall_btn);

        read_btn.setOnClickListener(this);
        mall_btn.setOnClickListener(this);
        wendu_btn.setOnClickListener(this);
        sudu_p0_BTN.setOnClickListener(this);//监听  确认修改   事件
        sudu_p1_BTN.setOnClickListener(this);
        sudu_i_BTN.setOnClickListener(this);
        enable_on.setOnClickListener(this);
        enable_off.setOnClickListener(this);
        current_p_BTN.setOnClickListener(this);
        current_i_BTN.setOnClickListener(this);
        weizhi_p_btn.setOnClickListener(this);
        current_btn.setOnClickListener(this);
        sudu_btn.setOnClickListener(this);
        frequency_btn.setOnClickListener(this);
        wendu_btn.setOnClickListener(this);


      sudu_p_Client=(TextView)findViewById(R.id.sudu_p);  //初始化TextView
      sudu_p_Client.setMovementMethod(ScrollingMovementMethod.getInstance());//设置滑动属性
      sudu_p1_Client= (TextView)findViewById(R.id.sudu_p1);
        sudu_p1_Client.setMovementMethod(ScrollingMovementMethod.getInstance());
      sudu_i_Client= (TextView)findViewById(R.id.sudu_i);
        sudu_i_Client.setMovementMethod(ScrollingMovementMethod.getInstance());
      current_p_Client=(TextView)findViewById(R.id.current_p);
       current_p_Client.setMovementMethod(ScrollingMovementMethod.getInstance());
      current_i_Client=(TextView)findViewById(R.id.current_i);
       current_i_Client.setMovementMethod(ScrollingMovementMethod.getInstance());
      weizhi_p_Client=(TextView)findViewById(R.id.weizhi_p);
       weizhi_p_Client.setMovementMethod(ScrollingMovementMethod.getInstance());
      current_Client=(TextView)findViewById(R.id.current);
       current_Client.setMovementMethod(ScrollingMovementMethod.getInstance());
      sudu_Client=(TextView)findViewById(R.id.sudu);
       sudu_Client.setMovementMethod(ScrollingMovementMethod.getInstance());
      frequency_Client=(TextView)findViewById(R.id.frequency);
       frequency_Client.setMovementMethod(ScrollingMovementMethod.getInstance());
      wendu_Client=(TextView)findViewById(R.id.wendu);
        wendu_Client.setMovementMethod(ScrollingMovementMethod.getInstance());


        sudu_p_correct=(EditText)findViewById(R.id.sudu_p_val);// 初始化EditText
        sudu_p1_correct=(EditText)findViewById(R.id.sudu_p1_val);
        sudu_i_correct=(EditText)findViewById(R.id.sudu_i_val);
        adress=(EditText)findViewById(R.id.auto);
        current_p_correct=(EditText)findViewById(R.id.current_p_val);
        current_i_correct=(EditText) findViewById(R.id.current_i_val);
        weizhi_p_correct=(EditText)findViewById(R.id.weizhi_p_val);
        current_correct=(EditText)findViewById(R.id.current_val);
        sudu_correct=(EditText)findViewById(R.id.sudu_val);
        frequency_correct=(EditText)findViewById(R.id.frequency_val);

    }
//读地址命令
private void readData(int flag) {
    try {
        String data=new String();
        char[] tempData=new char[17];
        tempData[0]=':';
        tempData[1]='0';
        tempData[2]=adress.getText().charAt(0);  //获取指定的驱动器地址
        tempData[3]='0';
        tempData[4]='3';
        tempData[5]='1';
        tempData[6]='0';
        switch(flag){
            case 0:
                tempData[7]='9'; //速度环比例系数0的地址
                tempData[8]='2';
                tempData[9]='0';
                tempData[10]='0';
                tempData[11]='0';
                tempData[12]='1';
                tempData[13]='5';
                tempData[14]='9';
                break;
            case 1://速度环比例系数1的地址
                tempData[7]='9';
                tempData[8]='3';
                tempData[9]='0';
                tempData[10]='0';
                tempData[11]='0';
                tempData[12]='1';
                tempData[13]='5';
                tempData[14]='8';
                break;
            case 2://速度环积分系数的地址
                tempData[7]='9';
                tempData[8]='4';
                tempData[9]='0';
                tempData[10]='0';
                tempData[11]='0';
                tempData[12]='1';
                tempData[13]='5';
                tempData[14]='7';
                break;
            case 3://电流环比例系数
                tempData[7]='6';
                tempData[8]='C';
                tempData[9]='0';
                tempData[10]='0';
                tempData[11]='0';
                tempData[12]='1';
                tempData[13]='7';
                tempData[14]='F';
                break;
            case 4://电流环积分系数
                tempData[7]='6';
                tempData[8]='D';
                tempData[9]='0';
                tempData[10]='0';
                tempData[11]='0';
                tempData[12]='1';
                tempData[13]='7';
                tempData[14]='E';
                break;
            case 5:             //位置环比例系数0
                tempData[7]='A';
                tempData[8]='0';
                tempData[9]='0';
                tempData[10]='0';
                tempData[11]='0';
                tempData[12]='1';
                tempData[13]='4';
                tempData[14]='B';
                break;
            case 6:              //实际电流值
                tempData[7]='0';
                tempData[8]='E';
                tempData[9]='0';
                tempData[10]='0';
                tempData[11]='0';
                tempData[12]='1';
                tempData[13]='D';
                tempData[14]='D';
                break;
            case 7:              //速度反馈值
                tempData[7]='0';
                tempData[8]='C';
                tempData[9]='0';
                tempData[10]='0';
                tempData[11]='0';
                tempData[12]='1';
                tempData[13]='D';
                tempData[14]='F';
                break;
            case 8:               //驱动频率
                tempData[7]='5';
                tempData[8]='B';
                tempData[9]='0';
                tempData[10]='0';
                tempData[11]='0';
                tempData[12]='1';
                tempData[13]='9';
                tempData[14]='0';
                break;
       //     case 9://软件版本
         //       tempData[5]='1';
           //     tempData[6]='0';
             //   tempData[7]='1';
               // tempData[8]='A';
                //break;
            //case 10://硬件版本
              //  tempData[5]='1';
                //tempData[6]='0';
                //tempData[7]='1';
                //tempData[8]='B';
                //break;
            case  9://驱动器温度
                tempData[7]='1';
                tempData[8]='0';
                tempData[9]='0';
                tempData[10]='0';
                tempData[11]='0';
                tempData[12]='1';
                tempData[13]='D';
                tempData[14]='B';
                break;
            default:
                break;
        }
       // String stemp=new String();  //声明两位校验码
       // stemp=lrc8(tempData,12);  //计算  两位校验码
      //  tempData[13]=stemp.charAt(0);
        //tempData[14]=stemp.charAt(1);//加上两位校验码
        tempData[15]='\r';
        tempData[16]='\n';
        String c=new String();
        c=String.valueOf(tempData);
        mPrintWriterClient.print(c);//发送字符串给服务器
        mPrintWriterClient.flush();//清空缓存
    }catch (Exception e) {
        e.printStackTrace();
    }
}

    private void  sendStr(int flag){
        try {
            String data=new String();
            String s=new String();
            int  a;
            char[] tempData=new char[17];
            tempData[0]=':';
            tempData[1]='0';
            tempData[2]=adress.getText().charAt(0);
            //tempData[2]=adress.getText().toString();  //获取指定的地址
            tempData[3]='0';
            tempData[4]='6';
            switch(flag){
                case 0:
                    tempData[5]='1';  //速度环比例系数0的地址
                    tempData[6]='0';
                    tempData[7]='9';
                    tempData[8]='2';
                    data = sudu_p_correct.getText().toString();
                    a=Integer.parseInt(data);
                    s=Integer.toHexString(a).toUpperCase();
                    break;
                case 1:
                    tempData[5]='1';   //速度环比例系数1的地址
                    tempData[6]='0';
                    tempData[7]='9';
                    tempData[8]='3';
                    data=sudu_p1_correct.getText().toString();
                    a=Integer.parseInt(data);
                    s=Integer.toHexString(a).toUpperCase();
                    break;
                case 2:
                    tempData[5]='1';  //速度环积分系数的地址
                    tempData[6]='0';
                    tempData[7]='9';
                    tempData[8]='4';
                     data=sudu_i_correct.getText().toString();
                    a=Integer.parseInt(data);
                    s=Integer.toHexString(a).toUpperCase();
                    break;
                case 3:
                    tempData[5]='1';  //开使能
                    tempData[6]='0';
                    tempData[7]='6';
                    tempData[8]='7';
                    data="3";
                    a=Integer.parseInt(data);
                    s=Integer.toHexString(a).toUpperCase();
                    break;
                case 4:
                    tempData[5]='1';  //关使能
                    tempData[6]='0';
                    tempData[7]='6';
                    tempData[8]='7';
                    data="4";
                    a=Integer.parseInt(data);
                    s=Integer.toHexString(a).toUpperCase();
                    break;
                case 5:
                    tempData[5]='1';  //电流环比例系数
                    tempData[6]='0';
                    tempData[7]='6';
                    tempData[8]='C';
                    data=current_p_correct.getText().toString();
                    a=Integer.parseInt(data);
                    s=Integer.toHexString(a).toUpperCase().toUpperCase();
                    break;
                case 6:
                    tempData[5]='1';  //电流环积分系数
                    tempData[6]='0';
                    tempData[7]='6';
                    tempData[8]='D';
                    data=current_i_correct.getText().toString();
                    a=Integer.parseInt(data);
                    s=Integer.toHexString(a).toUpperCase();
                    break;
                case 7://位置环比例P0
                    tempData[5]='1';
                    tempData[6]='0';
                    tempData[7]='A';
                    tempData[8]='0';
                    data=weizhi_p_correct.getText().toString();
                    a=Integer.parseInt(data);
                    s=Integer.toHexString(a).toUpperCase();
                    break;
                case 8: //电流给定
                    tempData[5]='1';
                    tempData[6]='0';
                    tempData[7]='0';
                    tempData[8]='F';
                    data=current_correct.getText().toString();
                    a=Integer.parseInt(data);
                    s=Integer.toHexString(a).toUpperCase();
                    break;
                case 9://速度给定
                    tempData[5]='1';
                    tempData[6]='0';
                    tempData[7]='0';
                    tempData[8]='D';
                    data=sudu_correct.getText().toString();
                    a=Integer.parseInt(data);
                    s=Integer.toHexString(a).toUpperCase();
                    break;
                case 10:
                    tempData[5]='1';
                    tempData[6]='0';
                    tempData[7]='5';
                    tempData[8]='B';
                    data=frequency_correct.getText().toString();
                    a=Integer.parseInt(data);
                    s=Integer.toHexString(a).toUpperCase();
                    break;
                    default:
                    break;
            }
            switch (s.length())
            {
                case 1:
                    tempData[9]='0';
                    tempData[10]='0';
                    tempData[11]='0';
                    tempData[12]= s.charAt(0);
                    break;
                case 2:
                    tempData[9]='0';
                    tempData[10]='0';
                    tempData[11]=s.charAt(0);
                    tempData[12]=s.charAt(1);
                    break;
                case 3:
                    tempData[9]='0';
                    tempData[10]=s.charAt(0);
                    tempData[11]=s.charAt(1);
                    tempData[12]=s.charAt(2);
                    break;
                case 4:
                    tempData[9]=s.charAt(0);
                    tempData[10]=s.charAt(1);
                    tempData[11]=s.charAt(2);
                    tempData[12]=s.charAt(3);
                    break;
                default:
                    break;
            }
            String stemp=new String();  //声明两位校验码
            stemp=lrc8(tempData,12);  //计算  两位校验码
            tempData[13]=stemp.charAt(0);
            tempData[14]=stemp.charAt(1);
           tempData[15]='\r';
            tempData[16]='\n';
            String c=new String();
            c=String.valueOf(tempData);
            mPrintWriterClient.print(c);//发送字符串给服务器
            mPrintWriterClient.flush();//清空缓存
        }catch (Exception e) {
            e.printStackTrace();
        }
    }

    //LRC校验函数,校验  写单个寄存器指令  12位   返回两位校验码
    private  String lrc8(char[] RSDATA,int N)
    {
       int lrc=0;
       int b=0;
        String a=new String();
        String c=new String();
        String result="";
        String d=new String();
        String thisBinary=new String();
        String thisBinary2=new String();
       try{
            for(int i=0;i<N/2;i++) //i<6
            {
                  a= String.valueOf(RSDATA[i*2+1])+String.valueOf(RSDATA[i*2+2]);//字符转字符串
                  lrc+=Integer.parseInt(a,16);//十六进制表示成十进制进行累加
            }
           if(Integer.toHexString(lrc).length()==2) {//转成十六进制字符串长度
                   thisBinary=Integer.toBinaryString(lrc);//十进制转成二进制字符串形式
                 System.out.println("thisBinary"+thisBinary);
                   for (int i=0;i<thisBinary.length();i++)//
                   {
                        if (thisBinary.charAt(i)=='1')
                            result+="0";
                       else{
                            result+="1";
                        }
                   }
                   System.out.println("result的值:"+result);
                   b=Integer.parseInt(result,2)+1;  //二进制字符串转成十进制数据+1
                   System.out.print("b的值为:"+b);
                   d = Integer.toHexString(b).toUpperCase();//十进制转十六进制字符串  大写
           }
           else
           {
               System.out.println("和为3个");
                thisBinary=Integer.toBinaryString(lrc);
               System.out.println("thisBinary"+thisBinary);
               thisBinary2=thisBinary.substring(thisBinary.length()-8,thisBinary.length());
               System.out.println(thisBinary2);
               for (int i=0;i<thisBinary2.length();i++)
               {
                   if (thisBinary2.charAt(i)=='1')
                       result+="0";
                   else{
                       result+="1";
                   }
               }
               System.out.println("result的值："+result);
               b=Integer.parseInt(result,2)+1;
               System.out.print("b为"+b);
               d = Integer.toHexString(b).toUpperCase();//十进制转十六进制字符串  转成大写
           }
       }catch (Exception e)
       {
           System.out.println("LRC校验异常");
        }
          return d;
    }
     /** 连接服务器*/
    public void Connection(){
        try
        {
            if (isConnecting)
            {
                isConnecting = false;
                try {
                    if(mSocketClient!=null)
                    {
                        mSocketClient.close();
                        mSocketClient = null;
                        mPrintWriterClient.close();
                        mPrintWriterClient = null;
                    }
                } catch (IOException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }
                mThreadClient.interrupt();
                startButton.setText("连接");//连接按钮
            }
            else  //如果没有连接上
            {
                isConnecting = true;
                DialogIpPort();	  //端口和IP地址输入对话框
            }
        }catch(Exception e)
        {
            Toast.makeText(mContext, "网络连接异常：" + e.getMessage(), Toast.LENGTH_SHORT).show();
        }
    }
    //播放按键提示音
public void musicplay(){
    Uri uri= RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
    Ringtone rt=RingtoneManager.getRingtone(getApplicationContext(),uri);
    rt.play();
}
    public void onClick(View v)
    {
       // Intent intent=null;
       int flag=0;
        MediaPlayer mediaPlayer01;

       if(isConnecting&&mSocketClient!=null){
            try{
                switch(v.getId()){
                  case R.id.wendu_btn:
                     mediaPlayer01=MediaPlayer.create(LoginActivity.this,R.raw.ailisi);
                      mediaPlayer01.start();
                        break;
                  case R.id.read_btn://读取数据
                       readData(0);
                      readData(1);
                        readData(2);
                        readData(3);
                        readData(4);
                        readData(5);
                        readData(6);
                        readData(7);
                        readData(8);
                        readData(9);
                       // readData(10);
                        //readData(11);
                        break;
                    case R.id.mall_btn: //导航到公司商城
                        Intent intent=new Intent(Intent.ACTION_VIEW);
                        intent.setData(Uri.parse("https://glwindcon.1688.com/"));
                        startActivity(intent);
                        break;
                    case R.id.sudu_p0_btn:  //确认修改速度环比例0
                        musicplay();
                        sendStr(0);//发送修改指令
                        break;
                    case R.id.sudu_p1_btn:    //确认修改速度环比例1
                        musicplay();
                        sendStr(1);
                        break;
                    case R.id.sudu_i_btn:  //确认修改速度环积分
                        musicplay();
                        sendStr(2);
                        break;
                    case R.id.enable_1: //开使能
                        musicplay();
                        sendStr(3);
                        break;
                    case R.id.enable_2:  //关使能
                        musicplay();
                        sendStr(4);
                        break;
                    case R.id.current_p_btn:  //电流环比例
                        musicplay();
                        sendStr(5);
                        break;
                    case R.id.current_i_btn:  //电流环积分
                        musicplay();
                        sendStr(6);
                        break;
                    case R.id.weizhi_p_btn: //位置环比例P
                        musicplay();
                        sendStr(7);
                        break;
                    case R.id.current_btn:  //电流
                        musicplay();
                        sendStr(8);
                        break;
                    case R.id.sudu_btn:  //速度
                        musicplay();
                        sendStr(9);
                        break;
                    case R.id.frequency_btn://频率
                        musicplay();
                        sendStr(10);
                    default:
                        break;
                }
            }catch(Exception e){
                Toast.makeText(mContext, "发送异常：" + e.getMessage(), Toast.LENGTH_SHORT).show();
            }
       }else{
         Toast.makeText(mContext, "没有连接相应的服务器", Toast.LENGTH_SHORT).show();
        }
    }
    /*
     * 端口和IP地址输入对话框，点击连接即开启客户端线程，文字变成断开，点击取消，文字依然是连接
     */
  public void DialogIpPort(){
        LayoutInflater inflater=getLayoutInflater();//寻找XML文件，并实例化
        View login_dialog = inflater.inflate(R.layout.dialog,(ViewGroup)findViewById(R.id.dialog));//
        dialog_ip=(EditText)login_dialog.findViewById(R.id.dialog_ip); //ip输入  关联起来
        dialog_port=(EditText)login_dialog.findViewById(R.id.dialog_port);//端口输入  关联起来
        new AlertDialog.Builder(this).setTitle("请输入正确的IP地址和端口号").setView(login_dialog).
                setPositiveButton("连接",new DialogInterface.OnClickListener() {
                   @Override
                    public void onClick(DialogInterface arg0, int arg1) {
                        // TODO Auto-generated method stub
                        try{
                            ip=dialog_ip.getText().toString();//ip
                            mport=Integer.parseInt(dialog_port.getText().toString());//端口
                            mThreadClient = new Thread(mRunnable);//客户端线程
                            mThreadClient.start();  //开启  客户端线程
                           // read_btn.setEnabled(true);//使能读取数据按钮
                            startButton.setText("断开");	//连接按钮  文字变成“断开”
                        }
                        catch(Exception e){
                            Toast.makeText(mContext, "网络连接异常：" + e.getMessage(), Toast.LENGTH_SHORT).show();//提示消息
                       }
                    }
                }).setNegativeButton("取消",new DialogInterface.OnClickListener() {

            @Override
            public void onClick(DialogInterface arg0, int arg1) {
                // TODO Auto-generated method stub
                startButton.setText("连接");
            }
        }).show();
    }
    //线程:监听服务器发来的消息   客户端线程
  private Runnable	mRunnable	= new Runnable()
    {
        public void run()
        {
            try
            {
                //连接服务器
                mSocketClient = new Socket(ip, mport);
                //取得输入、输出流
                mBufferedReaderClient = new BufferedReader(new InputStreamReader(mSocketClient.getInputStream()));//取得输入流
                mPrintWriterClient = new PrintWriter(mSocketClient.getOutputStream(), true);//取得输出流，  自动刷新
                mistakeMessage = "已经连接server!";   // 消息换行

              Message msg = new Message();
                msg.what = 0;
                mHandler.sendMessage(msg);  //处理消息msg  消息队列
                //break;
            }
            catch (Exception e)
            {
                mistakeMessage = "连接IP异常:" + e.toString() + e.getMessage() + "\n";//消息换行
                Message msg = new Message();
                msg.what = 1;
                mHandler.sendMessage(msg);  //消息队列
                return;
            }
            setRecvMessage();
        }
    };
    /*
     * 接受消息机制
     */
   public void setRecvMessage()
    {
        char[] buffer = new char[256];//256长度得数组
        int count = 0;
        while (isConnecting)
        {
            try
            {
                if((count = mBufferedReaderClient.read(buffer))>0)//如果接收数据长度大于0
                {
                    recvMessageClient = getInfoBuff(buffer, count);//将接收的数据存入数组
                    Message msg = new Message();  //
                    msg.what = 1;
                    mHandler.sendMessage(msg);
                }
            }
            catch (Exception e)
            {
                mistakeMessage = "连接断开:" + e.getMessage()+"\n";  //消息换行
                Message msg = new Message();
                msg.what = 0;
                mHandler.sendMessage(msg);
            }
        }
    }

    /******消息处理队列********/
 Handler mHandler = new Handler()//数据处理机制
    {
        public void handleMessage(Message msg)
        {
            super.handleMessage(msg);
            if(msg.what == 0)
            {
                Toast.makeText(LoginActivity.this,mistakeMessage,Toast.LENGTH_SHORT).show();//刷新消息机制
            }
            else if(msg.what == 1) {
                try {
                    String[] display=new String[15];
                    String[] result = recvMessageClient.split("\\\n");//.split("\\*");接收到的字符串  换行符作为分隔符    放到result数组里
                   //for (int i=0;i<=result.length;i++) {
                     //  char fir=result[i].charAt(4);
                      //if (fir=='3')
                        //{
                         //   display[0]=result[i].substring(7,11);
                          //  display[1]=result[i+1].substring(7,11);
                          //  display[2]=result[i+2].substring(7,11);
                         //   display[3]=result[i+3].substring(7,11);
                         //   display[4]=result[i+4].substring(7,11);
                        //    display[5]=result[i+5].substring(7,11);
                        //    display[6]=result[i+6].substring(7,11);
                        //    display[7]=result[i+7].substring(7,11);
                        //    display[8]=result[i+8].substring(7,11);
                       // }
                    //十六进制字符串转十进制字符串
                      //  String str1=result[0].substring(7,11);
                      //  String str2=result[1].substring(7,11);
                       // String str3=result[2].substring(7,11);
                       // String str4=result[3].substring(7,11);
                       // String str5=result[4].substring(7,11);
                       // String str6=result[5].substring(7,11);
                       // String str7=result[6].substring(7,11);
                       // String str8=result[7].substring(7,11);
                       // String str9=result[8].substring(7,11);
                       // String str10=result[9].substring(7,11);

                       // Integer in1=Integer.valueOf(str1,16);
                      //  Integer  in2=Integer.valueOf(str2,16);
                       // Integer  in3=Integer.valueOf(str3,16);
                      //  Integer  in4=Integer.valueOf(str4,16);
                      //  Integer  in5=Integer.valueOf(str5,16);
                      //  Integer  in6=Integer.valueOf(str6,16);
                      //  Integer  in7=Integer.valueOf(str7,16);
                      // Integer  in8=Integer.valueOf(str8,16);
                       //Integer   in9=Integer.valueOf(str9,16);
                     //  Integer  in10=Integer.valueOf(str10,16);

                  //  sudu_p_Client.setText(in1.toString());
                  //  sudu_p1_Client.setText(in2.toString());
                  //  sudu_i_Client.setText(in3.toString());
                  //  current_p_Client.setText(in4.toString());
                  //  current_i_Client.setText(in5.toString());
                  //  weizhi_p_Client.setText(in6.toString());
                  //  current_Client.setText(in7.toString());
                  //  sudu_Client.setText(in8.toString());
                  //  frequency_Client.setText(in9.toString());
                   // wendu_Client.setText(in10.toString());
                        sudu_p_Client.setText(Integer.toString(Integer.parseInt(result[0].substring(7,11),16)));
                        sudu_p1_Client.setText(Integer.toString(Integer.parseInt(result[1].substring(7,11),16)));
                        sudu_i_Client.setText(Integer.toString(Integer.parseInt(result[2].substring(7,11),16)));
                        current_p_Client.setText(Integer.toString(Integer.parseInt(result[3].substring(7,11),16)));
                        current_i_Client.setText(Integer.toString(Integer.parseInt(result[4].substring(7,11),16)));
                        weizhi_p_Client.setText(Integer.toString(Integer.parseInt(result[5].substring(7,11),16)));
                        current_Client.setText(Integer.toString(Integer.parseInt(result[6].substring(7,11),16)));
                        sudu_Client.setText(Integer.toString(Integer.parseInt(result[7].substring(7,11),16)));
                        frequency_Client.setText(Integer.toString(Integer.parseInt(result[8].substring(7,11),16)));
                        wendu_Client.setText(Integer.toString(Integer.parseInt(result[9].substring(7,11),16)));
                  //  }
              }catch(Exception e)
                {
                    Toast.makeText(LoginActivity.this,"无法获取数据，检查网络是否连接！",Toast.LENGTH_SHORT).show();//刷新消息机制
                }
            }
        }
    };

    private String getInfoBuff(char[] buff, int count)  //buffer数据装进数组
    {
        char[] temp = new char[count];
        for(int i=0; i<count; i++)
        {
            temp[i] = buff[i];
        }
        return new String(temp);
    }
}