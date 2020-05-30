package com.pvpin.pvpindemorl.killcount;

import net.minecraft.server.v1_15_R1.NBTTagCompound;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.craftbukkit.v1_15_R1.inventory.CraftItemStack;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerExpChangeEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.LinkedList;
import java.util.List;

public class ExpLis implements Listener {

    @EventHandler
    public void onExpChange(PlayerExpChangeEvent e){
        //获取玩家
        Player player = e.getPlayer();
        //日常排查空手
        if(player.getEquipment().getItemInMainHand().getType().equals(Material.AIR))return;
        //获取物品
        ItemStack item = player.getEquipment().getItemInMainHand();
        //获取nms物品
        net.minecraft.server.v1_15_R1.ItemStack nmsitem = CraftItemStack.asNMSCopy(item);
        //这次我们判断他有没有KillCount标签，所以如果物品没有nbt那就可以pass了
        if(!nmsitem.hasTag())return;
        //获取nbt
        NBTTagCompound nbttc = nmsitem.getTag();
        //排查是否有KillCount标签
        if(!nbttc.hasKey("KillCount"))return;
        //获取经验数量
        int ps=e.getAmount();
        //如果有ProudSoul标签，那就加上这次的数量再赋值
        if(nbttc.hasKey("ProudSoul")) {
            ps+=nbttc.getInt("ProudSoul");
            nbttc.setInt("ProudSoul", ps);

        }else{
            //如果没有，就设置为这次的数量
            nbttc.setInt("ProudSoul",e.getAmount());
        }
        //把物品设置回去
        nmsitem.setTag(nbttc);
        item=CraftItemStack.asBukkitCopy((nmsitem));

        //获取物品meta
        ItemMeta meta = item.getItemMeta();
        //检查物品有没有描述
        if(meta.hasLore()){
            //如果有的话，获取物品描述，并遍历一遍找找有没有我们想要的那一条
            List<String> lore = meta.getLore();
            int sindex=0;
            for(String str:lore){
                //如果有的话，更新下耀魂
                if(str.substring(0,4).equals(ChatColor.AQUA+"耀魂")){
                    lore.set(sindex,ChatColor.AQUA+"耀魂: "+ps);
                    break;
                }
                sindex++;
            }
            //如果没有，即遍历到尾还没找到，则新增一条lore
            if(sindex==lore.size())lore.add(ChatColor.AQUA+"耀魂: "+ps);
            //还是不要忘了把lore设置回meta
            meta.setLore(lore);
        }else{
            //如果没有lore，新建lore
            List<String> lore = new LinkedList<String>();
            lore.add(ChatColor.AQUA+"耀魂: "+ps);
            meta.setLore(lore);
        }
        //也不要忘了把meta设置回物品
        item.setItemMeta(meta);

        player.getEquipment().setItemInMainHand(item);

    }


}
