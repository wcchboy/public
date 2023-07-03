package com.igrs.sml.callback;

public interface TransferFileCallback {

    /**
     * 文件传输进度
     *
     * // 为100时表示文件传输(发送or接收)完成
     */
    void progress(String peerIp, String file_path, int progress);

    /**
     *  当前文件传输进度[0, 100];
     * @param code 200	// 打开文件失败;
     *             201	// 读取文件失败;
     *             202	// 写入文件失败;
     *             203	// 文件名解析失败
     *             204	// 对方取消文件发送
     * @param msg
     */
    void error(String peerIp, int code, String msg);
}