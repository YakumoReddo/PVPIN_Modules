package com.pvpin.pvpindemorl.killcount;

import net.minecraft.server.v1_15_R1.NBTTagCompound;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.craftbukkit.v1_15_R1.inventory.CraftItemStack;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.PrepareAnvilEvent;
import org.bukkit.inventory.AnvilInventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.Damageable;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.List;

public class RepairLis implements Listener {


    @EventHandler
    public void AnvilEvent(PrepareAnvilEvent e){
        //获取铁砧物品栏
        AnvilInventory inv = e.getInventory();
        //若前两个槽为空，则不进行下一步
        if(inv.getItem(0)==null)return;
        if(inv.getItem(1)==null)return;
        //获取待修理物品item和材料mat
        ItemStack item = inv.getItem(0);
        ItemStack mat = inv.getItem(1);

        //获取待修理物品的nms物品，检测并记录耀魂数
        net.minecraft.server.v1_15_R1.ItemStack nmsitem = CraftItemStack.asNMSCopy(item);
        if(!nmsitem.hasTag())return;
        NBTTagCompound nbttc = nmsitem.getTag();
        if(!nbttc.hasKey("ProudSoul"))return;
        int ps = nbttc.getInt("ProudSoul");
        //检测材料是不是耀魂
        if (!mat.isSimilar(PSDroupLis.getProudSoul()))return;
        //获取武器的耐久
        Damageable meta = (Damageable) item.getItemMeta();
        int damage = meta.getDamage();
        //目标是无论总耐久是多少，统统五次修好
        damage-=item.getType().getMaxDurability()/5;
        //因为是整除，所以修复五次后可能会产生0-4点耐久损耗，所以直接归零
        if(damage<=5)damage=0;
        //设置耐久值
        meta.setDamage(damage);

        ItemStack result = item.clone();
        result.setItemMeta((ItemMeta) meta);
        net.minecraft.server.v1_15_R1.ItemStack nmsresult = CraftItemStack.asNMSCopy(result);
        nbttc = nmsresult.getTag();
        //修复后，耀魂数+100
        nbttc.setInt("ProudSoul",ps+100);
        nmsresult.setTag(nbttc);
        result = CraftItemStack.asBukkitCopy(nmsresult);
        ItemMeta imeta=result.getItemMeta();
        //如果有的话，获取物品描述，并遍历一遍找找有没有我们想要的那一条
        List<String> lore = imeta.getLore();
        int sindex=0;
        for(String str:lore){
            //如果有的话，更新下耀魂
            if(str.substring(0,4).equals(ChatColor.AQUA+"耀魂")){
                lore.set(sindex,ChatColor.AQUA+"耀魂: "+ps);
                break;
            }
            sindex++;
        }
        //还是不要忘了把lore设置回meta
        imeta.setLore(lore);
        result.setItemMeta(imeta);

        //设置经验消耗1级
        inv.setRepairCost(1);
        //设置铁砧合成结果
        e.setResult(result);


    }


    @EventHandler
    public void onAnvilInteract(InventoryClickEvent e){

        
    }



}
