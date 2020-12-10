package app;

//Class for type of item generated
public final class ItemType {
    //Item's name
    private final char itemName;
    public ItemType(final char itemName) {
        this.itemName = itemName;
    }
    protected char getItemName() {
        return this.itemName;
    }
}