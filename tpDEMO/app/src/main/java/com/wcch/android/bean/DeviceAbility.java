package com.wcch.android.bean;

/**
 * @author created by Lzq
 * @time：2021/8/12
 * @Des：
 */
public class DeviceAbility {
    private int ability;//能力
    private boolean abilityWorking;//是否正在被使用
    private boolean itemClicked;//是否被点击

    public DeviceAbility() {
    }

    public DeviceAbility(int ability, boolean abilityWorking) {
        this.ability = ability;
        this.abilityWorking = abilityWorking;
    }

    public int getAbility() {
        return ability;
    }

    public void setAbility(int ability) {
        this.ability = ability;
    }

    public boolean isAbilityWorking() {
        return abilityWorking;
    }

    public void setAbilityWorking(boolean abilityWorking) {
        this.abilityWorking = abilityWorking;
    }

    public boolean isItemClicked() {
        return itemClicked;
    }

    public void setItemClicked(boolean itemClicked) {
        this.itemClicked = itemClicked;
    }
}
