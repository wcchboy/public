package com.wcch.java;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by RyanWang
 * Date: 2023/10/18
 * Describe:
 **/
public class Demo {

    public static void main(String[] args) {
        //String string = "{\"name\":\"Bingo\",\"age\":18}";
        //System.out.println("数据长度" + string.length());
        twoSum(new int[]{2,3,4,5},7);


    }
    public static int[] twoSum(int[] nums, int target) {

        //通过某种计算得到其索引值，再通过索引值就可以找到它了
        Map map = new HashMap();
        try {
            //[2,7,11,15]  , 9
            for (int i = 0; i < nums.length; i++) {
                // i =0, nums[i] = 2
                int tmpI = target - nums[i];
                //tmpI  taarget - nums[i]= 7
                if (map.containsKey(tmpI)){
                    //hash[7] ? true 直接return
                    //没的话就 再循环
                    //i = 1 nums[i] = 7 tagget-nums[i] = 2
                    //hash[2] 有吗？
                    //有return  没有 hash[7] =1
                    return new int[]{(int) map.get(tmpI),i} ;
                }
                //没有找到 hash[2] = 0
                map.put(nums[i],i);

            }
        }catch (Exception e){
            e.printStackTrace();
        }
        return null;
    }
}
