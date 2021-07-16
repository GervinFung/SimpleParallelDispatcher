package item;

public final class ItemType {
    //Item's name
    private final char itemName;

    public ItemType(final char itemName) {
        this.itemName = itemName;
    }

    public char getItemName() {
        return this.itemName;
    }
}