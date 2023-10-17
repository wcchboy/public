package com.wcch.android.soft.bonjourdiscover.module.bonjour;

import android.content.Context;
import android.net.nsd.NsdManager;
import android.net.nsd.NsdServiceInfo;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.Message;


import com.igrs.betotablet.soft.model.event.HostChangedEvent;

import java.util.HashMap;
import java.util.Map;

public class BonjourManager {
    public static BonjourManager manager;

    public static BonjourManager getInstance(Context context) {
        if (manager == null) {
            manager = new BonjourManager(context);
        }
        return manager;
    }

    private final String TAG = BonjourManager.class.getSimpleName();
    private Context context;
    public static final int STATUS_UNKNOWN = 0;
    public static final int STATUS_DISCOVER = 1; //正在发现服务

    NsdManager mNsdManager;
    NsdManager.ResolveListener mResolveListener;
    NsdManager.DiscoveryListener mDiscoveryListener;
    NsdManager.RegistrationListener mRegistrationListener;

    public static final String SERVICE_TYPE = "_screencast._tcp.";
    public NsdServiceInfo mService;
    private HandlerThread handlerThread;
    private Handler handler;
    private final static int MSG_DISCOVER = 100;

    public BonjourManager(Context context) {
        this.context = context;
        init(context);
        try {
            discoverServices();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void close() {
        tearDown();
    }

    private void handleMessage(Message msg) {
        switch (msg.what) {
            case MSG_DISCOVER:
                handler.removeMessages(MSG_DISCOVER);
                discoverServices();
                break;
        }
    }

    public void init(Context context) {
        if (!isFinishServiceFound) {
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    init(context);
                }
            }, 500);
            return;
        }
        isFinishServiceFound = false;
        mNsdManager = (NsdManager) context.getSystemService(Context.NSD_SERVICE);
        initializeNsd();
        if (handlerThread == null) {
            handlerThread = new HandlerThread("managethread");
            handlerThread.start();
            handler = new Handler(handlerThread.getLooper()) {
                @Override
                public void handleMessage(Message msg) {
                    super.handleMessage(msg);
                    BonjourManager.this.handleMessage(msg);
                }
            };
        }
    }

    public void initializeNsd() {
        initializeResolveListener();
        initializeDiscoveryListener();
        initializeRegistrationListener();
    }

    boolean isFinishServiceFound = true;
    public void initializeDiscoveryListener() {
        mDiscoveryListener = new NsdManager.DiscoveryListener() {

            @Override
            public void onDiscoveryStarted(String regType) {
//                Log.d(TAG, "Service discovery started");
            }

            @Override
            public void onServiceFound(NsdServiceInfo service) {
//                if (listener != null) {
//                    listener.onServiceFound();
//                }
//                Log.d(TAG, "Service discovery success" + service);
                if (!service.getServiceType().equalsIgnoreCase(SERVICE_TYPE)) {
//                    Log.d(TAG, "Unknown Service Type: " + service.getServiceType());
                } else {
                    isFinishServiceFound = true;
                    mNsdManager.resolveService(service, mResolveListener);

                }
            }

            @Override
            public void onServiceLost(NsdServiceInfo service) {
//                Log.e(TAG, "service lost" + service);
                if (mService == service) {
                    mService = null;
                }

                if (listener != null) {
                    listener.onServiceLost(service.getHost() + "");
                }
                setStatus(STATUS_DISCOVER);
            }

            @Override
            public void onDiscoveryStopped(String serviceType) {
//                Log.i(TAG, "Discovery stopped: " + serviceType);
                setStatus(STATUS_UNKNOWN);
            }

            @Override
            public void onStartDiscoveryFailed(String serviceType, int errorCode) {
//                Log.e(TAG, "Discovery failed: Error code:" + errorCode);
                mNsdManager.stopServiceDiscovery(this);
                setStatus(STATUS_UNKNOWN);
            }

            @Override
            public void onStopDiscoveryFailed(String serviceType, int errorCode) {
//                Log.e(TAG, "Discovery failed: Error code:" + errorCode);
                mNsdManager.stopServiceDiscovery(this);
            }
        };
    }


    public void initializeResolveListener() {
        mResolveListener = new NsdManager.ResolveListener() {

            @Override
            public void onResolveFailed(NsdServiceInfo serviceInfo, int errorCode) {
//                Log.e(TAG, "Resolve failed" + errorCode);
//                new Handler(Looper.getMainLooper()).post(new Runnable() {
//                    @Override
//                    public void run() {
//                        ToastUtil.getInstance().showToast(ToastUtil.ToastType.ERROR, "ip = " + serviceInfo.getHost() + "  errorCode " + errorCode);
//                    }
//                });

                if (listener != null) {
                    listener.onServiceLost(serviceInfo.getHost() + "");
                }
            }

            @Override
            public void onServiceResolved(NsdServiceInfo serviceInfo) {
//                Log.e(TAG, "Resolve Succeeded. " + serviceInfo);
//                new Handler(Looper.getMainLooper()).post(new Runnable() {
//                    @Override
//                    public void run() {
//                        ToastUtil.getInstance().showToast(ToastUtil.ToastType.SUCCESS, "ip = " + serviceInfo.getHost() + "发现");
//                    }
//                });

                if (listener != null) {
                    listener.onServiceFound(serviceInfo.getHost() + "");
                }
                mService = serviceInfo;
                //EventBus.getDefault().post(new HostChangedEvent(mService.getHost().getHostAddress(),""+mService.getPort()));
                onMessageEvent(new HostChangedEvent(mService.getHost().getHostAddress(), "" + mService.getPort()));
            }
        };
    }

    public void initializeRegistrationListener() {
        mRegistrationListener = new NsdManager.RegistrationListener() {

            @Override
            public void onServiceRegistered(NsdServiceInfo NsdServiceInfo) {
            }

            @Override
            public void onRegistrationFailed(NsdServiceInfo arg0, int arg1) {
            }

            @Override
            public void onServiceUnregistered(NsdServiceInfo arg0) {
            }

            @Override
            public void onUnregistrationFailed(NsdServiceInfo serviceInfo, int errorCode) {
            }

        };
    }

    public void registerService(int port) {
        NsdServiceInfo serviceInfo = new NsdServiceInfo();
        serviceInfo.setPort(port);
        String mServiceName = "signage";
        serviceInfo.setServiceName(mServiceName);
        serviceInfo.setServiceType(SERVICE_TYPE);

        mNsdManager.registerService(
                serviceInfo, NsdManager.PROTOCOL_DNS_SD, mRegistrationListener);

    }

    public void discoverServices() {
        mNsdManager.discoverServices(
                SERVICE_TYPE, NsdManager.PROTOCOL_DNS_SD, mDiscoveryListener);

    }

    public void stopDiscovery() {
        mNsdManager.stopServiceDiscovery(mDiscoveryListener);
    }

    public NsdServiceInfo getChosenServiceInfo() {
        return mService;
    }

    public void tearDown() {
        mNsdManager.unregisterService(mRegistrationListener);
    }

    private void setStatus(int status) {
        if (status == STATUS_UNKNOWN) {
            handler.sendEmptyMessageDelayed(MSG_DISCOVER, 1000);
        }
    }

    private void onMessageEvent(final HostChangedEvent ev) {
        //采用TCP Socket
        new Thread(new Runnable() {
            @Override
            public void run() {
                onHostChangedTcpSocket(ev.getHost());
            }
        }).start();
    }


    private Map<String, TcpSocketManager> socketMap = new HashMap();

    private void onHostLostTcpSocket(String host) {
        TcpSocketManager tcpSocket = socketMap.get(host);
        if (tcpSocket == null) {
            return;
        }
        tcpSocket.close();
        socketMap.remove(tcpSocket);
    }

    private void onHostChangedTcpSocket(String host) {
        onHostLostTcpSocket(host);
        TcpSocketManager tcpSocketManager = new TcpSocketManager(context, host);
        socketMap.put(host, tcpSocketManager);
    }

    public interface OnServiceStatusChange {
        void onServiceFound(String ip);

        void onServiceLost(String ip);
    }

    private OnServiceStatusChange listener;

    public void setOnServiceStatusChange(OnServiceStatusChange listener) {
        this.listener = listener;
    }
}
