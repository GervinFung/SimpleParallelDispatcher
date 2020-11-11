package app;

//Class for type of item generated
public final class ItemType {
    //Item's name
    private final String itemName;
    public ItemType(final String itemName) {
        this.itemName = itemName;
    }
    protected String getItemName() {
        return this.itemName;
    }
}
