package p.trovia.objects;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

public class Collection implements Article{

    String name;
    String desc;
    String rPath;
    int troveMR;
    int geodeMR;
    List<CollectionEnums.CollectionType> types;        // refer to CollectionEnums for CollectionTypes
    Map<CollectionEnums.Property, Double> properties;  // should not contain mastery info
    Map<CollectionEnums.Buff, Double> buffs;           // for dragons only, else is null
    List<String> notes;
    String recipe;

    // for non-dragons
    public Collection(String name, String desc, String rPath, List<CollectionEnums.CollectionType> types, Map<CollectionEnums.Property, Double> properties) {
        this.name = name;
        this.desc = desc;
        this.rPath = rPath;
        this.types = types;
        this.properties = properties;
    }

    // for dragons
    public Collection(String name, String desc, String rPath, Map<CollectionEnums.Property, Double> properties, Map<CollectionEnums.Buff, Double> buffs, boolean isMagRider) {
        this.name = name;
        this.desc = desc;
        this.rPath = rPath;
        this.types = new ArrayList<>();
        types.addAll(Arrays.asList(CollectionEnums.CollectionType.DRAGON, CollectionEnums.CollectionType.MOUNT, CollectionEnums.CollectionType.WINGS));
        this.buffs = buffs;
        this.properties = properties;
        if (isMagRider) {
            types.add(CollectionEnums.CollectionType.MAG);
        }
    }

    public String getName() {
        return name;
    }

    public String getDesc() {
        return desc;
    }

    @Override
    public String getRPath() {
        return rPath;
    }

    /**
     * Returns the types of this Collection entry. (e.g. mount, wings, boat)
     *
     * @return a list of CollectionEnums.CollectionType
     */
    public List<CollectionEnums.CollectionType> getTypes() {
        return types;
    }

    /**
     * Returns the properties of this Collection entry. (e.g. ground movement speed, turning rate)
     * Refer to CollectionEnums for a full list of properties.
     *
     * @return a map with keys CollectionEnums.Property, and values of the properties
     */
    public Map<CollectionEnums.Property, Double> getProperties() {
        return properties;
    }

    /**
     * Returns the stat buffs granted by obtaining this Collection entry. Returns null if the entry is not a final-state dragon.
     *
     * @return a map containing the buffs granted by this Collection, where keys are the stats, and values are the increases
     */
    public Map<CollectionEnums.Buff, Double> getBuffs() {
        if (buffs == null) {
            System.out.println("This isn't a dragon, so there are no buffs.");
            return null;
        } else {
            return buffs;
        }
    }

    /**
     * Add notes to this Collection entry. Text should not be directly added to this entry; instead, a key-value pair
     * should be entered to a user-generated LangFile entry (tentatively with relative path "languages/en/custom"),
     * and the key used in the LangFile entry should be input here as the parameter.
     *
     * @param key the key mapped to the corresponding string in LangFile with rPath languages/en/custom
     */
    public void addNotes(String key) {
        if (notes == null) {
            notes = new ArrayList<>();
        }
        notes.add(key);
    }

    public void setTroveMR(int mastery) {
        troveMR = mastery;
    }

    public void setGeodeMR(int mastery) {
        geodeMR = mastery;
    }

    public void setRecipe(String rPath) {
        recipe = rPath;
    }
}