package project.silkwormtester.bleservice;

import android.util.Log;

import java.util.Timer;
import java.util.TimerTask;

/**
 * @Authon Zhilong Zheng
 * @Email zhengzl0715@163.com
 * @Date 14:42 16/4/11
 */
//内部就是一些状态的迁移
public class SilkwormProtocol {
    private static final String TAG = "SilkwormProtocol";

    enum ProtocolState {
        None,
        Hello,
        Hello_Reply,
        M1,
        D1,
        D1_COMPLETE
    }

    //private BluetoothLeService bluetoothLeService; //用来发送数据
    private ProtocolState innerState;
    private SilkwormCallback silkwormCallback;  //回调
    private String sendData;
    private String view;     //表示显示的是哪个界面, 因为需要接收的数据不一样

    //定时器用于重传
    private Timer timer = new Timer();
    private TimerTask timerTask;
    private boolean retransmission = false;

    //private static SilkwormProtocol silkwormProtocol;  //不能使用单例模式

    public SilkwormProtocol () {
        //this.bluetoothLeService = bluetoothLeService;
        innerState = ProtocolState.Hello;
    }

    public void setSilkwormCallback (final SilkwormCallback silkwormCallback) {
        this.silkwormCallback = silkwormCallback;
        retransmission = false;
        timerTask = new TimerTask() {
            @Override
            public void run() {
                if (retransmission) {
                    Log.i(TAG, "true");
                    silkwormCallback.onSendData(sendData);
                    Log.i(TAG, "retransmission");
                }
            }
        };
        timer.schedule(this.timerTask, 0, 2000);  //2000ms一次重传

    }

    //调用该函数,协议开始工作
    public void start() {
        //this.timerTask = null;  //回收原来的task
        if (timerTask == null) {
            timerTask = new TimerTask() {
                @Override
                public void run() {
                    if (retransmission) {
                        silkwormCallback.onSendData(sendData);
                        Log.i(TAG, "retransmission");
                    }
                }
            };
            timer.schedule(this.timerTask, 0, 2000);  //2000ms一次重传
        }
        silkwormCallback.onSendData("hello");
        this.sendData = "hello";
        innerState = ProtocolState.Hello_Reply;
        retransmission = true;
    }

    public void reset() {
        timerTask = null;
        retransmission = false;
        innerState = ProtocolState.Hello;
    }

    //用于释放资源
    public void stop () {
        this.timerTask = null;
        retransmission = false;
    }

    public void setRecvData (String recvData) {
        recvData = recvData.trim();
        switch (innerState) {
            case Hello_Reply:
                Log.i(TAG+"recv", recvData);
                handleHelloReply(recvData);
                break;
            case D1:  //等待完整数据的接收
                try {
                    handleD1(recvData);
                } catch (Exception e) {
                    e.printStackTrace();
                }
                break;
            default:
                break;
        }
    }

    private int handleHelloReply (String recvData) {
        if (recvData.equals(SilkwormConstrain.M1_X)) {  //全茧量法
            silkwormCallback.onChangedView(SilkwormConstrain.M1_X);
            //应该应答了
            reply(SilkwormConstrain.M2_X);
            view = SilkwormConstrain.M2_X;
            innerState = ProtocolState.D1;
            retransmission = false;
            return 1;
        } else if (recvData.equals(SilkwormConstrain.M1_Y)) {
            silkwormCallback.onChangedView(SilkwormConstrain.M1_Y);
            reply(SilkwormConstrain.M2_Y);
            view = SilkwormConstrain.M2_Y;
            innerState = ProtocolState.D1;
            retransmission = false;
            return 1;
        } else if (recvData.equals(SilkwormConstrain.M1_Z)) {
            silkwormCallback.onChangedView(SilkwormConstrain.M1_Z);
            reply(SilkwormConstrain.M2_Z);
            view = SilkwormConstrain.M2_Z;
            innerState = ProtocolState.D1;
            retransmission = false;
            return 1;
        } else {   //未接收到应答消息, 重发hello消息
            //no-op
            return 0;
        }
    }

    private void handleD1 (String recvData) throws Exception{
        //这里是解决协议设计里的一个Bug: 评测仪端是否收到应答M2没办法确认,所以多加了一步判断来避免这个问题
        int indicator = handleHelloReply(recvData);
        if (indicator == 1) {    //说明评测仪未收到M2
            return;  //已经重发了reply了,所以不需要做任何操作
        }

        //正常接收D1的过程
        Log.i(TAG, "recived D1");
        if (recvData != null) {
            if (recvData.length() > 3) {  //表明这是内容数据
                String header = "" + recvData.charAt(0);
                String type = "" + recvData.charAt(1);
                int length = 0;
                String content = "";
                //校验,取出数据
                for (int i = 1; i <= recvData.length(); ++i) {
                    length = Integer.parseInt(recvData.substring(2, 2 + i));
                    content = recvData.substring(2 + i);
                    if (length == content.length()) {
                        break;
                    } else if (length > content.length()) {
                        length = 0;
                        content = "";
                        break;
                    } else {
                        continue;
                    }
                }
                Log.i(TAG, type + " : " + content);
                silkwormCallback.onContentAvai(type, content);
                reply(SilkwormConstrain.D1_REPLY_HEADER + type);
            } else if (recvData.length() == 2 && recvData.equals(SilkwormConstrain.M3)) {  //已经完成了数据发送的过程了
                reply(SilkwormConstrain.M4);
                silkwormCallback.onCompletedData();
                start(); //重新开始新的过程
            } else {
                throw new Exception("Unknown data");
            }
        } else {
            throw new Exception("Data of D1 is not completed form : " + recvData);
        }
    }

    private void reply (final String reply) {
        silkwormCallback.onSendData(reply);
    }
}
