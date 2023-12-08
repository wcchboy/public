package com.wcch.android.demo;

/**
 * Created by RyanWang
 * Date: 2023/10/17
 * Describe:
 **/
public class SinglethDemo {
    private volatile static SinglethDemo instance ;
    private SinglethDemo(){}
    public static SinglethDemo getInstance(){
        if (instance == null){
            synchronized (SinglethDemo.class){
                if (instance ==null){
                    instance = new SinglethDemo();
                }
            }
        }
        return instance;
    }


    //冒泡
    private void bubbleSort(int arr[]){
        for(int i =0 ;i< arr.length - 1; i++){
            boolean isSored = true;
            for (int j = 0;j<arr.length-i;j++){
                if (arr[j] > arr[j+1]){
                    isSored = false;
                    int tmpInt =arr[j];
                    arr[j] = arr[j+1];
                    arr[j+1] = tmpInt;
                }
            }
            if (isSored){
                break;
            }

        }
    }

}
