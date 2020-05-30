package com.pvpin.pvpindemorl.killcount;

import com.pvpin.pvpindemorl.DemoManager;
import com.pvpin.pvpindemorl.IDemoClass;

public class KillCount implements IDemoClass {

    @Override
    public void onEnable(){

        DemoManager.registerListener(new KillLis(),"KillCount");
        DemoManager.registerListener(new ExpLis(),"KillCount");
    }

    @Override
    public void onDisable(){

    }
}
