package com.wcch.java;


/**
 * Created by RyanWang
 * Date: 2023/10/19
 * Describe:
 * 给定一个整数数组 nums 和一个整数目标值 target，请你在该数组中找出 和为目标值 target 的那两个整数，并返回它们的数组下标。 *
 * 你可以假设每种输入只会对应一个答案。但是，数组中同一个元素在答案里不能重复出现。 *
 * 你可以按任意顺序返回答案。
 * 示例 1： *
 * 输入：nums = [2,7,11,15], target = 9
 * 输出：[0,1]
 * 解释：因为 nums[0] + nums[1] == 9 ，返回 [0, 1] 。
 * 示例 2： *
 * 输入：nums = [3,2,4], target = 6
 * 输出：[1,2]
 * 示例 3： *
 * 输入：nums = [3,3], target = 6
 * 输出：[0,1]
 **/
class Solution {
    /*public static void main(String[] args) {
        twoSum(new int[]{2,3,4,5},8);
    }
    public static int[] twoSum(int[] nums, int target) {

        Map map = new HashMap();
        try {
            for (int i = 0; i < nums.length; i++) {
                int tmpI = target - nums[i];
                if (map.containsKey(tmpI)){
                    //tmp i
                    return new int[]{(int) map.get(tmpI),i} ;
                }
                map.put(nums[i],i);

            }
        }catch (Exception e){
            e.printStackTrace();
        }
        return null;
    }*/
}
