package ua.com.tlftgames.waymc.screen.ui.window;

import java.util.ArrayList;

import com.badlogic.gdx.scenes.scene2d.Group;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.utils.Align;

import ua.com.tlftgames.waymc.Config;
import ua.com.tlftgames.waymc.GameCore;
import ua.com.tlftgames.waymc.Translator;
import ua.com.tlftgames.waymc.item.Item;
import ua.com.tlftgames.waymc.listener.Dispatcher;
import ua.com.tlftgames.waymc.listener.Listener;
import ua.com.tlftgames.waymc.natification.Notification;
import ua.com.tlftgames.waymc.screen.StageScreen;
import ua.com.tlftgames.waymc.screen.ui.Button;
import ua.com.tlftgames.waymc.screen.ui.ReceiptGroup;
import ua.com.tlftgames.waymc.screen.ui.ScrollPane;
import ua.com.tlftgames.waymc.screen.ui.TextButton;
import ua.com.tlftgames.waymc.screen.ui.UIGroup;

public class InventoryWindow extends Window implements Listener {
    private Group inventaryBlock;
    private ReceiptGroup receiptsBlock;
    private UIGroup group;
    private InfoWindow info;
    private ScrollPane receiptPane;

    public InventoryWindow(UIGroup group) {
        super(group.getHelper());
        this.group = group;
        this.info = new InfoWindow(this.getHelper());
        this.addInventoryBlock();
        this.addReceiptsBlock();
        this.setBottomButtons(this.getHelper().createReturnButton(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                InventoryWindow.this.getGroup().hideInventory();
            }
        }));
        this.windowGroup.addActor(info);

        this.updateItems();

        this.bg.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                InventoryWindow.this.getGroup().hideInventory();
            }
        });

        int[] events = { Dispatcher.EVENT_ITEMS_CHANGED, Dispatcher.EVENT_RECEIPTS_CHANGED };
        Dispatcher.getInstance().addListener(events, this);
    }

    public UIGroup getGroup() {
        return this.group;
    }

    private void addInventoryBlock() {
        float x = 30;
        float y = this.windowGroup.getHeight() - 30;
        Label inventoryLabel = new Label(Translator.getInstance().translate("invenory.inventory"),
                Config.getInstance().headerStyle);
        inventoryLabel.setPosition(x, y - inventoryLabel.getHeight());
        inventoryLabel.setWidth(this.windowGroup.getWidth() - 60);
        inventoryLabel.setAlignment(Align.center);
        this.windowGroup.addActor(inventoryLabel);

        Group cellGroup = group.getHelper().createItemCells();
        y = y - cellGroup.getHeight() - inventoryLabel.getHeight() - 15;
        cellGroup.setPosition(x, y);
        this.windowGroup.addActor(cellGroup);

        inventaryBlock = new Group();
        inventaryBlock.setBounds(x, y, cellGroup.getWidth(), cellGroup.getHeight());
        this.windowGroup.addActor(inventaryBlock);
    }

    private void addReceiptsBlock() {
        float x = 30;
        float y = inventaryBlock.getY() - 30;
        Label receiptsLabel = new Label(Translator.getInstance().translate("invenory.receipts"),
                Config.getInstance().headerStyle);
        receiptsLabel.setPosition(x, y - receiptsLabel.getHeight());
        receiptsLabel.setWidth(this.windowGroup.getWidth() - 60);
        receiptsLabel.setAlignment(Align.center);
        this.windowGroup.addActor(receiptsLabel);

        receiptsBlock = new ReceiptGroup(this.getHelper(), this.info, false);

        receiptPane = group.getHelper().createScrollPane(receiptsBlock);
        receiptPane.setBounds(x, x + 60, receiptsBlock.getWidth(), y - receiptsLabel.getHeight() - 105);
        receiptPane.updateVisualScroll();
        this.windowGroup.addActor(receiptPane);
    }

    protected void updateItems() {
        this.inventaryBlock.clear();
        int i = 0;
        for (String itemName : GameCore.getInstance().getItemManager().getOwnItems()) {
            Item item = GameCore.getInstance().getItemManager().getItem(itemName);
            Button itemBtn = this.group.getHelper().createItemCellBtn(item, i, new ClickListener() {
                @Override
                public void clicked(InputEvent event, float x, float y) {
                    Item item = (Item) event.getListenerActor().getUserObject();
                    InventoryWindow.this.showItemInfo(item);
                }
            });
            this.inventaryBlock.addActor(itemBtn);
            i++;
        }
    }

    protected void showItemInfo(final Item item) {
        info.setTitle(item.getName());
        info.setText(item.getInfo());
        info.setImage(item.getImage());
        ArrayList<TextButton> actions = new ArrayList<TextButton>();
        if (!item.isResource()) {
            TextButton deconstruct = this.getHelper().createTextButton("item.deconstruct");
            deconstruct.addListener(new ClickListener() {
                @Override
                public void clicked(InputEvent event, float x, float y) {
                    if (GameCore.getInstance().getItemManager().canDeconstruct(item)) {
                        InventoryWindow.this.info.hide();
                        InventoryWindow.this.getHelper().openDialog(InventoryWindow.this.windowGroup,
                                Translator.getInstance().translate("item.deconstruct.confirm"), new Runnable() {
                            @Override
                            public void run() {
                                GameCore.getInstance().getItemManager().deconstructItem(item);
                            }
                        }, new Runnable() {
                            @Override
                            public void run() {
                                InventoryWindow.this.info.show();
                            }
                        });
                    } else {
                        GameCore.getInstance().getNotificationManager()
                                .addNotification(new Notification("inventory", "notification.items.max.count.reached"));
                    }
                }
            });
            actions.add(deconstruct);
        }
        TextButton remove = this.getHelper().createTextButton("item.remove");
        remove.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                InventoryWindow.this.info.hide();
                InventoryWindow.this.getHelper().openDialog(InventoryWindow.this.windowGroup,
                        Translator.getInstance().translate("item.remove.confirm"), new Runnable() {
                    @Override
                    public void run() {
                        GameCore.getInstance().getItemManager().removeOwnItem(item);
                    }
                }, new Runnable() {
                    @Override
                    public void run() {
                        InventoryWindow.this.info.show();
                    }
                });
            }
        });
        actions.add(remove);
        info.setActions(actions);
        info.show();
    }

    @Override
    protected void afterShow() {
        StageScreen.getInstance().getStage().setScrollFocus(receiptPane);
    }

    @Override
    public void fireEvent(int event) {
        switch (event) {
        case Dispatcher.EVENT_ITEMS_CHANGED:
            this.updateItems();
            this.receiptsBlock.update();
            break;
        case Dispatcher.EVENT_RECEIPTS_CHANGED:
            this.receiptsBlock.update();
            break;
        }
    }

}
