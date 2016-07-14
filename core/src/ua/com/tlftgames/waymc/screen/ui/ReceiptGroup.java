package ua.com.tlftgames.waymc.screen.ui;

import com.badlogic.gdx.scenes.scene2d.Group;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.utils.Align;

import ua.com.tlftgames.waymc.Config;
import ua.com.tlftgames.waymc.GameCore;
import ua.com.tlftgames.waymc.Translator;
import ua.com.tlftgames.waymc.item.Item;
import ua.com.tlftgames.waymc.screen.ui.window.InfoWindow;

import java.util.ArrayList;

public class ReceiptGroup extends Table {
    private UIHelper helper;
    private InfoWindow info;
    private int width = 780;
    private boolean canConstruct;

    public ReceiptGroup(UIHelper helper, InfoWindow info, boolean canConstruct) {
        this.helper = helper;
        this.info = info;
        this.canConstruct = canConstruct;
        this.setWidth(width);
        this.top();
        this.update();
    }

    public void update() {
        this.clear();
        ArrayList<Group> unconstrReceipts = new ArrayList<Group>();
        ArrayList<Group> receipts = new ArrayList<Group>();
        for (String receiptsName : GameCore.getInstance().getItemManager().getOwnReceipts()) {
            Group receiptGroup = this.createReceipt(GameCore.getInstance().getItemManager().getItem(receiptsName));
            String constructable = (String) receiptGroup.getUserObject();
            if (constructable.contentEquals("ok")) {
                receipts.add(receiptGroup);
            } else {
                unconstrReceipts.add(receiptGroup);
            }
        }
        receipts.addAll(unconstrReceipts);
        for (Group receiptGroup : receipts) {
            this.add(receiptGroup).width(this.width).padBottom(13);
            this.row();
        }
    }

    private Table createReceipt(final Item receipt) {
        Table receiptLine = new Table();
        receiptLine.setWidth(width);
        Button receiptBtn = this.helper.createItemBtn(receipt, new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                ReceiptGroup.this.showInfo(receipt, true);
            }
        });
        receiptLine.add(receiptBtn).width(receiptBtn.getWidth());
        String sigh = "=";
        String result = "ok";
        int height = 100;
        ClickListener listener = new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                Item item = (Item) event.getListenerActor().getUserObject();
                ReceiptGroup.this.showInfo(item, false);
            }
        };
        for (String itemName : receipt.getResources()) {
            Label sighLabel = new Label(sigh, Config.getInstance().headerStyle);
            int sighWidth = 50;
            sighLabel.setSize(sighWidth, height);
            sighLabel.setAlignment(Align.center);
            receiptLine.add(sighLabel).width(sighWidth);
            Item item = GameCore.getInstance().getItemManager().getItem(itemName);
            boolean haveItem = GameCore.getInstance().getItemManager().hasItem(itemName);
            if (!haveItem)
                result = "fail";
            Button itemBtn = this.helper.createItemBtn(item, listener, haveItem);
            receiptLine.add(itemBtn).width(itemBtn.getWidth());
            sigh = "+";
        }
        receiptLine.setUserObject(result);
        int resultWidth = 230;
        if (canConstruct) {
            TextButton createBtn;
            if (result.contentEquals("ok") && receipt.isCreatable()) {
                createBtn = this.helper.createTextButton("item.create");
                createBtn.addListener(new ClickListener() {
                    @Override
                    public void clicked(InputEvent event, float x, float y) {
                        GameCore.getInstance().getItemManager().createOwnItem(receipt);
                    }
                });
            } else {
                createBtn = this.helper.createTextButton("item.create");
                createBtn.setChecked(true);
            }
            receiptLine.add(createBtn).width(resultWidth).align(Align.right);
        } else {
            Label resultLabel = new Label(Translator.getInstance().translate("receipt.result." + result),
                    Config.getInstance().normalStyle);
            resultLabel.setSize(resultWidth, height);
            resultLabel.setAlignment(Align.right);
            resultLabel.setWrap(true);
            receiptLine.add(resultLabel).width(resultWidth).align(Align.right);
        }
        return receiptLine;
    }

    protected void showInfo(Item item, boolean isChart) {
        String title = item.getName();
        if (isChart) title = "receipt.title+"+title;
        info.setTitle(title);
        info.setText(item.getInfo());
        info.setImage(item.getImage());
        info.setActions(null);
        info.show();
    }
}
