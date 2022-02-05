package caesreon.core.hilfsklassen;

import caesreon.core.Log;
import org.bukkit.configuration.serialization.ConfigurationSerialization;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.yaml.snakeyaml.DumperOptions;
import org.yaml.snakeyaml.Yaml;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public class SerializeUT {

    private static final Yaml yaml;

    static {
        DumperOptions dumperOptions = new DumperOptions();
        dumperOptions.setPrettyFlow(true);
        yaml = new Yaml(dumperOptions);
    }

    private Map<String, Map<String, Object>> serializableMap = new HashMap<>();

    public SerializeUT(ItemStack itemStackToMakeSerializable) {
        ItemStack itemStack = itemStackToMakeSerializable.clone();
        serializableMap.put("itemMeta", itemStack.hasItemMeta() ? itemStack.getItemMeta().serialize() : null);
        itemStack.setItemMeta(null);
        serializableMap.put("itemStack", itemStack.serialize());
        Log.SpigotLogger.Debug(String.valueOf(itemStack.serialize().toString()));
    }

    public SerializeUT(Map<String, Map<String, Object>> serializableMap) {
        this.serializableMap = serializableMap;
    }

    public SerializeUT(String yamlSerializedMap) {
        this.serializableMap = yaml.loadAs(yamlSerializedMap, Map.class);
    }

    public ItemStack toItemStack() {
        ItemStack itemStack = ItemStack.deserialize(serializableMap.get("itemStack"));
        if (serializableMap.get("itemMeta") != null)
        {
            Log.SpigotLogger.Debug(String.valueOf(serializableMap.get("itemMeta")));
            itemStack.setItemMeta((ItemMeta) ConfigurationSerialization.deserializeObject(serializableMap.get("itemMeta"), Objects.requireNonNull(ConfigurationSerialization.getClassByAlias("ItemMeta"))));
        }
     return itemStack;
    }

    public Map<String, Map<String, Object>> toSerializableMap() {
        return new HashMap<>(serializableMap);
    }

    public String toYaml() {
        Log.SpigotLogger.Debug(yaml.dump(serializableMap));
        return yaml.dump(serializableMap);
    }
}
