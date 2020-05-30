package com.pvpin.pvpindemorl.killcount;

import net.minecraft.server.v1_15_R1.NBTTagCompound;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.craftbukkit.v1_15_R1.inventory.CraftItemStack;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.LinkedList;
import java.util.List;

public class KillLis implements Listener {

    @EventHandler
    public void onKill(EntityDeathEvent e){
        //获取死亡的实体
        LivingEntity death = e.getEntity();
        //死亡实体的击杀者是不是玩家实体，不是则返回
        if(!(death.getKiller() instanceof Player))return;
        //如果是的话，取这个玩家
        Player player = death.getKiller();

        //如果手上物品为空气，也就是没有，不作处理
        if(player.getEquipment().getItemInMainHand().getType().equals(Material.AIR))return;
        //获取物品
        ItemStack item = player.getEquipment().getItemInMainHand();
        //如果物品没有meta，排除
        if(!item.hasItemMeta())return;
        //获取物品的meta，其中包含了名字、描述、属性等信息
        ItemMeta meta = item.getItemMeta();
        //如果没有名字，即普通物品，排除
        if(!meta.hasDisplayName())return;
        //如果不是我们想要的剑，排除
        if(!meta.getDisplayName().equals("无铭刀「木偶」"))return;

        //获取nms物品
        net.minecraft.server.v1_15_R1.ItemStack nmsitem = CraftItemStack.asNMSCopy(item);
        //获取物品的NBTTag，如果没有就新建一个空的
        NBTTagCompound nbttc = nmsitem.hasTag()?nmsitem.getTag():(new NBTTagCompound());
        //设置初始杀敌数
        int kc=1;
        //如果nbt中有KillCount这个Key，那就把kc设置成物品记录的杀敌数+1，再设置回物品的nbt中
        if(nbttc.hasKey("KillCount")){
            kc=nbttc.getInt("KillCount")+1;
            nbttc.setInt("KillCount",kc);
        }else{
            //不然的话，直接设置为1
            nbttc.setInt("KillCount",1);
        }
        //不要忘了把nbt给物品设置回去
        nmsitem.setTag(nbttc);
        //也不要忘了把nms的物品改成bukkit的物品
        item=CraftItemStack.asBukkitCopy(nmsitem);

        //获取物品meta。因为我们改过了nbt，所以要重新获取一下
        meta = item.getItemMeta();
        //检查物品有没有描述
        if(meta.hasLore()){
            //如果有的话，获取物品描述，并遍历一遍找找有没有我们想要的那一条
            List<String> lore = meta.getLore();
            int sindex=0;
            for(String str:lore){
                //如果有的话，更新下杀敌数
                if(str.substring(0,5).equals(ChatColor.DARK_RED+"杀敌数")){
                    lore.set(sindex,ChatColor.DARK_RED+"杀敌数: "+kc);
                    break;
                }
                sindex++;
            }
            //如果没有，即遍历到尾还没找到，则新增一条lore
            if(sindex==lore.size())lore.add(ChatColor.DARK_RED+"杀敌数: "+kc);
            //还是不要忘了把lore设置回meta
            meta.setLore(lore);
        }else{
            //如果没有lore，新建lore
            List<String> lore = new LinkedList<String>();
            lore.add(ChatColor.DARK_RED+"杀敌数: "+kc);
            meta.setLore(lore);
        }
        //也不要忘了把meta设置回物品
        item.setItemMeta(meta);

        //最后，把修改完的物品替换原来的物品
        player.getEquipment().setItemInMainHand(item);


    }

}
