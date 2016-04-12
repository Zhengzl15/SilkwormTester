package project.silkwormtester.bleservice;

/**
 * @Authon Zhilong Zheng
 * @Email zhengzl0715@163.com
 * @Date 14:20 16/4/11
 */

//协议内部状态变化时回调
public interface SilkwormCallback {
    void onSendData (String data);
    void onChangedView (String view);
    void onContentAvai(String type, String data);
    void onCompletedData();
}
