package caesreon.core.hilfsklassen;

import caesreon.main.SpigotMain;
import org.bukkit.inventory.ItemStack;

public class BossPluginUT {
    @SuppressWarnings("static-access")
    public static ItemStack getCustomItemStack(SpigotMain m, String S) {
        S = S.toUpperCase();
        switch (S) {
            case "ABSINT":
                return m.getTheOneAndOnlyEnte().ABSINT.getItemStack();
            case "MET":
                return m.getTheOneAndOnlyEnte().MET.getItemStack();

            case "ADAMANTIUM":
                return m.getTheOneAndOnlyEnte().ADAMANTIUM.getItemStack();
            case "COBALT":
                return m.getTheOneAndOnlyEnte().COBALT.getItemStack();

            case "ADAMANTIUM_AXT":
                return m.getTheOneAndOnlyEnte().ADAMANTIUM_AXT.getItemStack();
            case "ADAMANTIUM_BOGEN":
                return m.getTheOneAndOnlyEnte().ADAMANTIUM_BOGEN.getItemStack();
            case "ADAMANTIUM_HELM":
                return m.getTheOneAndOnlyEnte().ADAMANTIUM_HELM.getItemStack();
            case "ADAMANTIUM_HOSE":
                return m.getTheOneAndOnlyEnte().ADAMANTIUM_HOSE.getItemStack();
            case "ADAMANTIUM_BRUSTPLATTE":
                return m.getTheOneAndOnlyEnte().ADAMANTIUM_BRUSTPLATTE.getItemStack();
            case "ADAMANTIUM_SCHUHE":
                return m.getTheOneAndOnlyEnte().ADAMANTIUM_SCHUHE.getItemStack();
            case "ADAMANTIUM_SICHEL":
                return m.getTheOneAndOnlyEnte().ADAMANTIUM_SICHEL.getItemStack();

            case "VOID_HAMMER_LV1":
                return m.getTheOneAndOnlyEnte().VOID_HAMMER_LV1.getItemStack();
            case "VOID_HAMMER_LV2":
                return m.getTheOneAndOnlyEnte().VOID_HAMMER_LV2.getItemStack();
            case "VOID_HAMMER_LV3":
                return m.getTheOneAndOnlyEnte().VOID_HAMMER_LV3.getItemStack();
            case "VOID_HAMMER_LV4":
                return m.getTheOneAndOnlyEnte().VOID_HAMMER_LV4.getItemStack();
            case "VOID_HAMMER_LV5":
                return m.getTheOneAndOnlyEnte().VOID_HAMMER_LV5.getItemStack();
            case "VOID_HAMMER_LV6":
                return m.getTheOneAndOnlyEnte().VOID_HAMMER_LV6.getItemStack();
            case "VOID_HAMMER_LV7":
                return m.getTheOneAndOnlyEnte().VOID_HAMMER_LV7.getItemStack();


            case "LUCKY_PICKAXE_LV1":
                return m.getTheOneAndOnlyEnte().LUCKY_PICKAXE_LV1.getItemStack();
            case "LUCKY_PICKAXE_LV2":
                return m.getTheOneAndOnlyEnte().LUCKY_PICKAXE_LV2.getItemStack();
            case "LUCKY_PICKAXE_LV3":
                return m.getTheOneAndOnlyEnte().LUCKY_PICKAXE_LV3.getItemStack();
            case "LUCKY_PICKAXE_LV4":
                return m.getTheOneAndOnlyEnte().LUCKY_PICKAXE_LV4.getItemStack();
            case "LUCKY_PICKAXE_LV5":
                return m.getTheOneAndOnlyEnte().LUCKY_PICKAXE_LV5.getItemStack();
            case "LUCKY_PICKAXE_LV6":
                return m.getTheOneAndOnlyEnte().LUCKY_PICKAXE_LV6.getItemStack();
            case "LUCKY_PICKAXE_LV7":
                return m.getTheOneAndOnlyEnte().LUCKY_PICKAXE_LV7.getItemStack();
            case "LUCKY_PICKAXE_LV8":
                return m.getTheOneAndOnlyEnte().LUCKY_PICKAXE_LV8.getItemStack();


            case "COBALT_AXT_LV1":
                return m.getTheOneAndOnlyEnte().COBALT_AXT_LV1.getItemStack();
            case "COBALT_AXT_LV2":
                return m.getTheOneAndOnlyEnte().COBALT_AXT_LV2.getItemStack();
            case "COBALT_PICKAXE":
                return m.getTheOneAndOnlyEnte().COBALT_PICKAXE.getItemStack();
            case "COBALT_BOGEN":
                return m.getTheOneAndOnlyEnte().COBALT_BOGEN.getItemStack();

            case "DRACHEN_BRUSTPLATTE":
                return m.getTheOneAndOnlyEnte().DRACHEN_BRUSTPLATTE.getItemStack();
            case "DRACHEN_HELM":
                return m.getTheOneAndOnlyEnte().DRACHEN_HELM.getItemStack();
            case "DRACHEN_HOSE":
                return m.getTheOneAndOnlyEnte().DRACHEN_HOSE.getItemStack();
            case "DRACHEN_SCHUHE":
                return m.getTheOneAndOnlyEnte().DRACHEN_SCHUHE.getItemStack();

            case "BENTOBOX":
                return m.getTheOneAndOnlyEnte().BENTOBOX.getItemStack();
            case "CLUSTER_BUSTER":
                return m.getTheOneAndOnlyEnte().CLUSTER_BUSTER.getItemStack();
            case "TECH_HAMMER":
                return m.getTheOneAndOnlyEnte().TECH_HAMMER.getItemStack();
            case "GENTLE_HAMMER":
                return m.getTheOneAndOnlyEnte().GENTLE_HAMMER.getItemStack();
        }
        return null;
    }

}